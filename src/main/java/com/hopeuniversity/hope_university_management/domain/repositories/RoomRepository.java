package com.hopeuniversity.hope_university_management.domain.repositories;

import com.hopeuniversity.hope_university_management.domain.entities.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByRoomNumber(String roomNumber);

    @Query("SELECT r FROM Room r WHERE r.deletedAt IS NULL")
    Page<Room> findAllActive(Pageable pageable);

    @Query("SELECT r FROM Room r WHERE r.id = :id AND r.deletedAt IS NULL")
    Optional<Room> findActiveById(@Param("id") Long id);

    boolean existsByRoomNumber(String roomNumber);
}