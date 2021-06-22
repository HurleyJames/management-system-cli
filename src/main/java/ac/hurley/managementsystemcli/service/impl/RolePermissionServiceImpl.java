package ac.hurley.managementsystemcli.service.impl;

import ac.hurley.managementsystemcli.entitiy.SysRolePermission;
import ac.hurley.managementsystemcli.mapper.RolePermissionMapper;
import ac.hurley.managementsystemcli.service.RolePermissionService;
import ac.hurley.managementsystemcli.vo.req.RolePermissionOperationReqVO;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色权限关联 Service 实现类
 */
@Service
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, SysRolePermission> implements RolePermissionService {

    /**
     * 添加角色权限
     *
     * @param rolePermissionOperationReqVO
     */
    @Override
    public void addRolePermission(RolePermissionOperationReqVO rolePermissionOperationReqVO) {
        List<SysRolePermission> list = new ArrayList<>();
        for (String permissionId : rolePermissionOperationReqVO.getPermissionIds()) {
            SysRolePermission sysRolePermission = new SysRolePermission();
            sysRolePermission.setPermissionId(permissionId);
            sysRolePermission.setRoleId(rolePermissionOperationReqVO.getRoleId());
            list.add(sysRolePermission);
        }

        this.remove(Wrappers.<SysRolePermission>lambdaQuery()
                .eq(SysRolePermission::getRoleId, rolePermissionOperationReqVO.getRoleId()));
        this.saveBatch(list);
    }
}
