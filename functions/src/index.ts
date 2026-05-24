import { HttpsError, onCall } from "firebase-functions/v2/https";
import { onDocumentUpdated } from "firebase-functions/v2/firestore";
import * as admin from "firebase-admin";
import { defineSecret } from "firebase-functions/params";
import { createHash } from "node:crypto";

admin.initializeApp();

type DeepSeekModerationJson = {
  isInappropriate: boolean;
  reason: string;
  safeAlternative: string;
};

const deepSeekApiKey = defineSecret("DEEPSEEK_API_KEY");
const MAX_COMMENT_LENGTH = 300;
const NEARBY_PUBLICATION_RADIUS_KM = 5;
const FCM_MULTICAST_BATCH_SIZE = 500;

const DEFAULT_SAFE_ALTERNATIVE =
  "El lugar no fue de mi agrado, mi experiencia fue mala.";

const DEFAULT_FALLBACK_REASON =
  "No fue posible validar el comentario automáticamente.";

function hashValue(value: string): string {
  return createHash("sha256").update(value).digest("hex").slice(0, 12);
}

function normalizeCommentInput(rawComment: unknown): string {
  if (typeof rawComment !== "string") {
    return "";
  }

  return rawComment
    .replace(/[\u0000-\u001F\u007F]/g, " ")
    .replace(/\s+/g, " ")
    .trim();
}

function logModerationEvent(
  event: string,
  details: Record<string, string | number | boolean | null | undefined>
): void {
  console.info(
    JSON.stringify({
      source: "moderateCommentDeepSeek",
      event,
      ...details,
    })
  );
}

function extractModerationJson(content: string): DeepSeekModerationJson {
  const parsed = JSON.parse(content) as Partial<DeepSeekModerationJson>;

  return {
    isInappropriate: Boolean(parsed.isInappropriate),
    reason: typeof parsed.reason === "string" ? parsed.reason.trim() : "",
    safeAlternative:
      typeof parsed.safeAlternative === "string"
        ? parsed.safeAlternative.trim()
        : "",
  };
}

function buildFallbackModerationJson(): DeepSeekModerationJson {
  return {
    isInappropriate: true,
    reason: DEFAULT_FALLBACK_REASON,
    safeAlternative: DEFAULT_SAFE_ALTERNATIVE,
  };
}

function ensureFallbackValues(
  moderation: DeepSeekModerationJson
): DeepSeekModerationJson {
  const safeAlternative =
    moderation.isInappropriate && !moderation.safeAlternative
      ? DEFAULT_SAFE_ALTERNATIVE
      : moderation.safeAlternative;

  return {
    isInappropriate: moderation.isInappropriate,
    reason: moderation.reason || (moderation.isInappropriate ? DEFAULT_FALLBACK_REASON : ""),
    safeAlternative,
  };
}

function buildSystemPrompt(): string {
  return [
    "Eres un moderador de lenguaje para reseñas de lugares.",
    "Responde SOLO JSON valido.",
    "Clasifica si hay contenido ofensivo, despectivo, humillante o vulgar.",
    "Si el contenido es inapropiado, sugiere una alternativa neutral y respetuosa.",
    "No incluyas markdown ni texto adicional.",
    "Usa exactamente este esquema:",
    '{"isInappropriate":boolean,"reason":"string","safeAlternative":"string"}',
  ].join(" ");
}

function buildUserPrompt(comment: string): string {
  return [
    "Analiza el siguiente comentario para una resena de un lugar:",
    comment,
    "Si es apropiado, devuelve isInappropriate=false y safeAlternative vacio.",
    "Si es inapropiado, devuelve isInappropriate=true con una alternativa cordial.",
    "Responde en espanol.",
  ].join("\n");
}

type GeoPointLike = {
  latitud?: unknown;
  longitud?: unknown;
};

function parseCoordinate(value: unknown): number | null {
  if (typeof value !== "number" || !Number.isFinite(value)) {
    return null;
  }

  return value;
}

