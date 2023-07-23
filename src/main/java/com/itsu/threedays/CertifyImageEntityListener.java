package com.itsu.threedays;

import com.itsu.threedays.entity.CertifyImageEntity;
import com.itsu.threedays.service.S3uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.PostRemove;

@Slf4j
@RequiredArgsConstructor
public class CertifyImageEntityListener {

    private final S3uploader s3uploader;

    @PostRemove
    public void postRemove(CertifyImageEntity certifyImageEntity) {
        log.info("postRemove ");
        String imageUrl = certifyImageEntity.getImageUrl();
        log.info("imageURL :{}", imageUrl);

        s3uploader.delete(imageUrl);
    }
}
