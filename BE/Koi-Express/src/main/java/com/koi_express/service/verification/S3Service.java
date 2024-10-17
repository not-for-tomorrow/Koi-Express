package com.koi_express.service.verification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${spring.aws.s3.bucket-name}")
    private String bucketName;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFile(String fileName, File file) {

        String keyName = "images/" + fileName;
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .acl("public-read")
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));
        return s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(keyName)).toExternalForm();
    }
}
