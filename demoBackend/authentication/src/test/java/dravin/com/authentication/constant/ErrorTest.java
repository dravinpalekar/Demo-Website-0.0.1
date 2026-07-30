package dravin.com.authentication.constant;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

public class ErrorTest {

    @Test
    void shouldVerifyRoleNotFoundMessage() {
        assertEquals(" Role is not found.", Error.ROLE_NOT_FOUND);
    }

    @Test
    void shouldVerifyPermissionNotFoundMessage() {
        assertEquals(" Permission is not found.", Error.PERMISSION_NOT_FOUND);
    }

    @Test
    void shouldThrowExceptionWhenConstructorIsInvoked() throws Exception {
        Constructor<Error> constructor = Error.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        InvocationTargetException exception = assertThrows(InvocationTargetException.class, constructor::newInstance);

        assertInstanceOf(IllegalStateException.class, exception.getCause());
        assertEquals("Utility class", exception.getCause().getMessage());
    }
}
