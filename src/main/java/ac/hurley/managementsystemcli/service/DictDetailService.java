package ac.hurley.managementsystemcli.service;

import ac.hurley.managementsystemcli.entitiy.SysDict;
import ac.hurley.managementsystemcli.entitiy.SysDictDetail;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 数据字典细节 Service 类
 */
public interface DictDetailService extends IService<SysDictDetail> {

    /**
     * 分页
     * @param page
     * @param dictId
     * @return
     */
    IPage<SysDictDetail> listByPage(Page<SysDictDetail> page, String dictId);
}