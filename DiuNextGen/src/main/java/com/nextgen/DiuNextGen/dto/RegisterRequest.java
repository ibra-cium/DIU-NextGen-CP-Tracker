package com.nextgen.DiuNextGen.dto;
import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String batch;
    private String vjudgeHandle;
    private String password;
}