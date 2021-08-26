package ac.hurley.managementsystemcli.entitiy;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 角色部门实体类
 *
 * @author hurley
 */
@Data
@TableName("sys_role_dept")
public class SysRoleDept extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId("id")
    private String id;

    /**
     * 角色 Id
     */
    @TableField("role_id")
    private String roleId;

    /**
     * 菜单权限 Id
     */
    @TableField("dept_id")
    private String deptId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;
}
