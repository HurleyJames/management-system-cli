package ac.hurley.managementsystemcli.vo.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class HomeResVO {

    @ApiModelProperty(value = "用户信息")
    private UserResVO userResVO;
    @ApiModelProperty(value = "目录菜单")
    private List<PermissionResVO> menus;
}
