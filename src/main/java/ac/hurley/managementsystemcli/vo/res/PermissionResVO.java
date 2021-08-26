package ac.hurley.managementsystemcli.vo.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author hurley
 */
@Data
public class PermissionResVO {

    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "菜单权限名称")
    private String title;
    @ApiModelProperty(value = "菜单权限标识，Shiro 适配 RESTFUL")
    private String permissions;
    @ApiModelProperty(value = "接口地址")
    private String url;
    @ApiModelProperty(value = "icon")
    private String icon;

    private String target;
    @ApiModelProperty(value = "父级id")
    private String pid;
    @ApiModelProperty(value = "父级名称")
    private String pidName;
    @ApiModelProperty(value = "菜单权限类型(1:目录;2:菜单;3:按钮)")
    private Integer type;
    @ApiModelProperty(value = "排序码")
    private Integer orderNum;
    @ApiModelProperty(value = "是否展开 默认不展开(false)")
    private boolean spread = true;
    @ApiModelProperty(value = "是否选中 默认 false")
    private boolean checked;

    private List<?> children;
}
