package dravin.com.restApi.constant;

public class Error {

    private Error() {
        throw new IllegalStateException("Utility class");
    }


    public static final String USER_NOT_FOUND = "User not found";
    public static final String DATA_NOT_FOUND = "Data not found";
}
