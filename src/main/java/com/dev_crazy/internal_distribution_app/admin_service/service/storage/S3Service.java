package com.dev_crazy.internal_distribution_app.admin_service.service.storage;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.dev_crazy.internal_distribution_app.admin_service.model.BinaryDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Date;

@Service
public class S3Service implements IStorageService {
    @Autowired
    private AmazonS3 amazonS3;

    @Value("${amazon.s3.bucket}")
    private String bucket;

    @Value("${amazon.s3.signed-expiration}")
    private long signedExpiration;

    @Override
    public void saveObject(BinaryDetail binaryDetail, InputStream inputStream) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(binaryDetail.getType());
        metadata.setContentLength(binaryDetail.getFilesize());

        amazonS3.putObject(
                bucket,
                binaryDetail.getKeypath(),
                inputStream,
                metadata
        );
    }

    @Override
    public String getObject(String keypath) {

        Date expiration = new Date();
        long expTimeMillis = expiration.getTime() + (1000 * signedExpiration);
        expiration.setTime(expTimeMillis);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, keypath)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expiration);

        return amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString();
    }
}
