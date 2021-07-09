package ac.hurley.managementsystemcli.controller;

import ac.hurley.managementsystemcli.common.DataResult;
import ac.hurley.managementsystemcli.entitiy.SysFiles;
import ac.hurley.managementsystemcli.service.FileService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * 文件上传 Controller 类
 */
@RequestMapping("/sysFile")
@RestController
@Api(tags = "文件管理")
public class FileController {

    @Resource
    private FileService fileService;

    @PostMapping("/file")
    @ApiOperation(value = "新增")
    @RequiresPermissions(value = {"sys:file:add", "sys:content:add", "sys:content:update"}, logical = Logical.OR)
    public DataResult addFile(@RequestParam(value = "file") MultipartFile file) {
        if (file == null || file.getOriginalFilename() == null || "".equalsIgnoreCase(file.getOriginalFilename().trim())) {
            return DataResult.fail("文件为空");
        }
        return fileService.saveFile(file);
    }

    @DeleteMapping("/file")
    @ApiOperation(value = "删除")
    @RequiresPermissions("sys:file:delete")
    public DataResult deleteFile(@RequestBody @ApiParam(value = "id 集合") List<String> ids) {
        fileService.removeByIdsAndFiles(ids);
        return DataResult.success();
    }

    @PostMapping("/list")
    @ApiOperation(value = "查询分页数据")
    @RequiresPermissions("sys:file:list")
    public DataResult pageInfo(@RequestBody SysFiles sysFiles) {
        Page page = new Page(sysFiles.getPage(), sysFiles.getLimit());
        IPage<SysFiles> iPage = fileService.page(page, Wrappers.<SysFiles>lambdaQuery()
                .orderByDesc(SysFiles::getCreateDate));
        return DataResult.success(iPage);
    }
}
