package ac.hurley.managementsystemcli.service.impl;

import ac.hurley.managementsystemcli.common.exception.SysException;
import ac.hurley.managementsystemcli.common.exception.code.BaseResCode;
import ac.hurley.managementsystemcli.entitiy.SysDept;
import ac.hurley.managementsystemcli.entitiy.SysUser;
import ac.hurley.managementsystemcli.mapper.DeptMapper;
import ac.hurley.managementsystemcli.mapper.UserMapper;
import ac.hurley.managementsystemcli.service.DeptService;
import ac.hurley.managementsystemcli.vo.res.DeptResVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 部门 Service 实现类
 */
@Service
@Slf4j
public class DeptServiceImpl extends ServiceImpl<DeptMapper, SysDept> implements DeptService {

    @Resource
    private DeptMapper deptMapper;
    @Resource
    private UserMapper userMapper;

    /**
     * 添加一个新的部门
     *
     * @param sysDept
     */
    @Override
    public void addDept(SysDept sysDept) {
        String relationCode;
        // 获得一个新的部门编号
        String deptCode = this.getNewDeptCode();
        SysDept parent = deptMapper.selectById(sysDept.getPid());
        if ("0".equals(sysDept.getPid())) {
            relationCode = deptCode;
        } else if (parent == null) {
            throw new SysException(BaseResCode.DATA_ERROR);
        } else {
            relationCode = parent.getRelationCode() + deptCode;
        }
        sysDept.setDeptNo(deptCode);
        sysDept.setRelationCode(relationCode);
        sysDept.setStatus(1);
        deptMapper.insert(sysDept);
    }

    /**
     * 更新部门
     *
     * @param sysDept
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDept(SysDept sysDept) {
        SysDept sysDept1 = deptMapper.selectById(sysDept.getId());
        if (sysDept == null) {
            throw new SysException(BaseResCode.DATA_ERROR);
        }
        deptMapper.updateById(sysDept);

        if (!StringUtils.isEmpty(sysDept.getPid()) && !sysDept.getPid().equals(sysDept1.getPid())) {
            // 获得父级的 Id
            SysDept parent = deptMapper.selectById(sysDept.getPid());
            if (!"0".equals(sysDept.getPid()) && parent == null) {
                throw new SysException(BaseResCode.DATA_ERROR);
            }
            // 旧的父级
            SysDept oldParent = deptMapper.selectById(sysDept1.getPid());
            String oldRelationCode;
            String newRelationCode;
            // 根目录降到其它目录
            if ("0".equals(sysDept1.getPid())) {
                oldRelationCode = sysDept1.getDeptNo();
                newRelationCode = parent.getRelationCode() + sysDept1.getDeptNo();
            } else if ("0".equals(sysDept.getPid())) {
                // 其它目录升级为根目录
                oldRelationCode = sysDept1.getRelationCode();
                newRelationCode = sysDept1.getDeptNo();
            } else {
                oldRelationCode = oldParent.getRelationCode();
                newRelationCode = parent.getRelationCode();
            }

            LambdaQueryWrapper<SysDept> wrapper = Wrappers.lambdaQuery();
            wrapper.likeLeft(SysDept::getDeptNo, sysDept1.getDeptNo());
            List<SysDept> depts = deptMapper.selectList(wrapper);
            depts.parallelStream().forEach(entity -> {
                String relationCode = entity.getRelationCode().replace(oldRelationCode, newRelationCode);
                entity.setRelationCode(relationCode);
                deptMapper.updateById(entity);
            });
        }
    }

    /**
     * 删除部门
     *
     * @param id
     */
    @Override
    public void deleteDept(String id) {
        SysDept sysDept = deptMapper.selectById(id);
        if (sysDept == null) {
            throw new SysException(BaseResCode.DATA_ERROR);
        }
        // 查询对应的部门 Id
        List<Object> deptIds = deptMapper.selectObjs(Wrappers.<SysDept>lambdaQuery()
                .select(SysDept::getId).likeRight(SysDept::getRelationCode, sysDept.getRelationCode()));
        // 查询该部门 Id 下的用户
        List<SysUser> users = userMapper.selectList(Wrappers.<SysUser>lambdaQuery()
                .in(SysUser::getDeptId, deptIds));
        if (!CollectionUtils.isEmpty(users)) {
            // 如果用户不为空，则提示该部门下辖用户，不允许删除
            throw new SysException(BaseResCode.NOT_PERMISSION_DELETED_DEPT);
        }
        // 数据库中根据 id 删除
        deptMapper.deleteById(id);
    }

