package ac.hurley.managementsystemcli.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author hurley
 */
@Data
public class RolePermissionOperationReqVO {

    @ApiModelProperty(value = "角色 Id")
    @NotBlank(message = "角色 Id 不能为空")
    private String roleId;

    @ApiModelProperty(value = "菜单权限集合")
    @NotBlank(message = "菜单权限集合不能为空")
    private List<String> permissionIds;
}
