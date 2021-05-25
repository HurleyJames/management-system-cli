package ac.hurley.managementsystemcli.entitiy;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 系统内用户实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysUser extends BaseEntity implements Serializable {

    /**
     * 表的 Id
     */
    @TableId
    private String id;

    /**
     * 用户名
     */
    @NotBlank(message = "账号不能为空")
    private String username;

    /**
     * 盐：加盐密码
     */
    private String salt;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 旧密码
     */
    @TableField(exist = false)
    private String oldPwd;

    /**
     * 新密码
     */
    @TableField(exist = false)
    private String newPwd;

    /**
     * 密码
     */
    private String phone;

    /**
     * 所属部门 Id
     */
    private String deptId;

    /**
     * 所属部门名称
     */
    @TableField(exist = false)
    private String deptName;

    /**
     * 所属部门的 No.
     */
    @TableField(exist = false)
    private String deptNo;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 性别
     */
    private Integer sex;

    /**
     * 是否删除
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;

    /**
     * 创建的 Id
     */
    private String createId;

    /**
     * 更新的 Id
     */
    private String updateId;

    private Integer createWhere;

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

    /**
     * 开始加入时间
     */
    @TableField(exist = false)
    private String startTime;

    /**
     * 结束时间
     */
    @TableField(exist = false)
    private String endTime;

    /**
     * 所属的角色 Id
     */
    @TableField(exist = false)
    private List<String> roleIds;

    /**
     * 验证码
     */
    @TableField(exist = false)
    private String captcha;
}
