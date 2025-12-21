/**
 * API Service untuk Frontend - Is There Anyone Game
 * File ini berisi fungsi-fungsi untuk berkomunikasi dengan backend
 *
 * Cara pakai:
 * 1. Copy file ini ke project frontend
 * 2. Sesuaikan API_BASE_URL dengan URL backend kamu
 * 3. Import dan gunakan fungsi-fungsi yang tersedia
 */

import type {
  ApiResponse,
  SignupRequest,
  SigninRequest,
  AuthResponse,
  UserResponse,
  SaveGameRequest,
  GameSaveResponse,
  GameSaveData,
  SlotInfo,
  HealthCheckResponse,
} from './frontend-types';

// ==================== CONFIGURATION ====================

/**
 * Base URL backend - sesuaikan dengan environment kamu
 */
const API_BASE_URL = 'http://localhost:9090/api';

// ==================== HELPER FUNCTIONS ====================

/**
 * Generic API call function
 */
async function apiCall<T>(
  endpoint: string,
  options: RequestInit = {}
): Promise<ApiResponse<T>> {
  const response = await fetch(`${API_BASE_URL}${endpoint}`, {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
    ...options,
  });

  const data: ApiResponse<T> = await response.json();

  if (!response.ok || !data.success) {
    throw new Error(data.message || 'API request failed');
  }

  return data;
}

// ==================== AUTH API ====================

/**
 * Registrasi user baru
 */
export async function signup(request: SignupRequest): Promise<ApiResponse<AuthResponse>> {
  return apiCall<AuthResponse>('/auth/signup', {
    method: 'POST',
    body: JSON.stringify(request),
  });
}

/**
 * Login user
 */
export async function signin(request: SigninRequest): Promise<ApiResponse<AuthResponse>> {
  return apiCall<AuthResponse>('/auth/signin', {
    method: 'POST',
    body: JSON.stringify(request),
  });
}

/**
 * Cek apakah username sudah digunakan
 * @returns true jika sudah digunakan, false jika tersedia
 */
export async function checkUsername(username: string): Promise<ApiResponse<boolean>> {
  return apiCall<boolean>(`/auth/check/username/${username}`);
}

/**
 * Cek apakah email sudah terdaftar
 * @returns true jika sudah terdaftar, false jika tersedia
 */
export async function checkEmail(email: string): Promise<ApiResponse<boolean>> {
  return apiCall<boolean>(`/auth/check/email/${encodeURIComponent(email)}`);
}

/**
 * Get user by username
 */
export async function getUser(username: string): Promise<ApiResponse<UserResponse>> {
  return apiCall<UserResponse>(`/auth/user/${username}`);
}

// ==================== GAME SAVE API ====================

/**
 * Simpan game ke slot tertentu
 */
export async function saveGame(
  userId: string,
  slotId: number,
  saveData: GameSaveData
): Promise<ApiResponse<GameSaveResponse>> {
  const request: SaveGameRequest = { userId, slotId, saveData };
  return apiCall<GameSaveResponse>('/save', {
    method: 'POST',
    body: JSON.stringify(request),
  });
}

/**
 * Load game dari slot tertentu
 */
export async function loadGame(
  userId: string,
  slotId: number
): Promise<ApiResponse<GameSaveResponse>> {
  return apiCall<GameSaveResponse>(`/save/${userId}/${slotId}`);
}

/**
 * Get semua slot info (untuk tampilan menu load game)
 */
export async function getAllSlots(userId: string): Promise<ApiResponse<SlotInfo[]>> {
  return apiCall<SlotInfo[]>(`/save/${userId}/slots`);
}

/**
 * Hapus slot tertentu
 */
export async function deleteSlot(
  userId: string,
  slotId: number
): Promise<ApiResponse<null>> {
  return apiCall<null>(`/save/${userId}/${slotId}`, {
    method: 'DELETE',
  });
}

/**
 * Hapus semua slot user
 */
export async function deleteAllSlots(userId: string): Promise<ApiResponse<null>> {
  return apiCall<null>(`/save/${userId}`, {
    method: 'DELETE',
  });
}

/**
 * Cek apakah slot sudah ada isinya
 */
export async function checkSlotExists(
  userId: string,
  slotId: number
): Promise<ApiResponse<boolean>> {
  return apiCall<boolean>(`/save/${userId}/${slotId}/exists`);
}

// ==================== HEALTH API ====================

/**
 * Health check - untuk memastikan server berjalan
 */
export async function healthCheck(): Promise<ApiResponse<HealthCheckResponse>> {
  return apiCall<HealthCheckResponse>('/health');
}

/**
 * Simple ping
 */
export async function ping(): Promise<string> {
  const response = await fetch(`${API_BASE_URL}/ping`);
  return response.text();
}

// ==================== USAGE EXAMPLES ====================

/*
// Contoh penggunaan:

// 1. Registrasi
try {
  const result = await signup({
    username: 'player123',
    email: 'player@email.com',
    password: 'password123',
    displayName: 'Player One'
  });
  console.log('Registrasi berhasil:', result.data.user);
} catch (error) {
  console.error('Registrasi gagal:', error.message);
}

// 2. Login
try {
  const result = await signin({
    usernameOrEmail: 'player123',
    password: 'password123'
  });
  console.log('Login berhasil:', result.data.user);

  // Simpan user ke localStorage
  localStorage.setItem('user', JSON.stringify(result.data.user));
} catch (error) {
  console.error('Login gagal:', error.message);
}

// 3. Save Game
try {
  const gameState: GameSaveData = {
    playerState: {
      currentMap: 'forest_level',
      posX: 100,
      posY: 200,
      health: 100
    },
    stats: {
      allTimeDeathCount: 5,
      allTimeCompletedTask: 10
    },
    inventory: ['sword', 'potion', 'key']
  };

  await saveGame('player123', 1, gameState);
  console.log('Game tersimpan!');
} catch (error) {
  console.error('Gagal menyimpan:', error.message);
}

// 4. Load Game
try {
  const result = await loadGame('player123', 1);
  console.log('Game data:', result.data.saveData);

  // Apply ke game state
  const { playerState, stats, inventory } = result.data.saveData;
  // ... apply to game
} catch (error) {
  console.error('Gagal memuat:', error.message);
}

// 5. Get All Slots (untuk menu load game)
try {
  const result = await getAllSlots('player123');
  result.data.forEach(slot => {
    if (!slot.isEmpty) {
      console.log(`Slot ${slot.slotId}: ${slot.currentMap} - Deaths: ${slot.allTimeDeathCount}`);
    } else {
      console.log(`Slot ${slot.slotId}: Empty`);
    }
  });
} catch (error) {
  console.error('Gagal memuat slots:', error.message);
}
*/

export default {
  // Auth
  signup,
  signin,
  checkUsername,
  checkEmail,
  getUser,
  // Game Save
  saveGame,
  loadGame,
  getAllSlots,
  deleteSlot,
  deleteAllSlots,
  checkSlotExists,
  // Health
  healthCheck,
  ping,
};

