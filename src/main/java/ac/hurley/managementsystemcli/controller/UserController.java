package ac.hurley.managementsystemcli.controller;

import ac.hurley.managementsystemcli.common.DataResult;
import ac.hurley.managementsystemcli.common.annotation.LogAnnotation;
import ac.hurley.managementsystemcli.common.exception.code.BaseResCode;
import ac.hurley.managementsystemcli.entitiy.SysUser;
import ac.hurley.managementsystemcli.entitiy.SysUserRole;
import ac.hurley.managementsystemcli.service.HttpSessionService;
import ac.hurley.managementsystemcli.service.UserRoleService;
import ac.hurley.managementsystemcli.service.UserService;
import ac.hurley.managementsystemcli.vo.req.UserRoleOperationReqVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wf.captcha.utils.CaptchaUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;


/**
 * 用户管理
 * @author hurley
 */
@RestController
@Api(tags = "组织模块-用户管理")
@RequestMapping("/sys")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private UserRoleService userRoleService;
    @Resource
    private HttpSessionService httpSessionService;

    /**
     * 登录
     *
     * @param sysUser
     * @param request
     * @return
     */
    @PostMapping(value = "/user/login")
    @ApiOperation(value = "用户登录接口")
    public DataResult login(@RequestBody @Valid SysUser sysUser, HttpServletRequest request) {
        // 判断验证码
        if (!CaptchaUtil.ver(sysUser.getCaptcha(), request)) {
            // 如果验证码错误，则清除 session 中保存的验证码
            CaptchaUtil.clear(request);
            return DataResult.fail("验证码错误！");
        }
        return DataResult.success(userService.login(sysUser));
    }

    /**
     * 注册
     *
     * @param sysUser
     * @return
     */
    @PostMapping("/user/register")
    @ApiOperation(value = "用户注册接口")
    public DataResult register(@RequestBody @Valid SysUser sysUser) {
        userService.register(sysUser);
        return DataResult.success();
    }

    /**
     * 如果未登录，则提示用户去登录
     *
     * @return
     */
    @GetMapping("/user/unLogin")
    @ApiOperation(value = "引导客户端去登录")
    public DataResult unLogin() {
        return DataResult.getResult(BaseResCode.TOKEN_ERROR);
    }

    /**
     * 注销登录
     *
     * @return
     */
    @GetMapping("/user/logout")
    @ApiOperation(value = "退出登录接口")
    @LogAnnotation(title = "用户管理", action = "退出")
    public DataResult logout() {
        httpSessionService.abortAllUserByToken();
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return DataResult.success();
    }

    /**
     * 更新用户信息
     *
     * @param sysUser
     * @return
     */
    @PutMapping("/user")
    @ApiOperation(value = "更新用户信息接口")
    @LogAnnotation(title = "用户管理", action = "更新用户信息")
    @RequiresPermissions("sys:user:update")
    public DataResult updateUserInfo(@RequestBody SysUser sysUser) {
        if (StringUtils.isEmpty(sysUser.getId())) {
            return DataResult.fail("id 不能为空");
        }

        userService.updateUserInfo(sysUser);
        return DataResult.success();
    }

    /**
     * 查询某个用户详情信息
     *
     * @param id
     * @return
     */
    @GetMapping("/user/{id}")
    @ApiOperation(value = "查询用户详情接口")
    @LogAnnotation(title = "用户管理", action = "查询用户详情")
    @RequiresPermissions("sys:user:detail")
    public DataResult getDetailInfo(@PathVariable("id") String id) {
        return DataResult.success(userService.getById(id));
    }

    /**
     * 查询当前用户详情信息
     *
     * @return
     */
    @GetMapping("/user")
    @ApiOperation(value = "查询当前用户详情接口")
    @LogAnnotation(title = "用户管理", action = "查询用户详情")
    public DataResult getCurrentUserInfo() {
        String userId = httpSessionService.getCurrentUserId();
        return DataResult.success(userService.getById(userId));
    }

    /**
     * 分页获取用户列表
     *
     * @param sysUser
     * @return
     */
    @PostMapping("/users")
    @ApiOperation(value = "分页获取用户列表接口")
    @RequiresPermissions("sys:user:list")
    @LogAnnotation(title = "用户管理", action = "分页获取用户列表")
    public DataResult pageInfo(@RequestBody SysUser sysUser) {
        return DataResult.success(userService.pageInfo(sysUser));
    }


    /**
     * 新增用户
     *
     * @param sysUser
     * @return
     */
    @PostMapping("/user")
    @ApiOperation(value = "新增用户接口")
    @RequiresPermissions("sys:user:add")
    @LogAnnotation(title = "用户管理", action = "新增用户")
    public DataResult addUser(@RequestBody @Valid SysUser sysUser) {
        userService.addUser(sysUser);
        return DataResult.success();
    }

    /**
     * 修改密码
     *
     * @param sysUser
     * @return
     */
    @PutMapping("/user/pwd")
    @ApiOperation(value = "修改密码接口")
    @LogAnnotation(title = "用户管理", action = "更新密码")
    public DataResult updatePwd(@RequestBody SysUser sysUser) {
        if (StringUtils.isEmpty(sysUser.getOldPwd()) || StringUtils.isEmpty(sysUser.getNewPwd())) {
            return DataResult.fail("旧密码或新密码不能为空");
        }

        String userId = httpSessionService.getCurrentUserId();
        sysUser.setId(userId);
        userService.updatePwd(sysUser);
        return DataResult.success();
    }

    /**
     * 删除用户
     *
     * @param userIds
     * @return
     */
    @DeleteMapping("/user")
    @ApiOperation(value = "删除用户接口")
    @LogAnnotation(title = "用户管理", action = "删除用户")
    @RequiresPermissions("sys:user:delete")
    public DataResult deleteUser(@RequestBody @ApiParam(value = "用户 id 集合") List<String> userIds) {
        // 删除用户，删除 redis 的绑定的角色和权限
        httpSessionService.abortUserByUserIds(userIds);
        LambdaQueryWrapper<SysUser> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.in(SysUser::getId, userIds);
        userService.remove(queryWrapper);
        return DataResult.success();
    }

    /**
     * 获取所有角色
     *
     * @param userId
     * @return
     */
    @GetMapping("/user/roles/{userId}")
    @ApiOperation(value = "赋予角色-获取所有角色接口")
    @LogAnnotation(title = "用户管理", action = "赋予角色-获取所有角色接口")
    @RequiresPermissions("sys:user:role:detail")
    public DataResult getUserOwnRole(@PathVariable("userId") String userId) {
        DataResult result = DataResult.success();
        result.setData(userService.getUserRole(userId));
        return result;
    }

    /**
     * 设置用户角色
     *
     * @param userId
     * @param roleIds
     * @return
     */
    @PutMapping("/user/roles/{userId}")
    @ApiOperation(value = "赋予角色-用户赋予角色接口")
    @LogAnnotation(title = "用户管理", action = "赋予角色-用户赋予角色接口")
    @RequiresPermissions("sys:user:update:role")
    public DataResult setUserOwnRole(@PathVariable("userId") String userId, @RequestBody List<String> roleIds) {
        LambdaQueryWrapper<SysUserRole> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SysUserRole::getUserId, userId);
        userRoleService.remove(queryWrapper);
        if (!CollectionUtils.isEmpty(roleIds)) {
            UserRoleOperationReqVO reqVO = new UserRoleOperationReqVO();
            reqVO.setUserId(userId);
            reqVO.setRoleIds(roleIds);
            userRoleService.addUserRoleInfo(reqVO);
        }

        // 刷新权限
        httpSessionService.refreshUserId(userId);
        return DataResult.success();
    }
}
