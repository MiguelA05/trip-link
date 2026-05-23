"use strict";
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    var desc = Object.getOwnPropertyDescriptor(m, k);
    if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
      desc = { enumerable: true, get: function() { return m[k]; } };
    }
    Object.defineProperty(o, k2, desc);
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || (function () {
    var ownKeys = function(o) {
        ownKeys = Object.getOwnPropertyNames || function (o) {
            var ar = [];
            for (var k in o) if (Object.prototype.hasOwnProperty.call(o, k)) ar[ar.length] = k;
            return ar;
        };
        return ownKeys(o);
    };
    return function (mod) {
        if (mod && mod.__esModule) return mod;
        var result = {};
        if (mod != null) for (var k = ownKeys(mod), i = 0; i < k.length; i++) if (k[i] !== "default") __createBinding(result, mod, k[i]);
        __setModuleDefault(result, mod);
        return result;
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
exports.onPublicationStatusChange = exports.moderateCommentDeepSeek = void 0;
const https_1 = require("firebase-functions/v2/https");
const firestore_1 = require("firebase-functions/v2/firestore");
const admin = __importStar(require("firebase-admin"));
const params_1 = require("firebase-functions/params");
const node_crypto_1 = require("node:crypto");
admin.initializeApp();
const deepSeekApiKey = (0, params_1.defineSecret)("DEEPSEEK_API_KEY");
const MAX_COMMENT_LENGTH = 300;
const DEFAULT_SAFE_ALTERNATIVE = "El lugar no fue de mi agrado, mi experiencia fue mala.";
const DEFAULT_FALLBACK_REASON = "No fue posible validar el comentario automáticamente.";
function hashValue(value) {
    return (0, node_crypto_1.createHash)("sha256").update(value).digest("hex").slice(0, 12);
}
function normalizeCommentInput(rawComment) {
    if (typeof rawComment !== "string") {
        return "";
    }
    return rawComment
        .replace(/[\u0000-\u001F\u007F]/g, " ")
        .replace(/\s+/g, " ")
        .trim();
}
function logModerationEvent(event, details) {
    console.info(JSON.stringify({
        source: "moderateCommentDeepSeek",
        event,
        ...details,
    }));
}
function extractModerationJson(content) {
    const parsed = JSON.parse(content);
    return {
        isInappropriate: Boolean(parsed.isInappropriate),
        reason: typeof parsed.reason === "string" ? parsed.reason.trim() : "",
        safeAlternative: typeof parsed.safeAlternative === "string"
            ? parsed.safeAlternative.trim()
            : "",
    };
}
function buildFallbackModerationJson() {
    return {
        isInappropriate: true,
        reason: DEFAULT_FALLBACK_REASON,
        safeAlternative: DEFAULT_SAFE_ALTERNATIVE,
    };
}
function ensureFallbackValues(moderation) {
    const safeAlternative = moderation.isInappropriate && !moderation.safeAlternative
        ? DEFAULT_SAFE_ALTERNATIVE
        : moderation.safeAlternative;
    return {
        isInappropriate: moderation.isInappropriate,
        reason: moderation.reason || (moderation.isInappropriate ? DEFAULT_FALLBACK_REASON : ""),
        safeAlternative,
    };
}
function buildSystemPrompt() {
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
function buildUserPrompt(comment) {
    return [
        "Analiza el siguiente comentario para una resena de un lugar:",
        comment,
        "Si es apropiado, devuelve isInappropriate=false y safeAlternative vacio.",
        "Si es inapropiado, devuelve isInappropriate=true con una alternativa cordial.",
        "Responde en espanol.",
    ].join("\n");
}
exports.moderateCommentDeepSeek = (0, https_1.onCall)({
    region: "us-central1",
    timeoutSeconds: 20,
    memory: "256MiB",
    secrets: [deepSeekApiKey],
    enforceAppCheck: false,
    cors: true,
}, async (request) => {
    const rawComment = request.data?.comment;
    const comment = normalizeCommentInput(rawComment);
    const publicationId = typeof request.data?.context?.publicationId === "string"
        ? request.data.context.publicationId.trim()
        : "";
    const userId = typeof request.data?.context?.userId === "string"
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
        throw new https_1.HttpsError("invalid-argument", "El comentario supera el maximo permitido de 300 caracteres.");
    }
    // Try to read the secret from Firebase Secrets. If running locally (emulator)
    // or the secret is not configured, fall back to process.env.DEEPSEEK_API_KEY.
    let apiKey = "";
    try {
        // deepSeekApiKey.value() will throw if secret is not configured in the project
        // when running against real Cloud Functions. Wrap in try/catch to allow local env.
        if (deepSeekApiKey && typeof deepSeekApiKey.value === "function") {
            apiKey = deepSeekApiKey.value();
        }
    }
    catch (e) {
        // ignore, we'll try env fallback below
    }
    if (!apiKey) {
        apiKey = process.env.DEEPSEEK_API_KEY || "";
    }
    if (!apiKey) {
        throw new https_1.HttpsError("failed-precondition", "Falta configurar DEEPSEEK_API_KEY. En desarrollo local puedes exportarla como variable de entorno DEEPSEEK_API_KEY antes de iniciar los emuladores.");
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
            throw new https_1.HttpsError("unavailable", `DeepSeek rechazo la solicitud de moderacion (${deepSeekResponse.status}). ${errorText}`);
        }
        const json = (await deepSeekResponse.json());
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
        let moderation;
        try {
            moderation = ensureFallbackValues(extractModerationJson(content));
        }
        catch (parseError) {
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
    }
    catch (error) {
        if (error instanceof https_1.HttpsError) {
            logModerationEvent("error", {
                commentHash,
                code: error.code,
            });
            throw error;
        }
        logModerationEvent("unexpected-error", {
            commentHash,
        });
        throw new https_1.HttpsError("internal", `Error inesperado durante moderacion: ${String(error)}`);
    }
});
/**
 * Triggers when a publication status changes.
 * Notifies the author when their post is verified.
 *
 * OPTIMIZACIONES:
 * 1. Intenta usar authorFcmToken de la publicación (evita lectura a users)
 * 2. Si no está, fallback a lectura de users
 * 3. Paraleliza envíos de mensajes (no espera secuencial)
 */
exports.onPublicationStatusChange = (0, firestore_1.onDocumentUpdated)("publications/{publicationId}", async (event) => {
    const newData = event.data?.after.data();
    const previousData = event.data?.before.data();
    if (!newData || !previousData)
        return;
    console.log(`DEBUG: Cambio detectado en ${event.params.publicationId}. Estado anterior: ${previousData.estado}, Nuevo: ${newData.estado}`);
    // 1. Detectar transición a VERIFICADA (Ignorar mayúsculas/minúsculas del estado)
    if (newData.estado?.toUpperCase() === "VERIFICADA" && previousData.estado?.toUpperCase() !== "VERIFICADA") {
        // LIMPIEZA CRÍTICA: Firestore IDs suelen ser minúsculas y sin espacios
        const authorEmail = String(newData.usuarioAutorId || "").trim().toLowerCase();
        const publicationTitle = newData.titulo || "Nuevo lugar";
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
                }
                else {
                    console.error(`ERROR: No existe documento en 'users' para: ${authorEmail}`);
                }
            }
            // OPTIMIZACIÓN 3: Paralelizar envíos de mensajes (Promise.all)
            // Ambos se envían al mismo tiempo, no secuencial
            const messagingPromises = [];
            // Rama A: Notificación personalizada al autor
            if (authorFcmToken) {
                const personalizedMessage = {
                    notification: {
                        title: "¡Tu publicación ha sido aprobada!",
                        body: `Felicidades, "${publicationTitle}" ya es visible para toda la comunidad.`
                    },
                    token: authorFcmToken
                };
                messagingPromises.push(admin.messaging().send(personalizedMessage)
                    .then(() => console.log(`SUCCESS: Notificación personalizada enviada a ${authorEmail}`))
                    .catch((err) => console.warn(`WARN: Fallo notificación personalizada: ${err.message}`)));
            }
            else {
                console.warn(`WARN: No hay fcmToken para autor ${authorEmail}`);
            }
            // Rama B: Broadcast a todos (topic)
            const broadcastMessage = {
                notification: {
                    title: "¡Nuevo lugar descubierto!",
                    body: `Se ha publicado: ${publicationTitle}. ¡Ven a verlo!`
                },
                topic: "new_places"
            };
            messagingPromises.push(admin.messaging().send(broadcastMessage)
                .then(() => console.log("SUCCESS: Broadcast enviado al topic new_places"))
                .catch((err) => console.error(`ERROR: Fallo broadcast: ${err.message}`)));
            // Esperar ambos en paralelo (mucho más rápido que secuencial)
            await Promise.all(messagingPromises);
            console.log(`INFO: Notificaciones completadas para publicación ${event.params.publicationId}`);
        }
        catch (error) {
            console.error("ERROR CRÍTICO en la ejecución:", error);
        }
    }
});
