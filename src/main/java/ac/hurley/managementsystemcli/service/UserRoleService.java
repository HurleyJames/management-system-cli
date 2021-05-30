package ac.hurley.managementsystemcli.service;

import ac.hurley.managementsystemcli.entitiy.SysUserRole;
import ac.hurley.managementsystemcli.vo.req.UserRoleOperationReqVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 用户角色 Service 类
 */
public interface UserRoleService extends IService<SysUserRole> {

    /**
     * 根据 userId 获取绑定的角色 Id
     *
     * @param userId
     * @return
     */
    List<String> getRoleIdsByUserId(String userId);

    /**
     * 为用户绑定角色
     *
     * @param userRoleOperationReqVO
     */
    void addUserRoleInfo(UserRoleOperationReqVO userRoleOperationReqVO);

    /**
     * 根据角色 Id 获取绑定的用户
     *
     * @param roleId
     * @return
     */
    List<String> getUserIdsByRoleId(String roleId);
}
