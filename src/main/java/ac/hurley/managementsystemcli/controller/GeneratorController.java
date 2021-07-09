package ac.hurley.managementsystemcli.controller;

import ac.hurley.managementsystemcli.common.DataResult;
import ac.hurley.managementsystemcli.entitiy.SysGenerator;
import ac.hurley.managementsystemcli.service.SysGeneratorService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequestMapping("/sysGenerator")
@RestController
@Api(tags = "系统模块-代码生成")
@Slf4j
public class GeneratorController {

    @Resource
    private SysGeneratorService sysGeneratorService;

    @GetMapping("/gen")
    @ApiOperation(value = "生成")
    @RequiresPermissions("sys:generator:add")
    public void generateCode(String tables, HttpServletResponse response) throws IOException {
        byte[] data = sysGeneratorService.generatorCode(tables.split(","));

        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"manager.zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");

        IOUtils.write(data, response.getOutputStream());
    }

    @PostMapping("/list")
    @ApiOperation(value = "查询分页数据")
    @RequiresPermissions("sys:generator:list")
    public DataResult pageInfo(@RequestBody SysGenerator sysGenerator) {
        Page page = new Page(sysGenerator.getPage(), sysGenerator.getLimit());
        IPage<SysGenerator> iPage = sysGeneratorService.selectAllTables(page, sysGenerator);
        return DataResult.success(iPage);
    }
}
