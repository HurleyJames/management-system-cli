package ac.hurley.managementsystemcli.controller;

import ac.hurley.managementsystemcli.common.DataResult;
import ac.hurley.managementsystemcli.common.annotation.DataScope;
import ac.hurley.managementsystemcli.entitiy.SysContent;
import ac.hurley.managementsystemcli.service.ContentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping("/sysContent")
@Api(tags = "文章管理")
@RestController
public class ContentController {

    @Resource
    private ContentService contentService;

    @PostMapping("/content")
    @ApiOperation(value = "新增")
    @RequiresPermissions("sys:content:add")
    public DataResult addContent(@RequestBody SysContent sysContent) {
        contentService.save(sysContent);
        return DataResult.success();
    }

    @DeleteMapping("/content")
    @ApiOperation(value = "删除")
    @RequiresPermissions("sys:content:delete")
    public DataResult deleteContent(@RequestBody @ApiParam(value = "id 集合") List<String> ids) {
        contentService.removeByIds(ids);
        return DataResult.success();
    }

    @PutMapping("/content")
    @ApiOperation(value = "更新")
    @RequiresPermissions("sys:content:update")
    public DataResult updateContent(@RequestBody SysContent sysContent) {
        contentService.updateById(sysContent);
        return DataResult.success();
    }

    @PostMapping("/list")
    @ApiOperation(value = "查询分页数据")
    @RequiresPermissions("sys:content:list")
    @DataScope
    public DataResult pageInfo(@RequestBody SysContent sysContent) {
        Page page = new Page(sysContent.getPage(), sysContent.getLimit());
        LambdaQueryWrapper<SysContent> queryWrapper = Wrappers.lambdaQuery();

        if (!StringUtils.isEmpty(sysContent.getTitle())) {
            queryWrapper.like(SysContent::getTitle, sysContent.getTitle());
        }

        if (!CollectionUtils.isEmpty(sysContent.getCreateIds())) {
            queryWrapper.in(SysContent::getCreateId, sysContent.getCreateIds());
        }

        IPage<SysContent> iPage = contentService.page(page, queryWrapper);
        return DataResult.success(iPage);
    }

}
