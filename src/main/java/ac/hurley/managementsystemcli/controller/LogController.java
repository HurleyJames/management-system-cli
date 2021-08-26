package ac.hurley.managementsystemcli.controller;

import ac.hurley.managementsystemcli.common.DataResult;
import ac.hurley.managementsystemcli.common.annotation.LogAnnotation;
import ac.hurley.managementsystemcli.entitiy.SysLog;
import ac.hurley.managementsystemcli.service.LogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author hurley
 */
@RequestMapping("/sys")
@Api(tags = "系统模块-系统操作日志管理")
@RestController
public class LogController {

    @Resource
    private LogService logService;

    @PostMapping("/logs")
    @ApiOperation(value = "分页查询系统操作日志接口")
    @LogAnnotation(title = "系统操作日志管理", action = "分页查询系统操作日志")
    @RequiresPermissions("sys:log:list")
    public DataResult pageInfo(@RequestBody SysLog sysLog) {
        Page page = new Page(sysLog.getPage(), sysLog.getLimit());
        LambdaQueryWrapper<SysLog> queryWrapper = Wrappers.lambdaQuery();
        if (!StringUtils.isEmpty(sysLog.getUsername())) {
            queryWrapper.like(SysLog::getUsername, sysLog.getUsername());
        }
        if (!StringUtils.isEmpty(sysLog.getOperation())) {
            queryWrapper.like(SysLog::getOperation, sysLog.getOperation());
        }
        if (!StringUtils.isEmpty(sysLog.getStartTime())) {
            queryWrapper.gt(SysLog::getCreateTime, sysLog.getStartTime());
        }
        if (!StringUtils.isEmpty(sysLog.getEndTime())) {
            queryWrapper.lt(SysLog::getCreateTime, sysLog.getEndTime());
        }

        queryWrapper.orderByDesc(SysLog::getCreateTime);
        return DataResult.success(logService.page(page, queryWrapper));
    }

    @DeleteMapping("/logs")
    @ApiOperation(value = "删除日志接口")
    @LogAnnotation(title = "系统操作日志管理", action = "删除系统操作日志")
    @RequiresPermissions("sys:log:delete")
    public DataResult deleteLog(@RequestBody List<String> logIds) {
        logService.removeByIds(logIds);
        return DataResult.success();
    }
}
