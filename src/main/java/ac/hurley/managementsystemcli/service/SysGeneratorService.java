package ac.hurley.managementsystemcli.service;

import ac.hurley.managementsystemcli.entitiy.SysGenerator;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 生成代码 Service 类
 */
public interface SysGeneratorService {

    /**
     * 获取所有表
     *
     * @param page
     * @param sysGenerator
     * @return
     */
    IPage<SysGenerator> selectAllTables(Page<SysGenerator> page, SysGenerator sysGenerator);

    /**
     * 生成代码
     *
     * @param tables
     * @return
     */
    byte[] generatorCode(String[] tables);
}
