package dravin.com.authentication.constant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.*;

public class ErrorTest {

    @Test
    @DisplayName("Constants should hold expected String values")
    void constantsShouldHaveCorrectValues() {
        assertThat(Error.ROLE_NOT_FOUND).isEqualTo(" Role is not found.");
        assertThat(Error.PERMISSION_NOT_FOUND).isEqualTo(" Permission is not found.");
        assertThat(Error.USER_NOT_FOUND).isEqualTo("User not found");
        assertThat(Error.DATA_NOT_FOUND).isEqualTo("Data not found");
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
