package dravin.com.authentication.service.superAdmin;


import dravin.com.authentication.configuration.jwt.JwtUtils;
import dravin.com.authentication.requestmodel.superAdmin.UpdateMyProfileRequestModel;
import dravin.com.repository.constant.enumConstant.Gender;
import dravin.com.repository.entity.UserEntity;
import dravin.com.repository.entity.UserOtherInformationEntity;
import dravin.com.repository.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import net.coobird.thumbnailator.Thumbnails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.nio.file.Files;
import java.nio.file.Path;

import static dravin.com.authentication.constant.ConstantString.PROFILE_UPDATED_SUCCESSFULLY;
import static dravin.com.authentication.constant.Error.DATA_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MyProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private MyProfileService service;

    private UserEntity user;

    @BeforeEach
    void setup() {

        RequestContextHolder.setRequestAttributes( new ServletRequestAttributes(request));

        when(jwtUtils.parseJwt(any())).thenReturn("token");
        when(jwtUtils.getIdFromJwtToken(any())).thenReturn("1");

        user = new UserEntity();
        user.setId(1L);
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("getMyProfile() should return profile data")
    void testGetMyProfileSuccess() {

        UserOtherInformationEntity info = new UserOtherInformationEntity();
        info.setFirstName("John");

        user.setUserOtherInformation(info);
        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(user));

        ResponseEntity<Map<String, Object>> response = service.getMyProfile();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(info, response.getBody().get("data"));
    }

    @Test
    @DisplayName("getMyProfile() should return data not found")
    void testGetMyProfileDataNotFound() {

        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(user));
        ResponseEntity<Map<String, Object>> response = service.getMyProfile();

        assertEquals(DATA_NOT_FOUND, response.getBody().get("error"));
    }

    @Test
    @DisplayName("getMyProfile() should throw exception when user not found")
    void testGetMyProfileUserNotFound() {

        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.empty());
        assertThrows(NullPointerException.class, () -> service.getMyProfile());
    }

    @Test
    @DisplayName("getMyImage() should return image successfully")
    void testGetMyImageSuccess() throws Exception {

        UserOtherInformationEntity info = new UserOtherInformationEntity();
        info.setPhotoUrl("test.png");
        info.setFirstName("John");
        info.setMiddleName("Pal");
        info.setLastName("Doe");
        info.setGender(Gender.MALE);

        user.setUserOtherInformation(info);
        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(user));

        try (MockedStatic<Files> files = mockStatic(Files.class)) {

            Path path = Path.of("test");
            files.when(() -> Files.exists(any(Path.class))).thenReturn(true);
            files.when(() -> Files.readAllBytes(any(Path.class))).thenReturn(new byte[]{1, 2});
            files.when(() -> Files.probeContentType(any(Path.class))).thenReturn("image/png");

            ResponseEntity<Map<String, Object>> response = service.getMyImage();

            assertEquals(200, response.getStatusCode().value());
            assertTrue(response.getBody().containsKey("image"));
        }
    }

    @Test
    @DisplayName("getMyImage() should return 404 when image file does not exist")
    void testGetMyImageFileNotFound() {

        UserOtherInformationEntity info = new UserOtherInformationEntity();
        info.setPhotoUrl("abc.png");

        user.setUserOtherInformation(info);
        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(user));

        try (MockedStatic<Files> files = mockStatic(Files.class)) {

            files.when(() -> Files.exists(any(Path.class))).thenReturn(false);
            ResponseEntity<Map<String, Object>> response = service.getMyImage();

            assertEquals(404, response.getStatusCode().value());
        }
    }

    @Test
    @DisplayName("getMyImage() should return data not found when photo is null")
    void testGetMyImagePhotoNull() {

        user.setUserOtherInformation(new UserOtherInformationEntity());
        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(user));
        ResponseEntity<Map<String, Object>> response = service.getMyImage();

        assertEquals(DATA_NOT_FOUND, response.getBody().get("error"));
    }

    @Test
    @DisplayName("getMyImage() should return internal server error on exception")
    void testGetMyImageException() {

        UserOtherInformationEntity info = new UserOtherInformationEntity();
        info.setPhotoUrl("abc.png");

        user.setUserOtherInformation(info);
        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(user));

        try (MockedStatic<Files> files = mockStatic(Files.class)) {

            files.when(() -> Files.exists(any(Path.class))).thenReturn(true);
            files.when(() -> Files.readAllBytes(any(Path.class))).thenThrow(new RuntimeException());
            ResponseEntity<Map<String, Object>> response = service.getMyImage();

            assertEquals(500, response.getStatusCode().value());
        }
    }

    @Test
    @DisplayName("getMyImage() should throw exception when user not found")
    void testGetMyImageUserNotFound() {

        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.empty());
        assertThrows(NullPointerException.class, () -> service.getMyImage());
    }

    @Test
    @DisplayName("getMyImage() should return data not found when userOtherInformation is null")
    void testGetMyImageUserOtherInformationNull() {

        user.setUserOtherInformation(null);
        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(user));

        ResponseEntity<Map<String, Object>> response = service.getMyImage();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(DATA_NOT_FOUND, response.getBody().get("error"));
    }

    @Test
    @DisplayName("getMyImage() should return single space for middleName when middleName is null")
    void testGetMyImageMiddleNameNull() throws Exception {

        UserOtherInformationEntity info = new UserOtherInformationEntity();
        info.setPhotoUrl("test.png");
        info.setFirstName("John");
        info.setMiddleName(null); // Explicitly null
        info.setLastName("Doe");
        info.setGender(Gender.MALE);

        user.setUserOtherInformation(info);
        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(user));

        try (MockedStatic<Files> files = mockStatic(Files.class)) {
            files.when(() -> Files.exists(any(Path.class))).thenReturn(true);
            files.when(() -> Files.readAllBytes(any(Path.class))).thenReturn(new byte[]{1, 2});
            files.when(() -> Files.probeContentType(any(Path.class))).thenReturn("image/png");

            ResponseEntity<Map<String, Object>> response = service.getMyImage();

            assertEquals(200, response.getStatusCode().value());
            assertEquals(" ", response.getBody().get("middleName"));
        }
    }

    private UpdateMyProfileRequestModel prepareUpdateMyProfileRequest(){
        UpdateMyProfileRequestModel request = new UpdateMyProfileRequestModel();
        ReflectionTestUtils.setField(request, "firstName", "John");
        ReflectionTestUtils.setField(request, "lastName", "Doe");
        ReflectionTestUtils.setField(request, "age", 25);
        return request;
    }

    @Test
    @DisplayName("updateMyProfile() should update profile successfully without image")
    void testUpdateMyProfileWithoutImage() {

        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(user));
        ResponseEntity<Map<String, String>> response = service.updateMyProfile(prepareUpdateMyProfileRequest(), null);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(PROFILE_UPDATED_SUCCESSFULLY, response.getBody().get("message"));

        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("updateMyProfile() should return success when user not found")
    void testUpdateMyProfileUserNotFound() {

        UpdateMyProfileRequestModel request = new UpdateMyProfileRequestModel();

        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.empty());
        ResponseEntity<Map<String, String>> response = service.updateMyProfile(request, null);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(PROFILE_UPDATED_SUCCESSFULLY, response.getBody().get("message"));

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateMyProfile() should return error when image processing fails")
    void testUpdateMyProfileImageException() {

        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", new byte[]{1, 2});

        UpdateMyProfileRequestModel request = new UpdateMyProfileRequestModel();
        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(user));
        ResponseEntity<Map<String, String>> response = service.updateMyProfile(request, file);

        assertTrue(response.getBody().containsKey("error"));
    }

    @Test
    @DisplayName("updateMyProfile() should update profile successfully with image")
    void testUpdateMyProfileWithImage() throws Exception {

        MockMultipartFile file = new MockMultipartFile("file","profile.jpg","image/jpeg", new byte[]{1, 2, 3, 4});
        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(user));

        Thumbnails.Builder builder = mock(Thumbnails.Builder.class);

        try (MockedStatic<Thumbnails> thumbnails = mockStatic(Thumbnails.class)) {

            thumbnails.when(() -> Thumbnails.of(any(java.io.InputStream.class))).thenReturn(builder);

            when(builder.size(anyInt(), anyInt())).thenReturn(builder);
            when(builder.outputFormat(anyString())).thenReturn(builder);
            doNothing().when(builder).toFile(any(File.class));

            ResponseEntity<Map<String, String>> response = service.updateMyProfile(prepareUpdateMyProfileRequest(), file);

            assertEquals(200, response.getStatusCode().value());
            assertEquals(PROFILE_UPDATED_SUCCESSFULLY, response.getBody().get("message"));

            verify(userRepository).save(any(UserEntity.class));

            assertNotNull(user.getUserOtherInformation());
            assertEquals("John", user.getUserOtherInformation().getFirstName());

            assertNotNull(user.getUserOtherInformation().getPhotoUrl());
        }
    }

    @Test
    @DisplayName("updateMyProfile() should create new UserOtherInformation when it does not exist")
    void testUpdateMyProfileWhenUserOtherInformationIsNull() {

        UpdateMyProfileRequestModel request = new UpdateMyProfileRequestModel();
        ReflectionTestUtils.setField(request, "firstName", "John");

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUserOtherInformation(null);

        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(user));

        ResponseEntity<Map<String, String>> response = service.updateMyProfile(request, null);
        assertEquals(200, response.getStatusCode().value());

        verify(userRepository).save(any(UserEntity.class));
        assertNotNull(user.getUserOtherInformation());
    }

    @Test
    @DisplayName("updateMyProfile() should update profile successfully when existingInfo is NOT null and without image")
    void testUpdateMyProfileWithoutImageExistingInfo() {

        UserEntity userWithExistingInfo = new UserEntity();
        userWithExistingInfo.setId(1L);
        userWithExistingInfo.setUserOtherInformation(new UserOtherInformationEntity()); // NOT null

        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(userWithExistingInfo));
        ResponseEntity<Map<String, String>> response = service.updateMyProfile(prepareUpdateMyProfileRequest(), null);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(PROFILE_UPDATED_SUCCESSFULLY, response.getBody().get("message"));
        verify(userRepository).save(any(UserEntity.class));
    }
}
