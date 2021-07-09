package ac.hurley.managementsystemcli.controller;

import ac.hurley.managementsystemcli.common.DataResult;
import ac.hurley.managementsystemcli.entitiy.SysDict;
import ac.hurley.managementsystemcli.entitiy.SysDictDetail;
import ac.hurley.managementsystemcli.service.DictDetailService;
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

@RequestMapping("/sysDictDetail")
@RestController
@Api(tags = "字典明细管理")
public class DictDetailController {

    @Resource
    private DictDetailService dictDetailService;

    @PostMapping("/dictDetail")
    @ApiOperation(value = "新增")
    @RequiresPermissions("sys:dict:add")
    public DataResult addDictDetail(@RequestBody SysDictDetail sysDictDetail) {
        if (StringUtils.isEmpty(sysDictDetail.getValue())) {
            return DataResult.fail("字典值不能为空");
        }

        LambdaQueryWrapper<SysDictDetail> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SysDictDetail::getValue, sysDictDetail.getValue());
        queryWrapper.eq(SysDictDetail::getDictId, sysDictDetail.getDictId());

        SysDictDetail dictDetail = dictDetailService.getOne(queryWrapper);
        if (dictDetail != null) {
            return DataResult.fail("字典名称-字典值已存在");
        }
        dictDetailService.save(sysDictDetail);
        return DataResult.success();
    }

    @DeleteMapping("/dictDetail")
    @ApiOperation(value = "删除")
    @RequiresPermissions("sys:dict:delete")
    public DataResult deleteDictDetail(@RequestBody @ApiParam(value = "id 集合") List<String> ids) {
        dictDetailService.removeByIds(ids);
        return DataResult.success();
    }

    @PutMapping("/dictDetail")
    @ApiOperation(value = "更新")
    @RequiresPermissions("sys:dict:update")
    public DataResult updateDictDetail(@RequestBody SysDictDetail sysDictDetail) {
        if (StringUtils.isEmpty(sysDictDetail.getValue())) {
            return DataResult.fail("字典值不能为空");
        }

        LambdaQueryWrapper<SysDictDetail> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SysDictDetail::getValue, sysDictDetail.getValue());
        queryWrapper.eq(SysDictDetail::getDictId, sysDictDetail.getDictId());
        SysDictDetail dictDetail = dictDetailService.getOne(queryWrapper);
        if (dictDetail != null && !dictDetail.getId().equals(sysDictDetail.getId())) {
            return DataResult.fail("字典名称-字典值已经存在");
        }

        dictDetailService.updateById(sysDictDetail);
        return DataResult.success();
    }

    @PostMapping("/list")
    @ApiOperation(value = "查询列表数据")
    @RequiresPermissions("sys:dict:list")
    public DataResult pageInfo(@RequestBody SysDictDetail sysDictDetail) {
        Page page = new Page(sysDictDetail.getPage(), sysDictDetail.getLimit());
        if (StringUtils.isEmpty(sysDictDetail.getDictId())) {
            return DataResult.success();
        }

        IPage<SysDictDetail> iPage = dictDetailService.listByPage(page, sysDictDetail.getDictId());
        return DataResult.success(iPage);
    }
}
