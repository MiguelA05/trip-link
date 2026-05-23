"use strict";
// Alternativa de debugging: Función que NO requiere autenticación
// Úsala para comparar si el problema es de cliente o servidor
Object.defineProperty(exports, "__esModule", { value: true });
exports.moderateCommentDeepSeekNoAuth = void 0;
const https_1 = require("firebase-functions/v2/https");
const params_1 = require("firebase-functions/params");
const node_crypto_1 = require("node:crypto");
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
        source: "moderateCommentDeepSeekTest",
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
// VERSIÓN TEST: Sin autenticación requerida
exports.moderateCommentDeepSeekNoAuth = (0, https_1.onCall)({
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
    const commentHash = comment ? hashValue(comment) : "empty";
    // DEBUG: Log complete auth context
    console.log("=== DEBUG TEST: Complete request.auth (NO AUTH REQUIRED) ===");
    console.log("request.auth:", JSON.stringify({
        uid: request.auth?.uid || "NO_UID",
        token: request.auth?.token ? "present" : "MISSING",
        claims: request.auth?.token?.claims ? "present" : "MISSING",
    }, null, 2));
    logModerationEvent("received", {
        authPresent: Boolean(request.auth?.uid),
        isTest: true,
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
    // Try to read the secret from Firebase Secrets
    let apiKey = "";
    try {
        if (deepSeekApiKey && typeof deepSeekApiKey.value === "function") {
            apiKey = deepSeekApiKey.value();
        }
    }
    catch (e) {
        // ignore
    }
    if (!apiKey) {
        apiKey = process.env.DEEPSEEK_API_KEY || "";
    }
    if (!apiKey) {
        throw new https_1.HttpsError("failed-precondition", "DEEPSEEK_API_KEY not configured");
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
            throw new https_1.HttpsError("unavailable", `DeepSeek error (${deepSeekResponse.status})`);
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
        throw new https_1.HttpsError("internal", `Unexpected error: ${String(error)}`);
    }
});
