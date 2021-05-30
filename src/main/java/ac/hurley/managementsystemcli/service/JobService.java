package ac.hurley.managementsystemcli.service;

import ac.hurley.managementsystemcli.entitiy.SysJob;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 定时任务 Service 类
 */
public interface JobService extends IService<SysJob> {

    /**
     * 保存 Job
     *
     * @param sysJob
     */
    void saveJob(SysJob sysJob);

    /**
     * 根据 Id 更新 Job
     *
     * @param sysJob
     */
    void updateJobById(SysJob sysJob);

    /**
     * 删除 Job
     *
     * @param ids
     */
    void deleteJob(List<String> ids);

    /**
     * 运行 Job
     *
     * @param ids
     */
    void runJob(List<String> ids);

    /**
     * 暂停 Job
     *
     * @param ids
     */
    void pauseJob(List<String> ids);

    /**
     * 继续运行 Job
     *
     * @param ids
     */
    void resumeJob(List<String> ids);

    /**
     * 批量更新状态
     *
     * @param ids
     * @param status
     */
    void updateBatch(List<String> ids, int status);
}
