package com.itsu.threedays.service;

import com.itsu.threedays.entity.FollowEntity;
import com.itsu.threedays.entity.UserEntity;
import com.itsu.threedays.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowService {
    private final FollowRepository followRepository;
    private final UserService userService;

    public void unFollowUser(Long fromUserId, Long toUserId) throws Exception {
        FollowEntity follow = followRepository.findByFromUserIdAndToUserId(fromUserId, toUserId);
        if (follow != null) {
            followRepository.delete(follow);
        } else {
            throw new Exception("해당 팔로우 관계를 찾을 수 없습니다.");
        }
    }

    public List<FollowEntity> getFollowingList(Long userId) { //본인이 팔로잉한 목록조회
        UserEntity user = userService.getUser(userId);
        List<FollowEntity> allByFromUser = followRepository.findAllByFromUser(user);
        return allByFromUser;
    }

    public List<FollowEntity> getFollowerList(Long userId) { //본인을 팔로워한 목록조회
        UserEntity user = userService.getUser(userId);
        List<FollowEntity> allByToUser = followRepository.findAllByToUser(user);
        return allByToUser;
    }

    public List<UserEntity> getFollowingUsers(Long userId) {
        List<FollowEntity> followList = getFollowingList(userId);

        List<UserEntity> followingUsers = new ArrayList<>();
        for (FollowEntity follow : followList) {
            UserEntity followingUser = follow.getToUser();
            followingUsers.add(followingUser);
        }

        return followingUsers;
    }


}
