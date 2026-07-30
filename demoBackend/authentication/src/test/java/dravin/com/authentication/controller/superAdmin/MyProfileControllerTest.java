package dravin.com.authentication.controller.superAdmin;

import dravin.com.authentication.requestmodel.superAdmin.UpdateMyProfileRequestModel;
import dravin.com.authentication.service.superAdmin.MyProfileService;
import dravin.com.repository.constant.enumConstant.Gender;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;
import java.util.Set;

import static dravin.com.authentication.constant.RoutesFile.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class MyProfileControllerTest {

    private static Validator validator;
    private static ValidatorFactory factory;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @MockitoBean
    private MyProfileService myProfileService;

    private static final String BASE_URL = API_SUPER_ADMIN + MY_PROFILE;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Validator should produce zero violations for a completely valid request model")
    void testValidRequestModel_NoViolations() {
        UpdateMyProfileRequestModel model = createValidRequestModel();

        Set<ConstraintViolation<UpdateMyProfileRequestModel>> violations = validator.validate(model);

        assertTrue(violations.isEmpty(), "Valid model should not trigger any validation errors.");
    }

    @Test
    @DisplayName("Validator should catch missing mandatory string fields (firstName, lastName, country, city, address)")
    void testBlankMandatoryFields_TriggersViolations() {
        UpdateMyProfileRequestModel model = createValidRequestModel();
        ReflectionTestUtils.setField(model, "firstName", " ");
        ReflectionTestUtils.setField(model, "lastName", "");
        ReflectionTestUtils.setField(model, "country", null);
        ReflectionTestUtils.setField(model, "city", "  ");
        ReflectionTestUtils.setField(model, "address", null);

        Set<ConstraintViolation<UpdateMyProfileRequestModel>> violations = validator.validate(model);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Fist Name is mandatory.")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Last Name is mandatory.")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Country is mandatory.")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("City is mandatory.")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Address is mandatory.")));
    }

    @Test
    @DisplayName("Validator should enforce Min and Max constraints on Age")
    void testAgeConstraints_TriggersViolations() {
        // Case A: Below Minimum
        UpdateMyProfileRequestModel modelMin = createValidRequestModel();
        ReflectionTestUtils.setField(modelMin, "age", 3);
        Set<ConstraintViolation<UpdateMyProfileRequestModel>> violationsMin = validator.validate(modelMin);
        assertTrue(violationsMin.stream().anyMatch(v -> v.getMessage().equals("Age must be at least 04.")));

        // Case B: Above Maximum
        UpdateMyProfileRequestModel modelMax = createValidRequestModel();
        ReflectionTestUtils.setField(modelMax, "age", 101);
        Set<ConstraintViolation<UpdateMyProfileRequestModel>> violationsMax = validator.validate(modelMax);
        assertTrue(violationsMax.stream().anyMatch(v -> v.getMessage().equals("Age must not be more than 100.")));

        // Case C: Null Value
        UpdateMyProfileRequestModel modelNull = createValidRequestModel();
        ReflectionTestUtils.setField(modelNull, "age", null);
        Set<ConstraintViolation<UpdateMyProfileRequestModel>> violationsNull = validator.validate(modelNull);
        assertTrue(violationsNull.stream().anyMatch(v -> v.getMessage().equals("Age is mandatory.")));
    }

    @Test
    @DisplayName("Validator should enforce Min and Max constraints on Pin-Code")
    void testPinCodeConstraints_TriggersViolations() {
        // Case A: Below Minimum
        UpdateMyProfileRequestModel modelMin = createValidRequestModel();
        ReflectionTestUtils.setField(modelMin, "pinCode", 3);
        Set<ConstraintViolation<UpdateMyProfileRequestModel>> violationsMin = validator.validate(modelMin);
        assertTrue(violationsMin.stream().anyMatch(v -> v.getMessage().equals("Pin-Code must be at least 04.")));

        // Case B: Above Maximum
        UpdateMyProfileRequestModel modelMax = createValidRequestModel();
        ReflectionTestUtils.setField(modelMax, "pinCode", 1000000);
        Set<ConstraintViolation<UpdateMyProfileRequestModel>> violationsMax = validator.validate(modelMax);
        assertTrue(violationsMax.stream().anyMatch(v -> v.getMessage().equals("Pin-Code must not be more than 999999.")));

        // Case C: Null Value
        UpdateMyProfileRequestModel modelNull = createValidRequestModel();
        ReflectionTestUtils.setField(modelNull, "pinCode", null);
        Set<ConstraintViolation<UpdateMyProfileRequestModel>> violationsNull = validator.validate(modelNull);
        assertTrue(violationsNull.stream().anyMatch(v -> v.getMessage().equals("Pin-Code is mandatory.")));
    }

    private UpdateMyProfileRequestModel createValidRequestModel() {
        UpdateMyProfileRequestModel model = new UpdateMyProfileRequestModel();
        ReflectionTestUtils.setField(model, "firstName", "John");
        ReflectionTestUtils.setField(model, "middleName", "Pal");
        ReflectionTestUtils.setField(model, "lastName", "Doe");
        ReflectionTestUtils.setField(model, "gender", Gender.MALE);
        ReflectionTestUtils.setField(model, "age", 25);
        ReflectionTestUtils.setField(model, "country", "India");
        ReflectionTestUtils.setField(model, "city", "Delhi");
        ReflectionTestUtils.setField(model, "pinCode", 110001);
        ReflectionTestUtils.setField(model, "address", "221B Baker Street");
        return model;
    }

