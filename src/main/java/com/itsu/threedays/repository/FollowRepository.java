package com.itsu.threedays.repository;

import com.itsu.threedays.entity.FollowEntity;
import com.itsu.threedays.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<FollowEntity, Long> {
    List<FollowEntity> findAllByToUser(UserEntity toUser); //본인을 팔로우한 목록

    List<FollowEntity> findAllByFromUser(UserEntity fromUser); //본인이 팔로우한 목록

    FollowEntity findByFromUserIdAndToUserId(Long fromUserId, Long toUserId); //팔로우관계 조회
}
