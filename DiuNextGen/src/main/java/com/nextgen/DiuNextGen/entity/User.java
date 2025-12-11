package com.nextgen.DiuNextGen.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "users",indexes = @Index(columnList = "batch"))
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false,unique = true)
    private String email;
    private String name;
    @Column(nullable = false)
    private String batch;
    @Column(nullable = false,unique = true)
    private String vjudgeHandle;
    private String photoUrl;
    private Integer totalSolved = 0;
    @com.fasterxml.jackson.annotation.JsonIgnore
    private String password;
    private String role;
    private Integer consecutiveMisses = 0;

    private String status = "ACTIVE";
}
