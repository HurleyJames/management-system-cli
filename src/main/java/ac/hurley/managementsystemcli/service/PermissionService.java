package ac.hurley.managementsystemcli.service;

import ac.hurley.managementsystemcli.entitiy.SysPermission;
import ac.hurley.managementsystemcli.vo.res.PermissionResVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Set;

/**
 * 菜单权限 Service 类
 */
public interface PermissionService extends IService<SysPermission> {

    /**
     * 根据 userId 获取权限
     *
     * @param userId
     * @return
     */
    List<SysPermission> getPermission(String userId);

    /**
     * 删除权限
     *
     * @param permissionId
     */
    void deleted(String permissionId);

    /**
     * 获取所有权限
     *
     * @return
     */
    List<SysPermission> selectAll();

    /**
     * 根据 userId 获取对应的权限
     *
     * @param userId
     * @return
     */
    Set<String> getPermissionByUserId(String userId);

    /**
     * 根据 userId 获取权限树
     *
     * @param userId
     * @return
     */
    List<PermissionResVO> permissionTreeList(String userId);

    /**
     * 根据权限树选择所有
     *
     * @return
     */
    List<PermissionResVO> selectAllByTree();

    /**
     * 根据目录树选择所有
     *
     * @param permissionId
     * @return
     */
    List<PermissionResVO> selectAllMenuByTree(String permissionId);

    /**
     * 根据权限 id 获取所绑定的所有 userIds
     *
     * @param permissionId
     * @return
     */
    List<String> getUserIdsById(String permissionId);

    /**
     * 更新权限
     *
     * @param sysPermission
     */
    void updatePermission(SysPermission sysPermission);
}
