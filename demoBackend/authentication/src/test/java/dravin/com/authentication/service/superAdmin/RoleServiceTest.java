package dravin.com.authentication.service.superAdmin;

import dravin.com.authentication.requestmodel.superAdmin.CreateRoleRequestModel;
import dravin.com.repository.constant.enumConstant.Permissions;
import dravin.com.repository.constant.enumConstant.Roles;
import dravin.com.repository.entity.PermissionEntity;
import dravin.com.repository.entity.RoleEntity;
import dravin.com.repository.repository.PermissionRepository;
import dravin.com.repository.repository.RoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static dravin.com.authentication.constant.ConstantString.*;
import static dravin.com.authentication.constant.Error.PERMISSION_NOT_FOUND;
import static dravin.com.authentication.constant.Error.ROLE_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.AssertionsKt.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @Test
    @DisplayName("While Admin-user should create role and success")
    void testCreateRoleSuccess() {

        when(roleRepository.findByNameAndDeletedAtIsNull(Roles.ROLE_ADMIN)).thenReturn(Optional.empty());
        when(permissionRepository.findByNameAndDeletedAtIsNull(Permissions.ALL)).thenReturn(Optional.of(new PermissionEntity(Permissions.ALL)));

        ResponseEntity<Map<String, String>> response = roleService.createRole(prepareCreateRoleRequestFunction(Roles.ROLE_ADMIN, Permissions.ALL));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ROLE_CREATED_SUCCESSFULLY, response.getBody().get("message"));

        verify(roleRepository).save(any(RoleEntity.class));
    }

    @Test
    @DisplayName("While Admin-user should create role and role already exists")
    void testCreateRoleAlreadyExists() {

        when(roleRepository.findByNameAndDeletedAtIsNull(Roles.ROLE_ADMIN)).thenReturn(Optional.of(prepareRoleEntityRequestFunction()));

        ResponseEntity<Map<String, String>> response = roleService.createRole(prepareCreateRoleRequestFunction(Roles.ROLE_ADMIN, Permissions.ALL));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(ROLE_ALREADY_EXISTS, response.getBody().get("message"));

        verify(roleRepository, never()).save(any());
    }

    @Test
    @DisplayName("While Admin-user should create role and permission not found")
    void testCreateRolePermissionNotFound() {

        when(roleRepository.findByNameAndDeletedAtIsNull(Roles.ROLE_ADMIN)).thenReturn(Optional.empty());
        when(permissionRepository.findByNameAndDeletedAtIsNull(Permissions.ALL)).thenReturn(Optional.empty());

        NullPointerException ex = assertThrows(NullPointerException.class, () -> roleService.createRole(prepareCreateRoleRequestFunction(Roles.ROLE_ADMIN, Permissions.ALL)));

        assertEquals(PERMISSION_NOT_FOUND, ex.getMessage());
    }

    @Test
    @DisplayName("While Admin-user should update role by Id and Success")
    void testUpdateRoleSuccess() {

        RoleEntity roleEntity = prepareRoleEntityRequestFunction();

        when(roleRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(roleEntity));
        when(roleRepository.findByNameAndDeletedAtIsNull(Roles.ROLE_ADMIN)).thenReturn(Optional.empty());
        when(permissionRepository.findByNameAndDeletedAtIsNull(Permissions.ALL)).thenReturn(Optional.of(new PermissionEntity(Permissions.ALL)));

        ResponseEntity<Map<String,String>> response = roleService.updateRoleById(1L, prepareCreateRoleRequestFunction(Roles.ROLE_ADMIN, Permissions.ALL));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ROLE_UPDATED_SUCCESSFULLY, response.getBody().get("message"));

        verify(roleRepository).save(roleEntity);
    }

    @Test
    @DisplayName("While Admin-user should update role by Id and  role not found")
    void testUpdateRoleNotFound() {

        when(roleRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.empty());
        ResponseEntity<Map<String,String>> response = roleService.updateRoleById(1L, prepareCreateRoleRequestFunction(Roles.ROLE_ADMIN, Permissions.ALL));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(ROLE_NOT_FOUND, response.getBody().get("message"));
    }

    @Test
    @DisplayName("While Admin-user should update role by Id and duplicate role not allow")
    void testUpdateRoleDuplicate() {

        RoleEntity duplicate = new RoleEntity();

        when(roleRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(prepareRoleEntityRequestFunction()));
        when(roleRepository.findByNameAndDeletedAtIsNull(Roles.ROLE_ADMIN)).thenReturn(Optional.of(duplicate));

        ResponseEntity<Map<String,String>> response = roleService.updateRoleById(1L, prepareCreateRoleRequestFunction(Roles.ROLE_ADMIN, Permissions.ALL));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(ROLE_ALREADY_EXISTS, response.getBody().get("message"));

        verify(roleRepository, never()).save(any());
    }

    @Test
    @DisplayName("While Admin-user should update role by Id and permission not found")
    void testUpdateRolePermissionNotFound() {

        when(roleRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(prepareRoleEntityRequestFunction()));
        when(roleRepository.findByNameAndDeletedAtIsNull(Roles.ROLE_ADMIN)).thenReturn(Optional.empty());
        when(permissionRepository.findByNameAndDeletedAtIsNull(Permissions.ALL)).thenReturn(Optional.empty());

        NullPointerException ex = assertThrows(NullPointerException.class,
                () -> roleService.updateRoleById(1L, prepareCreateRoleRequestFunction(Roles.ROLE_ADMIN, Permissions.ALL)));

        assertEquals(PERMISSION_NOT_FOUND, ex.getMessage());
    }

    @Test
    @DisplayName("While Admin-user should get all roles")
    void testGetAllRoles() {

        List<RoleEntity> roles = List.of(prepareRoleEntityRequestFunction());

        when(roleRepository.findByDeletedAtIsNull()).thenReturn(roles);
        ResponseEntity<Map<String,Object>> response = roleService.getAllRoles();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(roles, response.getBody().get("data"));
    }

    @Test
    @DisplayName("While Admin-user should get role by Id and success")
    void testGetRoleByIdSuccess() {

        RoleEntity roleEntity = prepareRoleEntityRequestFunction();
        when(roleRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(roleEntity));

        ResponseEntity<Map<String,Object>> response = roleService.getRoleById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(roleEntity, response.getBody().get("data"));
    }

    @Test
    @DisplayName("While Admin-user should get role by Id and not found")
    void testGetRoleByIdNotFound() {

        when(roleRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.empty());
        NullPointerException ex = assertThrows(NullPointerException.class, () -> roleService.getRoleById(1L));

        assertEquals(ROLE_NOT_FOUND, ex.getMessage());
    }

    @Test
    @DisplayName("deleteRole() - Success")
    void testDeleteRoleSuccess() {

        RoleEntity roleEntity = prepareRoleEntityRequestFunction();

        when(roleRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(roleEntity));
        ResponseEntity<Map<String,String>> response = roleService.deleteRole(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ROLE_DELETED_SUCCESSFULLY, response.getBody().get("message"));

        verify(roleRepository).save(roleEntity);
        assertNotNull(roleEntity.getDeletedAt());
    }

    @Test
    @DisplayName("deleteRole() - Not Found")
    void testDeleteRoleNotFound() {

        when(roleRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.empty());
        ResponseEntity<Map<String,String>> response = roleService.deleteRole(1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(ROLE_NOT_FOUND, response.getBody().get("message"));

        verify(roleRepository, never()).save(any());
    }


    private RoleEntity prepareRoleEntityRequestFunction() {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setId(1L);
        roleEntity.setName(Roles.ROLE_ADMIN);
        return roleEntity;
    }

    private CreateRoleRequestModel prepareCreateRoleRequestFunction(Roles roleName, Permissions permissionName) {
        CreateRoleRequestModel request = new CreateRoleRequestModel();
        ReflectionTestUtils.setField(request, "roleName", roleName);
        ReflectionTestUtils.setField(request, "permissionsName", permissionName);
        return request;
    }

}
