package com.fincore.fincorebank.aws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    public String uploadFile(MultipartFile file, String folderName) throws IOException, S3Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload an empty or null file.");
        }

        String fileExtension = getFileExtension(file.getOriginalFilename());
        String newFileName = UUID.randomUUID() + fileExtension;
        
        String cleanFolderName = folderName != null ? folderName.replaceAll("^/|/$", "") : "";
        String s3Key = cleanFolderName.isEmpty() ? newFileName : cleanFolderName + "/" + newFileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        return s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(s3Key)).toExternalForm();
    }

    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            log.warn("Attempted to delete a file with a null or blank URL.");
            return false;
        }

        try {
            String s3Key = extractKeyFromUrl(fileUrl);

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("Successfully deleted object with key: {} from bucket: {}", s3Key, bucketName);
            return true;

        } catch (S3Exception e) {
            log.error("AWS S3 error while deleting file URL [{}]: {}", fileUrl, e.awsErrorDetails().errorMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("Failed to parse or delete file URL [{}]: {}", fileUrl, e.getMessage(), e);
            return false;
        }
    }


    private String extractKeyFromUrl(String fileUrl) {
        URI uri = URI.create(fileUrl);
        String path = uri.getPath();
        
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        if (path.startsWith(bucketName + "/")) {
            path = path.substring(bucketName.length() + 1);
        }

        return path;
    }

    private String getFileExtension(String originalFileName) {
        if (originalFileName != null && originalFileName.contains(".")) {
            return originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        return "";
    }
}