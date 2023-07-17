package com.itsu.threedays.repository;

import com.itsu.threedays.entity.CertifyImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertifyImageRepository extends JpaRepository<CertifyImageEntity, Long> {
}
