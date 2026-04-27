package com.hopeuniversity.hope_university_management.service;

import com.hopeuniversity.hope_university_management.domain.entities.Room;
import com.hopeuniversity.hope_university_management.domain.repositories.RoomRepository;
import com.hopeuniversity.hope_university_management.dto.request.RoomRequest;
import com.hopeuniversity.hope_university_management.dto.response.RoomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public Page<RoomResponse> getAllRooms(Pageable pageable) {
        return roomRepository.findAllActive(pageable).map(this::toResponse);
    }

    public RoomResponse getRoomById(Long id) {
        Room room = roomRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        return toResponse(room);
    }

    @Transactional
    public RoomResponse createRoom(RoomRequest request) {
        if (roomRepository.existsByRoomNumber(request.getRoomNumber())) {
            throw new RuntimeException("Room number already exists");
        }
        Room room = new Room();
        room.setRoomNumber(request.getRoomNumber());
        room.setBuilding(request.getBuilding());
        room.setCapacity(request.getCapacity());
        room.setType(request.getType());
        Room saved = roomRepository.save(room);
        return toResponse(saved);
    }

    @Transactional
    public RoomResponse updateRoom(Long id, RoomRequest request) {
        Room room = roomRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        if (!room.getRoomNumber().equals(request.getRoomNumber()) && roomRepository.existsByRoomNumber(request.getRoomNumber())) {
            throw new RuntimeException("Room number already exists");
        }
        room.setRoomNumber(request.getRoomNumber());
        room.setBuilding(request.getBuilding());
        room.setCapacity(request.getCapacity());
        room.setType(request.getType());
        Room updated = roomRepository.save(room);
        return toResponse(updated);
    }

    @Transactional
    public void deleteRoom(Long id) {
        roomRepository.findById(id).ifPresent(room -> {
            room.setDeletedAt(LocalDateTime.now());
            roomRepository.save(room);
        });
    }

    private RoomResponse toResponse(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getRoomNumber(),
                room.getBuilding(),
                room.getCapacity(),
                room.getType(),
                room.getCreatedAt(),
                room.getUpdatedAt()
        );
    }
}