package dravin.com.authentication.constant;

public class Error {

    private Error() {
        throw new IllegalStateException("Utility class");
    }

    public static final String ROLE_NOT_FOUND = " Role is not found.";
    public static final String PERMISSION_NOT_FOUND = " Permission is not found.";
    public static final String USER_NOT_FOUND = "User not found";

    public static final String DATA_NOT_FOUND = "Data not found";
}
