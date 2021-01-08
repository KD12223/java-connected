package com.kylerdeggs.javaconnected.configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Configuration class and methods that are enabled for AWS.
 *
 * @author Kyler Deggs
 * @version 1.0.0
 */
@Configuration
public class AWSConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(AWSConfig.class);
    private final MimeTypes types = MimeTypes.getDefaultMimeTypes();

    private AmazonS3 s3Client;

    @Value(value = "${aws.bucket.name}")
    private String bucketName;

    @Value(value = "${aws.credentials.access-key}")
    private String accessKey;

    @Value(value = "${aws.credentials.secret-key}")
    private String secretKey;

    @PostConstruct
    private void initializeAmazon() {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        s3Client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_1)
                .build();

        LOGGER.info("AWS S3 client has been built");
    }

    /**
     * Uploads a image or video to the specified S3 bucket.
     *
     * @param userId User ID that corresponds to the upload
     * @param media  Media that needs to be uploaded
     * @return A string that represents the location of the media in the bucket
     * @throws IOException       Media file processing error
     * @throws MimeTypeException Trying to upload a restricted file type
     */
    public String processUpload(String userId, MultipartFile media) throws IOException, MimeTypeException {
        LOGGER.info("Processing media upload for user ID: " + userId);
        MimeType currentType = types.forName(media.getContentType());
        String key;

        if (currentType.toString().contains("image") || currentType.toString().contains("video")) {
            String fileName = LocalDateTime.now().toString() + "-" + userId.toLowerCase() + currentType.getExtension();
            key = userId.toLowerCase() + "/" + fileName;

            s3Client.putObject(bucketName, key, media.getInputStream(), null);
            LOGGER.info("Media has been added to S3 at s3://" + bucketName + "/" + key);
        } else
            throw new IllegalArgumentException("The media type must be an image or video");


        return key;
    }

    /**
     * Deletes the media at the specified location from the S3 bucket.
     *
     * @param key Location of the media to be deleted
     */
    public void deleteMedia(String key) {
        s3Client.deleteObject(bucketName, key);
    }
}
