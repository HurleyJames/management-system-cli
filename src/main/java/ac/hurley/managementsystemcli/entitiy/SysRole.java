package ac.hurley.managementsystemcli.entitiy;

import ac.hurley.managementsystemcli.vo.res.DeptResVO;
import ac.hurley.managementsystemcli.vo.res.PermissionResVO;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 角色实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class SysRole extends BaseEntity implements Serializable {

    /**
     * 表的 Id
     */
    @TableId
    private String id;

    /**
     * 表名
     */
    @NotBlank(message = "名称不能为空")
    private String name;

    /**
     * 备注描述
     * 不能命名为 desc，会与数据库关键字冲突
     */
    private String description;

    /**
     * 状态
     */
    private Integer status;

    private Integer dataScope;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;

    @TableField(exist = false)
    private String startTime;

    @TableField(exist = false)
    private String endTime;

    @TableField(exist = false)
    private List<String> permissions;

    @TableField(exist = false)
    private List<PermissionResVO> permissionResVOS;

    @TableField(exist = false)
    private List<DeptResVO> deptResVOS;

    /**
     * 部门列表
     */
    @TableField(exist = false)
    private List<String> depts;
}
