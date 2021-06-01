package ac.hurley.managementsystemcli.service.impl;

import ac.hurley.managementsystemcli.common.exception.SysException;
import ac.hurley.managementsystemcli.common.exception.code.BaseResCode;
import ac.hurley.managementsystemcli.entitiy.SysRole;
import ac.hurley.managementsystemcli.entitiy.SysRoleDept;
import ac.hurley.managementsystemcli.entitiy.SysRolePermission;
import ac.hurley.managementsystemcli.entitiy.SysUserRole;
import ac.hurley.managementsystemcli.mapper.RoleMapper;
import ac.hurley.managementsystemcli.service.*;
import ac.hurley.managementsystemcli.vo.req.RolePermissionOperationReqVO;
import ac.hurley.managementsystemcli.vo.res.DeptResVO;
import ac.hurley.managementsystemcli.vo.res.PermissionResVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RoleServiceImpl extends ServiceImpl<RoleMapper, SysRole> implements RoleService {

    @Resource
    private RoleMapper roleMapper;
    @Resource
    private UserRoleService userRoleService;
    @Resource
    private RolePermissionService rolePermissionService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private HttpSessionService httpSessionService;
    @Resource
    private DeptService deptService;
    @Resource
    private RoleDeptService roleDeptService;

    /**
     * 添加角色
     *
     * @param sysRole
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addRole(SysRole sysRole) {
        sysRole.setStatus(1);
        // 数据库中添加
        roleMapper.insert(sysRole);
        if (!CollectionUtils.isEmpty(sysRole.getPermissions())) {
            // 设置权限
            RolePermissionOperationReqVO reqVO = new RolePermissionOperationReqVO();
            reqVO.setRoleId(sysRole.getId());
            reqVO.setPermissionIds(sysRole.getPermissions());
            rolePermissionService.addRolePermission(reqVO);
        }
    }

    /**
     * 更新角色
     *
     * @param sysRole
     */
    @Override
    public void updateRole(SysRole sysRole) {
        SysRole sysRole1 = roleMapper.selectById(sysRole.getId());
        if (sysRole == null) {
            log.error("传入的 id:{} 不合法", sysRole.getId());
            throw new SysException(BaseResCode.DATA_ERROR);
        }
        // 数据库更新角色
        roleMapper.updateById(sysRole);
        // 删除角色权限关联
        rolePermissionService.remove(Wrappers.<SysRolePermission>lambdaQuery()
                .eq(SysRolePermission::getRoleId, sysRole1.getId()));
        // 重新设置权限
        if (!CollectionUtils.isEmpty(sysRole.getPermissions())) {
            RolePermissionOperationReqVO reqVO = new RolePermissionOperationReqVO();
            reqVO.setRoleId(sysRole1.getId());
            reqVO.setPermissionIds(sysRole.getPermissions());
            rolePermissionService.addRolePermission(reqVO);
            // 刷新权限
            httpSessionService.refreshRolePermission(sysRole1.getId());
        }
    }

    /**
     * 获取角色的详情信息
     *
     * @param id
     * @return
     */
    @Override
    public SysRole roleInfo(String id) {
        SysRole sysRole = roleMapper.selectById(id);
        if (sysRole == null) {
            log.error("传入的 id:{} 不合法", id);
            throw new SysException(BaseResCode.DATA_ERROR);
        }

        // 获取所有的权限
        List<PermissionResVO> permissionResVOS = permissionService.selectAllByTree();
        // 获取该角色的权限
        LambdaQueryWrapper<SysRolePermission> queryWrapperPermission = Wrappers.<SysRolePermission>lambdaQuery()
                .select(SysRolePermission::getPermissionId)
                .eq(SysRolePermission::getRoleId, sysRole.getId());
        Set<Object> checkPermissionList = new HashSet<>(rolePermissionService.listObjs(queryWrapperPermission));
        setCheckedPermission(permissionResVOS, checkPermissionList);
        sysRole.setPermissionResVOS(permissionResVOS);

        // 获得该角色的部门
        LambdaQueryWrapper<SysRoleDept> queryWrapperDept = Wrappers.<SysRoleDept>lambdaQuery()
                .select(SysRoleDept::getDeptId)
                .eq(SysRoleDept::getRoleId, sysRole.getId());
        List<DeptResVO> deptResVOS = deptService.deptTreeList(null, true);
        Set<Object> checkDeptList = new HashSet<>(roleDeptService.listObjs(queryWrapperDept));
        setCheckedDept(deptResVOS, checkDeptList);
        sysRole.setDeptResVOS(deptResVOS);

        return sysRole;
    }

    /**
     * 删除角色
     *
     * @param id
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteRole(String id) {
        // 获取关联 userId
        List<String> userIds = userRoleService.getUserIdsByRoleId(id);
        // 数据库中删除
        roleMapper.deleteById(id);
        // 角色权限移除
        rolePermissionService.remove(Wrappers.<SysRolePermission>lambdaQuery()
                .eq(SysRolePermission::getRoleId, id));
        // 用户角色移除
        userRoleService.remove(Wrappers.<SysUserRole>lambdaQuery()
                .eq(SysUserRole::getRoleId, id));
        if (!CollectionUtils.isEmpty(userIds)) {
            // 刷新权限
            userIds.parallelStream().forEach(httpSessionService::refreshUserId);
        }
    }

    @Override
    public List<SysRole> getRoleInfoByUserId(String userId) {
        List<String> roldIds = userRoleService.getRoleIdsByUserId(userId);
        if (CollectionUtils.isEmpty(roldIds)) {
            return null;
        }
        return roleMapper.selectBatchIds(roldIds);
    }

    @Override
    public List<String> getRoleNamesByUserId(String userId) {
        List<SysRole> sysRoles = getRoleInfoByUserId(userId);
        if (CollectionUtils.isEmpty(sysRoles)) {
            return null;
        }
        return sysRoles.stream().map(SysRole::getName).collect(Collectors.toList());
    }

    /**
     * 设置选中 Dept
     *
     * @param list
     * @param checkDeptList
     */
    private void setCheckedDept(List<DeptResVO> list, Set<Object> checkDeptList) {
        for (DeptResVO node : list) {
            if (checkDeptList.contains(node.getId())) {
                node.setChecked(true);
            }
            setCheckedDept((List<DeptResVO>) node.getChildren(), checkDeptList);
        }
    }

    /**
     * 设置选中 Permission
     *
     * @param list
     * @param checkList
     */
    private void setCheckedPermission(List<PermissionResVO> list, Set<Object> checkList) {
        for (PermissionResVO node : list) {
            if (checkList.contains(node.getId()) && CollectionUtils.isEmpty(node.getChildren())) {
                node.setChecked(true);
            }
            // 循环遍历
            setCheckedPermission((List<PermissionResVO>) node.getChildren(), checkList);
        }
    }
}
