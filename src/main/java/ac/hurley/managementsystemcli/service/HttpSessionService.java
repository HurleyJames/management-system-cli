package ac.hurley.managementsystemcli.service;

import ac.hurley.managementsystemcli.common.Constant;
import ac.hurley.managementsystemcli.entitiy.SysUser;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Http Session 管理 Service 类
 */
@Service
public class HttpSessionService {

    @Resource
    private RedisService redisService;
    @Resource
    private UserRoleService userRoleService;
    @Resource
    private RolePermissionService rolePermissionService;
    @Resource
    private HttpServletRequest httpServletRequest;
    @Resource
    private PermissionService permissionService;
    @Resource
    private RoleService roleService;

    @Value("${spring.redis.key.prefix.userToken}")
    private String userTokenPrefix;
    @Value("${spring.redis.key.expire.userToken}")
    private int expire;

    @Value("${spring.redis.key.prefix.permissionRefresh}")
    private String redisPermissionRefreshKey;
    @Value("${spring.redis.key.expire.permissionRefresh}")
    private Long redisPermissionRefreshExpire;

    /**
     * 创建用户的 Token 并存入 Session
     *
     * @param sysUser
     * @param roles
     * @param permissions
     * @return
     */
    public String createTokenAndUser(SysUser sysUser, List<String> roles, Set<String> permissions) {
        // 方便根据 id 找到 redis 的 key，修改密码、退出登录
        String token = getRandomToken() + "#" + sysUser.getId();
        JSONObject sessionInfo = new JSONObject();
        sessionInfo.put(Constant.USER_ID_KEY, sysUser.getId());
        sessionInfo.put(Constant.USERNAME_KEY, sysUser.getUsername());
        sessionInfo.put(Constant.ROLES_KEY, roles);
        sessionInfo.put(Constant.PERMISSIONS_KEY, permissions);

        String key = userTokenPrefix + token;
        // 设置该用户已登录的 Token
        redisService.setAndExpire(key, sessionInfo.toJSONString(), expire);

        // 登录后删除权限刷新标志
        redisService.del(redisPermissionRefreshKey + sysUser.getId());
        return token;
    }

    /**
     * 根据 Token 获取 userId
     *
     * @param token
     * @return
     */
    public static String getUserIdByToken(String token) {
        if (StringUtils.isBlank(token) || !token.contains("#")) {
            return "";
        } else {
            return token.substring(token.indexOf("#") + 1);
        }
    }

    /**
     * 使某个用户的 Token 失效
     *
     * @param userId
     */
    public void abortUserByUserId(String userId) {
        redisService.delKeys(userTokenPrefix + "*#" + userId);
    }

    /**
     * 使多个用户的 Token 集体失效
     *
     * @param userIds
     */
    public void abortUserByUserIds(List<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            for (String id : userIds) {
                redisService.delKeys(userTokenPrefix + "*#" + id);
            }
        }
    }

    /**
     * 使所有用户的 Token 失效
     */
    public void abortAllUserByToken() {
        String token = getTokenFromHeader();
        String userId = getUserIdByToken(token);
        redisService.delKeys(userTokenPrefix + "*#" + userId);
    }

    /**
     * 根据 userId，刷新 redis 用户权限
     *
     * @param userId
     */
    public void refreshUserId(String userId) {
        Set<String> keys = redisService.keys("#" + userId);
        // 如果修改了角色权限，那么就刷新权限
        for (String key : keys) {
            JSONObject redisSession = JSON.parseObject(redisService.get(key));

            // 根据 userId 获取角色名
            List<String> roleNames = getRolesByUserId(userId);

            if (!CollectionUtils.isEmpty(roleNames)) {
                redisSession.put(Constant.ROLES_KEY, roleNames);
            }

            // 根据 userId 获取权限
            Set<String> permissions = getPermissionsByUserId(userId);
            redisSession.put(Constant.PERMISSIONS_KEY, permissions);
            Long redisTokenKeyExpire = redisService.getExpire(key);

            // 刷新 token 绑定的角色权限
            redisService.setAndExpire(key, redisSession.toJSONString(), redisTokenKeyExpire);
        }
    }

    /**
     * 根据 roleId，刷新 redis 用户权限
     *
     * @param roleId
     */
    public void refreshRolePermission(String roleId) {
        List<String> userIds = userRoleService.getUserIdsByRoleId(roleId);
        if (!CollectionUtils.isEmpty(userIds)) {
            userIds.parallelStream().forEach(this::refreshUserId);
        }
    }

    /**
     * 根据 permissionId，刷新 redis 用户权限
     *
     * @param permissionId
     */
    public void refreshPermission(String permissionId) {
        List<String> userIds = permissionService.getUserIdsById(permissionId);
        if (!CollectionUtils.isEmpty(userIds)) {
            userIds.parallelStream().forEach(this::refreshUserId);
        }
    }

    /**
     * 生成随机的 Token
     *
     * @return
     */
    private String getRandomToken() {
        Random random = new Random();
        StringBuilder randomStr = new StringBuilder();

        for (int i = 0; i < 32; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";

            // 输出字母还是数字
            if ("char".equalsIgnoreCase(charOrNum)) {
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                randomStr.append((char) (random.nextInt(26) + temp));
            } else {
                randomStr.append(random.nextInt(10));
            }
        }

        return randomStr.toString();
    }

    /**
     * 根据 userId 获取该用户的角色
     *
     * @param userId
     * @return
     */
    private List<String> getRolesByUserId(String userId) {
        return roleService.getRoleNamesByUserId(userId);
    }

    /**
     * 根据 userId 获取该用户的权限
     *
     * @param userId
     * @return
     */
    private Set<String> getPermissionsByUserId(String userId) {
        return permissionService.getPermissionByUserId(userId);
    }

    /**
     * 获取 Header 中的 Token
     *
     * @return
     */
    public String getTokenFromHeader() {
        String token = httpServletRequest.getHeader(Constant.ACCESS_TOKEN);
        if (StringUtils.isBlank(token)) {
            token = httpServletRequest.getParameter(Constant.ACCESS_TOKEN);
        }
        return token;
    }

    /**
     * 获取当前 Session 信息
     *
     * @return
     */
    public JSONObject getCurrentSession() {
        String token = getTokenFromHeader();
        if (token != null) {
            if (redisService.exists(userTokenPrefix + token)) {
                String sessionInfo = redisService.get(userTokenPrefix + token);
                return JSON.parseObject(sessionInfo);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 获取当前 Session 信息里的 Username
     *
     * @return
     */
    public String getCurrentUsername() {
        if (getCurrentSession() != null) {
            return getCurrentSession().getString(Constant.USERNAME_KEY);
        } else {
            return null;
        }
    }

    /**
     * 获取当前 session 信息 UserId
     *
     * @return
     */
    public String getCurrentUserId() {
        if (getCurrentSession() != null) {
            return getCurrentSession().getString(Constant.USER_ID_KEY);
        } else {
            return null;
        }
    }
}
