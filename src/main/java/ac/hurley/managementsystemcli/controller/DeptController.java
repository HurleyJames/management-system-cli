package ac.hurley.managementsystemcli.controller;

import ac.hurley.managementsystemcli.common.DataResult;
import ac.hurley.managementsystemcli.common.annotation.LogAnnotation;
import ac.hurley.managementsystemcli.entitiy.SysDept;
import ac.hurley.managementsystemcli.service.DeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 组织部门管理
 */
@RequestMapping("/sys")
@RestController
@Api(tags = "组织模块-机构管理")
public class DeptController {

    @Resource
    private DeptService deptService;

    @PostMapping("/dept")
    @ApiOperation(value = "新增组织接口")
    @LogAnnotation(title = "机构管理", action = "新增组织")
    @RequiresPermissions("sys:dept:add")
    public DataResult addDept(@RequestBody @Valid SysDept sysDept) {
        deptService.addDept(sysDept);
        return DataResult.success();
    }

    @DeleteMapping("/dept/{id}")
    @ApiOperation(value = "删除组织接口")
    @LogAnnotation(title = "机构管理", action = "删除组织")
    @RequiresPermissions("sys:dept:delete")
    public DataResult deleteDept(@PathVariable("id") String id) {
        deptService.deleteDept(id);
        return DataResult.success();
    }

    @PutMapping("/dept")
    @ApiOperation(value = "更新组织接口")
    @LogAnnotation(title = "机构管理", action = "更新组织")
    @RequiresPermissions("sys:dept:update")
    public DataResult updateDept(@RequestBody SysDept sysDept) {
        if (StringUtils.isEmpty(sysDept.getId())) {
            return DataResult.fail("id 不能为空");
        }
        deptService.updateDept(sysDept);
        return DataResult.success();
    }

    @GetMapping("/dept/{id}")
    @ApiOperation(value = "查询组织详情接口")
    @LogAnnotation(title = "机构管理", action = "查询组织详情")
    @RequiresPermissions("sys:dept:detail")
    public DataResult getDeptDetail(@PathVariable("id") String id) {
        return DataResult.success(deptService.getById(id));
    }

    @GetMapping("/dept/tree")
    @ApiOperation(value = "树型组织列表接口")
    @LogAnnotation(title = "机构管理", action = "树型组织列表")
    @RequiresPermissions(value = {"sys:user:list", "sys:user:update", "sys:user:add", "sys:dept:update", "sys:dept:add"}, logical = Logical.OR)
    public DataResult getDeptTree(@RequestParam(required = false) String deptId) {
        return DataResult.success(deptService.deptTreeList(deptId, false));
    }

    @GetMapping("/depts")
    @ApiOperation(value = "获取机构列表接口")
    @LogAnnotation(title = "机构管理", action = "获取所有组织机构")
    @RequiresPermissions("sys:dept:list")
    public DataResult getAllDepts() {
        List<SysDept> deptList = deptService.list();
        deptList.parallelStream().forEach(entity -> {
            SysDept parentDept = deptService.getById(entity.getPid());
            if (parentDept != null) {
                entity.setPidName(parentDept.getName());
            }
        });
        return DataResult.success(deptList);
    }
}
