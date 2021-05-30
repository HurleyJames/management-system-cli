package ac.hurley.managementsystemcli.job;

import ac.hurley.managementsystemcli.common.DataResult;
import ac.hurley.managementsystemcli.common.utils.SpringContextUtils;
import ac.hurley.managementsystemcli.entitiy.SysJob;
import ac.hurley.managementsystemcli.entitiy.SysJobLog;
import ac.hurley.managementsystemcli.service.JobLogService;
import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.lang.reflect.Method;

/**
 * 定时任务
 */
public class ScheduleJob extends QuartzJobBean {

    private Logger logger = LoggerFactory.getLogger(getClass());

    final JobLogService jobLogService;

    public ScheduleJob(JobLogService jobLogService) {
        this.jobLogService = jobLogService;
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        SysJob sysJob = (SysJob) jobExecutionContext.getMergedJobDataMap().get(SysJob.JOB_PARAM_KEY);

        // 获取 Spring Bean
        JobLogService jobLogService = (JobLogService) SpringContextUtils.getBean("sysJobLogService");

        // 数据库保存执行记录
        SysJobLog log = new SysJobLog();
        log.setJobId(sysJob.getId());
        log.setBeanName(sysJob.getBeanName());
        log.setParams(sysJob.getParams());

        // 任务开始执行时间
        long startTime = System.currentTimeMillis();

        try {
            // 执行任务
            logger.debug("任务准备执行，任务 ID：" + sysJob.getId());

            Object target = SpringContextUtils.getBean(sysJob.getBeanName());
            assert target != null;
            Method method = target.getClass().getDeclaredMethod("run", String.class);
            method.invoke(target, sysJob.getParams());

            // 任务执行总时长
            long times = System.currentTimeMillis() - startTime;
            log.setTimes((int) times);
            // 任务状态    0：成功    1：失败
            log.setStatus(0);

            logger.debug("任务执行完毕，任务 ID：" + sysJob.getId() + "  总共耗时：" + times + "毫秒");
        } catch (Exception e) {
            logger.error("任务执行失败，任务 ID：" + sysJob.getId(), e);

            // 任务执行总时长
            long times = System.currentTimeMillis() - startTime;
            log.setTimes((int) times);

            // 任务状态    0：成功    1：失败
            log.setStatus(1);
            log.setError(StringUtils.substring(e.toString(), 0, 2000));
            e.printStackTrace();
        } finally {
            assert jobLogService != null;
            jobLogService.save(log);
        }
    }

    /**
     * 判断 Bean 是否有效
     *
     * @param beanName
     * @return
     */
    public static DataResult isBeanValid(String beanName) {
        if (org.springframework.util.StringUtils.isEmpty(beanName)) {
            return DataResult.fail("Spring Bean 名称不能为空");
        }

        Object target = SpringContextUtils.getBean(beanName);
        if (target == null) {
            return DataResult.fail("Spring Bean 不存在，请重新检查");
        }

        Method method;
        try {
            method = target.getClass().getDeclaredMethod("run", String.class);
        } catch (Exception e) {
            return DataResult.fail("Spring Bean 中的 run 方法不存在，请重新检查");
        }
        if (method == null) {
            return DataResult.fail("Spring Bean 中的 run 方法不存在，请重新检查");
        }

        return DataResult.success();
    }
}
