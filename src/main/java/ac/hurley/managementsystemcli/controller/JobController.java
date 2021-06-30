package ac.hurley.managementsystemcli.controller;

import ac.hurley.managementsystemcli.common.DataResult;
import ac.hurley.managementsystemcli.common.annotation.LogAnnotation;
import ac.hurley.managementsystemcli.common.exception.code.BaseResCode;
import ac.hurley.managementsystemcli.entitiy.SysJob;
import ac.hurley.managementsystemcli.job.ScheduleJob;
import ac.hurley.managementsystemcli.service.JobService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.quartz.TriggerUtils;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "定时任务")
@RestController
@RequestMapping("/sysJob")
public class JobController {

    @Resource
    private JobService jobService;

    @PostMapping("/job")
    @ApiOperation(value = "新增")
    @LogAnnotation(title = "新增")
    @RequiresPermissions("sys:job:add")
    public DataResult addJob(@RequestBody SysJob sysJob) {
        if (isExpressionNotValid(sysJob.getCronExpression())) {
            return DataResult.fail("cron 表达式有误");
        }
        DataResult dataResult = ScheduleJob.isBeanValid(sysJob.getBeanName());
        if (BaseResCode.SUCCESS.getCode() != dataResult.getCode()) {
            return dataResult;
        }

        jobService.saveJob(sysJob);
        return DataResult.success();
    }

    @DeleteMapping("/job")
    @ApiOperation(value = "删除")
    @LogAnnotation(title = "删除")
    @RequiresPermissions("sys:job:delete")
    public DataResult deleteJob(@RequestBody @ApiParam(value = "id 集合") List<String> ids) {
        jobService.deleteJob(ids);
        return DataResult.success();
    }

    @PutMapping("/job")
    @ApiOperation(value = "更新")
    @LogAnnotation(title = "更新")
    @RequiresPermissions("sys:job:update")
    public DataResult updateJob(@RequestBody SysJob sysJob) {
        if (isExpressionNotValid(sysJob.getCronExpression())) {
            return DataResult.fail("cron 表达式有误");
        }
        DataResult dataResult = ScheduleJob.isBeanValid(sysJob.getBeanName());
        if (BaseResCode.SUCCESS.getCode() != dataResult.getCode()) {
            return dataResult;
        }

        jobService.updateJobById(sysJob);
        return DataResult.success();
    }

    @PostMapping("jobs")
    @ApiOperation(value = "查询分页数据")
    @RequiresPermissions("sys:job:list")
    public DataResult pageInfo(@RequestBody SysJob sysJob) {
        Page page = new Page(sysJob.getPage(), sysJob.getLimit());
        LambdaQueryWrapper<SysJob> queryWrapper = Wrappers.lambdaQuery();

        if (!StringUtils.isEmpty(sysJob.getBeanName())) {
            queryWrapper.like(SysJob::getBeanName, sysJob.getBeanName());
        }

        IPage<SysJob> iPage = jobService.page(page, queryWrapper);
        return DataResult.success(iPage);
    }

    @PostMapping("/run")
    @ApiOperation(value = "立即执行任务")
    @LogAnnotation(title = "立即执行任务")
    @RequiresPermissions("sys:job:run")
    public DataResult runJob(@RequestBody List<String> ids) {
        jobService.runJob(ids);
        return DataResult.success();
    }

    @PostMapping("/pause")
    @ApiOperation(value = "暂停定时任务")
    @LogAnnotation(title = "暂停定时任务")
    @RequiresPermissions("sys:job:pause")
    public DataResult pauseJob(@RequestBody List<String> ids) {
        jobService.pauseJob(ids);
        return DataResult.success();
    }

    @PostMapping("/resume")
    @ApiOperation(value = "恢复定时任务")
    @LogAnnotation(title = "恢复定时任务")
    @RequiresPermissions("sys:job:resume")
    public DataResult resumeJob(@RequestBody List<String> ids) {
        jobService.resumeJob(ids);
        return DataResult.success();
    }

    @PostMapping("/getRecentTriggerTime")
    @ApiOperation(value = "获取运行时间")
    @LogAnnotation(title = "获取运行时间")
    @RequiresPermissions("sys:job:add")
    public DataResult getRecentTriggerTime(String cron) {
        List<String> list = new ArrayList<>();
        try {
            CronTriggerImpl cronTrigger = new CronTriggerImpl();
            cronTrigger.setCronExpression(cron);

            List<Date> dates = TriggerUtils.computeFireTimes(cronTrigger, null, 5);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (Date date : dates) {
                list.add(dateFormat.format(date));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return DataResult.success(list);
    }

    /**
     * 判断 cron 表达式是否有效
     * true 代表无效，false 代表有效
     *
     * @param expression cron 表达式
     * @return
     */
    public static boolean isExpressionNotValid(String expression) {
        CronTriggerImpl trigger = new CronTriggerImpl();
        try {
            trigger.setCronExpression(expression);
            Date date = trigger.computeFirstFireTime(null);
            return date == null || !date.after(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
