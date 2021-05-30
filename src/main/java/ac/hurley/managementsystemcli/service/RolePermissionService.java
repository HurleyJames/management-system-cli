package ac.hurley.managementsystemcli.service;

import ac.hurley.managementsystemcli.entitiy.SysRolePermission;
import ac.hurley.managementsystemcli.vo.req.RolePermissionOperationReqVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 角色权限关联 Service 类
 */
public interface RolePermissionService extends IService<SysRolePermission> {

    /**
     * 角色绑定权限
     *
     * @param rolePermissionOperationReqVO
     */
    void addRolePermission(RolePermissionOperationReqVO rolePermissionOperationReqVO);
}
