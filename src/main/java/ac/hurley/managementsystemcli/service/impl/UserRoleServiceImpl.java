package ac.hurley.managementsystemcli.service.impl;

import ac.hurley.managementsystemcli.entitiy.SysUserRole;
import ac.hurley.managementsystemcli.mapper.UserRoleMapper;
import ac.hurley.managementsystemcli.service.UserRoleService;
import ac.hurley.managementsystemcli.vo.req.UserRoleOperationReqVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户角色 Service 实现类
 *
 * @author hurley
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, SysUserRole> implements UserRoleService {

    @Resource
    private UserRoleMapper userRoleMapper;

    /**
     * 根据 userId 获取对应的 RoleIds
     *
     * @param userId
     * @return
     */
    @Override
    public List getRoleIdsByUserId(String userId) {
        LambdaQueryWrapper<SysUserRole> queryWrapper = Wrappers.<SysUserRole>lambdaQuery()
                .select(SysUserRole::getRoleId)
                .eq(SysUserRole::getUserId, userId);
        return userRoleMapper.selectObjs(queryWrapper);
    }

    /**
     * 添加用户角色信息
     *
     * @param userRoleOperationReqVO
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addUserRoleInfo(UserRoleOperationReqVO userRoleOperationReqVO) {
        if (CollectionUtils.isEmpty(userRoleOperationReqVO.getRoleIds())) {
            return;
        }
        List<SysUserRole> list = new ArrayList<>();
        for (String roleId : userRoleOperationReqVO.getRoleIds()) {
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(userRoleOperationReqVO.getUserId());
            sysUserRole.setRoleId(roleId);
            list.add(sysUserRole);
        }
        userRoleMapper.delete(Wrappers.<SysUserRole>lambdaQuery()
                .eq(SysUserRole::getUserId, userRoleOperationReqVO.getUserId()));
        // 批量插入
        this.saveBatch(list);
    }

    /**
     * 根据 roleId 获取对应的 userId
     *
     * @param roleId
     * @return
     */
    @Override
    public List getUserIdsByRoleId(String roleId) {
        return userRoleMapper.selectObjs(Wrappers.<SysUserRole>lambdaQuery()
                .select(SysUserRole::getUserId)
                .eq(SysUserRole::getRoleId, roleId));
    }
}
