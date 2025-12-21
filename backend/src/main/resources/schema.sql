-- =============================================
-- Is There Anyone - Game Database Schema
-- PostgreSQL (Neon Console)
-- =============================================

-- Drop tables jika sudah ada (untuk fresh start)
DROP TABLE IF EXISTS game_saves;
DROP TABLE IF EXISTS users;

-- =============================================
-- Table: users
-- =============================================
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    display_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index untuk query by username dan email
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);

-- =============================================
-- Table: game_saves
-- =============================================
CREATE TABLE game_saves (
    user_id VARCHAR(50) NOT NULL,
    slot_id INT NOT NULL CHECK (slot_id BETWEEN 1 AND 3),
    save_data JSONB NOT NULL,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Composite Primary Key
    PRIMARY KEY (user_id, slot_id)
);

-- Index untuk query by user_id
CREATE INDEX idx_game_saves_user_id ON game_saves(user_id);

-- Index untuk query JSONB (optional, untuk search dalam save_data)
CREATE INDEX idx_game_saves_player_map ON game_saves USING GIN ((save_data -> 'playerState'));

-- =============================================
-- Contoh Insert Data (untuk testing)
-- =============================================

-- Insert sample save data
INSERT INTO game_saves (user_id, slot_id, save_data) VALUES
('player123', 1, '{
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
}'::jsonb);

-- =============================================
-- Useful Queries
-- =============================================

-- Get all saves for a user
-- SELECT * FROM game_saves WHERE user_id = 'player123';

-- Get specific slot
-- SELECT * FROM game_saves WHERE user_id = 'player123' AND slot_id = 1;

-- Get current map from save_data
-- SELECT user_id, slot_id, save_data -> 'playerState' ->> 'currentMap' as current_map
-- FROM game_saves;

-- Get death count
-- SELECT user_id, slot_id, save_data -> 'stats' ->> 'allTimeDeathCount' as deaths
-- FROM game_saves;