function parseLocation(value: unknown): { latitud: number; longitud: number } | null {
  if (!value || typeof value !== "object") {
    return null;
  }

  const location = value as GeoPointLike;
  const latitud = parseCoordinate(location.latitud);
  const longitud = parseCoordinate(location.longitud);

  if (latitud === null || longitud === null) {
    return null;
  }

  if (latitud < -90 || latitud > 90 || longitud < -180 || longitud > 180) {
    return null;
  }

  return { latitud, longitud };
}

function distanceKm(
  from: { latitud: number; longitud: number },
  to: { latitud: number; longitud: number }
): number {
  const earthRadiusKm = 6371;
  const dLat = ((to.latitud - from.latitud) * Math.PI) / 180;
  const dLon = ((to.longitud - from.longitud) * Math.PI) / 180;
  const fromLat = (from.latitud * Math.PI) / 180;
  const toLat = (to.latitud * Math.PI) / 180;

  const a =
    Math.sin(dLat / 2) ** 2 +
    Math.cos(fromLat) * Math.cos(toLat) * Math.sin(dLon / 2) ** 2;

  return 2 * earthRadiusKm * Math.asin(Math.sqrt(Math.min(1, Math.max(0, a))));
}

async function findNearbyUserTokens(params: {
  authorEmail: string;
  publicationLocation: { latitud: number; longitud: number };
  radiusKm: number;
}): Promise<string[]> {
  const usersSnapshot = await admin.firestore()
    .collection("users")
    .where("activo", "==", true)
    .get();

  const tokens = new Set<string>();

  usersSnapshot.forEach((userDoc) => {
    const user = userDoc.data();
    const email = String(user.email || userDoc.id || "").trim().toLowerCase();
    const token = typeof user.fcmToken === "string" ? user.fcmToken.trim() : "";
    const location = parseLocation(user.ubicacion);

    if (!token || !location || email === params.authorEmail) {
      return;
    }

    if (user.ubicacionExactaActiva === false) {
      return;
    }

    const userDistanceKm = distanceKm(params.publicationLocation, location);
    if (userDistanceKm <= params.radiusKm) {
      tokens.add(token);
    }
  });

  return [...tokens];
}

async function sendNearbyPublicationNotifications(params: {
  tokens: string[];
  publicationId: string;
  publicationTitle: string;
}): Promise<void> {
  if (params.tokens.length === 0) {
    console.log("INFO: No hay usuarios cercanos con token FCM para notificar");
    return;
  }

  const title = "¡Nuevo lugar cercano!";
  const body = `Se ha publicado cerca de ti: ${params.publicationTitle}. ¡Ven a verlo!`;

  for (let start = 0; start < params.tokens.length; start += FCM_MULTICAST_BATCH_SIZE) {
    const tokens = params.tokens.slice(start, start + FCM_MULTICAST_BATCH_SIZE);
    const response = await admin.messaging().sendEachForMulticast({
      data: {
        type: "new_publication",
        publication_id: params.publicationId,
        notification_id: `new_publication_${params.publicationId}`,
        title,
        body,
      },
      tokens,
    });

    console.log(
      `INFO: Notificaciones cercanas enviadas. Exitosas=${response.successCount}, fallidas=${response.failureCount}`
    );
  }
}

