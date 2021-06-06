package ac.hurley.managementsystemcli.common.utils;


import ac.hurley.managementsystemcli.common.Constant;
import ac.hurley.managementsystemcli.common.exception.SysException;
import ac.hurley.managementsystemcli.entitiy.SysJob;
import ac.hurley.managementsystemcli.job.ScheduleJob;
import org.quartz.*;

/**
 * 定时任务工具类
 */
public class ScheduleUtils {

    /**
     * 后面接 jobId
     */
    private final static String JOB_NAME = "TASK_";

    /**
     * 获取触发器 key
     *
     * @param jobId
     * @return
     */
    public static TriggerKey getTriggerKey(String jobId) {
        return TriggerKey.triggerKey(JOB_NAME + jobId);
    }

    /**
     * 获取 Job Key
     *
     * @param jobId
     * @return
     */
    public static JobKey getJobKey(String jobId) {
        return JobKey.jobKey(JOB_NAME + jobId);
    }

    /**
     * 获取表达式触发器
     *
     * @param scheduler
     * @param jobId
     * @return
     */
    public static CronTrigger getCronTrigger(Scheduler scheduler, String jobId) {
        try {
            return (CronTrigger) scheduler.getTrigger(getTriggerKey(jobId));
        } catch (SchedulerException e) {
            throw new SysException("获取定时任务 CronTrigger 触发器异常");
        }
    }

    /**
     * 创建定时任务
     *
     * @param scheduler
     * @param sysJob
     */
    public static void createScheduleJob(Scheduler scheduler, SysJob sysJob) {
        try {
            // 创建 Job 信息
            JobDetail jobDetail = JobBuilder.newJob(ScheduleJob.class).withIdentity(getJobKey(sysJob.getId())).build();

            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder
                    .cronSchedule(sysJob.getCronExpression())
                    .withMisfireHandlingInstructionDoNothing();

            // 按照新的 CronExpression 表达式构架一个新的 Trigger
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(getTriggerKey(sysJob.getId())).forJob(jobDetail).withSchedule(scheduleBuilder).build();

            // 将参数放入 Job 中
            jobDetail.getJobDataMap().put(SysJob.JOB_PARAM_KEY, sysJob);

            scheduler.scheduleJob(jobDetail, trigger);

            if (Constant.SCHEDULER_STATUS_PAUSE.equals(sysJob.getStatus())) {
                // 暂停任务
                pauseJob(scheduler, sysJob.getId());
            }
        } catch (SchedulerException e) {
            throw new SysException("创建定时任务失败");
        }
    }

    /**
     * 更新定时任务
     *
     * @param scheduler
     * @param sysJob
     */
    public static void updateScheduleJob(Scheduler scheduler, SysJob sysJob) {
        try {
            // 获取触发器 Key
            TriggerKey triggerKey = getTriggerKey(sysJob.getId());

            // 表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder
                    .cronSchedule(sysJob.getCronExpression())
                    .withMisfireHandlingInstructionDoNothing();

            CronTrigger trigger = getCronTrigger(scheduler, sysJob.getId());

            // 按照新的 cronExpression 表达式重新构建 trigger
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();

            // 参数
            trigger.getJobDataMap().put(SysJob.JOB_PARAM_KEY, sysJob);

            // 重新分配任务
            scheduler.rescheduleJob(triggerKey, trigger);

            // 如果是暂停任务
            if (Constant.SCHEDULER_STATUS_PAUSE.equals(sysJob.getStatus())) {
                pauseJob(scheduler, sysJob.getId());
            }
        } catch (SchedulerException e) {
            throw new SysException("更新定时任务失败");
        }
    }

    /**
     * 立即执行定时任务
     *
     * @param scheduler
     * @param sysJob
     */
    public static void runJob(Scheduler scheduler, SysJob sysJob) {
        try {
            JobDataMap dataMap = new JobDataMap();
            dataMap.put(SysJob.JOB_PARAM_KEY, sysJob);
            // 触发定时任务
            scheduler.triggerJob(getJobKey(sysJob.getId()), dataMap);
        } catch (SchedulerException e) {
            throw new SysException("立即执行定时任务失败");
        }
    }

    /**
     * 暂停任务
     *
     * @param scheduler
     * @param jobId
     */
    public static void pauseJob(Scheduler scheduler, String jobId) {
        try {
            // 调用暂停任务的方法
            scheduler.pauseJob(getJobKey(jobId));
        } catch (SchedulerException e) {
            throw new SysException("暂停定时任务失败");
        }
    }

    /**
     * 恢复任务
     *
     * @param scheduler
     * @param jobId
     */
    public static void resumeJob(Scheduler scheduler, String jobId) {
        try {
            // 调用恢复任务的方法
            scheduler.resumeJob(getJobKey(jobId));
        } catch (SchedulerException e) {
            throw new SysException("恢复定时任务失败");
        }
    }

    /**
     * 删除定时任务
     *
     * @param scheduler
     * @param jobId
     */
    public static void deleteJob(Scheduler scheduler, String jobId) {
        try {
            scheduler.deleteJob(getJobKey(jobId));
        } catch (SchedulerException e) {
            throw new SysException("删除定时任务失败");
        }
    }
}
