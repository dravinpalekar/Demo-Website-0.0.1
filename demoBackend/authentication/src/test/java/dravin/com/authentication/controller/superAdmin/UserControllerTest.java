package dravin.com.authentication.controller.superAdmin;

import dravin.com.authentication.requestmodel.superAdmin.ActivateRequestModel;
import dravin.com.authentication.service.superAdmin.UserService;
import dravin.com.repository.constant.enumConstant.Status;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static dravin.com.authentication.constant.RoutesFile.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    private static Validator validator;
    private static ValidatorFactory factory;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }


    @Test
    @DisplayName("While Super-Admin valid request should have no validation errors")
    void testValidRequest() throws Exception {

        Set<ConstraintViolation<ActivateRequestModel>> violations = validator.validate(prepareActiveRequestFunction(Status.ENABLE));
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("While Super-Admin request validation and null id")
    void testNullId() throws Exception {

        ActivateRequestModel request = new ActivateRequestModel();
        ReflectionTestUtils.setField(request, "status", Status.ENABLE);

        Set<ConstraintViolation<ActivateRequestModel>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        ConstraintViolation<ActivateRequestModel> violation = violations.iterator().next();

        assertEquals("Id is required.", violation.getMessage());
        assertEquals("id", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("While Super-Admin validation and id less than one")
    void testInvalidId() throws Exception {

        ActivateRequestModel request = new ActivateRequestModel();
        ReflectionTestUtils.setField(request, "id", 0L);
        ReflectionTestUtils.setField(request, "status", Status.ENABLE);

        Set<ConstraintViolation<ActivateRequestModel>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        ConstraintViolation<ActivateRequestModel> violation = violations.iterator().next();

        assertEquals("Id must be greater than 0.", violation.getMessage());
        assertEquals("id", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("While Super-Admin validation and negative id")
    void testNegativeId() throws Exception {

        ActivateRequestModel request = new ActivateRequestModel();
        ReflectionTestUtils.setField(request, "id", -10L);
        ReflectionTestUtils.setField(request, "status", Status.ENABLE);

        Set<ConstraintViolation<ActivateRequestModel>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        ConstraintViolation<ActivateRequestModel> violation = violations.iterator().next();

        assertEquals("Id must be greater than 0.", violation.getMessage());
    }

    @Test
    @DisplayName("While Super-Admin validation and null status")
    void testNullStatus() throws Exception {

        ActivateRequestModel request = new ActivateRequestModel();
        ReflectionTestUtils.setField(request, "id", 1L);

        Set<ConstraintViolation<ActivateRequestModel>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        ConstraintViolation<ActivateRequestModel> violation = violations.iterator().next();

        assertEquals("Status is required.", violation.getMessage());
        assertEquals("status", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("While Super-Admin validation and null id and null status")
    void testNullIdAndStatus() {

        ActivateRequestModel request = new ActivateRequestModel();
        Set<ConstraintViolation<ActivateRequestModel>> violations = validator.validate(request);
        assertEquals(2, violations.size());
    }

    private ActivateRequestModel prepareActiveRequestFunction(Status statusName){
        ActivateRequestModel request = new ActivateRequestModel();
        ReflectionTestUtils.setField(request, "id", 1L);
        ReflectionTestUtils.setField(request, "status", statusName);
        return request;
    }

    @Test
    @DisplayName("While Super-Admin hit request get user and success")
    void testGetAllUser() throws Exception {

        Map<String, Object> response = Map.of("data", List.of(Map.of("id", 1, "name", "Admin")));

        when(userService.getAllUser()).thenReturn(ResponseEntity.ok(response));
        mockMvc.perform( get(API_SUPER_ADMIN + USER + GET)).andExpect(status().isOk()).andExpect(jsonPath("$.data").isArray());

        verify(userService).getAllUser();
    }

    @Test
    @DisplayName("While Super-Admin hit request delete user and success")
    void testDeleteUserByIdSuccess() throws Exception {

        Long id = 1L;

        when(userService.deleteUserById(id)).thenReturn(ResponseEntity.ok(Map.of("message", "User deleted successfully.")));

        mockMvc.perform(delete(API_SUPER_ADMIN + USER + DELETE + "/" + id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully."));

        verify(userService).deleteUserById(id);
    }

    @Test
    @DisplayName("While Super-Admin hit request delete user and not found")
    void testDeleteUserByIdNotFound() throws Exception {

        Long id = 10L;

        when(userService.deleteUserById(id)).thenReturn(ResponseEntity.badRequest().body(Map.of("message", "User not found")));

        mockMvc.perform(delete(API_SUPER_ADMIN + USER + DELETE + "/" + id))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("User not found"));

        verify(userService).deleteUserById(id);
    }

    @Test
    @DisplayName("While Super-Admin hit request post activate user and success")
    void testActivateDeactivateUserSuccess() throws Exception {

        when(userService.activeAndDeactivateUserByID(any(ActivateRequestModel.class)))
                .thenReturn(ResponseEntity.ok(Map.of("message", "User active successfully.")));

        mockMvc.perform(post(API_SUPER_ADMIN + USER + ACTIVE_DEACTIVATE).contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(prepareActiveRequestFunction(Status.ENABLE))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.message").value("User active successfully."));

        verify(userService).activeAndDeactivateUserByID(any(ActivateRequestModel.class));
    }

    @Test
    @DisplayName("While Super-Admin hit request post activate user and user not found")
    void testActivateDeactivateUserNotFound() throws Exception {

        ActivateRequestModel request = new ActivateRequestModel();
        ReflectionTestUtils.setField(request, "id", 100L);
        ReflectionTestUtils.setField(request, "status", Status.DISABLE);

        when(userService.activeAndDeactivateUserByID(any(ActivateRequestModel.class)))
                .thenReturn(ResponseEntity.badRequest().body(Map.of("message", "User not found")));

        mockMvc.perform(post(API_SUPER_ADMIN + USER + ACTIVE_DEACTIVATE).contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("User not found"));

        verify(userService).activeAndDeactivateUserByID(any(ActivateRequestModel.class));
    }

    @Test
    @DisplayName("While Super-Admin hit request post activate user and validation failure")
    void testActivateDeactivateValidationFailure() throws Exception {

        mockMvc.perform(post(API_SUPER_ADMIN + USER + ACTIVE_DEACTIVATE).contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new ActivateRequestModel()))).andExpect(status().isNotAcceptable());
    }


}
