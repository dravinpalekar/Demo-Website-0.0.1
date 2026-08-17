package dravin.com.userApi.constant;

public class RoutesFile {

    private RoutesFile() {
        throw new IllegalStateException("Utility class");
    }

    public static final String API = "/api";

    //    All nouns related string

    public static final String GET = "/get";
    public static final String SEND = "send";
    public static final String ACCEPT = "accept";
    public static final String CANCEL = "cancel";
    public static final String CREATE = "/create";
    public static final String MY_PROFILE = "/myProfile";
    public static final String GET_MY_IMAGE = "/getMyImage";

    public static final String API_USER = API + "/user";
    public static final String PEOPLE_GET = "people" + GET;
    public static final String FRIEND_GET = "friendList" + GET;
    public static final String FRIEND_REQUEST_GET = "friendRequestList" + GET;
    public static final String SEND_REQUEST = SEND + "/request";

    public static final String ACCEPT_REQUEST = ACCEPT + "/request";
    public static final String CANCEL_REQUEST = CANCEL + "/request";



}
