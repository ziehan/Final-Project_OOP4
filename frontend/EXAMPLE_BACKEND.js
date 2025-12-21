/**
 * Example Backend Implementation untuk Neon Console
 * Gunakan sebagai referensi untuk membuat backend Anda
 */

// ==================== INSTALLATION ====================
// npm init -y
// npm install express cors dotenv pg bcryptjs jsonwebtoken
// npm install --save-dev nodemon

// ==================== .env FILE ====================
/*
NODE_ENV=development
DATABASE_URL=postgresql://user:password@db.neon.tech/dbname
JWT_SECRET=your-super-secret-key
JWT_EXPIRY=24h
PORT=3000
CORS_ORIGIN=http://localhost:8080
*/

// ==================== server.js ====================

const express = require('express');
const cors = require('cors');
const dotenv = require('dotenv');
const { Pool } = require('pg');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');

dotenv.config();

const app = express();

// Middleware
app.use(cors({
    origin: process.env.CORS_ORIGIN || '*',
    credentials: true
}));
app.use(express.json());

// Database Connection
const pool = new Pool({
    connectionString: process.env.DATABASE_URL,
    ssl: { rejectUnauthorized: false } // Required for Neon
});

// Verify database connection
pool.on('error', (err) => {
    console.error('Unexpected error on idle client', err);
});

// ==================== MIDDLEWARE ====================

// Authentication middleware
const authenticateToken = (req, res, next) => {
    const authHeader = req.headers['authorization'];
    const token = authHeader && authHeader.split(' ')[1]; // Bearer token

    if (!token) {
        return res.status(401).json({ success: false, message: 'No token provided' });
    }

    jwt.verify(token, process.env.JWT_SECRET, (err, user) => {
        if (err) {
            return res.status(403).json({ success: false, message: 'Invalid token' });
        }
        req.user = user;
        next();
    });
};

// ==================== HELPER FUNCTIONS ====================

// Generate JWT Token
const generateToken = (username, userId) => {
    return jwt.sign(
        { username, userId },
        process.env.JWT_SECRET,
        { expiresIn: process.env.JWT_EXPIRY || '24h' }
    );
};

// Hash password
const hashPassword = async (password) => {
    return await bcrypt.hash(password, 10);
};

// Compare password
const comparePassword = async (password, hash) => {
    return await bcrypt.compare(password, hash);
};

// ==================== ROUTES ====================

// Health Check
app.get('/api/health', (req, res) => {
    res.json({ status: 'Backend is running' });
});

// ==================== AUTHENTICATION ROUTES ====================

// POST /api/auth/signup
app.post('/api/auth/signup', async (req, res) => {
    try {
        const { username, password, email } = req.body;

        // Validation
        if (!username || !password || !email) {
            return res.status(400).json({
                success: false,
                message: 'Username, password, and email are required'
            });
        }

        if (password.length < 6) {
            return res.status(400).json({
                success: false,
                message: 'Password must be at least 6 characters'
            });
        }

        // Check if user exists
        const checkUser = await pool.query(
            'SELECT * FROM users WHERE username = $1 OR email = $2',
            [username, email]
        );

        if (checkUser.rows.length > 0) {
            return res.status(409).json({
                success: false,
                message: 'Username or email already exists'
            });
        }

        // Hash password
        const passwordHash = await hashPassword(password);

        // Insert user
        const result = await pool.query(
            'INSERT INTO users (username, email, password_hash) VALUES ($1, $2, $3) RETURNING id, username, email, created_at',
            [username, email, passwordHash]
        );

        const user = result.rows[0];

        // Create save slots for new user
        for (let i = 1; i <= 3; i++) {
            await pool.query(
                'INSERT INTO save_slots (user_id, slot_number) VALUES ($1, $2)',
                [user.id, i]
            );
        }

        res.status(201).json({
            success: true,
            message: 'User registered successfully',
            user: {
                id: user.id,
                username: user.username,
                email: user.email,
                createdAt: user.created_at
            }
        });
    } catch (error) {
        console.error('Signup error:', error);
        res.status(500).json({
            success: false,
            message: 'Server error during signup'
        });
    }
});

// POST /api/auth/login
app.post('/api/auth/login', async (req, res) => {
    try {
        const { username, password } = req.body;

        // Validation
        if (!username || !password) {
            return res.status(400).json({
                success: false,
                message: 'Username and password are required'
            });
        }

        // Find user
        const userResult = await pool.query(
            'SELECT * FROM users WHERE username = $1',
            [username]
        );

        if (userResult.rows.length === 0) {
            return res.status(401).json({
                success: false,
                message: 'Invalid username or password'
            });
        }

        const user = userResult.rows[0];

        // Compare password
        const passwordMatch = await comparePassword(password, user.password_hash);

        if (!passwordMatch) {
            return res.status(401).json({
                success: false,
                message: 'Invalid username or password'
            });
        }

        // Generate token
        const token = generateToken(user.username, user.id);

        res.json({
            success: true,
            message: 'Login successful',
            token: token,
            user: {
                id: user.id,
                username: user.username,
                email: user.email
            }
        });
    } catch (error) {
        console.error('Login error:', error);
        res.status(500).json({
            success: false,
            message: 'Server error during login'
        });
    }
});

// POST /api/auth/logout
app.post('/api/auth/logout', authenticateToken, (req, res) => {
    // Token is invalidated on client side
    res.json({
        success: true,
        message: 'Logout successful'
    });
});

// ==================== SAVE SLOTS ROUTES ====================

