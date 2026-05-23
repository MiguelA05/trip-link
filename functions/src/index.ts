import { HttpsError, onCall, Request } from "firebase-functions/v2/https";
import { defineSecret } from "firebase-functions/params";
import { createHash } from "node:crypto";

type DeepSeekModerationJson = {
  isInappropriate: boolean;
  reason: string;
  safeAlternative: string;
};

const deepSeekApiKey = defineSecret("DEEPSEEK_API_KEY");
const MAX_COMMENT_LENGTH = 300;

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














