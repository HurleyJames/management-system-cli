package ac.hurley.managementsystemcli.service;

import ac.hurley.managementsystemcli.entitiy.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 角色 Service 类
 *
 * @author hurley
 */
public interface RoleService extends IService<SysRole> {

    /**
     * 添加角色
     *
     * @param sysRole
     */
    void addRole(SysRole sysRole);

    /**
     * 更新角色
     *
     * @param sysRole
     */
    void updateRole(SysRole sysRole);

    /**
     * 根据 id 获取角色的详情
     *
     * @param id
     * @return
     */
    SysRole getRoleInfo(String id);

    /**
     * 根据 id 删除
     *
     * @param id
     */
    void deleteRole(String id);

    /**
     * 根据 userId 获取绑定的角色
     *
     * @param userId
     * @return
     */
    List<SysRole> getRoleInfoByUserId(String userId);

    /**
     * 根据 userId 获取绑定的角色名
     *
     * @param userId
     * @return
     */
    List<String> getRoleNamesByUserId(String userId);
}
