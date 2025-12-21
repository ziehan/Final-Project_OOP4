package com.isthereanyone.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class SigninRequest {

    @NotBlank(message = "Username atau email tidak boleh kosong")
    private String usernameOrEmail;

    @NotBlank(message = "Password tidak boleh kosong")
    private String password;

    public SigninRequest() {}

    public SigninRequest(String usernameOrEmail, String password) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
    }

    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

