package com.spendsense.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String userId;
    private String email;
    private String token;
    private String type = "Bearer";

    public AuthResponse(String userId,
                        String email,
                        String token) {
        this.userId = userId;
        this.email  = email;
        this.token  = token;
    }
}