package com.nextgen.DiuNextGen.repository;
import com.nextgen.DiuNextGen.entity.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CalendarRepository extends JpaRepository<CalendarEvent, Long> {}