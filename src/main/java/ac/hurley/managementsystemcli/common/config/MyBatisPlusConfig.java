package ac.hurley.managementsystemcli.common.config;

import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Mybatis-Plus Config
 *
 * @author hurley
 */
@Configuration
public class MyBatisPlusConfig {

    /**
     * 配置 Mybatis-Plus 分页插件
     *
     * @return
     */
    @Bean
    public PaginationInnerInterceptor paginationInnerInterceptor() {
        return new PaginationInnerInterceptor();
    }

}
