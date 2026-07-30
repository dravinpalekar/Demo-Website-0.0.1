package dravin.com.authentication.constant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ConstantStringTest {

    @Test
    @DisplayName("Constants should hold expected String values")
    void constantsShouldHaveCorrectValues() {
        // Headers & Keys
        assertThat(ConstantString.MESSAGE).isEqualTo("message");
        assertThat(ConstantString.AUTHORIZATION).isEqualTo("Authorization");
        assertThat(ConstantString.BEARER).isEqualTo("Bearer");

        // User messages
        assertThat(ConstantString.USER_REGISTERED_SUCCESSFULLY).isEqualTo("User registered successfully.");
        assertThat(ConstantString.SUPER_ADMIN_IS_ALREADY_EXISTS).isEqualTo("Super Admin is already exists.");
        assertThat(ConstantString.USER_DELETED_SUCCESSFULLY).isEqualTo("User deleted successfully.");

        // Role messages
        assertThat(ConstantString.ROLE_ALREADY_EXISTS).isEqualTo("Role already exists.");
        assertThat(ConstantString.ROLE_CREATED_SUCCESSFULLY).isEqualTo("Role created successfully.");
        assertThat(ConstantString.ROLE_UPDATED_SUCCESSFULLY).isEqualTo("Role updated successfully.");
        assertThat(ConstantString.ROLE_DELETED_SUCCESSFULLY).isEqualTo("Role deleted successfully.");

        // Permission messages
        assertThat(ConstantString.PERMISSION_ALREADY_EXISTS).isEqualTo("Permission already exists.");
        assertThat(ConstantString.PERMISSION_CREATED_SUCCESSFULLY).isEqualTo("Permission created successfully.");
        assertThat(ConstantString.PERMISSION_UPDATED_SUCCESSFULLY).isEqualTo("Permission updated successfully.");
        assertThat(ConstantString.PERMISSION_DELETED_SUCCESSFULLY).isEqualTo("Permission deleted successfully.");

        // Profile messages
        assertThat(ConstantString.PROFILE_UPDATED_SUCCESSFULLY).isEqualTo("Profile updated successfully.");
    }

    @Test
    @DisplayName("Private constructor should throw IllegalStateException when invoked via reflection")
    void privateConstructorShouldThrowException() throws NoSuchMethodException {

        Constructor<ConstantString> constructor = ConstantString.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThatThrownBy(constructor::newInstance).isInstanceOf(InvocationTargetException.class).hasCauseInstanceOf(IllegalStateException.class)
                .hasRootCauseMessage("Utility class");
    }
}
