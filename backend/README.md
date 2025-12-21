# Is There Anyone - Game Backend API

Backend REST API untuk game "Is There Anyone" menggunakan Spring Boot dan PostgreSQL (Neon Console).

## 🚀 Quick Start

### 1. Setup Database (Neon Console)

1. Buat akun di [Neon Console](https://neon.tech/)
2. Buat project baru
3. Copy connection string
4. Jalankan query dari `src/main/resources/schema.sql` di Neon SQL Editor

### 2. Konfigurasi

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://ep-xxxxx.ap-southeast-1.aws.neon.tech/neondb?sslmode=require
spring.datasource.username=neondb_owner
spring.datasource.password=YOUR_PASSWORD_HERE
```

### 3. Jalankan Server

```bash
# Windows
.\gradlew.bat bootRun

# Linux/Mac
./gradlew bootRun
```

Server akan berjalan di `http://localhost:8080`

---

## 📡 API Endpoints

### Health Check

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/health` | Health check |
| GET | `/api/ping` | Simple ping |

### Authentication

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/signup` | Register user baru |
| POST | `/api/auth/signin` | Login user |
| GET | `/api/auth/check/username/{username}` | Cek username tersedia |
| GET | `/api/auth/check/email/{email}` | Cek email tersedia |
| GET | `/api/auth/user/{username}` | Get user info |

### Game Save

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/save` | Save game ke slot |
| GET | `/api/save/{userId}/{slotId}` | Load game dari slot |
| GET | `/api/save/{userId}/slots` | Get semua slot info |
| DELETE | `/api/save/{userId}/{slotId}` | Hapus slot tertentu |
| DELETE | `/api/save/{userId}` | Hapus semua slot user |
| GET | `/api/save/{userId}/{slotId}/exists` | Cek slot exists |

---

## 📝 Request/Response Examples

### Authentication

#### Signup (Register)

**Request:**
```http
POST /api/auth/signup
Content-Type: application/json

{
  "username": "player123",
  "email": "player@example.com",
  "password": "password123",
  "displayName": "Player One"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Registrasi berhasil",
  "data": {
    "message": "Registrasi berhasil",
    "user": {
      "id": 1,
      "username": "player123",
      "email": "player@example.com",
      "displayName": "Player One",
      "createdAt": "2024-12-21T13:00:00"
    }
  },
  "timestamp": "2024-12-21T13:00:00"
}
```

#### Signin (Login)

**Request:**
```http
POST /api/auth/signin
Content-Type: application/json

{
  "usernameOrEmail": "player123",
  "password": "password123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login berhasil",
  "data": {
    "message": "Login berhasil",
    "user": {
      "id": 1,
      "username": "player123",
      "email": "player@example.com",
      "displayName": "Player One"
    }
  },
  "timestamp": "2024-12-21T13:00:00"
}
```

#### Check Username

**Request:**
```http
GET /api/auth/check/username/player123
```

**Response:**
```json
{
  "success": true,
  "message": "Username sudah digunakan",
  "data": true,
  "timestamp": "2024-12-21T13:00:00"
}
```

---

### Game Save

#### Save Game

**Request:**
```http
POST /api/save
Content-Type: application/json

{
  "userId": "player123",
  "slotId": 1,
  "saveData": {
    "saveSlot": 1,
    "timestamp": 1734710000,
    "playerState": {
      "x": 1400.5,
      "y": 2300.0,
      "currentMap": "Map_Level_1"
    },
    "ghostState": {
      "x": 1200.0,
      "y": 600.0
    },
    "stats": {
      "allTimeDeathCount": 5,
      "allTimeCompletedTask": 2
    },
    "worldState": {
      "completedTaskTiledIds": [352, 104],
      "itemsOnGround": [
        {"type": "FLOWER", "x": 500.5, "y": 1200.0},
        {"type": "DAGGER", "x": 2400.0, "y": 300.0}
      ]
    },
    "inventory": ["CANDLE", "DOLL"]
  }
}
```

**Response:**
```json
{
  "success": true,
  "message": "Game berhasil disimpan",
  "data": {
    "userId": "player123",
    "slotId": 1,
    "saveData": { ... },
    "lastUpdated": "2024-12-21T10:30:00"
  },
  "timestamp": "2024-12-21T10:30:00"
}
```

### Load Game

**Request:**
```http
GET /api/save/player123/1
```

**Response:**
```json
{
  "success": true,
  "message": "Game berhasil dimuat",
  "data": {
    "userId": "player123",
    "slotId": 1,
    "saveData": { ... },
    "lastUpdated": "2024-12-21T10:30:00"
  },
  "timestamp": "2024-12-21T10:30:00"
}
```

### Get All Slots

**Request:**
```http
GET /api/save/player123/slots
```

**Response:**
```json
{
  "success": true,
  "message": "Slots berhasil dimuat",
  "data": [
    {
      "slotId": 1,
      "isEmpty": false,
      "lastUpdated": "2024-12-21T10:30:00",
      "currentMap": "Map_Level_1",
      "allTimeDeathCount": 5,
      "allTimeCompletedTask": 2
    },
    {
      "slotId": 2,
      "isEmpty": true
    },
    {
      "slotId": 3,
      "isEmpty": true
    }
  ],
  "timestamp": "2024-12-21T10:30:00"
}
```

---

## 🎮 LibGDX Integration

### Contoh kode untuk game client (Java/LibGDX):

```java
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.Json;

public class GameSaveClient {
    
    private static final String BASE_URL = "http://localhost:8080/api/save";
    private final Json json = new Json();
    
    public void saveGame(String userId, int slotId, SaveData saveData) {
        SaveRequest request = new SaveRequest(userId, slotId, saveData);
        
        Net.HttpRequest httpRequest = new HttpRequestBuilder()
            .newRequest()
            .method(Net.HttpMethods.POST)
            .url(BASE_URL)
            .header("Content-Type", "application/json")
            .content(json.toJson(request))
            .build();
        
        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String response = httpResponse.getResultAsString();
                Gdx.app.log("SaveGame", "Success: " + response);
            }
            
            @Override
            public void failed(Throwable t) {
                Gdx.app.error("SaveGame", "Failed: " + t.getMessage());
            }
            
            @Override
            public void cancelled() {
                Gdx.app.log("SaveGame", "Cancelled");
            }
        });
    }
    
    public void loadGame(String userId, int slotId, GameLoadCallback callback) {
        String url = BASE_URL + "/" + userId + "/" + slotId;
        
        Net.HttpRequest httpRequest = new HttpRequestBuilder()
            .newRequest()
            .method(Net.HttpMethods.GET)
            .url(url)
            .build();
        
        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String response = httpResponse.getResultAsString();
                // Parse response and callback
                callback.onLoaded(parseResponse(response));
            }
            
            @Override
            public void failed(Throwable t) {
                callback.onError(t.getMessage());
            }
            
            @Override
            public void cancelled() {
                callback.onError("Request cancelled");
            }
        });
    }
}
```

---

## 📁 Project Structure

```
backend/
├── src/main/java/com/isthereanyone/backend/
│   ├── BackendApplication.java          # Main application
│   ├── config/
│   │   └── CorsConfig.java              # CORS configuration
│   ├── controller/
│   │   ├── GameSaveController.java      # REST endpoints
│   │   └── HealthController.java        # Health check
│   ├── dto/
│   │   ├── ApiResponse.java             # Generic response wrapper
│   │   ├── GameSaveResponse.java        # Save response DTO
│   │   ├── SaveGameRequest.java         # Save request DTO
│   │   └── SlotInfo.java                # Slot info DTO
│   ├── entity/
│   │   ├── GameSave.java                # JPA Entity
│   │   └── GameSaveId.java              # Composite Key
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java  # Exception handler
│   │   ├── InvalidOperationException.java
│   │   └── ResourceNotFoundException.java
│   ├── repository/
│   │   └── GameSaveRepository.java      # JPA Repository
│   └── service/
│       └── GameSaveService.java         # Business logic
├── src/main/resources/
│   ├── application.properties           # Configuration
│   └── schema.sql                       # Database schema
└── build.gradle                         # Dependencies
```

---

## 🔧 Technologies

- **Java 17**
- **Spring Boot 3.2.1**
- **Spring Data JPA**
- **PostgreSQL** (Neon Console)
- **Hypersistence Utils** (JSONB support)

---

## 📄 License

MIT License

