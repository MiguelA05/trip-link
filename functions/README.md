# Moderacion IA de comentarios (DeepSeek + Firebase Functions)

Este modulo implementa `moderateCommentDeepSeek`, una callable function usada por Android para revisar lenguaje ofensivo en reseñas.

## Flujo

1. La app envia `comment` a la callable function.
2. La function llama DeepSeek (`deepseek-chat`) con salida JSON estricta.
3. Retorna:
   - `isInappropriate`
   - `reason`
   - `safeAlternative`
4. Si es inapropiado, Android muestra modal con alternativa y permite reemplazar.

## Requisitos

- Node.js 20
- Firebase CLI (`npm i -g firebase-tools`)
- Proyecto Firebase configurado
- Secreto configurado: `DEEPSEEK_API_KEY`

## Configuracion

Desde la raiz del proyecto:

```powershell
Push-Location "C:\Users\Miguel Angel\AndroidStudioProjects\trip-link\functions"
npm install
Pop-Location
```

Configura el secreto de DeepSeek:

```powershell
Push-Location "C:\Users\Miguel Angel\AndroidStudioProjects\trip-link"
firebase functions:secrets:set DEEPSEEK_API_KEY
Pop-Location
```

## Desarrollo local

```powershell
Push-Location "C:\Users\Miguel Angel\AndroidStudioProjects\trip-link\functions"
npm run build
npm run serve
Pop-Location
```

## Despliegue

```powershell
Push-Location "C:\Users\Miguel Angel\AndroidStudioProjects\trip-link\functions"
npm run build
npm run deploy
Pop-Location
```

## Costos en desarrollo

No es posible garantizar costo cero absoluto. En trafico bajo y con free tier activo puedes operar sin costo, pero si excedes cuotas de Functions/red/Firestore o DeepSeek, se generan cobros.

