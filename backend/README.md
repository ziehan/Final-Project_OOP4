# 📖 API Documentation - Is There Anyone

## 🌐 Base Configuration

| Item | Value                       |
|------|-----------------------------|
| **Base URL** | `http://localhost:9090/api` |
| **Content-Type** | `application/json`          |
| **Server Port** | `9090`                      |

---

## 📦 Standard Response Format

Semua endpoint mengembalikan response dengan format berikut:

```json
{
  "success": true,
  "message": "Pesan sukses/error",
  "data": { ... },
  "timestamp": "2025-12-21T10:30:00"
}
```

---

## 🔐 Authentication Endpoints

### 1. Sign Up (Registrasi)

**Endpoint:** `POST /api/auth/signup`

**Request Body:**
```json
{
  "username": "player123",
  "email": "player@email.com",
  "password": "password123",
  "displayName": "Player One"  // optional
}
```

**Validation:**
- `username`: wajib, 3-50 karakter
- `email`: wajib, format email valid
- `password`: wajib, minimal 6 karakter
- `displayName`: opsional

**Success Response (201 Created):**
```json
{
  "success": true,
  "message": "Registrasi berhasil",
  "data": {
    "message": "Registration successful",
    "user": {
      "id": 1,
      "username": "player123",
      "email": "player@email.com",
      "displayName": "Player One",
      "createdAt": "2025-12-21T10:30:00"
    },
    "token": null
  },
  "timestamp": "2025-12-21T10:30:00"
}
```

---

### 2. Sign In (Login)

**Endpoint:** `POST /api/auth/signin`

**Request Body:**
```json
{
  "usernameOrEmail": "player123",
  "password": "password123"
}
```

**Validation:**
- `usernameOrEmail`: wajib (bisa username atau email)
- `password`: wajib

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Login berhasil",
  "data": {
    "message": "Login successful",
    "user": {
      "id": 1,
      "username": "player123",
      "email": "player@email.com",
      "displayName": "Player One",
      "createdAt": "2025-12-21T10:30:00"
    },
    "token": null
  },
  "timestamp": "2025-12-21T10:30:00"
}
```

---

### 3. Check Username Availability

**Endpoint:** `GET /api/auth/check/username/{username}`

**Example:** `GET /api/auth/check/username/player123`

**Response:**
```json
{
  "success": true,
  "message": "Username sudah digunakan",  // atau "Username tersedia"
  "data": true,  // true jika sudah digunakan, false jika tersedia
  "timestamp": "2025-12-21T10:30:00"
}
```

---

### 4. Check Email Availability

**Endpoint:** `GET /api/auth/check/email/{email}`

**Example:** `GET /api/auth/check/email/player@email.com`

**Response:**
```json
{
  "success": true,
  "message": "Email sudah terdaftar",  // atau "Email tersedia"
  "data": true,  // true jika sudah terdaftar, false jika tersedia
  "timestamp": "2025-12-21T10:30:00"
}
```

---

### 5. Get User by Username

**Endpoint:** `GET /api/auth/user/{username}`

**Example:** `GET /api/auth/user/player123`

**Response:**
```json
{
  "success": true,
  "message": "User ditemukan",
  "data": {
    "id": 1,
    "username": "player123",
    "email": "player@email.com",
    "displayName": "Player One",
    "createdAt": "2025-12-21T10:30:00"
  },
  "timestamp": "2025-12-21T10:30:00"
}
```

---

## 💾 Game Save Endpoints

### 1. Save Game

**Endpoint:** `POST /api/save`

**Request Body:**
```json
{
  "userId": "player123",
  "slotId": 1,
  "saveData": {
    "playerState": {
      "currentMap": "forest_level",
      "posX": 100,
      "posY": 200,
      "health": 100
    },
    "stats": {
      "allTimeDeathCount": 5,
      "allTimeCompletedTask": 10
    },
    "inventory": ["sword", "potion", "key"]
  }
}
```

**Validation:**
- `userId`: wajib
- `slotId`: wajib, nilai 1-3
- `saveData`: wajib (bisa berisi data apapun dalam format JSON)

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Game berhasil disimpan",
  "data": {
    "userId": "player123",
    "slotId": 1,
    "saveData": { ... },
    "lastUpdated": "2025-12-21T10:30:00"
  },
  "timestamp": "2025-12-21T10:30:00"
}
```

