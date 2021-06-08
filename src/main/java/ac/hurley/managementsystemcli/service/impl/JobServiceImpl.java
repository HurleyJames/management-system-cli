package ac.hurley.managementsystemcli.service.impl;

import ac.hurley.managementsystemcli.common.Constant;
import ac.hurley.managementsystemcli.common.exception.SysException;
import ac.hurley.managementsystemcli.common.utils.ScheduleUtils;
import ac.hurley.managementsystemcli.entitiy.SysJob;
import ac.hurley.managementsystemcli.mapper.JobMapper;
import ac.hurley.managementsystemcli.service.JobService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * 定时任务 Service 实现类
 */
@Service("jobService")
public class JobServiceImpl extends ServiceImpl<JobMapper, SysJob> implements JobService {

    @Resource
    private Scheduler scheduler;
    @Resource
    private JobMapper jobMapper;

    @Override
    public void saveJob(SysJob sysJob) {
        // 设置为正常的工作状态
        sysJob.setStatus(Constant.SCHEDULER_STATUS_NORMAL);
        this.save(sysJob);

        // 创建 Job
        ScheduleUtils.createScheduleJob(scheduler, sysJob);
    }

    @Override
    public void updateJobById(SysJob sysJob) {
        SysJob sysJob1 = this.getById(sysJob.getId());
        if (sysJob1 == null) {
            throw new SysException("获取定时任务异常");
        }
        // 设置状态
        sysJob.setStatus(sysJob1.getStatus());
        // 更新状态
        ScheduleUtils.updateScheduleJob(scheduler, sysJob);

        this.updateById(sysJob);
    }

    @Override
    public void deleteJob(List<String> ids) {
        for (String jobId : ids) {
            ScheduleUtils.deleteJob(scheduler, jobId);
        }
        jobMapper.deleteBatchIds(ids);
    }

    @Override
    public void runJob(List<String> ids) {
        for (String jobId : ids) {
            ScheduleUtils.runJob(scheduler, this.getById(jobId));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pauseJob(List<String> ids) {
        for (String jobId : ids) {
            ScheduleUtils.pauseJob(scheduler, jobId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resumeJob(List<String> ids) {
        for (String jobId : ids) {
            ScheduleUtils.resumeJob(scheduler, jobId);
        }

        // 批量更新状态
        updateBatch(ids, Constant.SCHEDULER_STATUS_NORMAL);
    }

    @Override
    public void updateBatch(List<String> ids, int status) {
        ids.parallelStream().forEach(id -> {
            SysJob sysJob = new SysJob();
            sysJob.setId(id);
            sysJob.setStatus(status);
            baseMapper.updateById(sysJob);
        });
    }

    @PostConstruct
    public void init() {
        // 系统任务队列集合
        List<SysJob> jobList = this.list();
        for (SysJob sysJob : jobList) {
            CronTrigger cronTrigger = ScheduleUtils.getCronTrigger(scheduler, sysJob.getId());
            if (cronTrigger == null) {
                // 如果没有就创建
                ScheduleUtils.createScheduleJob(scheduler, sysJob);
            } else {
                // 如果已经有就更新
                ScheduleUtils.updateScheduleJob(scheduler, sysJob);
            }
        }
    }
}
