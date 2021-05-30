package ac.hurley.managementsystemcli.service;

import ac.hurley.managementsystemcli.entitiy.SysDept;
import ac.hurley.managementsystemcli.vo.res.DeptResVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 部门 Service 类
 */
public interface DeptService extends IService<SysDept> {

    /**
     * 添加部门
     *
     * @param sysDept
     */
    void addDept(SysDept sysDept);

    /**
     * 更新部门
     *
     * @param sysDept
     */
    void updateDept(SysDept sysDept);

    /**
     * 删除部门
     *
     * @param id
     */
    void deleteDept(String id);

    /**
     * 部门树形列表
     *
     * @param deptId
     * @param disabled 最顶级是否可用
     * @return
     */
    List<DeptResVO> deptTreeList(String deptId, Boolean disabled);
}