// GET /api/saves/:username
app.get('/api/saves/:username', authenticateToken, async (req, res) => {
    try {
        const { username } = req.params;

        // Verify user is requesting their own saves
        if (req.user.username !== username) {
            return res.status(403).json({
                success: false,
                message: 'Unauthorized'
            });
        }

        // Get user
        const userResult = await pool.query(
            'SELECT id FROM users WHERE username = $1',
            [username]
        );

        if (userResult.rows.length === 0) {
            return res.status(404).json({
                success: false,
                message: 'User not found'
            });
        }

        const userId = userResult.rows[0].id;

        // Get save slots
        const slotsResult = await pool.query(
            'SELECT * FROM save_slots WHERE user_id = $1 ORDER BY slot_number',
            [userId]
        );

        const slots = slotsResult.rows.map(slot => ({
            slotNumber: slot.slot_number,
            hasData: slot.has_data,
            data: slot.has_data ? {
                playerLevel: slot.player_level,
                playerHP: slot.player_hp,
                playerX: slot.player_x,
                playerY: slot.player_y,
                characterName: slot.character_name,
                lastSavedTime: slot.last_saved_at ? new Date(slot.last_saved_at).getTime() : 0
            } : null
        }));

        res.json({
            success: true,
            slots: slots
        });
    } catch (error) {
        console.error('Load save slots error:', error);
        res.status(500).json({
            success: false,
            message: 'Server error while loading save slots'
        });
    }
});

// POST /api/saves/:username/:slotNumber
app.post('/api/saves/:username/:slotNumber', authenticateToken, async (req, res) => {
    try {
        const { username, slotNumber } = req.params;
        const { playerLevel, playerHP, playerX, playerY, characterName } = req.body;

        // Verify user is saving their own data
        if (req.user.username !== username) {
            return res.status(403).json({
                success: false,
                message: 'Unauthorized'
            });
        }

        // Validation
        const slot = parseInt(slotNumber);
        if (slot < 1 || slot > 3) {
            return res.status(400).json({
                success: false,
                message: 'Invalid slot number'
            });
        }

        // Get user
        const userResult = await pool.query(
            'SELECT id FROM users WHERE username = $1',
            [username]
        );

        if (userResult.rows.length === 0) {
            return res.status(404).json({
                success: false,
                message: 'User not found'
            });
        }

        const userId = userResult.rows[0].id;

        // Update save slot
        const updateResult = await pool.query(
            `UPDATE save_slots
             SET has_data = true,
                 player_level = $1,
                 player_hp = $2,
                 player_x = $3,
                 player_y = $4,
                 character_name = $5,
                 last_saved_at = NOW(),
                 updated_at = NOW()
             WHERE user_id = $6 AND slot_number = $7
             RETURNING *`,
            [playerLevel, playerHP, playerX, playerY, characterName, userId, slot]
        );

        if (updateResult.rows.length === 0) {
            return res.status(404).json({
                success: false,
                message: 'Save slot not found'
            });
        }

        res.json({
            success: true,
            message: `Game saved successfully to slot ${slot}`
        });
    } catch (error) {
        console.error('Save game error:', error);
        res.status(500).json({
            success: false,
            message: 'Server error while saving game'
        });
    }
});

// DELETE /api/saves/:username/:slotNumber
app.delete('/api/saves/:username/:slotNumber', authenticateToken, async (req, res) => {
    try {
        const { username, slotNumber } = req.params;

        // Verify user is deleting their own data
        if (req.user.username !== username) {
            return res.status(403).json({
                success: false,
                message: 'Unauthorized'
            });
        }

        // Validation
        const slot = parseInt(slotNumber);
        if (slot < 1 || slot > 3) {
            return res.status(400).json({
                success: false,
                message: 'Invalid slot number'
            });
        }

        // Get user
        const userResult = await pool.query(
            'SELECT id FROM users WHERE username = $1',
            [username]
        );

        if (userResult.rows.length === 0) {
            return res.status(404).json({
                success: false,
                message: 'User not found'
            });
        }

        const userId = userResult.rows[0].id;

        // Delete save slot data
        const deleteResult = await pool.query(
            `UPDATE save_slots
             SET has_data = false,
                 player_level = 1,
                 player_hp = 100,
                 player_x = 0,
                 player_y = 0,
                 character_name = NULL,
                 last_saved_at = NULL,
                 updated_at = NOW()
             WHERE user_id = $1 AND slot_number = $2`,
            [userId, slot]
        );

        res.json({
            success: true,
            message: `Save slot ${slot} deleted successfully`
        });
    } catch (error) {
        console.error('Delete save slot error:', error);
        res.status(500).json({
            success: false,
            message: 'Server error while deleting save slot'
        });
    }
});

// ==================== ERROR HANDLING ====================

// 404 Not Found
app.use((req, res) => {
    res.status(404).json({
        success: false,
        message: 'Endpoint not found'
    });
});

// Global Error Handler
app.use((err, req, res, next) => {
    console.error('Global error:', err);
    res.status(500).json({
        success: false,
        message: 'Internal server error'
    });
});

// ==================== START SERVER ====================

const PORT = process.env.PORT || 3000;

app.listen(PORT, () => {
    console.log(`Server is running on http://localhost:${PORT}`);
});

// ==================== package.json ====================
/*
{
  "name": "game-backend",
  "version": "1.0.0",
  "description": "Backend for Is There Anyone game",
  "main": "server.js",
  "scripts": {
    "start": "node server.js",
    "dev": "nodemon server.js"
  },
  "dependencies": {
    "express": "^4.18.2",
    "cors": "^2.8.5",
    "dotenv": "^16.0.3",
    "pg": "^8.11.0",
    "bcryptjs": "^2.4.3",
    "jsonwebtoken": "^9.1.2"
  },
  "devDependencies": {
    "nodemon": "^3.0.1"
  }
}
*/

