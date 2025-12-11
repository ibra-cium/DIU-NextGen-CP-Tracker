package com.nextgen.DiuNextGen.repository;
import com.nextgen.DiuNextGen.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    boolean existsByName(String name);
}
