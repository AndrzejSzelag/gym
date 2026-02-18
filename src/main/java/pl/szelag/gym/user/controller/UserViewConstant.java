package pl.szelag.gym.user.controller;

/** UI constants for the User module to prevent magic strings and ensure model key consistency. */
public final class UserViewConstant {

    /** Thymeleaf template name for the administrative users list. */
    public static final String VIEW_USERS_LIST = "users";

    /** Thymeleaf template name for the login page. */
    public static final String VIEW_LOGIN = "login";

    /** Thymeleaf template name for the registration form. */
    public static final String VIEW_REGISTRATION = "user";

    /** Model attribute key for the currently authenticated principal. */
    public static final String ATTR_CURRENT_USER = "currentUser";

    /** Model attribute key for passing email-specific validation or conflict errors. */
    public static final String ATTR_EMAIL_ERROR = "emailError";

    /** Model attribute key for the UserDto used in registration and profile flows. */
    public static final String ATTR_USER_DTO = "user";

    /** Prevents instantiation of this utility class. @throws UnsupportedOperationException always */
    private UserViewConstant() {
        throw new UnsupportedOperationException("Utility class");
    }
}