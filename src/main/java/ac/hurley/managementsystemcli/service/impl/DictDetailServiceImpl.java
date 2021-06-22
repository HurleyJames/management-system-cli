package ac.hurley.managementsystemcli.service.impl;

import ac.hurley.managementsystemcli.common.exception.SysException;
import ac.hurley.managementsystemcli.entitiy.SysDict;
import ac.hurley.managementsystemcli.entitiy.SysDictDetail;
import ac.hurley.managementsystemcli.mapper.DictDetailMapper;
import ac.hurley.managementsystemcli.mapper.DictMapper;
import ac.hurley.managementsystemcli.service.DictDetailService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;

/**
 * 数据字典 Detail Service 实现类
 */
public class DictDetailServiceImpl extends ServiceImpl<DictDetailMapper, SysDictDetail> implements DictDetailService {

    @Resource
    private DictDetailMapper dictDetailMapper;
    @Resource
    private DictMapper dictMapper;

    /**
     * 通过分页的形式展示数据字典 Detail
     *
     * @param page
     * @param dictId
     * @return
     */
    @Override
    public IPage<SysDictDetail> listByPage(Page<SysDictDetail> page, String dictId) {
        SysDict sysDict = dictMapper.selectById(dictId);
        if (sysDict == null) {
            throw new SysException("获取数据字典失败");
        }

        LambdaQueryWrapper<SysDictDetail> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysDictDetail::getDictId, dictId);
        wrapper.orderByAsc(SysDictDetail::getSort);
        IPage<SysDictDetail> result = dictDetailMapper.selectPage(page, wrapper);
        if (!CollectionUtils.isEmpty(result.getRecords())) {
            result.getRecords().parallelStream().forEach(entity ->
                    entity.setDictName(sysDict.getName()));
        }

        return result;
    }
}
