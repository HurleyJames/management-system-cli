package ac.hurley.managementsystemcli.service.impl;

import ac.hurley.managementsystemcli.entitiy.SysDept;
import ac.hurley.managementsystemcli.entitiy.SysUser;
import ac.hurley.managementsystemcli.service.DeptService;
import ac.hurley.managementsystemcli.service.HomeService;
import ac.hurley.managementsystemcli.service.PermissionService;
import ac.hurley.managementsystemcli.service.UserService;
import ac.hurley.managementsystemcli.vo.res.HomeResVO;
import ac.hurley.managementsystemcli.vo.res.PermissionResVO;
import ac.hurley.managementsystemcli.vo.res.UserResVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 首页 Service 实现类
 */
@Service
public class HomeServiceImpl implements HomeService {

    @Resource
    private UserService userService;
    @Resource
    private DeptService deptService;
    @Resource
    private PermissionService permissionService;

    @Override
    public HomeResVO getHomeInfo(String userId) {
        SysUser sysUser = userService.getById(userId);
        UserResVO userResVO = new UserResVO();

        if (sysUser != null) {
            BeanUtils.copyProperties(sysUser, userResVO);
            // 部门
            SysDept sysDept = deptService.getById(sysUser.getDeptId());
            if (sysDept != null) {
                userResVO.setDeptId(sysDept.getId());
                userResVO.setDeptName(sysDept.getName());
            }
        }

        List<PermissionResVO> menus = permissionService.permissionTreeList(userId);

        HomeResVO homeResVO = new HomeResVO();
        homeResVO.setMenus(menus);
        homeResVO.setUserResVO(userResVO);

        return homeResVO;
    }
}
