package com.nextgen.DiuNextGen.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class CalendarEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;        // "DP Contest R4"
    private LocalDate eventDate; // "2025-12-10"
    private String type;         // "CONTEST" or "CLASS"
}