package ac.hurley.managementsystemcli.controller;

import ac.hurley.managementsystemcli.common.DataResult;
import ac.hurley.managementsystemcli.entitiy.SysDict;
import ac.hurley.managementsystemcli.entitiy.SysDictDetail;
import ac.hurley.managementsystemcli.service.DictDetailService;
import ac.hurley.managementsystemcli.service.DictService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
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

@RequestMapping("/sysDict")
@RestController
@Api(tags = "字典管理")
public class DictController {

    @Resource
    private DictService dictService;
    @Resource
    private DictDetailService dictDetailService;

    @PostMapping("/dict")
    @ApiOperation(value = "新增")
    @RequiresPermissions("sys:dict:add")
    public DataResult addDict(@RequestBody SysDict sysDict) {
        if (StringUtils.isEmpty(sysDict.getName())) {
            return DataResult.fail("字典名称不能为空");
        }
        SysDict dict = dictService.getOne(Wrappers.<SysDict>lambdaQuery().eq(SysDict::getName, sysDict.getName()));
        if (dict != null) {
            return DataResult.fail("字典名称已存在");
        }
        dictService.save(sysDict);
        return DataResult.success();
    }

    @DeleteMapping("/dict")
    @ApiOperation(value = "删除")
    @RequiresPermissions("sys:dict:delete")
    public DataResult deleteDict(@RequestBody @ApiParam(value = "id 集合") List<String> ids) {
        dictService.removeByIds(ids);
        // 删除字典 Detail
        dictDetailService.remove(Wrappers.<SysDictDetail>lambdaQuery().in(SysDictDetail::getDictId, ids));
        return DataResult.success();
    }

    @PutMapping("/dict")
    @ApiOperation(value = "更新")
    @RequiresPermissions("sys:dict:update")
    public DataResult updateDict(@RequestBody SysDict sysDict) {
        if (StringUtils.isEmpty(sysDict.getName())) {
            return DataResult.fail("字典名称不能为空");
        }

        SysDict dict = dictService.getOne(Wrappers.<SysDict>lambdaQuery().eq(SysDict::getName, sysDict.getName()));
        if (dict != null && !dict.getId().equals(sysDict.getId())) {
            return DataResult.fail("字典名称已存在");
        }

        dictService.updateById(sysDict);
        return DataResult.success();
    }

    @PostMapping("/list")
    @ApiOperation(value = "查询分页数据")
    @RequiresPermissions("sys:dict:list")
    public DataResult pageInfo(@RequestBody SysDict sysDict) {
        Page page = new Page(sysDict.getPage(), sysDict.getLimit());
        LambdaQueryWrapper<SysDict> queryWrapper = Wrappers.lambdaQuery();

        if (!StringUtils.isEmpty(sysDict.getName())) {
            queryWrapper.like(SysDict::getName, sysDict.getName());
            queryWrapper.or();
            queryWrapper.like(SysDict::getRemark, sysDict.getName());
        }

        // 根据名字来排序
        queryWrapper.orderByAsc(SysDict::getName);
        IPage<SysDict> iPage = dictService.page(page, queryWrapper);
        return DataResult.success(iPage);
    }
}
