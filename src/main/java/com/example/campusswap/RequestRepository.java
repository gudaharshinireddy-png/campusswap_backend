package com.example.campusswap;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByRequesterId(Long requesterId);

    List<Request> findByOwnerId(Long ownerId);

    List<Request> findByItemId(Long itemId);
}