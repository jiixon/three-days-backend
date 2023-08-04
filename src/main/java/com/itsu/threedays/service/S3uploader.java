package com.itsu.threedays.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@Component
@Slf4j
@RequiredArgsConstructor
public class S3uploader {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile multipartFile, String fileName) throws IOException {
        byte[] fileBytes = multipartFile.getBytes();
        String imageUrl = putS3(fileBytes, fileName);
        return imageUrl;
    }

    public List<String> upload(List<MultipartFile> multipartFiles, String dirName) throws IOException {
        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            String originalFilename = multipartFile.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + extension;
            String fileName = dirName + "/" + uniqueFilename;

            byte[] fileBytes = multipartFile.getBytes();
            String imageUrl = putS3(fileBytes, fileName);
            imageUrls.add(imageUrl);
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

    public void delete(String imageUrl) {
        AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
        String objectKey = extractObjectKeyFromImageUrl(imageUrl); // 이미지 URL에서 객체 키 추출
        s3Client.deleteObject(bucket, objectKey);

    }

    private String extractObjectKeyFromImageUrl(String imageUrl) {
        // 이미지 URL에서 S3 객체 키를 추출하는 로직 구현
        String objectKey = null;
        try {
            URL url = new URL(imageUrl);
            String path = url.getPath();
            objectKey = URLDecoder.decode(path, "UTF-8").substring(1); // 앞에 '/' 제거
        } catch (Exception e) {
            // URL 파싱 오류 처리
            e.printStackTrace();
        }
        return objectKey;
    }

}
