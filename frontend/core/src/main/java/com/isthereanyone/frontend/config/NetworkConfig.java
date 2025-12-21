package com.isthereanyone.frontend.config;

public class NetworkConfig {
    public static final String BACKEND_URL = "https://final-projectoop4-production.up.railway.app/api";

    public static final String ENDPOINT_SIGNUP = "/auth/signup";
    public static final String ENDPOINT_LOGIN = "/auth/login";
    public static final String ENDPOINT_LOGOUT = "/auth/logout";
    public static final String ENDPOINT_SAVE_SLOTS = "/saves";

    public static final int CONNECT_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 10000;
    public static final int WRITE_TIMEOUT = 10000;

    public static final String CONTENT_TYPE = "application/json";
    public static final String CHARSET = "UTF-8";
}
