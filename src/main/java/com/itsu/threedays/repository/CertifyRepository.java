package com.itsu.threedays.repository;

import com.itsu.threedays.entity.CertifyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertifyRepository extends JpaRepository<CertifyEntity, Long> {

    List<CertifyEntity> findByHabitId(Long habitId);
}