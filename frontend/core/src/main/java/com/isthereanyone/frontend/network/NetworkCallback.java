package com.isthereanyone.frontend.network;

/**
 * Callback interface untuk async network operations
 */
public interface NetworkCallback<T> {
    void onSuccess(T result);
    void onFailure(String error);
}

