package ac.hurley.managementsystemcli.service.impl;

import ac.hurley.managementsystemcli.entitiy.SysDict;
import ac.hurley.managementsystemcli.entitiy.SysDictDetail;
import ac.hurley.managementsystemcli.mapper.DictDetailMapper;
import ac.hurley.managementsystemcli.mapper.DictMapper;
import ac.hurley.managementsystemcli.service.DictService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 数据字典 Service 实现类
 *
 * @author hurley
 */
@Service("dictService")
public class DictServiceImpl extends ServiceImpl<DictMapper, SysDict> implements DictService {

    @Resource
    private DictDetailMapper dictDetailMapper;

    /**
     * 根据字典类型查询字典的数据信息
     *
     * @param name
     * @return
     */
    public JSONArray getType(String name) {
        if (StringUtils.isEmpty(name)) {
            return new JSONArray();
        }

        // 根据名称获取字典对象
        SysDict dict = this.getOne(Wrappers.<SysDict>lambdaQuery()
                .eq(SysDict::getName, name));

        if (dict == null || dict.getId() == null) {
            return new JSONArray();
        }

        // 获取字典 Detail
        List<SysDictDetail> list = dictDetailMapper.selectList(Wrappers.<SysDictDetail>lambdaQuery()
                .eq(SysDictDetail::getDictId, dict.getId()));
        return JSONArray.parseArray(JSON.toJSONString(list));
    }
}
