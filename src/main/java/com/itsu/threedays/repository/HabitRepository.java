package com.itsu.threedays.repository;

import com.itsu.threedays.entity.HabitEntity;
import com.itsu.threedays.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabitRepository extends JpaRepository<HabitEntity, Long> {
    List<HabitEntity> findAllByUserIdAndDeleteYnAndStopDateIsNull(UserEntity userId, boolean deleteYn);

    List<HabitEntity> findAllByDeleteYn(boolean deleteYn);
}