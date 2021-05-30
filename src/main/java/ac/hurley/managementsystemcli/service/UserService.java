package ac.hurley.managementsystemcli.service;

import ac.hurley.managementsystemcli.entitiy.SysDept;
import ac.hurley.managementsystemcli.entitiy.SysUser;
import ac.hurley.managementsystemcli.vo.res.LoginResVO;
import ac.hurley.managementsystemcli.vo.res.UserRoleResVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户 Service 类
 */
public interface UserService extends IService<SysUser> {

    /**
     * 注册
     *
     * @param sysUser
     */
    void register(SysUser sysUser);

    /**
     * 登录
     *
     * @param sysUser
     * @return
     */
    LoginResVO login(SysUser sysUser);

    /**
     * 更新用户信息
     *
     * @param sysUser
     */
    void updateUserInfo(SysUser sysUser);

    /**
     * 分页
     *
     * @param sysUser
     * @return
     */
    IPage<SysUser> pageInfo(SysUser sysUser);

    /**
     * 添加用户
     *
     * @param sysUser
     */
    void addUser(SysUser sysUser);

    /**
     * 删除用户
     *
     * @param userId
     */
    void deleteUser(String userId);

    /**
     * 修改密码
     *
     * @param sysUser
     */
    void updatePwd(SysUser sysUser);

    /**
     * 根据 UserId 获取绑定的角色
     *
     * @param userId
     * @return
     */
    UserRoleResVO getUserRole(String userId);

    /**
     * 修改自己的信息
     *
     * @param sysUser
     */
    void updateMyInfo(SysUser sysUser);
}
