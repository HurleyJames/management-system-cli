package ac.hurley.managementsystemcli.service.impl;

import ac.hurley.managementsystemcli.common.exception.SysException;
import ac.hurley.managementsystemcli.common.exception.code.BaseResCode;
import ac.hurley.managementsystemcli.entitiy.SysPermission;
import ac.hurley.managementsystemcli.entitiy.SysRolePermission;
import ac.hurley.managementsystemcli.entitiy.SysUserRole;
import ac.hurley.managementsystemcli.mapper.PermissionMapper;
import ac.hurley.managementsystemcli.service.HttpSessionService;
import ac.hurley.managementsystemcli.service.PermissionService;
import ac.hurley.managementsystemcli.service.RolePermissionService;
import ac.hurley.managementsystemcli.service.UserRoleService;
import ac.hurley.managementsystemcli.vo.res.PermissionResVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 菜单权限 Service 实现类
 */
@Service
@Slf4j
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, SysPermission> implements PermissionService {

    @Resource
    private UserRoleService userRoleService;
    @Resource
    private RolePermissionService rolePermissionService;
    @Resource
    private PermissionMapper permissionMapper;
    @Resource
    private HttpSessionService httpSessionService;

    /**
     * 根据用户查询拥有的权限
     * 先查询出用户对应的角色
     * 再去查询用户所拥有的权限
     * 也可以多表关联查询
     *
     * @param userId
     * @return
     */
    @Override
    public List<SysPermission> getPermission(String userId) {
        // 根据 userId 获取对应的 角色 Id
        List<String> roldIds = userRoleService.getRoleIdsByUserId(userId);
        // 如果角色 Id 为空
        if (CollectionUtils.isEmpty(roldIds)) {
            return null;
        }
        // 根据 roleIds 获取对应的 权限 Id 列表
        List<Object> permissionIds = rolePermissionService.listObjs(Wrappers.<SysRolePermission>lambdaQuery()
                .select(SysRolePermission::getPermissionId)
                .in(SysRolePermission::getRoleId, roldIds));

        if (CollectionUtils.isEmpty(permissionIds)) {
            return null;
        }

        LambdaQueryWrapper<SysPermission> queryWrapper = Wrappers.<SysPermission>lambdaQuery()
                .in(SysPermission::getId, permissionIds)
                .orderByAsc(SysPermission::getOrderNum);

        return permissionMapper.selectList(queryWrapper);
    }

    /**
     * 删除菜单权限
     * 判断是否有角色关联
     * 判断是否权限下有子集
     *
     * @param permissionId
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deletePermission(String permissionId) {
        // 获取该权限 id 关联的所有 userId
        List<String> userIds = getUserIdsById(permissionId);
        // 根据权限 id 获取整个权限对象
        SysPermission sysPermission = permissionMapper.selectById(permissionId);
        if (sysPermission == null) {
            log.error("传入的 id:{} 不合法", permissionId);
            throw new SysException(BaseResCode.DATA_ERROR);
        }

        // 获取当前权限的下一级孩子节点
        List<SysPermission> children = permissionMapper.selectList(Wrappers.<SysPermission>lambdaQuery()
                .eq(SysPermission::getPid, permissionId));
        if (!CollectionUtils.isEmpty(children)) {
            throw new SysException(BaseResCode.ROLE_PERMISSION_RELATION);
        }

        // 删除权限
        permissionMapper.deleteById(permissionId);
        // 删除和角色关联的权限
        rolePermissionService.remove(Wrappers.<SysRolePermission>lambdaQuery()
                .eq(SysRolePermission::getPermissionId, permissionId));

        if (!CollectionUtils.isEmpty(userIds)) {
            // 刷新权限
            userIds.parallelStream().forEach(httpSessionService::refreshUserId);
        }
    }

    /**
     * 获取所有菜单权限
     *
     * @return
     */
    @Override
    public List<SysPermission> selectAllPermission() {
        // 根据 OrderNum 排序，获取所有权限
        List<SysPermission> result = permissionMapper.selectList(Wrappers.<SysPermission>lambdaQuery()
                .orderByAsc(SysPermission::getOrderNum));
        // 如果查询出的结果不为空
        if (!CollectionUtils.isEmpty(result)) {
            for (SysPermission sysPermission : result) {
                // 获取权限的父权限
                SysPermission parent = permissionMapper.selectById(sysPermission.getPid());
                if (parent != null) {
                    sysPermission.setPidName(parent.getName());
                }
            }
        }
        return result;
    }

    /**
     * 根据 userId 获取权限
     *
     * @param userId
     * @return
     */
    @Override
    public Set<String> getPermissionByUserId(String userId) {
        // 根据 userId 获取权限对象
        List<SysPermission> list = getPermission(userId);
        Set<String> permissions = new HashSet<>();
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        for (SysPermission sysPermission : list) {
            if (!StringUtils.isEmpty(sysPermission.getPermissions())) {
                // 将 SysPermission 对象中的权限提取出来
                permissions.add(sysPermission.getPermissions());
            }
        }
        return permissions;
    }

    /**
     * 用树型的方式将用户拥有的菜单权限返回给客户端
     *
     * @param userId
     * @return
     */
    @Override
    public List<PermissionResVO> permissionTreeList(String userId) {
        List<SysPermission> list = getPermission(userId);
        return getTree(list, true);
    }

    /**
     * 获取所有的菜单权限
     *
     * @return
     */
    @Override
    public List<PermissionResVO> selectAllByTree() {
        List<SysPermission> list = selectAllPermission();
        return getTree(list, false);
    }

    /**
     * 只获取目录和菜单
     *
     * @param permissionId
     * @return
     */
    @Override
    public List<PermissionResVO> selectAllMenuByTree(String permissionId) {
        // 获取所有权限
        List<SysPermission> list = selectAllPermission();
        if (!CollectionUtils.isEmpty(list) && !StringUtils.isEmpty(permissionId)) {
            for (SysPermission sysPermission : list) {
                if (sysPermission.getId().equals(permissionId)) {
                    list.remove(sysPermission);
                    break;
                }
            }
        }

        List<PermissionResVO> result = new ArrayList<>();
        // 新增顶级目录，为了方便添加一级目录
        PermissionResVO resVO = new PermissionResVO();
        resVO.setId("0");
        resVO.setTitle("默认顶级菜单");
        resVO.setSpread(true);
        // 将孩子节点以树型结构设置
        resVO.setChildren(getTree(list, true));
        result.add(resVO);
        return result;
    }

    /**
     * 根据权限 id 获取用户 Id
     *
     * @param permissionId
     * @return
     */
    @Override
    public List getUserIdsById(String permissionId) {
        // 根据权限 Id，获取所有角色 Id
        List<Object> roleIds = rolePermissionService.listObjs(Wrappers.<SysRolePermission>lambdaQuery()
                .select(SysRolePermission::getRoleId)
                .eq(SysRolePermission::getPermissionId, permissionId));

        if (!CollectionUtils.isEmpty(roleIds)) {
            // 根据角色 Id，获取关联的用户
            return userRoleService.listObjs(Wrappers.<SysUserRole>lambdaQuery()
                    .select(SysUserRole::getUserId)
                    .in(SysUserRole::getRoleId, roleIds));
        }
        return null;
    }

    @Override
    public void updatePermission(SysPermission sysPermission) {
        permissionMapper.updateById(sysPermission);
        httpSessionService.refreshRolePermission(sysPermission.getId());
    }

    /**
     * 递归获取整个菜单树
     *
     * @param permissions
     * @param type
     * @return
     */
    private List<PermissionResVO> getTree(List<SysPermission> permissions, boolean type) {
        List<PermissionResVO> list = new ArrayList<>();
        // 如果权限菜单为空
        if (CollectionUtils.isEmpty(permissions)) {
            return list;
        }
        for (SysPermission sysPermission : permissions) {
            if ("0".equals(sysPermission.getPid())) {
                PermissionResVO permissionResVO = new PermissionResVO();
                BeanUtils.copyProperties(sysPermission, permissionResVO);
                permissionResVO.setTitle(sysPermission.getName());

                // 根据类型判断显示的类型
                if (type) {
                    // 显示目录和菜单
                    permissionResVO.setChildren(getAllDictAndMenuChild(sysPermission.getId(), permissions));
                } else {
                    // 显示所有节点
                    permissionResVO.setChildren(getAllChild(sysPermission.getId(), permissions));
                }

                list.add(permissionResVO);
            }
        }

        return list;
    }

    /**
     * 递归遍历所有孩子节点
     *
     * @param id
     * @param permissions
     * @return
     */
    private List<PermissionResVO> getAllChild(String id, List<SysPermission> permissions) {
        List<PermissionResVO> list = new ArrayList<>();
        for (SysPermission sysPermission : permissions) {
            if (sysPermission.getPid().equals(id)) {
                PermissionResVO permissionResVO = new PermissionResVO();
                BeanUtils.copyProperties(sysPermission, permissionResVO);
                permissionResVO.setTitle(sysPermission.getName());
                // 递归遍历
                permissionResVO.setChildren(getAllChild(sysPermission.getId(), permissions));
                list.add(permissionResVO);
            }
        }
        return list;
    }

    /**
     * 递归获取目录和菜单
     *
     * @param id
     * @param permissions
     * @return
     */
    private List<PermissionResVO> getAllDictAndMenuChild(String id, List<SysPermission> permissions) {
        List<PermissionResVO> list = new ArrayList<>();
        for (SysPermission sysPermission : permissions) {
            if (sysPermission.getPid().equals(id) && sysPermission.getType() != 3) {
                PermissionResVO permissionResVO = new PermissionResVO();
                BeanUtils.copyProperties(sysPermission, permissionResVO);
                permissionResVO.setTitle(sysPermission.getName());
                permissionResVO.setChildren(getAllDictAndMenuChild(sysPermission.getId(), permissions));
                list.add(permissionResVO);
            }
        }
        return list;
    }
}
