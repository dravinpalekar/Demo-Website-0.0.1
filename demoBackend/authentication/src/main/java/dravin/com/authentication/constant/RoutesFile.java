package dravin.com.authentication.constant;

public class RoutesFile {

    private RoutesFile() {
        throw new IllegalStateException("Utility class");
    }

    public static final String API = "/api";

    // All nouns related string

    public static final String API_AUTH = API + "/auth";
    public static final String API_SUPER_ADMIN = API + "/super/admin";
    public static final String ROLE = "/role";
    public static final String PERMISSION = "/permission";
    public static final String MY_PROFILE = "/myProfile";
    public static final String GET_MY_IMAGE = "/getMyImage";
    public static final String USER = "/user";

    public static final String ID = "/{id}";

    // All verbs related string
    public static final String SIGN_IN = "/signIn";
    public static final String SIGN_UP = "/signUp";
    public static final String LOGOUT = "/logout";
    public static final String REFRESH_TOKEN = "/refreshToken";
    public static final String CREATE = "/create";
    public static final String UPDATE = "/update";
    public static final String GET = "/get";
    public static final String DELETE = "/delete";
    public static final String ACTIVE_DEACTIVATE = "/activateDeactivate";
}