---

### 2. Load Game

**Endpoint:** `GET /api/save/{userId}/{slotId}`

**Example:** `GET /api/save/player123/1`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Game berhasil dimuat",
  "data": {
    "userId": "player123",
    "slotId": 1,
    "saveData": {
      "playerState": { ... },
      "stats": { ... },
      "inventory": [ ... ]
    },
    "lastUpdated": "2025-12-21T10:30:00"
  },
  "timestamp": "2025-12-21T10:30:00"
}
```

---

### 3. Get All Save Slots

**Endpoint:** `GET /api/save/{userId}/slots`

**Example:** `GET /api/save/player123/slots`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Slots berhasil dimuat",
  "data": [
    {
      "slotId": 1,
      "isEmpty": false,
      "lastUpdated": "2025-12-21T10:30:00",
      "currentMap": "forest_level",
      "allTimeDeathCount": 5,
      "allTimeCompletedTask": 10
    },
    {
      "slotId": 2,
      "isEmpty": true,
      "lastUpdated": null,
      "currentMap": null,
      "allTimeDeathCount": null,
      "allTimeCompletedTask": null
    },
    {
      "slotId": 3,
      "isEmpty": true,
      "lastUpdated": null,
      "currentMap": null,
      "allTimeDeathCount": null,
      "allTimeCompletedTask": null
    }
  ],
  "timestamp": "2025-12-21T10:30:00"
}
```

---

### 4. Delete Save Slot

**Endpoint:** `DELETE /api/save/{userId}/{slotId}`

**Example:** `DELETE /api/save/player123/1`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Slot berhasil dihapus",
  "data": null,
  "timestamp": "2025-12-21T10:30:00"
}
```

---

### 5. Delete All Save Slots

**Endpoint:** `DELETE /api/save/{userId}`

**Example:** `DELETE /api/save/player123`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Semua slot berhasil dihapus",
  "data": null,
  "timestamp": "2025-12-21T10:30:00"
}
```

---

### 6. Check Slot Exists

**Endpoint:** `GET /api/save/{userId}/{slotId}/exists`

**Example:** `GET /api/save/player123/1/exists`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Success",
  "data": true,  // true jika slot ada, false jika kosong
  "timestamp": "2025-12-21T10:30:00"
}
```

---

## 🏥 Health Check Endpoints

### 1. Health Check

**Endpoint:** `GET /api/health`

**Response:**
```json
{
  "success": true,
  "message": "Server is running",
  "data": {
    "status": "UP",
    "service": "Is There Anyone - Game Backend",
    "version": "1.0.0",
    "timestamp": "2025-12-21T10:30:00"
  },
  "timestamp": "2025-12-21T10:30:00"
}
```

---

### 2. Ping

**Endpoint:** `GET /api/ping`

**Response:** `pong` (plain text)

---

## ❌ Error Responses

### Validation Error (400 Bad Request)
```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "username": "Username harus 3-50 karakter",
    "email": "Format email tidak valid"
  },
  "timestamp": "2025-12-21T10:30:00"
}
```

### Not Found (404)
```json
{
  "success": false,
  "message": "User tidak ditemukan",
  "data": null,
  "timestamp": "2025-12-21T10:30:00"
}
```

### Conflict (409)
```json
{
  "success": false,
  "message": "Username sudah digunakan",
  "data": null,
  "timestamp": "2025-12-21T10:30:00"
}
```

### Server Error (500)
```json
{
  "success": false,
  "message": "Internal server error",
  "data": null,
  "timestamp": "2025-12-21T10:30:00"
}
```

---

## 🔧 Frontend Integration Example (JavaScript/TypeScript)

### API Service Setup

```javascript
const API_BASE_URL = 'http://localhost:9090/api';

