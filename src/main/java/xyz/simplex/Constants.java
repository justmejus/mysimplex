package xyz.simplex;

/**
 * Constant class.
 *
 * @author <a href="http://www.sczyh30.com">Eric Zhao</a>
 */
public final class Constants {

    private Constants() {}

    /** API Route */
    public static final String API_GET = "/todos/:todoId";
    public static final String API_LIST_ALL = "/todos";
    public static final String API_GET_SOLUTION = "/solutions/:todoId";
    public static final String API_CREATE = "/todos";
    public static final String API_UPDATE = "/todos/:todoId";
    public static final String API_DELETE = "/todos/:todoId";
    public static final String API_DELETE_ALL = "/todos";

    /** Persistence key */
    public static final String REDIS_TASK_KEY = "VERT_TASK";
    public static final String REDIS_SOLUTION_KEY = "VERT_PROBLEM";

}
