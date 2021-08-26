package ac.hurley.managementsystemcli.service.impl;

import ac.hurley.managementsystemcli.entitiy.SysLog;
import ac.hurley.managementsystemcli.mapper.LogMapper;
import ac.hurley.managementsystemcli.service.LogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 系统日志 Service 实现类
 *
 * @author hurley
 */
@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, SysLog> implements LogService {
}
