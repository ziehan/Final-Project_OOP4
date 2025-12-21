# Is there Anyone?

> An indie horror game made by Group 4

## 📁 Project Structure

```
Final-Project_OOP4/
├── backend/          # Spring Boot REST API
├── frontend/         # LibGDX Game Client
└── README.md
```

## 🚀 Quick Start

### 1. Start Backend Server

```bash
cd backend
.\gradlew.bat bootRun   # Windows
./gradlew bootRun       # Linux/Mac
```

Server akan berjalan di `http://localhost:9090`

### 2. Start Frontend Game

```bash
cd frontend
.\gradlew.bat lwjgl3:run   # Windows
./gradlew lwjgl3:run       # Linux/Mac
```

## 🔗 Frontend-Backend Integration

### Network Architecture

```
┌─────────────────┐     HTTP/REST     ┌─────────────────┐
│   LibGDX Game   │ ◄───────────────► │  Spring Boot    │
│   (Frontend)    │                   │   (Backend)     │
└─────────────────┘                   └─────────────────┘
        │                                     │
        │                                     ▼
        │                            ┌─────────────────┐
        │                            │   PostgreSQL    │
        │                            │  (Neon Cloud)   │
        └────────────────────────────┴─────────────────┘
```

### Key Components

#### Frontend (LibGDX)
- `NetworkManager` - Manages user session and provides simplified API
- `ApiService` - Handles HTTP requests to backend
- `GameStateManager` - Manages game save/load operations
- Screen classes: `LoginScreen`, `RegisterScreen`, `SaveLoadScreen`

#### Backend (Spring Boot)
- `/api/auth/*` - Authentication endpoints (login, register)
- `/api/save/*` - Game save/load endpoints
- `/api/health` - Health check

### API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/signup` | Register new user |
| POST | `/api/auth/signin` | Login user |
| POST | `/api/save` | Save game to slot |
| GET | `/api/save/{userId}/{slotId}` | Load game from slot |
| GET | `/api/save/{userId}/slots` | Get all slot info |
| DELETE | `/api/save/{userId}/{slotId}` | Delete save slot |

### Configuration

Edit `frontend/core/src/main/java/.../config/GameConfig.java`:

```java
// Change this for production server
public static final String API_BASE_URL = "http://localhost:9090/api";
```

## 🎮 Game Features

- **Authentication**: Login/Register with cloud save support
- **Save System**: 3 save slots per user
- **Horror Gameplay**: Survive against ghosts, complete tasks
- **Cloud Sync**: Game progress saved to cloud database

## 🛠️ Tech Stack

- **Frontend**: Java, LibGDX, Gson
- **Backend**: Java, Spring Boot, PostgreSQL
- **Database**: Neon PostgreSQL (Cloud)

## 👥 Team

Group 4 - OOP Final Project
