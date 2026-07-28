package dravin.com.authentication.service.loaduser;

import dravin.com.repository.constant.enumConstant.Status;
import dravin.com.repository.entity.UserEntity;
import dravin.com.repository.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();

        userEntity.setId(1L);
        userEntity.setUserName("admin");
        userEntity.setPassword("password");
        userEntity.setActive(Status.ENABLE);
    }

    @Test
    @DisplayName("Load user by user name should return user details when user exists")
    void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {

        when(userRepository.findByUserNameAndDeletedAtIsNullAndActive("admin", Status.ENABLE)).thenReturn(Optional.of(userEntity));

        UserDetails userDetails = userDetailsService.loadUserByUsername("admin");

        assertNotNull(userDetails);
        assertEquals("admin", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());

        verify(userRepository, times(1)).findByUserNameAndDeletedAtIsNullAndActive("admin", Status.ENABLE);
    }

    @Test
    @DisplayName("Load user by user name should throw exception when user not found")
    void loadUserByUsernameShouldThrowExceptionWhenUserNotFound() {

        when(userRepository.findByUserNameAndDeletedAtIsNullAndActive("admin", Status.ENABLE)).thenReturn(Optional.empty());
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,() -> userDetailsService.loadUserByUsername("admin"));

        assertEquals("User Not Found with username: admin", exception.getMessage());

        verify(userRepository, times(1)).findByUserNameAndDeletedAtIsNullAndActive("admin", Status.ENABLE);
    }
}
