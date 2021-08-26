package ac.hurley.managementsystemcli.service.impl;

import ac.hurley.managementsystemcli.entitiy.SysContent;
import ac.hurley.managementsystemcli.mapper.ContentMapper;
import ac.hurley.managementsystemcli.service.ContentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 内容 Service 实现类
 *
 * @author hurley
 */
@Service("contentService")
public class ContentServiceImpl extends ServiceImpl<ContentMapper, SysContent> implements ContentService {


}
