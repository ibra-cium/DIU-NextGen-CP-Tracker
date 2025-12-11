package com.nextgen.DiuNextGen.entity;
import jakarta.persistence.Entity;
import lombok.Data;
import jakarta.persistence.*;
@Entity
@Data
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;
}
