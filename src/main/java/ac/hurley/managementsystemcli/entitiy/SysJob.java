package ac.hurley.managementsystemcli.entitiy;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 定时任务实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_job")
public class SysJob extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务调度参数 Key
     */
    public static final String JOB_PARAM_KEY = "JOB_PARAM_KEY";

    /**
     * 任务 Id
     */
    @TableId("id")
    private String id;

    /**
     * Spring Bean 的名称
     */
    @TableField("bean_name")
    private String beanName;

    /**
     * 参数
     */
    @TableField("params")
    private String params;

    /**
     * cron 表达式
     */
    @TableField("cron_expression")
    private String cronExpression;

    /**
     * 任务状态
     * 0：正常 1：暂停
     */
    @TableField("status")
    private Integer status;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;
}
