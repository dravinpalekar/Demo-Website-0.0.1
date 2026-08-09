package dravin.com.userApi.constant;

public class Error {

    private Error() {
        throw new IllegalStateException("Utility class");
    }


    public static final String USER_NOT_FOUND = "User not found";
    public static final String FRIEND_REQUEST_ALREADY_SENT = "Friend request already sent.";
    public static final String DATA_NOT_FOUND = "Data not found";
}
