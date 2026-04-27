package com.hopeuniversity.hope_university_management.domain.repositories;

import com.hopeuniversity.hope_university_management.domain.entities.TimeSlot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

    @Query("SELECT t FROM TimeSlot t WHERE t.deletedAt IS NULL")
    Page<TimeSlot> findAllActive(Pageable pageable);

    @Query("SELECT t FROM TimeSlot t WHERE t.id = :id AND t.deletedAt IS NULL")
    Optional<TimeSlot> findActiveById(@Param("id") Long id);
}