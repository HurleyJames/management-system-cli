package ac.hurley.managementsystemcli.service.impl;

import ac.hurley.managementsystemcli.entitiy.SysGenerator;
import ac.hurley.managementsystemcli.mapper.GeneratorMapper;
import ac.hurley.managementsystemcli.service.SysGeneratorService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成 Service 实现类
 */
@Service
@Slf4j
public class GeneratorServiceImpl implements SysGeneratorService {

    private final GeneratorMapper generatorMapper;

    public GeneratorServiceImpl(GeneratorMapper generatorMapper) {
        this.generatorMapper = generatorMapper;
    }


    /**
     * 选择所有的表
     *
     * @param page
     * @param sysGenerator
     * @return
     */
    @Override
    public IPage<SysGenerator> selectAllTables(Page<SysGenerator> page, SysGenerator sysGenerator) {
        return generatorMapper.selectAllTables(page, sysGenerator);
    }

    /**
     * 生成代码
     *
     * @param tables
     * @return
     */
    @Override
    public byte[] generatorCode(String[] tables) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);

        for (String tableName : tables) {
            // 查询表的信息
            Map<String, String> table = queryTable(tableName);
            // 查询列的信息
            List<Map<String, String>> columns = queryColumns(tableName);
            // 生成代码
        }
        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }

    /**
     * 查询表
     *
     * @param tableName
     * @return
     */
    public Map<String, String> queryTable(String tableName) {
        return generatorMapper.queryTable(tableName);
    }

    /**
     * 查询列
     *
     * @param tableName
     * @return
     */
    public List<Map<String, String>> queryColumns(String tableName) {
        return generatorMapper.queryColumns(tableName);
    }
}
