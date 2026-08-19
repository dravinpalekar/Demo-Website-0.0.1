package dravin.com.restApi.service;

import dravin.com.restApi.configuration.jwt.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.Map;

@Service
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    private final JwtUtils jwtUtils;
    private final S3Client s3Client;

    @Value("${awsBucketName}")
    private String bucketName;

    @Value("${awsRegion}")
    private String awsRegion;


    public ChatService(JwtUtils jwtUtils, S3Client s3Client) {
        this.jwtUtils = jwtUtils;
        this.s3Client = s3Client;
    }

    public ResponseEntity<?> uploadImage(MultipartFile file) throws IOException {

        if ((file != null)) {
            boolean check = this.checkFileAlreadyExist("users/oneToOne/private/" +file.getOriginalFilename());
            if(!check)
                this.uploadImageIntoAws(file, file.getOriginalFilename());

            String url = "https://s3." + this.awsRegion + ".amazonaws.com/" + this.bucketName + "/users/oneToOne/private/" + file.getOriginalFilename();
            return ResponseEntity.ok().body(Map.of("data", url));
        }

        return ResponseEntity.ok().body(Map.of("data", "file is null"));
    }

    private boolean  checkFileAlreadyExist(String fileKey) {
        try {
            HeadObjectRequest request = HeadObjectRequest.builder().bucket(bucketName).key(fileKey).build();

            s3Client.headObject(request);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                return false;
            }
            throw e;
        }
    }

    private void uploadImageIntoAws(MultipartFile file, String fileName) throws IOException {

        String fileKey = "users/oneToOne/private/" + fileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName).key(fileKey).contentType(file.getContentType()).build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
    }
}
