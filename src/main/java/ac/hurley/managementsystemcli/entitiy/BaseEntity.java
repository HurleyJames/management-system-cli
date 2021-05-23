package ac.hurley.managementsystemcli.entitiy;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.List;

/**
 * 所有实体类的基类
 */
@Data
public class BaseEntity {

    /**
     * 页号
     */
    @JSONField(serialize = false)
    @TableField(exist = false)
    private int page = 1;

    /**
     * 每页的条目个数
     */
    @JSONField(serialize = false)
    @TableField(exist = false)
    private int limit = 10;

    /**
     * 数据权限：用户 id
     */
    @TableField(exist = false)
    private List<String> createIds;
}
