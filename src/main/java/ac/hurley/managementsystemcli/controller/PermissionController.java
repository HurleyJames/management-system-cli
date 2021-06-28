package ac.hurley.managementsystemcli.controller;

import ac.hurley.managementsystemcli.common.DataResult;
import ac.hurley.managementsystemcli.common.annotation.LogAnnotation;
import ac.hurley.managementsystemcli.common.exception.SysException;
import ac.hurley.managementsystemcli.common.exception.code.BaseResCode;
import ac.hurley.managementsystemcli.entitiy.SysPermission;
import ac.hurley.managementsystemcli.service.PermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RequestMapping("/sys")
@RestController
@Api(tags = "组织模块-菜单权限管理")
public class PermissionController {

    @Resource
    private PermissionService permissionService;

    @PostMapping("/permission")
    @ApiOperation(value = "新增菜单权限接口")
    @LogAnnotation(title = "菜单权限管理", action = "新增菜单权限")
    @RequiresPermissions("sys:permission:add")
    public DataResult addPermission(@RequestBody @Valid SysPermission sysPermission) {
        verifyByPid(sysPermission);
        sysPermission.setStatus(1);
        permissionService.save(sysPermission);
        return DataResult.success();
    }

    @DeleteMapping("/permission/{id}")
    @ApiOperation(value = "删除菜单权限接口")
    @LogAnnotation(title = "菜单权限管理", action = "删除菜单权限")
    @RequiresPermissions("sys:permission:delete")
    public DataResult deletePermission(@PathVariable("id") String id) {
        permissionService.deletePermission(id);
        return DataResult.success();
    }

    @PutMapping("/permission")
    @ApiOperation(value = "更新菜单权限接口")
    @LogAnnotation(title = "菜单权限管理", action = "更新菜单权限")
    @RequiresPermissions("sys:permission:update")
    public DataResult updatePermission(@RequestBody @Valid SysPermission sysPermission) {
        // 如果权限 id 为空
        if (StringUtils.isEmpty(sysPermission.getId())) {
            return DataResult.fail("id 不能为空");
        }
        // 根据当前权限 id 查询到对象的权限对象
        SysPermission sysPermission1 = permissionService.getById(sysPermission.getId());
        if (sysPermission1 == null) {
            throw new SysException(BaseResCode.DATA_ERROR);
        }
        // 判断两个权限的类型或者父 id 是否相同
        if (sysPermission1.getType().equals(sysPermission.getType()) || !sysPermission1.getPid().equals(sysPermission.getPid())) {
            verifyByPid(sysPermission);
        }

        permissionService.updatePermission(sysPermission);
        return DataResult.success();
    }

    @GetMapping("/permission/{id}")
    @ApiOperation(value = "查询菜单权限接口")
    @LogAnnotation(title = "菜单权限管理", action = "查询菜单权限")
    @RequiresPermissions("sys:permission:detail")
    public DataResult getPermissionDetail(@PathVariable("id") String id) {
        return DataResult.success(permissionService.getById(id));
    }

    @GetMapping("/permissions")
    @ApiOperation(value = "获取所有菜单权限接口")
    @LogAnnotation(title = "菜单权限管理", action = "获取所有菜单权限")
    @RequiresPermissions("sys:permission:list")
    public DataResult getAllMenusPermission() {
        return DataResult.success(permissionService.selectAllPermission());
    }

    @GetMapping("/permission/tree")
    @ApiOperation(value = "获取所有菜单目录树接口")
    @LogAnnotation(title = "菜单权限管理", action = "获取所有菜单目录树")
    @RequiresPermissions(value = {"sys:permission:update", "sys:permission:add"}, logical = Logical.OR)
    public DataResult getAllMenusPermissionTree(@RequestParam(required = false) String permissionId) {
        return DataResult.success(permissionService.selectAllMenuByTree(permissionId));
    }

    @GetMapping("/permission/tree/all")
    @ApiOperation(value = "获取所有目录菜单树接口")
    @LogAnnotation(title = "菜单权限管理", action = "获取所有菜单目录树")
    @RequiresPermissions(value = {"sys:role:update", "sys:role:add"}, logical = Logical.OR)
    public DataResult getAllPermissionTree() {
        return DataResult.success(permissionService.selectAllByTree());
    }

    /**
     * 根据父类型判断权限操作
     * 如果操作后的菜单类型是目录，那么父类型也必须是目录
     * 如果操作后的菜单类型是菜单，那么父类型必须是目录
     * 如果操作后的菜单类型是按钮，那么父类型必须是菜单
     *
     * @param sysPermission
     */
    private void verifyByPid(SysPermission sysPermission) {
        SysPermission parent = permissionService.getById(sysPermission.getPid());
        // 判断权限的类型
        switch (sysPermission.getType()) {
            case 1:
                // 如果父类型不为空
                if (parent != null) {
                    if (parent.getType() != 1) {
                        // 操作后的菜单类型是目录，所属菜单必须为默认顶级菜单或者目录
                        throw new SysException(BaseResCode.OPERATION_MENU_PERMISSION_CATALOG_ERROR);
                    }
                } else if (!"0".equals(sysPermission.getPid())) {
                    // 操作后的菜单类型是目录，所属菜单必须为默认顶级菜单或者目录
                    throw new SysException(BaseResCode.OPERATION_MENU_PERMISSION_CATALOG_ERROR);
                }
                break;
            case 2:
                if (parent == null || parent.getType() != 1) {
                    // 操作后的菜单类型是菜单，所属菜单必须为目录类型
                    throw new SysException(BaseResCode.OPERATION_MENU_PERMISSION_MENU_ERROR);
                }
                if (StringUtils.isEmpty(sysPermission.getUrl())) {
                    // 菜单权限的 url 不能为空
                    throw new SysException(BaseResCode.OPERATION_MENU_PERMISSION_URL_NOT_NULL);
                }
                break;
            case 3:
                if (parent == null || parent.getType() != 2) {
                    // 操作后的菜单类型是按钮，所属菜单必须为菜单类型
                    throw new SysException(BaseResCode.OPERATION_MENU_PERMISSION_BTN_ERROR);
                }
                if (StringUtils.isEmpty(sysPermission.getPermissions())) {
                    // 菜单权限的标识符不能为空
                    throw new SysException(BaseResCode.OPERATION_MENU_PERMISSION_URL_PERMS_NULL);
                }
                if (StringUtils.isEmpty(sysPermission.getUrl())) {
                    // 菜单权限的 url 不能为空
                    throw new SysException(BaseResCode.OPERATION_MENU_PERMISSION_URL_NOT_NULL);
                }
                break;
            default:
        }
    }
}
