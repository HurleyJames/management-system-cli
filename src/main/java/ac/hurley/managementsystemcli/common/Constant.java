package ac.hurley.managementsystemcli.common;

/**
 * 静态常量类
 */
public class Constant {

    /**
     * Redis 相关 Token
     */
    public static final String ACCESS_TOKEN = "authorization";
    public static final String PERMISSIONS_KEY = "permissions-key";
    public static final String USER_ID_KEY = "user-id-key";
    public static final String USERNAME_KEY = "username-key";
    public static final String ROLES_KEY = "roles-key";

    /**
     * 定时任务的状态
     */
    public static final Integer SCHEDULER_STATUS_NORMAL = 0;
    public static final Integer SCHEDULER_STATUS_PAUSE = 1;

    /**
     * 访问用户的数据范围类型
     * 1：所有
     * 2：自定义
     * 3：本部门以及以下
     * 4：仅本部门
     * 5：自己
     */
    public static final Integer DATA_SCOPE_ALL = 1;
    public static final Integer DATA_SCOPE_CUSTOM = 2;
    public static final Integer DATA_SCOPE_DEPT_AND_CHILD = 3;
    public static final Integer DATA_SCOPE_DEPT = 4;
    public static final Integer DATA_SCOPE_DEPT_SELF = 5;
}
