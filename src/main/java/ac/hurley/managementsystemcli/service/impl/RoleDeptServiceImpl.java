package ac.hurley.managementsystemcli.service.impl;

import ac.hurley.managementsystemcli.entitiy.SysRoleDept;
import ac.hurley.managementsystemcli.mapper.RoleDeptMapper;
import ac.hurley.managementsystemcli.service.RoleDeptService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 角色部门 Service 实现类
 */
@Service("roleDeptService")
public class RoleDeptServiceImpl extends ServiceImpl<RoleDeptMapper, SysRoleDept> implements RoleDeptService {
}
