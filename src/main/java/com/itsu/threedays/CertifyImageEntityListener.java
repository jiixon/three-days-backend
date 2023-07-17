package com.itsu.threedays;

import com.itsu.threedays.entity.CertifyImageEntity;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.PostRemove;

@Slf4j
public class CertifyImageEntityListener {

    @PostRemove
    public void postRemove(CertifyImageEntity certifyImageEntity) {
        log.info("postRemove ");
        String imageUrl = certifyImageEntity.getImageUrl();
        log.info("imageURL");


    }
}