//    @Test
//    @DisplayName("updateMyProfile() should pass when valid request model and valid file are provided")
//    void testUpdateMyProfile_Success_WithValidFile() {
//
//        UpdateMyProfileRequestModel request = createValidRequestModel();
//        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", new byte[]{1, 2, 3});
//
//        when(myProfileService.updateMyProfile(any(), any())).thenReturn(ResponseEntity.ok(Map.of("message", "Profile updated successfully.")));
//
//        ResponseEntity<Map<String, String>> response = myProfileController.updateMyProfile(file, request);
//
//        assertEquals(200, response.getStatusCode().value());
//        assertEquals("Profile updated successfully.", response.getBody().get("message"));
//        verify(myProfileService, times(1)).updateMyProfile(request, file);
//    }
//
//    @Test
//    @DisplayName("updateMyProfile() should pass when file is null or empty")
//    void testUpdateMyProfile_Success_NullOrEmptyFile() {
//
//        UpdateMyProfileRequestModel request = createValidRequestModel();
//        MockMultipartFile emptyFile = new MockMultipartFile("file", "", "image/png", new byte[0]);
//
//        when(myProfileService.updateMyProfile(any(), any())).thenReturn(ResponseEntity.ok(Map.of("message", "Profile updated successfully.")));
//
//        // Test null file
//        ResponseEntity<Map<String, String>> res1 = myProfileController.updateMyProfile(null, request);
//        assertEquals(200, res1.getStatusCode().value());
//
//        // Test empty file
//        ResponseEntity<Map<String, String>> res2 = myProfileController.updateMyProfile(emptyFile, request);
//        assertEquals(200, res2.getStatusCode().value());
//
//        verify(myProfileService, times(2)).updateMyProfile(any(), any());
//    }
//
//    @Test
//    @DisplayName("validateImageFile() should throw MaxUploadSizeExceededException when file exceeds 1 MB")
//    void testUpdateMyProfile_FileTooLarge_ThrowsException() {
//
//        UpdateMyProfileRequestModel request = createValidRequestModel();
//        // 1 MB + 1 byte
//        byte[] largeContent = new byte[1048577];
//        MockMultipartFile largeFile = new MockMultipartFile("file", "big.png", "image/png", largeContent);
//
//        assertThrows(MaxUploadSizeExceededException.class, () -> myProfileController.updateMyProfile(largeFile, request));
//
//        verify(myProfileService, never()).updateMyProfile(any(), any());
//    }
//
//    @Test
//    @DisplayName("validateImageFile() should throw IllegalArgumentException when content-type is unsupported or null")
//    void testUpdateMyProfile_InvalidContentType_ThrowsException() {
//
//        UpdateMyProfileRequestModel request = createValidRequestModel();
//
//        // Case A: PDF media type
//        MockMultipartFile pdfFile = new MockMultipartFile("file", "document.pdf", "application/pdf", new byte[]{1, 2});
//        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () -> myProfileController.updateMyProfile(pdfFile, request));
//        assertEquals("Only JPEG, JPG, JPE or PNG images are allowed", ex1.getMessage());
//
//        // Case B: Null content type
//        MockMultipartFile nullTypeFile = new MockMultipartFile("file", "image.jpg", null, new byte[]{1, 2});
//        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> myProfileController.updateMyProfile(nullTypeFile, request));
//        assertEquals("Only JPEG, JPG, JPE or PNG images are allowed", ex2.getMessage());
//
//        verify(myProfileService, never()).updateMyProfile(any(), any());
//    }
//
//    @Test
//    @DisplayName("getMyProfile() should delegate call to service layer and return response")
//    void testGetMyProfile_Success() {
//
//        when(myProfileService.getMyProfile()).thenReturn(ResponseEntity.ok(Map.of("data", "Profile Details")));
//
//        ResponseEntity<Map<String, Object>> response = myProfileController.getMyProfile();
//
//        assertEquals(200, response.getStatusCode().value());
//        assertEquals("Profile Details", response.getBody().get("data"));
//        verify(myProfileService, times(1)).getMyProfile();
//    }
//
//    @Test
//    @DisplayName("getMyImage() should delegate call to service layer and return response")
//    void testGetMyImage_Success() {
//
//        when(myProfileService.getMyImage()).thenReturn(ResponseEntity.ok(Map.of("image", "base64EncodedData")));
//
//        ResponseEntity<Map<String, Object>> response = myProfileController.getMyImage();
//
//        assertEquals(200, response.getStatusCode().value());
//        assertEquals("base64EncodedData", response.getBody().get("image"));
//        verify(myProfileService, times(1)).getMyImage();
//    }

    @Test
    @DisplayName("POST updateMyProfile should succeed with valid multipart JSON payload and valid image")
    void testUpdateMyProfile_Success_WithFile() throws Exception {
        UpdateMyProfileRequestModel requestModel = createValidRequestModel();

        MockMultipartFile jsonPart = new MockMultipartFile("updateMyProfileRequest", "",  MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(requestModel));

        MockMultipartFile imagePart = new MockMultipartFile("file","avatar.png","image/png", new byte[]{1, 2, 3, 4});

        when(myProfileService.updateMyProfile(any(UpdateMyProfileRequestModel.class), any())).thenReturn(ResponseEntity.ok(Map.of("message", "Profile updated successfully.")));

        mockMvc.perform(multipart(BASE_URL + CREATE).file(imagePart).file(jsonPart).contentType(MediaType.MULTIPART_FORM_DATA_VALUE)).andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Profile updated successfully."));

        verify(myProfileService, times(1)).updateMyProfile(any(UpdateMyProfileRequestModel.class), any());
    }

    @Test
    @DisplayName("POST updateMyProfile should throw MaxUploadSizeExceededException when image > 1MB")
    void testUpdateMyProfile_FileExceedsSize_ThrowsException() throws Exception {
        UpdateMyProfileRequestModel requestModel = createValidRequestModel();

        MockMultipartFile jsonPart = new MockMultipartFile("updateMyProfileRequest","", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(requestModel)
        );

        // File size > 1MB (1048577 bytes)
        byte[] largeFileBytes = new byte[1048577];
        MockMultipartFile largeImagePart = new MockMultipartFile("file","large_image.png","image/png", largeFileBytes);

        mockMvc.perform(multipart(BASE_URL + CREATE).file(largeImagePart).file(jsonPart).contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MaxUploadSizeExceededException));

        verify(myProfileService, never()).updateMyProfile(any(), any());
    }

    @Test
    @DisplayName("POST updateMyProfile should throw IllegalArgumentException when image format is invalid")
    void testUpdateMyProfile_InvalidContentType_ThrowsException() throws Exception {
        UpdateMyProfileRequestModel requestModel = createValidRequestModel();

        MockMultipartFile jsonPart = new MockMultipartFile("updateMyProfileRequest","",MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(requestModel));

        // Unsupported content type
        MockMultipartFile pdfPart = new MockMultipartFile("file","document.pdf","application/pdf", new byte[]{1, 2, 3});

        Exception exception = assertThrows(Exception.class, () -> mockMvc.perform(multipart(BASE_URL + CREATE).file(pdfPart).file(jsonPart).contentType(MediaType.MULTIPART_FORM_DATA_VALUE)));

        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertEquals("Only JPEG, JPG, JPE or PNG images are allowed", exception.getCause().getMessage());

        verify(myProfileService, never()).updateMyProfile(any(), any());
    }

    @Test
    @DisplayName("GET getMyProfile should successfully return profile data")
    void testGetMyProfile_Success() throws Exception {

        when(myProfileService.getMyProfile()).thenReturn(ResponseEntity.ok(Map.of("data", "Profile details object")));

        mockMvc.perform(get(BASE_URL + GET)).andExpect(status().isOk()).andExpect(jsonPath("$.data").value("Profile details object"));

        verify(myProfileService, times(1)).getMyProfile();
    }

    @Test
    @DisplayName("GET getMyImage should successfully return Base64 image payload")
    void testGetMyImage_Success() throws Exception {

        when(myProfileService.getMyImage()).thenReturn(ResponseEntity.ok(Map.of("image", "data:image/png;base64,fakeData")));

        mockMvc.perform(get(BASE_URL + GET_MY_IMAGE)).andExpect(status().isOk()).andExpect(jsonPath("$.image").value("data:image/png;base64,fakeData"));

        verify(myProfileService, times(1)).getMyImage();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }
}
