package com.isthereanyone.frontend.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

import java.util.Map;

/**
 * HTTP Client untuk komunikasi dengan backend
 */
public class HttpClient {
    private static final String BASE_URL = "http://localhost:9090";
    private final Json json;

    public HttpClient() {
        json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
    }

    public void get(String endpoint, NetworkCallback<String> callback) {
        Net.HttpRequest request = new HttpRequestBuilder()
            .newRequest()
            .method(Net.HttpMethods.GET)
            .url(BASE_URL + endpoint)
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .build();

        sendRequest(request, callback);
    }

    public void post(String endpoint, Object body, NetworkCallback<String> callback) {
        String jsonBody = json.toJson(body);

        Net.HttpRequest request = new HttpRequestBuilder()
            .newRequest()
            .method(Net.HttpMethods.POST)
            .url(BASE_URL + endpoint)
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .content(jsonBody)
            .build();

        sendRequest(request, callback);
    }

    public void put(String endpoint, Object body, NetworkCallback<String> callback) {
        String jsonBody = json.toJson(body);

        Net.HttpRequest request = new HttpRequestBuilder()
            .newRequest()
            .method(Net.HttpMethods.PUT)
            .url(BASE_URL + endpoint)
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .content(jsonBody)
            .build();

        sendRequest(request, callback);
    }

    public void delete(String endpoint, NetworkCallback<String> callback) {
        Net.HttpRequest request = new HttpRequestBuilder()
            .newRequest()
            .method(Net.HttpMethods.DELETE)
            .url(BASE_URL + endpoint)
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .build();

        sendRequest(request, callback);
    }

    private void sendRequest(Net.HttpRequest request, NetworkCallback<String> callback) {
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                final String response = httpResponse.getResultAsString();
                final int statusCode = httpResponse.getStatus().getStatusCode();

                Gdx.app.postRunnable(() -> {
                    if (statusCode >= 200 && statusCode < 300) {
                        callback.onSuccess(response);
                    } else {
                        callback.onFailure("HTTP Error: " + statusCode + " - " + response);
                    }
                });
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() -> {
                    callback.onFailure("Connection failed: " + t.getMessage());
                });
            }

            @Override
            public void cancelled() {
                Gdx.app.postRunnable(() -> {
                    callback.onFailure("Request cancelled");
                });
            }
        });
    }

    public <T> T fromJson(String jsonString, Class<T> type) {
        return json.fromJson(type, jsonString);
    }

    public String toJson(Object object) {
        return json.toJson(object);
    }
}

