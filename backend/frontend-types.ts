/**
 * API Types untuk Frontend - Is There Anyone Game
 * File ini berisi semua TypeScript interface yang sesuai dengan backend API
 *
 * Cara pakai: Copy file ini ke project frontend kamu
 */

// ==================== BASE TYPES ====================

/**
 * Standard API Response dari semua endpoint
 */
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

// ==================== AUTH TYPES ====================

/**
 * Request untuk signup/registrasi
 */
export interface SignupRequest {
  username: string;      // wajib, 3-50 karakter
  email: string;         // wajib, format email valid
  password: string;      // wajib, minimal 6 karakter
  displayName?: string;  // opsional
}

/**
 * Request untuk signin/login
 */
export interface SigninRequest {
  usernameOrEmail: string;  // bisa username atau email
  password: string;
}

/**
 * Response data user
 */
export interface UserResponse {
  id: number;
  username: string;
  email: string;
  displayName: string | null;
  createdAt: string;
}

/**
 * Response setelah signup/signin
 */
export interface AuthResponse {
  message: string;
  user: UserResponse;
  token: string | null;
}

// ==================== GAME SAVE TYPES ====================

/**
 * Request untuk menyimpan game
 */
export interface SaveGameRequest {
  userId: string;
  slotId: number;  // 1-3
  saveData: GameSaveData;
}

/**
 * Struktur data save game (sesuaikan dengan kebutuhan game kamu)
 */
export interface GameSaveData {
  playerState?: {
    currentMap?: string;
    posX?: number;
    posY?: number;
    health?: number;
    [key: string]: any;  // field tambahan
  };
  stats?: {
    allTimeDeathCount?: number;
    allTimeCompletedTask?: number;
    [key: string]: any;  // field tambahan
  };
  inventory?: string[];
  [key: string]: any;  // field tambahan lainnya
}

/**
 * Response ketika load game
 */
export interface GameSaveResponse {
  userId: string;
  slotId: number;
  saveData: GameSaveData;
  lastUpdated: string;
}

/**
 * Info slot untuk tampilan di menu load game
 */
export interface SlotInfo {
  slotId: number;
  isEmpty: boolean;
  lastUpdated: string | null;
  currentMap: string | null;
  allTimeDeathCount: number | null;
  allTimeCompletedTask: number | null;
}

// ==================== HEALTH CHECK TYPES ====================

export interface HealthCheckResponse {
  status: string;
  service: string;
  version: string;
  timestamp: string;
}

// ==================== API ENDPOINTS ====================

/**
 * Daftar semua endpoint yang tersedia
 */
export const API_ENDPOINTS = {
  // Auth
  SIGNUP: '/api/auth/signup',
  SIGNIN: '/api/auth/signin',
  CHECK_USERNAME: (username: string) => `/api/auth/check/username/${username}`,
  CHECK_EMAIL: (email: string) => `/api/auth/check/email/${encodeURIComponent(email)}`,
  GET_USER: (username: string) => `/api/auth/user/${username}`,

  // Game Save
  SAVE_GAME: '/api/save',
  LOAD_GAME: (userId: string, slotId: number) => `/api/save/${userId}/${slotId}`,
  GET_ALL_SLOTS: (userId: string) => `/api/save/${userId}/slots`,
  DELETE_SLOT: (userId: string, slotId: number) => `/api/save/${userId}/${slotId}`,
  DELETE_ALL_SLOTS: (userId: string) => `/api/save/${userId}`,
  CHECK_SLOT_EXISTS: (userId: string, slotId: number) => `/api/save/${userId}/${slotId}/exists`,

  // Health
  HEALTH: '/api/health',
  PING: '/api/ping',
} as const;