    /**
     * 部门树型列表
     *
     * @param deptId
     * @param disabled 最顶级是否可用
     * @return
     */
    @Override
    public List<DeptResVO> deptTreeList(String deptId, Boolean disabled) {
        List<SysDept> depts;
        if (StringUtils.isEmpty(deptId)) {
            depts = deptMapper.selectList(Wrappers.emptyWrapper());
        } else {
            SysDept sysDept = deptMapper.selectById(deptId);
            if (sysDept == null) {
                throw new SysException(BaseResCode.DATA_ERROR);
            }
            LambdaQueryWrapper<SysDept> queryWrapper = Wrappers.<SysDept>lambdaQuery().likeRight(SysDept::getRelationCode, sysDept.getRelationCode());
            List<Object> childIds = deptMapper.selectObjs(queryWrapper);
            depts = deptMapper.selectList(Wrappers.<SysDept>lambdaQuery().notIn(SysDept::getId, childIds));
        }

        DeptResVO deptResVO = new DeptResVO();
        deptResVO.setTitle("默认顶级部门");
        deptResVO.setId("0");
        deptResVO.setSpread(true);
        // 设置最顶级是否可用
        deptResVO.setDisabled(disabled);
        deptResVO.setChildren(getTree(depts));
        List<DeptResVO> result = new ArrayList<>();
        result.add(deptResVO);
        return result;
    }

    /**
     * 获取部门树
     *
     * @param depts
     * @return
     */
    private List<DeptResVO> getTree(List<SysDept> depts) {
        List<DeptResVO> list = new ArrayList<>();
        for (SysDept sysDept : depts) {
            if ("0".equals(sysDept.getPid())) {
                DeptResVO deptTree = new DeptResVO();
                BeanUtils.copyProperties(sysDept, deptTree);
                deptTree.setTitle(sysDept.getName());
                deptTree.setSpread(true);
                deptTree.setChildren(getChild(sysDept.getId(), depts));
                list.add(deptTree);
            }
        }
        return list;
    }

    /**
     * 获取的部门树的孩子节点
     *
     * @param id
     * @param depts
     * @return
     */
    private List<DeptResVO> getChild(String id, List<SysDept> depts) {
        List<DeptResVO> list = new ArrayList<>();
        for (SysDept sysDept : depts) {
            if (sysDept.getPid().equals(id)) {
                DeptResVO deptTree = new DeptResVO();
                BeanUtils.copyProperties(sysDept, deptTree);
                deptTree.setTitle(sysDept.getName());
                deptTree.setChildren(getChild(sysDept.getId(), depts));
                list.add(deptTree);
            }
        }
        return list;
    }

    /**
     * 获取新的部门编码
     *
     * @return
     */
    public String getNewDeptCode() {
        LambdaQueryWrapper<SysDept> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.select(SysDept::getDeptNo);
        // 获取所有的 deptCode
        List<Object> deptCodes = deptMapper.selectObjs(lambdaQueryWrapper);
        AtomicReference<Integer> maxDeptCode = new AtomicReference<>(0);

        // 遍历获取最大的 DeptCode
        deptCodes.forEach(o -> {
            String str = String.valueOf(o);
            if (str.length() >= 7) {
                Integer one = Integer.parseInt(str.substring(str.length() - 5));
                if (one > maxDeptCode.get()) {
                    maxDeptCode.set(one);
                }
            }
        });

        // 为编号剩余的位置补上 0
        return padRight(maxDeptCode.get() + 1, 6, "0");
    }

    /**
     * 右补位，左对齐
     *
     * @param oriStr 原字符串
     * @param len    目标字符串长度
     * @param alexi  补位字符
     * @return 目标字符串
     * 以 alexi 作为补位
     */
    public static String padRight(int oriStr, int len, String alexi) {
        StringBuilder str = new StringBuilder();
        int strLen = String.valueOf(oriStr).length();
        if (strLen < len) {
            for (int i = 0; i < len - strLen; i++) {
                str.append(alexi);
            }
        }
        str.append(oriStr);
        return "D" + str.toString();
    }
}
