package ac.hurley.managementsystemcli.service;


import ac.hurley.managementsystemcli.common.DataResult;
import ac.hurley.managementsystemcli.entitiy.SysFiles;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件上传 Service 类
 */
public interface FileService extends IService<SysFiles> {

    /**
     * 保存文件
     * @param file
     * @return
     */
    DataResult saveFile(MultipartFile file);

    /**
     * 根据 Id 移除文件
     * @param ids
     */
    void removeByIdsAndFiles(List<String> ids);
}
