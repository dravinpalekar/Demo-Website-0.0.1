package dravin.com.restApi.constant;

public class RoutesFile {

    private RoutesFile() {
        throw new IllegalStateException("Utility class");
    }

    public static final String API = "/api";

    //    All nouns related string


    public static final String API_USER = API + "/user";
    public static final String CONNECT = "/connect";


    // One To One Messaging Application Destination Prefixes

    public static final String WEBSOCKET_CONNECTION = "/ws";
    public static final String WEBSOCKET_TOPIC = "/topic";
    public static final String WEBSOCKET_PRIVATE = "/private";
    public static final String WEBSOCKET_USER_DESTINATION_PREFIX = "/user";
    public static final String WEBSOCKET_APPLICATION_DESTINATION_PREFIX = "/app";

    public static final String ONE_TO_ONE_SEND_MESSAGE = "oneToOneSendMessage";
    public static final String ONE_TO_ONE_ADD_USER = "oneToOneAddUser";

    public static final String UPLOAD_FILE = "upload/file";
    public static final String CREATE_ROOM = "create/room";

}
