package com.itsu.threedays.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
@Component
@Slf4j
@RequiredArgsConstructor
public class S3uploader {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public List<String> upload(List<MultipartFile> multipartFiles, String dirName) throws IOException {
        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            byte[] fileBytes = multipartFile.getBytes();
            String fileName = dirName + "/" + multipartFile.getOriginalFilename(); // S3 객체의 파일 이름을 설정
            String imageUrl = putS3(fileBytes, fileName);
            imageUrls.add(imageUrl); // 업로드된 파일의 S3 URL을 이미지 URL 목록에 추가
        }
        return imageUrls;
    }

    private String putS3(byte[] fileBytes, String fileName) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileBytes.length);
        // 바이트 배열과 메타데이터를 사용하여 파일을 S3에 업로드
        amazonS3Client.putObject(bucket, fileName, new ByteArrayInputStream(fileBytes), metadata);
        return amazonS3Client.getUrl(bucket, fileName).toString(); // 업로드된 파일의 S3 URL을 생성하여 반환
    }

}
