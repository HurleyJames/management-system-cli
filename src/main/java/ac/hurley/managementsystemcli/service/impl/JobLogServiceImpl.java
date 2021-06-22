package ac.hurley.managementsystemcli.service.impl;

import ac.hurley.managementsystemcli.entitiy.SysJobLog;
import ac.hurley.managementsystemcli.mapper.JobLogMapper;
import ac.hurley.managementsystemcli.service.JobLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 任务日志 Service 实现类
 */
@Service("jobLogService")
public class JobLogServiceImpl extends ServiceImpl<JobLogMapper, SysJobLog> implements JobLogService {
}
