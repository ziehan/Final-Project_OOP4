package com.isthereanyone.frontend.network;

/**
 * Callback interface untuk menangani response dari network request
 * @param <T> tipe data response
 */
public interface NetworkCallback<T> {
    /**
     * Dipanggil ketika request berhasil
     * @param result hasil response dari server
     */
    void onSuccess(T result);

    /**
     * Dipanggil ketika request gagal
     * @param error pesan error
     */
    void onFailure(String error);
}

