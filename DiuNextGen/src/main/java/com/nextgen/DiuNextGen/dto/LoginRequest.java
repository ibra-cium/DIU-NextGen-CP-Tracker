package com.nextgen.DiuNextGen.dto;
import lombok.Data;

@Data
public class LoginRequest {
    private String vjudgeHandle; // We use handle to login
    private String password;
}