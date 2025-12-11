package com.nextgen.DiuNextGen.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title; // e.g. "Binary Search Tutorial"
    private String url;   // e.g. "https://youtube.com..."

    @ManyToOne
    @JoinColumn(name = "round_id")
    @JsonIgnore // Prevent infinite recursion loop in JSON
    private Round round;
}