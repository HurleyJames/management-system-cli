package ac.hurley.managementsystemcli.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 测试定时任务（演示 Demo，可以删除）
 * testTask 是 Spring Bean 的名称，方法名必须是 run()
 */
@Component("testTask")
public class TestTask {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public void run(String params) {
        logger.debug("TestTask 定时任务正在执行，参数为：{}", params);
    }
}
