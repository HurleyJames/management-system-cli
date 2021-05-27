package ac.hurley.managementsystemcli.vo.res;

import ac.hurley.managementsystemcli.entitiy.SysRole;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class UserRoleResVO {

    @ApiModelProperty("所有角色集合")
    private List<SysRole> allRole;

    @ApiModelProperty(value = "用户所拥有角色集合")
    private List<String> userRole;
}
