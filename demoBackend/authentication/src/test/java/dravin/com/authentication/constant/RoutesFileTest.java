package dravin.com.authentication.constant;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

public class RoutesFileTest {

    @Test
    void shouldVerifyApiConstants() {
        assertEquals("/api", RoutesFile.API);
        assertEquals("/api/auth", RoutesFile.API_AUTH);
        assertEquals("/api/super/admin", RoutesFile.API_SUPER_ADMIN);
    }

    @Test
    void shouldVerifyNounConstants() {
        assertEquals("/role", RoutesFile.ROLE);
        assertEquals("/permission", RoutesFile.PERMISSION);
        assertEquals("/myProfile", RoutesFile.MY_PROFILE);
        assertEquals("/getMyImage", RoutesFile.GET_MY_IMAGE);
        assertEquals("/user", RoutesFile.USER);
        assertEquals("/{id}", RoutesFile.ID);
    }

    @Test
    void shouldVerifyVerbConstants() {
        assertEquals("/signIn", RoutesFile.SIGN_IN);
        assertEquals("/signUp", RoutesFile.SIGN_UP);
        assertEquals("/logout", RoutesFile.LOGOUT);
        assertEquals("/create", RoutesFile.CREATE);
        assertEquals("/update", RoutesFile.UPDATE);
        assertEquals("/get", RoutesFile.GET);
        assertEquals("/delete", RoutesFile.DELETE);
        assertEquals("/activateDeactivate", RoutesFile.ACTIVE_DEACTIVATE);
    }

    @Test
    void shouldThrowExceptionWhenConstructorInvoked() throws Exception {

        Constructor<RoutesFile> constructor = RoutesFile.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        InvocationTargetException exception = assertThrows(InvocationTargetException.class, constructor::newInstance);

        assertTrue(exception.getCause() instanceof IllegalStateException);
        assertEquals("Utility class", exception.getCause().getMessage());
    }
}
