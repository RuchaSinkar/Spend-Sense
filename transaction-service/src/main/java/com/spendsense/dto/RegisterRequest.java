package com.spendsense.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String userId;
    private String email;
    private String password;
}