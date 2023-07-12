package com.itsu.threedays.repository;

import com.itsu.threedays.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findById(Long id);
    @Query("select m from UserEntity m where m.refreshToken = :refreshToken")
    Optional<UserEntity> findByRefreshToken(@Param("refreshToken") String refreshToken);
}