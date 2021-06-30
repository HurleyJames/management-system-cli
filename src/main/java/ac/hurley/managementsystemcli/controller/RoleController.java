package ac.hurley.managementsystemcli.controller;

import ac.hurley.managementsystemcli.common.DataResult;
import ac.hurley.managementsystemcli.common.annotation.LogAnnotation;
import ac.hurley.managementsystemcli.entitiy.SysRole;
import ac.hurley.managementsystemcli.entitiy.SysRoleDept;
import ac.hurley.managementsystemcli.service.RoleDeptService;
import ac.hurley.managementsystemcli.service.RoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/sys")
@RestController
@Api(tags = "组织模块-角色管理")
public class RoleController {

    @Resource
    private RoleService roleService;
    @Resource
    private RoleDeptService roleDeptService;

    @PostMapping("/role")
    @ApiOperation(value = "新增角色接口")
    @LogAnnotation(title = "角色管理", action = "新增角色")
    @RequiresPermissions("sys:role:add")
    public DataResult addRole(@RequestBody @Valid SysRole sysRole) {
        roleService.addRole(sysRole);
        return DataResult.success();
    }

    @DeleteMapping("/role/{id}")
    @ApiOperation(value = "删除角色接口")
    @LogAnnotation(title = "角色管理", action = "删除角色")
    @RequiresPermissions("sys:role:delete")
    public DataResult deleteRole(@PathVariable("id") String id) {
        roleService.deleteRole(id);
        return DataResult.success();
    }

    @PutMapping("/role")
    @ApiOperation(value = "更新角色信息接口")
    @LogAnnotation(title = "角色管理", action = "更新角色信息")
    @RequiresPermissions("sys:role:update")
    public DataResult updateRole(@RequestBody SysRole sysRole) {
        if (StringUtils.isEmpty(sysRole.getId())) {
            return DataResult.fail("id 不能为空");
        }
        roleService.updateRole(sysRole);
        return DataResult.success();
    }

    @GetMapping("/role/{id}")
    @ApiOperation(value = "查询角色详情接口")
    @LogAnnotation(title = "角色管理", action = "查询角色详情")
    @RequiresPermissions("sys:role:detail")
    public DataResult getRoleDetail(@PathVariable("id") String id) {
        return DataResult.success(roleService.getRoleInfo(id));
    }

    @PostMapping("/roles")
    @ApiOperation(value = "分页获取角色信息接口")
    @LogAnnotation(title = "角色管理", action = "分页获取角色信息")
    @RequiresPermissions("sys:role:list")
    @SuppressWarnings("unchecked")
    public DataResult pageInfo(@RequestBody SysRole sysRole) {
        // getLimit() 获取每页显示的数量
        Page page = new Page(sysRole.getPage(), sysRole.getLimit());
        LambdaQueryWrapper<SysRole> queryWrapper = Wrappers.lambdaQuery();
        // 如果角色名不为空，则按照名称排序
        if (!StringUtils.isEmpty(sysRole.getName())) {
            queryWrapper.like(SysRole::getName, sysRole.getName());
        }
        // 起始时间
        if (!StringUtils.isEmpty(sysRole.getStartTime())) {
            queryWrapper.gt(SysRole::getCreateTime, sysRole.getStartTime());
        }
        // 时长
        if (!StringUtils.isEmpty(sysRole.getEndTime())) {
            queryWrapper.lt(SysRole::getCreateTime, sysRole.getEndTime());
        }
        // 状态
        if (!StringUtils.isEmpty(sysRole.getStatus())) {
            queryWrapper.eq(SysRole::getStatus, sysRole.getStatus());
        }

        queryWrapper.orderByDesc(SysRole::getCreateTime);
        return DataResult.success(roleService.page(page, queryWrapper));
    }

    @PostMapping("/role/bindDept")
    @ApiOperation(value = "绑定角色部门接口")
    @LogAnnotation(title = "角色管理", action = "绑定角色部门信息")
    @RequiresPermissions("sys:role:bindDept")
    public DataResult bindDept(@RequestBody SysRole sysRole) {
        if (StringUtils.isEmpty(sysRole.getId())) {
            return DataResult.fail("id 不能为空");
        }
        if (roleService.getById(sysRole.getId()) == null) {
            return DataResult.fail("获取角色失败");
        }

        // 先删除所有绑定
        roleDeptService.remove(Wrappers.<SysRoleDept>lambdaQuery().eq(SysRoleDept::getRoleId, sysRole.getId()));

        // 如果不是自定义
        if (sysRole.getDataScope() != 2) {
            // 设置角色关联的部门为空
            sysRole.setDepts(null);
        }

        if (!CollectionUtils.isEmpty(sysRole.getDepts())) {
            List<SysRoleDept> list = new ArrayList<>();
            // 将部门列表里的部门与该角色一一绑定
            for (String deptId : sysRole.getDepts()) {
                SysRoleDept sysRoleDept = new SysRoleDept();
                sysRoleDept.setDeptId(deptId);
                sysRoleDept.setRoleId(sysRole.getId());
                list.add(sysRoleDept);
            }

            roleDeptService.saveBatch(list);
        }

        roleService.updateById(new SysRole().setId(sysRole.getId()).setDataScope(sysRole.getDataScope()));

        return DataResult.success();
    }
}
