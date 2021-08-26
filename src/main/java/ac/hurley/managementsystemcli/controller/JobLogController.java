package ac.hurley.managementsystemcli.controller;

import ac.hurley.managementsystemcli.common.DataResult;
import ac.hurley.managementsystemcli.common.annotation.LogAnnotation;
import ac.hurley.managementsystemcli.entitiy.SysJobLog;
import ac.hurley.managementsystemcli.service.JobLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author hurley
 */
@RequestMapping("/sysJobLog")
@RestController
@Api(tags = "定时任务日志")
public class JobLogController {

    @Resource
    private JobLogService jobLogService;

    @PostMapping("/list")
    @ApiOperation(value = "查询分页数据")
    @RequiresPermissions("sys:job:list")
    public DataResult pageInfo(@RequestBody SysJobLog sysJobLog) {
        Page page = new Page(sysJobLog.getPage(), sysJobLog.getLimit());
        LambdaQueryWrapper<SysJobLog> queryWrapper = Wrappers.lambdaQuery();

        if (!StringUtils.isEmpty(sysJobLog.getJobId())) {
            queryWrapper.like(SysJobLog::getJobId, sysJobLog.getJobId());
        }
        queryWrapper.orderByDesc(SysJobLog::getCreateTime);

        IPage<SysJobLog> iPage = jobLogService.page(page, queryWrapper);
        return DataResult.success(iPage);
    }

    @DeleteMapping("/jobLog")
    @ApiOperation(value = "清空定时任务日志")
    @LogAnnotation(title = "清空")
    @RequiresPermissions("sys:job:delete")
    public DataResult deleteJobLog() {
        jobLogService.remove(Wrappers.emptyWrapper());
        return DataResult.success();
    }
}
