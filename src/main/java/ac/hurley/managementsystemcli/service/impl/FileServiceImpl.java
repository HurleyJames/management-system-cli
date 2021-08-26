package ac.hurley.managementsystemcli.service.impl;

import ac.hurley.managementsystemcli.common.DataResult;
import ac.hurley.managementsystemcli.common.config.FileUploadConfig;
import ac.hurley.managementsystemcli.common.exception.SysException;
import ac.hurley.managementsystemcli.entitiy.SysFiles;
import ac.hurley.managementsystemcli.mapper.FilesMapper;
import ac.hurley.managementsystemcli.service.FileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.DateUtils;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

/**
 * 文件上传 Service 实现类
 *
 * @author hurley
 */
@EnableConfigurationProperties(FileUploadConfig.class)
@Service("filesService")
public class FileServiceImpl extends ServiceImpl<FilesMapper, SysFiles> implements FileService {

    @Resource
    private FileUploadConfig fileUploadConfig;

    /**
     * 上传保存文件
     *
     * @param file
     * @return
     */
    @Override
    public DataResult saveFile(MultipartFile file) {
        // 创建要存储的文件夹格式：创建时间，路径
        String createTime = DateUtils.format(new Date(), "yyyyMMdd", Locale.ENGLISH);
        String newPath = fileUploadConfig.getPath() + createTime + File.separator;
        File uploadDirectory = new File(newPath);
        // 如果要创建的文件夹已经存在
        if (uploadDirectory.exists()) {
            // 如果是一个目录文件夹
            if (!uploadDirectory.isDirectory()) {
                // 删除
                uploadDirectory.delete();
            }
        } else {
            // 创建该文件夹
            uploadDirectory.mkdir();
        }

        try {
            String fileName = file.getOriginalFilename();
            // 将 id 与 fileName 保持一致
            String newFileName = UUID.randomUUID().toString().replace("-", "") + getFileType(fileName);
            String newFilePathName = newPath + newFileName;
            // 文件的 url 链接地址
            String url = fileUploadConfig.getUrl() + "/" + createTime + "/" + newFileName;

            // 创建输出文件的对象
            File outputFile = new File(newFilePathName);
            FileUtils.copyInputStreamToFile(file.getInputStream(), outputFile);
            // 保存文件记录
            SysFiles sysFiles = new SysFiles();
            sysFiles.setFileName(fileName);
            sysFiles.setFilePath(newFilePathName);
            sysFiles.setUrl(url);
            this.save(sysFiles);
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("src", url);
            return DataResult.success(resultMap);
        } catch (Exception e) {
            throw new SysException("上传文件失败");
        }
    }

    /**
     * 删除文件并移除 ids
     *
     * @param ids
     */
    @Override
    public void removeByIdsAndFiles(List<String> ids) {
        List<SysFiles> list = this.listByIds(ids);
        list.forEach(entity -> {
            File file = new File(entity.getFilePath());
            // 如果之前存在文件，则删除
            if (file.exists()) {
                file.delete();
            }
        });
        // 移除 ids
        this.removeByIds(ids);
    }

    /**
     * 获取文件类型
     *
     * @param fileName
     * @return
     */
    private String getFileType(String fileName) {
        // . 之后的即为文件类型格式
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf("."));
        }
        return "";
    }
}
