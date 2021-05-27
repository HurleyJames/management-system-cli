package ac.hurley.managementsystemcli.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class UserRoleOperationReqVO {

    @ApiModelProperty(value = "用户 Id")
    @NotBlank(message = "用户 Id 不能为空")
    private String userId;

    @ApiModelProperty(value = "角色 Id 集合")
    @NotEmpty(message = "角色 Id 集合不能为空")
    private List<String> roleIds;
}
