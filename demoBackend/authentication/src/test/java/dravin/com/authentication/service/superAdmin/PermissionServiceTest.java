package dravin.com.authentication.service.superAdmin;

import dravin.com.authentication.requestmodel.superAdmin.CreatePermissionRequestModel;
import dravin.com.repository.constant.enumConstant.Permissions;
import dravin.com.repository.entity.PermissionEntity;
import dravin.com.repository.repository.PermissionRepository;
import dravin.com.repository.repository.RoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static dravin.com.authentication.constant.ConstantString.*;
import static dravin.com.authentication.constant.Error.PERMISSION_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PermissionServiceTest {

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private PermissionService permissionService;

    @Test
    @DisplayName("While Admin-User Should pass when both fields are valid")
    void authenticateUserShouldReturnJwtToken() {

        when(permissionRepository.findByNameAndDeletedAtIsNull(Permissions.ALL)).thenReturn(Optional.empty());
        ResponseEntity<Map<String,String>> response = permissionService.createPermission(prepareCreatePermissionRequestFunction(Permissions.ALL));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(PERMISSION_CREATED_SUCCESSFULLY, response.getBody().get("message"));

        verify(permissionRepository).save(any(PermissionEntity.class));
    }

    @Test
    @DisplayName("While Admin-User Should create permission but permission already exists")
    void testCreatePermissionAlreadyExists() {

        PermissionEntity permission = new PermissionEntity();
        permission.setName(Permissions.ALL);

        when(permissionRepository.findByNameAndDeletedAtIsNull(Permissions.ALL)).thenReturn(Optional.of(permission));

        ResponseEntity<Map<String, String>> response = permissionService.createPermission(prepareCreatePermissionRequestFunction(Permissions.ALL));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(PERMISSION_ALREADY_EXISTS, response.getBody().get("message"));

        verify(permissionRepository, never()).save(any());
    }

    @Test
    @DisplayName("While Admin-User Should update permission by Id but permission not found")
    void testUpdatePermissionNotFound() {

        when(permissionRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.empty());
        ResponseEntity<Map<String, String>> response = permissionService.updatePermissionById(1L, prepareCreatePermissionRequestFunction(Permissions.ALL));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(PERMISSION_NOT_FOUND, response.getBody().get("message"));

        verify(permissionRepository, never()).save(any());
    }

    @Test
    @DisplayName("While Admin-User Should update permission by Id but Duplicate Permission not allowed")
    void testUpdatePermissionDuplicate() {

        PermissionEntity existing = new PermissionEntity();
        existing.setId(1L);

        PermissionEntity duplicate = new PermissionEntity();
        duplicate.setId(2L);

        when(permissionRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(existing));
        when(permissionRepository.findByNameAndDeletedAtIsNull(Permissions.ALL)).thenReturn(Optional.of(duplicate));
        ResponseEntity<Map<String, String>> response = permissionService.updatePermissionById(1L, prepareCreatePermissionRequestFunction(Permissions.ALL));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(PERMISSION_ALREADY_EXISTS, response.getBody().get("message"));

        verify(permissionRepository, never()).save(any());
    }

    @Test
    @DisplayName("While Admin-User Should update permission by Id and Success")
    void testUpdatePermissionSuccess() {

        PermissionEntity permission = new PermissionEntity();
        permission.setId(1L);
        permission.setName(Permissions.UPDATE);

        when(permissionRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(permission));
        when(permissionRepository.findByNameAndDeletedAtIsNull(Permissions.ALL)).thenReturn(Optional.empty());
        ResponseEntity<Map<String, String>> response = permissionService.updatePermissionById(1L, prepareCreatePermissionRequestFunction(Permissions.ALL));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(PERMISSION_UPDATED_SUCCESSFULLY, response.getBody().get("message"));

        assertEquals(Permissions.ALL, permission.getName());

        verify(permissionRepository).save(permission);
    }

    @Test
    @DisplayName("While Admin-User Should get all permission list")
    void testGetAllPermission() {

        List<PermissionEntity> list = List.of( new PermissionEntity(Permissions.ALL), new PermissionEntity(Permissions.CREATE), new PermissionEntity(Permissions.DELETE));
        Page<PermissionEntity> page = new PageImpl<>(list, PageRequest.of(0, 10), list.size());

        when(permissionRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        ResponseEntity<Map<String, Object>> response = permissionService.getAllPermission(PageRequest.of(0, 10), null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(list, response.getBody().get("data"));
        assertEquals(3, response.getBody().get("pageSize"));
        assertEquals(3L, response.getBody().get("getTotalElements"));
    }


    @Test
    @DisplayName("While Admin-User Should get permission by Id and Success")
    void testGetPermissionByIdSuccess() {

        PermissionEntity permission = new PermissionEntity();
        permission.setId(1L);
        permission.setName(Permissions.UPDATE);

        when(permissionRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(permission));

        ResponseEntity<Map<String, Object>> response = permissionService.getPermissionById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(permission, response.getBody().get("data"));
    }

    @Test
    @DisplayName("While Admin-User Should get permission by Id and permission Not Found")
    void testGetPermissionByIdNotFound() {

        when(permissionRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.empty());
        NullPointerException ex = assertThrows( NullPointerException.class, () -> permissionService.getPermissionById(1L));

        assertEquals(PERMISSION_NOT_FOUND, ex.getMessage());
    }

    @Test
    @DisplayName("While Admin-User Should delete permission but permission not found")
    void testDeletePermissionNotFound() {

        when(permissionRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.empty());

        ResponseEntity<Map<String, String>> response = permissionService.deletePermission(1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(PERMISSION_NOT_FOUND, response.getBody().get("message"));
    }

    @Test
    @DisplayName("While Admin-User Should delete permission by ID and assigned to role")
    void testDeletePermissionAssignedRole() {

        PermissionEntity permission = new PermissionEntity();
        permission.setId(1L);

        when(permissionRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(permission));
        when(roleRepository.existsByPermissionId(1L)).thenReturn(true);
        ResponseEntity<Map<String, String>> response = permissionService.deletePermission(1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Cannot delete permission because it is currently assigned to one or more roles.", response.getBody().get("error"));

        verify(permissionRepository, never()).save(any());
    }

    @Test
    @DisplayName("While Admin-User Should delete permission by iD and Success")
    void testDeletePermissionSuccess() {

        PermissionEntity permission = new PermissionEntity();
        permission.setId(1L);

        when(permissionRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(permission));
        when(roleRepository.existsByPermissionId(1L)).thenReturn(false);
        ResponseEntity<Map<String, String>> response = permissionService.deletePermission(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(PERMISSION_DELETED_SUCCESSFULLY, response.getBody().get("message"));

        ArgumentCaptor<PermissionEntity> captor = ArgumentCaptor.forClass(PermissionEntity.class);

        verify(permissionRepository).save(captor.capture());
        assertNotNull(captor.getValue().getDeletedAt());
    }

    private CreatePermissionRequestModel prepareCreatePermissionRequestFunction(Permissions permissionName) {
        CreatePermissionRequestModel request = new CreatePermissionRequestModel();
        ReflectionTestUtils.setField(request, "name", permissionName);
        return request;
    }




}
