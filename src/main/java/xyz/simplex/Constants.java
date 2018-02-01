package xyz.simplex;

/**
 * Constant class.
 *
 * @author marimpietri
 */
public final class Constants {

    private Constants() {}

    /** API Route */
    public static final String API_GET = "/knapsack/tasks/:taskId";
    public static final String API_LIST_ALL = "/knapsack/admin/tasks/";
    public static final String API_GET_SOLUTION = "/knapsack/solutions/:taskId";
    public static final String API_CREATE = "/knapsack/tasks/";
    public static final String API_UPDATE = "/knapsack/tasks/:taskId";
    public static final String API_DELETE = "/knapsack/tasks/:taskId";
    public static final String API_DELETE_ALL = "/knapsack/tasks/";
    public static final String API_SHUTDOWN = "/knapsack/admin/shutdown";

    /** Persistence key */
    public static final String REDIS_TASK_KEY = "VERT_TASK";
    public static final String REDIS_SOLUTION_KEY = "VERT_PROBLEM";

}