export const moderateCommentDeepSeek = onCall(
  {
    region: "us-central1",
    timeoutSeconds: 20,
    memory: "256MiB",
    secrets: [deepSeekApiKey],
    enforceAppCheck: false,
    cors: true,
  },
  async (request) => {
    const rawComment = request.data?.comment;
    const comment = normalizeCommentInput(rawComment);
    const publicationId =
      typeof request.data?.context?.publicationId === "string"
        ? request.data.context.publicationId.trim()
        : "";
    const userId =
      typeof request.data?.context?.userId === "string"
        ? request.data.context.userId.trim()
        : "";
    const authUid = request.auth?.uid ?? "";
    const commentHash = comment ? hashValue(comment) : "empty";

    // DEBUG: Log complete auth context
    console.log("=== DEBUG: Complete request.auth ===");
    console.log("request.auth:", JSON.stringify({
      uid: request.auth?.uid,
      token: request.auth?.token ? "present" : "missing",
      claims: request.auth?.token?.claims ? "present" : "missing",
      email: request.auth?.token?.email || "no_email",
      iss: request.auth?.token?.iss || "no_iss",
      aud: request.auth?.token?.aud || "no_aud",
    }, null, 2));

    logModerationEvent("received", {
      authPresent: Boolean(authUid),
      authHash: authUid ? hashValue(authUid) : null,
      userHash: userId ? hashValue(userId) : null,
      publicationHash: publicationId ? hashValue(publicationId) : null,
      commentHash,
      length: comment.length,
    });

    if (!comment) {
      logModerationEvent("skipped-empty", {
        commentHash,
      });
      return {
        ok: true,
        result: {
          isInappropriate: false,
          reason: "",
          safeAlternative: "",
        },
      };
    }

    if (comment.length > MAX_COMMENT_LENGTH) {
      logModerationEvent("rejected-too-long", {
        commentHash,
        length: comment.length,
      });
      throw new HttpsError(
        "invalid-argument",
        "El comentario supera el maximo permitido de 300 caracteres."
      );
    }

    // Try to read the secret from Firebase Secrets. If running locally (emulator)
    // or the secret is not configured, fall back to process.env.DEEPSEEK_API_KEY.
    let apiKey = "";
    try {
      // deepSeekApiKey.value() will throw if secret is not configured in the project
      // when running against real Cloud Functions. Wrap in try/catch to allow local env.
      if (deepSeekApiKey && typeof (deepSeekApiKey as any).value === "function") {
        apiKey = (deepSeekApiKey as any).value();
      }
    } catch (e) {
      // ignore, we'll try env fallback below
    }

    if (!apiKey) {
      apiKey = process.env.DEEPSEEK_API_KEY || "";
    }

    if (!apiKey) {
      throw new HttpsError("failed-precondition", "Falta configurar DEEPSEEK_API_KEY. En desarrollo local puedes exportarla como variable de entorno DEEPSEEK_API_KEY antes de iniciar los emuladores.");
    }

    const payload = {
      model: "deepseek-chat",
      temperature: 0.2,
      response_format: { type: "json_object" },
      messages: [
        {
          role: "system",
          content: buildSystemPrompt(),
        },
        {
          role: "user",
          content: buildUserPrompt(comment),
        },
      ],
    };

    try {
      const deepSeekResponse = await fetch("https://api.deepseek.com/chat/completions", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${apiKey}`,
        },
        body: JSON.stringify(payload),
      });

      if (!deepSeekResponse.ok) {
        const errorText = await deepSeekResponse.text();
        logModerationEvent("deepseek-http-error", {
          commentHash,
          status: deepSeekResponse.status,
        });
        throw new HttpsError(
          "unavailable",
          `DeepSeek rechazo la solicitud de moderacion (${deepSeekResponse.status}). ${errorText}`
        );
      }

      const json = (await deepSeekResponse.json()) as {
        choices?: Array<{ message?: { content?: string } }>;
      };

      const content = json.choices?.[0]?.message?.content;
      if (!content) {
        logModerationEvent("deepseek-empty-content", {
          commentHash,
        });
        const fallback = buildFallbackModerationJson();
        return {
          ok: true,
          result: fallback,
        };
      }

      let moderation: DeepSeekModerationJson;
      try {
        moderation = ensureFallbackValues(extractModerationJson(content));
      } catch (parseError) {
        logModerationEvent("deepseek-parse-fallback", {
          commentHash,
        });
        moderation = buildFallbackModerationJson();
      }

      logModerationEvent("classified", {
        commentHash,
        isInappropriate: moderation.isInappropriate,
      });

      return {
        ok: true,
        result: {
          isInappropriate: moderation.isInappropriate,
          reason: moderation.reason,
          safeAlternative: moderation.safeAlternative,
        },
      };
    } catch (error) {
      if (error instanceof HttpsError) {
        logModerationEvent("error", {
          commentHash,
          code: error.code,
        });
        throw error;
      }
      logModerationEvent("unexpected-error", {
        commentHash,
      });
      throw new HttpsError("internal", `Error inesperado durante moderacion: ${String(error)}`);
    }
  }
);

/**
 * Triggers when a publication status changes.
 * Notifies the author when their post is verified.
 *
 * OPTIMIZACIONES:
 * 1. Intenta usar authorFcmToken de la publicación (evita lectura a users)
 * 2. Si no está, fallback a lectura de users
 * 3. Paraleliza envíos de mensajes (no espera secuencial)
 */
export const onPublicationStatusChange = onDocumentUpdated(
  "publications/{publicationId}",
  async (event) => {
    const newData = event.data?.after.data();
    const previousData = event.data?.before.data();

    if (!newData || !previousData) return;

    console.log(`DEBUG: Cambio detectado en ${event.params.publicationId}. Estado anterior: ${previousData.estado}, Nuevo: ${newData.estado}`);

    // 1. Detectar transición a VERIFICADA (Ignorar mayúsculas/minúsculas del estado)
    if (newData.estado?.toUpperCase() === "VERIFICADA" && previousData.estado?.toUpperCase() !== "VERIFICADA") {

      // LIMPIEZA CRÍTICA: Firestore IDs suelen ser minúsculas y sin espacios
      const authorEmail = String(newData.usuarioAutorId || "").trim().toLowerCase();
      const publicationTitle = newData.titulo || "Nuevo lugar";
      const publicationLocation = parseLocation(newData.ubicacion);

      console.log(`INFO: Procesando notificación para autor: [${authorEmail}]`);

      if (!authorEmail) {
        console.error("ERROR: La publicación no tiene usuarioAutorId");
        return;
      }

      try {
        // OPTIMIZACIÓN 1: Intentar obtener token del documento de publicación (si está cached)
        let authorFcmToken = newData.authorFcmToken || null;

        // OPTIMIZACIÓN 2: Si no está en publicación, buscar en users (fallback)
        if (!authorFcmToken) {
          const userDoc = await admin.firestore().collection("users").doc(authorEmail).get();
          if (userDoc.exists) {
            authorFcmToken = userDoc.data()?.fcmToken || null;
          } else {
            console.error(`ERROR: No existe documento en 'users' para: ${authorEmail}`);
          }
        }

        if (!publicationLocation) {
          console.warn(`WARN: Publicación ${event.params.publicationId} no tiene ubicación válida; no se notifican usuarios cercanos`);
        }

        const messagingPromises = [];

        // Rama A: Notificación personalizada al autor
        if (authorFcmToken) {
          const title = "¡Tu publicación ha sido aprobada!";
          const body = `Felicidades, "${publicationTitle}" ya es visible para toda la comunidad.`;
          const personalizedMessage = {
            data: {
              type: "publication_approved",
              publication_id: event.params.publicationId,
              notification_id: `approved_${event.params.publicationId}`,
              title,
              body,
            },
            token: authorFcmToken
          };
          messagingPromises.push(
            admin.messaging().send(personalizedMessage)
              .then(() => console.log(`SUCCESS: Notificación personalizada enviada a ${authorEmail}`))
              .catch((err) => console.warn(`WARN: Fallo notificación personalizada: ${err.message}`))
          );
        } else {
          console.warn(`WARN: No hay fcmToken para autor ${authorEmail}`);
        }

        if (publicationLocation) {
          messagingPromises.push(
            findNearbyUserTokens({
              authorEmail,
              publicationLocation,
              radiusKm: NEARBY_PUBLICATION_RADIUS_KM,
            })
              .then((nearbyTokens) =>
                sendNearbyPublicationNotifications({
                  tokens: nearbyTokens,
                  publicationId: event.params.publicationId,
                  publicationTitle,
                })
              )
              .catch((err) => console.error(`ERROR: Fallo notificaciones cercanas: ${err.message}`))
          );
        }

        // Esperar ambos en paralelo (mucho más rápido que secuencial)
        await Promise.all(messagingPromises);
        console.log(`INFO: Notificaciones completadas para publicación ${event.params.publicationId}`);

      } catch (error) {
        console.error("ERROR CRÍTICO en la ejecución:", error);
      }
    }
  }
);












