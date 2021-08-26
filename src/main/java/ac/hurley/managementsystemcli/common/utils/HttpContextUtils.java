package ac.hurley.managementsystemcli.common.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author hurley
 */
public class HttpContextUtils {

    public static HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }

    public static boolean isAjaxRequest(HttpServletRequest request) {
        String accept = request.getHeader("accept");
        String xRequestedWidth = request.getHeader("X-Requested-With");

        // 如果是异步请求或者是手机端，则直接返回信息
        return ((accept != null && accept.contains("application/json")
                || (xRequestedWidth != null && xRequestedWidth.contains("XMLHttpRequest"))));
    }
}
