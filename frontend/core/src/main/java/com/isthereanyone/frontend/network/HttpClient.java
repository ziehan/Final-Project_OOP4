package com.isthereanyone.frontend.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

import java.util.List;
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

    /**
     * Custom JSON serialization that handles nested Maps properly
     */
    private String toJsonString(Object obj) {
        if (obj == null) return "null";

        if (obj instanceof Map) {
            StringBuilder sb = new StringBuilder("{");
            Map<?, ?> map = (Map<?, ?>) obj;
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first) sb.append(",");
                first = false;
                sb.append("\"").append(entry.getKey()).append("\":");
                sb.append(toJsonString(entry.getValue()));
            }
            sb.append("}");
            return sb.toString();
        }

        if (obj instanceof List) {
            StringBuilder sb = new StringBuilder("[");
            List<?> list = (List<?>) obj;
            boolean first = true;
            for (Object item : list) {
                if (!first) sb.append(",");
                first = false;
                sb.append(toJsonString(item));
            }
            sb.append("]");
            return sb.toString();
        }

        if (obj instanceof String) {
            // Escape special characters
            String str = (String) obj;
            str = str.replace("\\", "\\\\")
                     .replace("\"", "\\\"")
                     .replace("\n", "\\n")
                     .replace("\r", "\\r")
                     .replace("\t", "\\t");
            return "\"" + str + "\"";
        }

        if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        }

        // For complex objects, use libGDX Json
        return json.toJson(obj);
    }

    public void get(String endpoint, NetworkCallback<String> callback) {
        Gdx.app.log("HTTP", "GET " + endpoint);

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
        String jsonBody;

        // Check if body has saveData (Map) that needs custom serialization
        if (body instanceof com.isthereanyone.frontend.network.dto.SaveGameRequest) {
            com.isthereanyone.frontend.network.dto.SaveGameRequest req =
                (com.isthereanyone.frontend.network.dto.SaveGameRequest) body;
            StringBuilder sb = new StringBuilder("{");
            sb.append("\"userId\":").append(toJsonString(req.getUserId())).append(",");
            sb.append("\"slotId\":").append(req.getSlotId()).append(",");
            sb.append("\"saveData\":").append(toJsonString(req.getSaveData()));
            sb.append("}");
            jsonBody = sb.toString();
        } else {
            jsonBody = json.toJson(body);
        }

        // Debug: Log the JSON being sent
        Gdx.app.log("HTTP", "POST " + endpoint);
        Gdx.app.log("HTTP", "Body: " + jsonBody);

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

                // Debug: Log response
                Gdx.app.log("HTTP", "Response status: " + statusCode);
                Gdx.app.log("HTTP", "Response body: " + (response.length() > 500 ? response.substring(0, 500) + "..." : response));

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

