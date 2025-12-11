package com.nextgen.DiuNextGen.repository;
import com.nextgen.DiuNextGen.entity.ContestHistory;
import com.nextgen.DiuNextGen.entity.Round;
import com.nextgen.DiuNextGen.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface ContestHistoryRepository extends JpaRepository<ContestHistory, Long> {
    Optional<ContestHistory> findByUserAndRound(User user, Round round);

}
