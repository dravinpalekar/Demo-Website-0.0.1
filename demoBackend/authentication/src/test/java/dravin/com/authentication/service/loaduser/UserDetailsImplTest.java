package dravin.com.authentication.service.loaduser;

import dravin.com.repository.constant.enumConstant.Roles;
import dravin.com.repository.entity.RoleEntity;
import dravin.com.repository.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserDetailsImplTest {

    @Test
    @DisplayName("Should create UserDetailsImpl using constructor and verify all getter methods")
    void testConstructorAndGetters() {

        UserDetailsImpl userDetails = new UserDetailsImpl(1L,"john","john@test.com","password",List.of());

        assertEquals(1L, userDetails.getId());
        assertEquals("john", userDetails.getUsername());
        assertEquals("john@test.com", userDetails.getEmail());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());
    }

    @Test
    @DisplayName("Should build UserDetailsImpl from UserEntity")
    void testBuild() {

        RoleEntity role = new RoleEntity();
        role.setName(Roles.ROLE_SUPER_ADMIN);

        UserEntity user = new UserEntity();
        user.setId(100L);
        user.setUserName("dravin");
        user.setEmail("dravin@test.com");
        user.setPassword("secret");
        user.setRole(Set.of(role));

        UserDetailsImpl details = UserDetailsImpl.build(user);

        assertEquals(100L, details.getId());
        assertEquals("dravin", details.getUsername());
        assertEquals("dravin@test.com", details.getEmail());
        assertEquals("secret", details.getPassword());

        assertEquals(1, details.getAuthorities().size());

        GrantedAuthority authority = details.getAuthorities().iterator().next();

        assertEquals("ROLE_SUPER_ADMIN", authority.getAuthority());
    }

    @Test
    @DisplayName("Should return true when account is not expired")
    void testAccountNonExpired() {

        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "user", "mail", "pass", List.of());
        assertTrue(userDetails.isAccountNonExpired());
    }

    @Test
    @DisplayName("Should return true when account is not locked")
    void testAccountNonLocked() {

        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "user", "mail", "pass", List.of());
        assertTrue(userDetails.isAccountNonLocked());
    }

    @Test
    @DisplayName("Should return true when credentials are not expired")
    void testCredentialsNonExpired() {

        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "user", "mail", "pass", List.of());
        assertTrue(userDetails.isCredentialsNonExpired());
    }

    @Test
    @DisplayName("Should return true when user is enabled")
    void testEnabled() {

        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "user", "mail", "pass", List.of());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    @DisplayName("Should return true when comparing the same object")
    void testEqualsSameObject() {

        UserDetailsImpl user = new UserDetailsImpl(1L, "user", "mail", "pass", List.of());
        assertEquals(user, user);
    }

    @Test
    @DisplayName("Should return true when two UserDetailsImpl objects have the same id")
    void testEqualsSameId() {

        UserDetailsImpl user1 = new UserDetailsImpl(1L, "user1", "mail1", "pass", List.of());
        UserDetailsImpl user2 = new UserDetailsImpl(1L, "user2", "mail2", "pass", List.of());

        assertEquals(user1, user2);
    }

    @Test
    @DisplayName("Should return false when two UserDetailsImpl objects have different ids")
    void testEqualsDifferentId() {

        UserDetailsImpl user1 = new UserDetailsImpl(1L, "user1", "mail1", "pass", List.of());
        UserDetailsImpl user2 = new UserDetailsImpl(2L, "user2", "mail2", "pass", List.of());

        assertNotEquals(user1, user2);
    }

    @Test
    @DisplayName("Should return false when compared with null")
    void testEqualsNull() {

        UserDetailsImpl user = new UserDetailsImpl(1L, "user", "mail", "pass", List.of());
        assertNotEquals(null, user);
    }

    @Test
    @DisplayName("Should return false when compared with a different class")
    void testEqualsDifferentClass() {

        UserDetailsImpl user = new UserDetailsImpl(1L, "user", "mail", "pass", List.of());
        assertNotEquals("Test", user);
    }

    @Test
    @DisplayName("equals should return false when object is null")
    void testEqualsWithNull() {

        UserDetailsImpl user = new UserDetailsImpl(1L, "user", "mail", "pass", List.of());
        assertFalse(user.equals(null));
    }

    @Test
    @DisplayName("equals should return false when object type is different")
    void testEqualsWithDifferentClass() {

        UserDetailsImpl user = new UserDetailsImpl(1L, "user", "mail", "pass", List.of());
        assertFalse(user.equals(new Object()));
    }
}
