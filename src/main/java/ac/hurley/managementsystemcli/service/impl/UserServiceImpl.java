package ac.hurley.managementsystemcli.service.impl;

import ac.hurley.managementsystemcli.common.exception.SysException;
import ac.hurley.managementsystemcli.common.exception.code.BaseResCode;
import ac.hurley.managementsystemcli.common.utils.PasswordUtils;
import ac.hurley.managementsystemcli.entitiy.SysDept;
import ac.hurley.managementsystemcli.entitiy.SysRole;
import ac.hurley.managementsystemcli.entitiy.SysUser;
import ac.hurley.managementsystemcli.mapper.DeptMapper;
import ac.hurley.managementsystemcli.mapper.UserMapper;
import ac.hurley.managementsystemcli.service.*;
import ac.hurley.managementsystemcli.vo.req.UserRoleOperationReqVO;
import ac.hurley.managementsystemcli.vo.res.LoginResVO;
import ac.hurley.managementsystemcli.vo.res.UserRoleResVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, SysUser> implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private RoleService roleService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private UserRoleService userRoleService;
    @Resource
    private DeptMapper deptMapper;
    @Resource
    private HttpSessionService httpSessionService;

    /**
     * 是否允许多用户登录
     */
    @Value("${spring.redis.isAllowMultipleLogin}")
    private Boolean isAllowMultipleLogin;
    @Value("${spring.profiles.active}")
    /**
     * 环境
     */
    private String env;

    /**
     * 注册
     *
     * @param sysUser
     */
    @Override
    public void register(SysUser sysUser) {
        SysUser sysUser1 = userMapper.selectOne(Wrappers.<SysUser>lambdaQuery().
                eq(SysUser::getUsername, sysUser.getUsername()));
        if (sysUser1 != null) {
            throw new SysException("用户名已存在！");
        }
        // 生成盐值
        sysUser.setSalt(PasswordUtils.getSalt());
        // 为密码和盐值加密
        String encodePwd = PasswordUtils.encode(sysUser.getPassword(), sysUser.getSalt());
        // 设置加密后的密码
        sysUser.setPassword(encodePwd);
        // 添加到数据库中
        userMapper.insert(sysUser);
    }

    @Override
    public LoginResVO login(SysUser sysUser) {
        SysUser sysUser1 = userMapper.selectOne(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getUsername, sysUser.getUsername()));
        if (sysUser1 == null) {
            // 账号不存在
            throw new SysException(BaseResCode.NON_EXIST_ACCOUNT);
        }
        if (sysUser1.getStatus() == 2) {
            // 账户已经被锁定
            throw new SysException(BaseResCode.USER_LOCKED);
        }
        if (!PasswordUtils.matches(sysUser1.getSalt(), sysUser.getPassword(), sysUser1.getPassword())) {
            // 账号密码不匹配
            throw new SysException(BaseResCode.PASSWORD_ERROR);
        }

        LoginResVO loginResVO = new LoginResVO();
        BeanUtils.copyProperties(sysUser1, loginResVO);

        // 如果不允许多用户登录
        if (!isAllowMultipleLogin) {
            // 登出该用户
            httpSessionService.abortUserByUserId(sysUser1.getId());
        }

        // 如果部门不为空
        if (StringUtils.isNotBlank(sysUser1.getDeptId())) {
            SysDept sysDept = deptMapper.selectById(sysUser1.getDeptId());
            if (sysDept != null) {
                // 为该用户设置部门编号
                sysUser1.setDeptNo(sysDept.getDeptNo());
            }
        }

        // 生成该用户的 Token
        String token = httpSessionService.createTokenAndUser(sysUser1,
                roleService.getRoleNamesByUserId(sysUser1.getId()),
                permissionService.getPermissionByUserId(sysUser1.getId()));
        loginResVO.setAccessToken(token);
        return loginResVO;
    }

    /**
     * 更新用户信息
     *
     * @param sysUser
     */
    @Override
    public void updateUserInfo(SysUser sysUser) {
        SysUser sysUser1 = userMapper.selectById(sysUser.getId());
        if (sysUser1 == null) {
            throw new SysException(BaseResCode.DATA_ERROR);
        }

        // 如果用户名发生变更
        if (!sysUser1.getUsername().equals(sysUser.getUsername())) {
            SysUser sysUser2 = userMapper.selectOne(Wrappers.<SysUser>lambdaQuery()
                    .eq(SysUser::getUsername, sysUser.getUsername()));
            if (sysUser2 != null) {
                throw new SysException("用户名已存在！");
            }
        }

        // 如果用户名、密码、状态之中发生了变更，删除 redis 中用户绑定的角色权限
        if (!sysUser1.getUsername().equals(sysUser.getUsername())
                || (!StringUtils.isEmpty(sysUser.getPassword())
                && !sysUser1.getPassword().equals(PasswordUtils.encode(sysUser.getPassword(), sysUser1.getSalt())))
                || !sysUser1.getStatus().equals(sysUser.getStatus())) {

            // 使该用户的 Token 失效
            httpSessionService.abortUserByUserId(sysUser.getId());
        }

        if (!StringUtils.isEmpty(sysUser.getPassword())) {
            String newPwd = PasswordUtils.encode(sysUser.getPassword(), sysUser.getSalt());
            sysUser.setPassword(newPwd);
        } else {
            sysUser.setPassword(null);
        }

        sysUser.setUpdateId(httpSessionService.getCurrentUserId());
        // 更新数据库
        userMapper.updateById(sysUser);

    }

    /**
     * 分页信息处理
     *
     * @param sysUser
     * @return
     */
    @Override
    public IPage<SysUser> pageInfo(SysUser sysUser) {
        Page page = new Page(sysUser.getPage(), sysUser.getLimit());
        LambdaQueryWrapper<SysUser> queryWrapper = Wrappers.lambdaQuery();

        if (!StringUtils.isEmpty(sysUser.getUsername())) {
            queryWrapper.like(SysUser::getUsername, sysUser.getUsername());
        }
        if (!StringUtils.isEmpty(sysUser.getStartTime())) {
            queryWrapper.gt(SysUser::getCreateTime, sysUser.getStartTime());
        }
        if (!StringUtils.isEmpty(sysUser.getEndTime())) {
            queryWrapper.lt(SysUser::getCreateTime, sysUser.getEndTime());
        }
        if (!StringUtils.isEmpty(sysUser.getNickName())) {
            queryWrapper.like(SysUser::getNickName, sysUser.getNickName());
        }
        if (sysUser.getStatus() != null) {
            queryWrapper.eq(SysUser::getStatus, sysUser.getStatus());
        }
        if (!StringUtils.isEmpty(sysUser.getDeptNo())) {
            LambdaQueryWrapper<SysDept> queryWrapperDept = Wrappers.lambdaQuery();
            queryWrapperDept.select(SysDept::getId).like(SysDept::getRelationCode, sysUser.getDeptNo());
            List<Object> list = deptMapper.selectObjs(queryWrapperDept);
            queryWrapper.in(SysUser::getDeptId, list);
        }

        // 根据创建时间排列
        queryWrapper.orderByDesc(SysUser::getCreateTime);
        IPage<SysUser> iPage = userMapper.selectPage(page, queryWrapper);
        if (!CollectionUtils.isEmpty(iPage.getRecords())) {
            for (SysUser sysUser1 : iPage.getRecords()) {
                SysDept sysDept = deptMapper.selectById(sysUser1.getDeptId());
                if (sysDept != null) {
                    sysUser1.setDeptName(sysDept.getName());
                }
            }
        }
        return iPage;
    }

    /**
     * 添加用户
     *
     * @param sysUser
     */
    @Override
    public void addUser(SysUser sysUser) {
        SysUser sysUser1 = userMapper.selectOne(Wrappers.<SysUser>lambdaQuery().
                eq(SysUser::getUsername, sysUser.getUsername()));
        if (sysUser1 != null) {
            throw new SysException("用户已存在，请勿重复添加！");
        }
        sysUser.setSalt(PasswordUtils.getSalt());
        String encodePwd = PasswordUtils.encode(sysUser.getPassword(), sysUser.getSalt());
        sysUser.setPassword(encodePwd);
        sysUser.setStatus(1);
        sysUser.setCreateWhere(1);
        userMapper.insert(sysUser);
        if (!CollectionUtils.isEmpty(sysUser.getRoleIds())) {
            UserRoleOperationReqVO reqVO = new UserRoleOperationReqVO();
            reqVO.setUserId(sysUser.getId());
            reqVO.setRoleIds(sysUser.getRoleIds());
            userRoleService.addUserRoleInfo(reqVO);
        }
    }

    @Override
    public void deleteUser(String userId) {

    }

    /**
     * 更新密码
     *
     * @param sysUser
     */
    @Override
    public void updatePwd(SysUser sysUser) {
        SysUser sysUser1 = userMapper.selectById(sysUser.getId());
        if (sysUser1 == null) {
            throw new SysException(BaseResCode.DATA_ERROR);
        }
        if ("test".equals(env) && "guest".equals(sysUser1.getUsername())) {
            throw new SysException("演示环境禁止修改演示账号密码");
        }
        // 旧密码输入错误
        if (!PasswordUtils.matches(sysUser1.getSalt(), sysUser1.getOldPwd(), sysUser1.getPassword())) {
            throw new SysException(BaseResCode.OLD_PASSWORD_ERROR);
        }
        // 如果新密码与旧密码相同
        if (sysUser1.getPassword().equals(PasswordUtils.encode(sysUser.getNewPwd(), sysUser1.getSalt()))) {
            throw new SysException("新密码不能与旧密码相同");
        }

        // 更新密码
        sysUser1.setPassword(PasswordUtils.encode(sysUser.getNewPwd(), sysUser1.getSalt()));
        // 数据库操作更新密码
        userMapper.updateById(sysUser1);
        // 退出用户登录
        httpSessionService.abortAllUserByToken();

    }

    /**
     * 根据 userId 获取用户所对应的角色
     *
     * @param userId
     * @return
     */
    @Override
    public UserRoleResVO getUserRole(String userId) {
        // 根据 userId 获取其对应的角色 Ids
        List<String> roleIdsByUserId = userRoleService.getRoleIdsByUserId(userId);
        List<SysRole> list = roleService.list();
        UserRoleResVO vo = new UserRoleResVO();
        vo.setAllRole(list);
        vo.setUserRole(roleIdsByUserId);
        return vo;
    }

    /**
     * 更新个人（我）信息
     *
     * @param sysUser
     */
    @Override
    public void updateMyInfo(SysUser sysUser) {
        // 获取当前登录的用户 Id
        SysUser sysUser1 = userMapper.selectById(httpSessionService.getCurrentUserId());
        if (sysUser1 == null) {
            throw new SysException(BaseResCode.DATA_ERROR);
        }
        if (!StringUtils.isEmpty(sysUser.getPassword())) {
            // 获取密码与盐加密后的密码
            String newPwd = PasswordUtils.encode(sysUser.getPassword(), sysUser1.getSalt());
            // 存入
            sysUser.setPassword(newPwd);
        } else {
            sysUser.setPassword(null);
        }
        // 更新当前用户的信息
        sysUser.setUpdateId(httpSessionService.getCurrentUserId());
        // 在数据库中更新信息
        userMapper.updateById(sysUser);
    }
}
