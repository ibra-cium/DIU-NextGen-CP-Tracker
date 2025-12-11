package com.nextgen.DiuNextGen.repository;
import com.nextgen.DiuNextGen.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface UserRepository extends JpaRepository<User, Long> {
    User findByVjudgeHandle(String vjudgeHandle);
    List<User> findByBatchOrderByTotalSolvedDesc(String batch);
}
