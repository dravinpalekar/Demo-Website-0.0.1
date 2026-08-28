package dravin.com.authentication.controller.superAdmin;


import dravin.com.authentication.requestmodel.superAdmin.CreatePermissionRequestModel;
import dravin.com.authentication.service.superAdmin.PermissionService;
import dravin.com.repository.constant.enumConstant.Permissions;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static dravin.com.authentication.constant.RoutesFile.*;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class PermissionControllerTest {

    private static Validator validator;
    private static ValidatorFactory factory;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PermissionService permissionService;

    @Autowired
    private JsonMapper objectMapper;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("While Super-Admin added permission should valid request permission name")
    void testValidRequestModel() throws Exception {

        Set<ConstraintViolation<CreatePermissionRequestModel>> violations = validator.validate(preparePermissionRequestFunction(Permissions.ALL));
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("While Super-Admin added permission name Should Be Null")
    void testPermissionNameNull() {

        Set<ConstraintViolation<CreatePermissionRequestModel>> violations = validator.validate(new CreatePermissionRequestModel());
        assertEquals(1, violations.size());

        ConstraintViolation<CreatePermissionRequestModel> violation = violations.iterator().next();

        assertEquals("Permission name is required.", violation.getMessage());
        assertEquals("name", violation.getPropertyPath().toString());
    }

    private CreatePermissionRequestModel preparePermissionRequestFunction(Permissions permissionName) {
        CreatePermissionRequestModel request = new CreatePermissionRequestModel();
        ReflectionTestUtils.setField(request, "name", permissionName);
        return request;
    }


    @Test
    @DisplayName("While Super-Admin post request to create permission")
    void testCreatePermission() throws Exception {

        when(permissionService.createPermission(any(CreatePermissionRequestModel.class))).thenReturn(ResponseEntity.ok(Map.of("message", "Permission created successfully.")));

        mockMvc.perform(post(API_SUPER_ADMIN + PERMISSION + CREATE).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(preparePermissionRequestFunction(Permissions.CREATE))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.message").value("Permission created successfully."));
    }

    @Test
    @DisplayName("While Super-Admin put update permission")
    void testUpdatePermission() throws Exception {

        when(permissionService.updatePermissionById(eq(1L), any(CreatePermissionRequestModel.class))).thenReturn(ResponseEntity.ok(Map.of(
                        "message", "Permission updated successfully." )));

        mockMvc.perform(put(API_SUPER_ADMIN + PERMISSION + UPDATE + "/1").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(preparePermissionRequestFunction(Permissions.UPDATE))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.message").value("Permission updated successfully."));
    }

    @Test
    @DisplayName("While Super-Admin get all permissions")
    void testGetAllPermission() throws Exception {

        when(permissionService.getAllPermission(any(Pageable.class), any())).thenReturn(ResponseEntity.ok(Map.of("data", List.of())));

        mockMvc.perform(get(API_SUPER_ADMIN + PERMISSION + GET)).andExpect(status().isOk()).andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("While Super-Admin get permission by Id")
    void testGetPermissionById() throws Exception {

        when(permissionService.getPermissionById(1L)).thenReturn(ResponseEntity.ok(Map.of("data", Map.of("id", 1,"name", Permissions.CREATE.toString()))));

        mockMvc.perform(get(API_SUPER_ADMIN + PERMISSION + GET + "/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value(Permissions.CREATE.toString()));
    }

    @Test
    @DisplayName("While Super-Admin should delete permission by id")
    void testDeletePermission() throws Exception {

        when(permissionService.deletePermission(1L)).thenReturn(ResponseEntity.ok(Map.of("message", "Permission deleted successfully.")));

        mockMvc.perform(delete(API_SUPER_ADMIN + PERMISSION + DELETE + "/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Permission deleted successfully."));
    }


    @AfterAll
    static void tearDown() {
        factory.close();
    }

}
