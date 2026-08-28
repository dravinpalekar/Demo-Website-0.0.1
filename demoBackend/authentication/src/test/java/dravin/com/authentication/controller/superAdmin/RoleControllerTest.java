package dravin.com.authentication.controller.superAdmin;

import dravin.com.authentication.requestmodel.superAdmin.CreateRoleRequestModel;
import dravin.com.authentication.service.superAdmin.RoleService;
import dravin.com.repository.constant.enumConstant.Permissions;
import dravin.com.repository.constant.enumConstant.Roles;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.MediaType;
import static dravin.com.authentication.constant.RoutesFile.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class RoleControllerTest {

    private static Validator validator;
    private static ValidatorFactory factory;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @MockitoBean
    private RoleService roleService;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("While Super-Admin valid request should have no validation errors")
    void testValidRequest() {

        Set<ConstraintViolation<CreateRoleRequestModel>> violations = validator
                .validate(prepareCreateRoleRequestFunction(Roles.ROLE_ADMIN, Permissions.ALL));
        assertEquals(0, violations.size());
    }

    @Test
    @DisplayName("While Super-Admin null fields should have no validation errors because no constraints are defined")
    void testNullFields() {

        Set<ConstraintViolation<CreateRoleRequestModel>> violations = validator.validate(new CreateRoleRequestModel());
        assertEquals(2, violations.size());
    }

    private CreateRoleRequestModel prepareCreateRoleRequestFunction(Roles roleName, Permissions permissionName) {
        CreateRoleRequestModel request = new CreateRoleRequestModel();
        ReflectionTestUtils.setField(request, "roleName", roleName);
        ReflectionTestUtils.setField(request, "permissionsName", permissionName);
        return request;
    }

    @Test
    @DisplayName("While Super-Admin should request post /create - create role")
    void testCreateRole() throws Exception {

        when(roleService.createRole(any(CreateRoleRequestModel.class)))
                .thenReturn(ResponseEntity.ok(Map.of("message", "Role created successfully.")));

        mockMvc.perform(post(API_SUPER_ADMIN + ROLE + CREATE).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper
                        .writeValueAsString(prepareCreateRoleRequestFunction(Roles.ROLE_ADMIN, Permissions.ALL))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Role created successfully."));

        verify(roleService).createRole(any(CreateRoleRequestModel.class));
    }

    @Test
    @DisplayName("While Super-Admin should request put /update/{id} - update role")
    void testUpdateRole() throws Exception {

        when(roleService.updateRoleById(eq(1L), any(CreateRoleRequestModel.class)))
                .thenReturn(ResponseEntity.ok(Map.of("message", "Role updated successfully.")));

        mockMvc.perform(put(API_SUPER_ADMIN + ROLE + UPDATE + "/1").contentType(MediaType.APPLICATION_JSON).content(
                objectMapper.writeValueAsString(prepareCreateRoleRequestFunction(Roles.ROLE_ADMIN, Permissions.ALL))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.message").value("Role updated successfully."));

        verify(roleService).updateRoleById(eq(1L), any(CreateRoleRequestModel.class));
    }

    @Test
    @DisplayName("While Super-Admin should request get data - get all roles")
    void testGetAllRoles() throws Exception {

        when(roleService.getAllRoles(any(Pageable.class), any(), any())).thenReturn(ResponseEntity.ok(Map.of("data", List.of())));
        mockMvc.perform(get(API_SUPER_ADMIN + ROLE + GET)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());

        verify(roleService).getAllRoles(any(Pageable.class), any(), any());
    }

    @Test
    @DisplayName("While Super-Admin should request get data /get/{id} - get role by Id")
    void testGetRoleById() throws Exception {

        when(roleService.getRoleById(1L)).thenReturn(ResponseEntity.ok(Map.of("data", Map.of("id", 1))));
        mockMvc.perform(get(API_SUPER_ADMIN + ROLE + GET + "/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));

        verify(roleService).getRoleById(1L);
    }

    @Test
    @DisplayName("While Super-Admin should request delete data /delete/{id} - delete role")
    void testDeleteRole() throws Exception {

        when(roleService.deleteRole(1L)).thenReturn(ResponseEntity.ok(Map.of("message", "Role deleted successfully.")));
        mockMvc.perform(delete(API_SUPER_ADMIN + ROLE + DELETE + "/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Role deleted successfully."));

        verify(roleService).deleteRole(1L);
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

}
