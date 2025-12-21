# 📖 API Documentation - Is There Anyone Game Backend

## 🌐 Base Configuration

| Item | Value |
|------|-------|
| **Base URL** | `http://localhost:9090/api` |
| **Content-Type** | `application/json` |
| **Server Port** | `9090` |

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
  "displayName": "Player One"
}
```

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
    }
  }
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

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Login berhasil",
  "data": {
    "user": {
      "id": 1,
      "username": "player123"
    }
  }
}
```

---

## 💾 Game Save Endpoints

### 1. Save Game - FULL STRUCTURE

**Endpoint:** `POST /api/save`

**Request Body:**
```json
{
  "userId": "1",
  "slotId": 1,
  "saveData": {
    "playerHp": 2,
    "maxHp": 3,
    "playerX": 245.5,
    "playerY": 180.3,
    "playerDirection": "right",
    "isHiding": false,
    
    "currentLevel": 2,
    "currentRoom": "hallway_b",
    "currentMap": "level2.tmx",
    "completedLevels": [1],
    "unlockedRooms": ["lobby", "hallway_a", "hallway_b"],
    
    "ghosts": [
      {
        "x": 300.0,
        "y": 200.0,
        "state": "patrol",
        "currentPatrolPoint": "point_3",
        "alertLevel": 0
      }
    ],
    
    "tasks": [
      {
        "taskId": "task_find_key",
        "taskName": "Find the Key",
        "status": "completed",
        "progress": 1,
        "maxProgress": 1,
        "location": "storage_room",
        "isRequired": true
      },
      {
        "taskId": "task_unlock_door",
        "taskName": "Unlock the Door",
        "status": "in_progress",
        "progress": 0,
        "maxProgress": 1,
        "location": "hallway_b",
        "isRequired": true
      }
    ],
    "completedTaskIds": ["task_find_key"],
    "currentActiveTaskId": "task_unlock_door",
    "totalTasksCompleted": 1,
    "totalTasksRequired": 5,
    
    "inventoryItems": [
      {
        "itemId": "key_gold",
        "itemName": "Golden Key",
        "itemType": "key",
        "quantity": 1,
        "isEquipped": true,
        "slotPosition": "hand"
      },
      {
        "itemId": "flashlight",
        "itemName": "Flashlight",
        "itemType": "tool",
        "quantity": 1,
        "isEquipped": false,
        "slotPosition": "bag_1"
      }
    ],
    "currentlyHeldItemId": "key_gold",
    "inventoryCapacity": 5,
    
    "coins": 150,
    "playTime": 1800000,
    "deathCount": 3,
    "saveCount": 5,
    
    "useCustomSpawn": true,
    "customSpawnX": 245.5,
    "customSpawnY": 180.3,
    "spawnRoom": "hallway_b",
    
    "saveTimestamp": 1703145000000
  }
}
```

**IMPORTANT FIELDS:**

| Field | Type | Description |
|-------|------|-------------|
| `useCustomSpawn` | boolean | **TRUE** = spawn di posisi save, **FALSE** = spawn di Tiled point |
| `ghosts` | array | Posisi dan state semua ghost |
| `tasks` | array | Status semua task |
| `completedTaskIds` | array | ID task yang sudah selesai |
| `inventoryItems` | array | Barang di inventory |
| `currentlyHeldItemId` | string | Item yang sedang dipegang |

---

### 2. Load Game

**Endpoint:** `GET /api/save/{userId}/{slotId}`

**Response:**
```json
{
  "success": true,
  "message": "Game berhasil dimuat",
  "data": {
    "userId": "1",
    "slotId": 1,
    "saveData": {
      "playerHp": 2,
      "ghosts": [...],
      "tasks": [...],
      "completedTaskIds": ["task_find_key"],
      "inventoryItems": [...],
      "useCustomSpawn": true
    },
    "lastUpdated": "2025-12-21T10:30:00"
  }
}
```

---

### 3. Get All Slots

**Endpoint:** `GET /api/save/{userId}/slots`

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "slotId": 1,
      "isEmpty": false,
      "lastUpdated": "2025-12-21T10:30:00",
      "currentMap": "level2.tmx",
      "playerHp": 2,
      "maxHp": 3,
      "tasksCompleted": 1,
      "totalTasks": 5,
      "playTime": 1800000,
      "itemCount": 2
    },
    {
      "slotId": 2,
      "isEmpty": true
    },
    {
      "slotId": 3,
      "isEmpty": true
    }
  ]
}
```

---

### 4. Delete Slot

**Endpoint:** `DELETE /api/save/{userId}/{slotId}`

---

### 5. Check Slot Exists

**Endpoint:** `GET /api/save/{userId}/{slotId}/exists`

---

## 🏥 Health Check

**Endpoint:** `GET /api/health`

**Endpoint:** `GET /api/ping` → Returns `pong`

---

## 📋 Endpoint Summary

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/auth/signup` | Registrasi |
| `POST` | `/api/auth/signin` | Login |
| `GET` | `/api/auth/check/username/{username}` | Cek username |
| `GET` | `/api/auth/check/email/{email}` | Cek email |
| `POST` | `/api/save` | Simpan game |
| `GET` | `/api/save/{userId}/{slotId}` | Load game |
| `GET` | `/api/save/{userId}/slots` | Get semua slot |
| `DELETE` | `/api/save/{userId}/{slotId}` | Hapus slot |
| `GET` | `/api/health` | Health check |

