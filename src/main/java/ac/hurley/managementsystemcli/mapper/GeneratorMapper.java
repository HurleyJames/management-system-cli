package ac.hurley.managementsystemcli.mapper;

import ac.hurley.managementsystemcli.entitiy.SysGenerator;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author hurley
 */
public interface GeneratorMapper extends BaseMapper<SysGenerator> {

    /**
     * 选择所有的表
     *
     * @param page
     * @param sysGenerator
     * @return
     */
    IPage<SysGenerator> selectAllTables(Page<SysGenerator> page,
                                        @Param(value = "sysGenerator") SysGenerator sysGenerator);

    /**
     * 查询表
     *
     * @param tableName
     * @return
     */
    Map<String, String> queryTable(String tableName);

    /**
     * 查询表中的某些属性列
     *
     * @param tableName
     * @return
     */
    List<Map<String, String>> queryColumns(String tableName);

}
