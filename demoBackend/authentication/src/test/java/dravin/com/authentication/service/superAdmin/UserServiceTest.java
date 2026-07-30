package dravin.com.authentication.service.superAdmin;

import dravin.com.authentication.requestmodel.superAdmin.ActivateRequestModel;
import dravin.com.repository.constant.enumConstant.Status;
import dravin.com.repository.entity.UserEntity;
import dravin.com.repository.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static dravin.com.authentication.constant.ConstantString.USER_DELETED_SUCCESSFULLY;
import static dravin.com.authentication.constant.Error.USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.AssertionsKt.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("While Admin-user should get all user and success")
    void testGetAllUserSuccess() {

        List<UserEntity> users = List.of(prepareUserRequestFunction());
        when(userRepository.findByDeletedAtIsNull()).thenReturn(users);

        ResponseEntity<Map<String, Object>> response = userService.getAllUser();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(users, response.getBody().get("data"));

        verify(userRepository).findByDeletedAtIsNull();
    }

    @Test
    @DisplayName("While Admin-user should delete user by Id and success")
    void testDeleteUserByIdSuccess() {

        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(prepareUserRequestFunction()));

        ResponseEntity<Map<String, String>> response = userService.deleteUserById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(USER_DELETED_SUCCESSFULLY, response.getBody().get("message"));

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);

        verify(userRepository).save(captor.capture());
        UserEntity savedUser = captor.getValue();

        assertNotNull(savedUser.getDeletedAt());
    }

    @Test
    @DisplayName("While Admin-user should delete user by Id and user not found")
    void testDeleteUserByIdNotFound() {

        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.empty());

        ResponseEntity<Map<String, String>> response = userService.deleteUserById(1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(USER_NOT_FOUND, response.getBody().get("message"));

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("While Admin-user should active and deactivate user by Id and activate success")
    void testActivateUserSuccess() {

        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(prepareUserRequestFunction()));

        ResponseEntity<Map<String, String>> response = userService.activeAndDeactivateUserByID(prepareActiveRequestFunction(Status.ENABLE));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User enable successfully.", response.getBody().get("message"));

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);

        verify(userRepository).save(captor.capture());

        assertEquals( Status.ENABLE, captor.getValue().getActive());
    }

    @Test
    @DisplayName("While Admin-user should active and deactivate user by id and deactivate success")
    void testDeactivateUserSuccess() {

        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(prepareUserRequestFunction()));

        ResponseEntity<Map<String, String>> response = userService.activeAndDeactivateUserByID(prepareActiveRequestFunction(Status.DISABLE));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User disable successfully.", response.getBody().get("message"));

        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("While Admin-user should active and deactivate user by id and user not found")
    void testActivateDeactivateUserNotFound() {

        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.empty());

        ResponseEntity<Map<String, String>> response = userService.activeAndDeactivateUserByID(prepareActiveRequestFunction(Status.ENABLE));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(USER_NOT_FOUND, response.getBody().get("message"));

        verify(userRepository, never()).save(any());
    }

    private UserEntity prepareUserRequestFunction() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setActive(Status.ENABLE);
        return userEntity;
    }

    private ActivateRequestModel prepareActiveRequestFunction(Status statusName){
        ActivateRequestModel request = new ActivateRequestModel();
        ReflectionTestUtils.setField(request, "id", 1L);
        ReflectionTestUtils.setField(request, "status", statusName);
        return request;
    }

}
