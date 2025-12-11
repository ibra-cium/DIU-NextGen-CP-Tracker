package com.nextgen.DiuNextGen.entity;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class Round {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(nullable = false)
    private String vjudgeContestId;
    @ManyToOne
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;
    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL)
    private java.util.List<Resource> resources;
}
