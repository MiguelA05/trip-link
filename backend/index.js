// Minimal Express server that mints Firebase custom tokens
// Usage: node index.js

const express = require('express')
const admin = require('firebase-admin')
const app = express()
const port = process.env.PORT || 3000

// Make sure you place your service account key JSON as ./serviceAccountKey.json
try {
  const serviceAccount = require('./serviceAccountKey.json')
  admin.initializeApp({ credential: admin.credential.cert(serviceAccount) })
  console.log('Firebase Admin initialized')
} catch (err) {
  console.error('Failed to initialize Firebase Admin. Please provide serviceAccountKey.json in this folder.', err)
  process.exit(1)
}

// Simple endpoint that returns a custom token for a provided uid
// Example: GET /custom-token?uid=user-123
app.get('/custom-token', async (req, res) => {
  const uid = req.query.uid || `anon-${Date.now()}`
  const additionalClaims = { role: 'user' }
  try {
    const token = await admin.auth().createCustomToken(uid, additionalClaims)
    res.json({ token })
  } catch (err) {
    console.error('Failed to create custom token', err)
    res.status(500).json({ error: 'Failed to create custom token' })
  }
})

app.listen(port, () => {
  console.log(`Custom token server running on http://localhost:${port}`)
})

