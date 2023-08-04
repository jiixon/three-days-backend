package com.itsu.threedays.repository;

import com.itsu.threedays.entity.ProfileEntity;
import com.itsu.threedays.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {
    ProfileEntity findByUserId(UserEntity user);
}