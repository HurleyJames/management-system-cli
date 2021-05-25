package ac.hurley.managementsystemcli.entitiy;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 定时任务日志实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_job_log")
public class SysJobLog extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务日志表的主键 Id
     */
    @TableId("id")
    private String id;

    /**
     * 任务 Id
     */
    private String jobId;

    /**
     * Spring Bean 的名称
     */
    private String beanName;

    /**
     * 参数
     */
    private String params;

    /**
     * 任务状态
     * 0：正常 1：暂停
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String error;

    /**
     * 耗时（单位：毫秒）
     */
    private Integer times;

    /**
     * 创建时间
     */
    private Date createTime;
}