// Helper function untuk API calls
async function apiCall(endpoint, options = {}) {
  const response = await fetch(`${API_BASE_URL}${endpoint}`, {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
    ...options,
  });
  
  const data = await response.json();
  
  if (!data.success) {
    throw new Error(data.message);
  }
  
  return data;
}

// Auth Functions
async function signup(username, email, password, displayName = null) {
  return apiCall('/auth/signup', {
    method: 'POST',
    body: JSON.stringify({ username, email, password, displayName }),
  });
}

async function signin(usernameOrEmail, password) {
  return apiCall('/auth/signin', {
    method: 'POST',
    body: JSON.stringify({ usernameOrEmail, password }),
  });
}

async function checkUsername(username) {
  return apiCall(`/auth/check/username/${username}`);
}

async function checkEmail(email) {
  return apiCall(`/auth/check/email/${encodeURIComponent(email)}`);
}

async function getUser(username) {
  return apiCall(`/auth/user/${username}`);
}

// Game Save Functions
async function saveGame(userId, slotId, saveData) {
  return apiCall('/save', {
    method: 'POST',
    body: JSON.stringify({ userId, slotId, saveData }),
  });
}

async function loadGame(userId, slotId) {
  return apiCall(`/save/${userId}/${slotId}`);
}

async function getAllSlots(userId) {
  return apiCall(`/save/${userId}/slots`);
}

async function deleteSlot(userId, slotId) {
  return apiCall(`/save/${userId}/${slotId}`, {
    method: 'DELETE',
  });
}

async function deleteAllSlots(userId) {
  return apiCall(`/save/${userId}`, {
    method: 'DELETE',
  });
}

async function checkSlotExists(userId, slotId) {
  return apiCall(`/save/${userId}/${slotId}/exists`);
}

// Health Check
async function healthCheck() {
  return apiCall('/health');
}
```

### Usage Example

```javascript
// Login
try {
  const result = await signin('player123', 'password123');
  console.log('User:', result.data.user);
  
  // Save user info to localStorage
  localStorage.setItem('user', JSON.stringify(result.data.user));
} catch (error) {
  console.error('Login failed:', error.message);
}

// Save Game
try {
  const gameState = {
    playerState: {
      currentMap: 'forest_level',
      posX: 100,
      posY: 200,
      health: 100
    },
    stats: {
      allTimeDeathCount: 5,
      allTimeCompletedTask: 10
    }
  };
  
  await saveGame('player123', 1, gameState);
  console.log('Game saved!');
} catch (error) {
  console.error('Save failed:', error.message);
}

// Load Game
try {
  const result = await loadGame('player123', 1);
  console.log('Game data:', result.data.saveData);
} catch (error) {
  console.error('Load failed:', error.message);
}
```

---

## 📋 Endpoint Summary

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/auth/signup` | Registrasi user baru |
| `POST` | `/api/auth/signin` | Login user |
| `GET` | `/api/auth/check/username/{username}` | Cek ketersediaan username |
| `GET` | `/api/auth/check/email/{email}` | Cek ketersediaan email |
| `GET` | `/api/auth/user/{username}` | Get user by username |
| `POST` | `/api/save` | Simpan game |
| `GET` | `/api/save/{userId}/{slotId}` | Load game dari slot |
| `GET` | `/api/save/{userId}/slots` | Get semua slot info |
| `DELETE` | `/api/save/{userId}/{slotId}` | Hapus slot tertentu |
| `DELETE` | `/api/save/{userId}` | Hapus semua slot user |
| `GET` | `/api/save/{userId}/{slotId}/exists` | Cek apakah slot ada |
| `GET` | `/api/health` | Health check |
| `GET` | `/api/ping` | Ping endpoint |

