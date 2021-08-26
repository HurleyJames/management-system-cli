package ac.hurley.managementsystemcli.common.exception.handler;

import ac.hurley.managementsystemcli.common.DataResult;
import ac.hurley.managementsystemcli.common.exception.SysException;
import ac.hurley.managementsystemcli.common.exception.code.BaseResCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 * @author hurley
 */
@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {

    /**
     * 系统繁忙，请稍后再试
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public DataResult handleException(Exception e) {
        log.error("Exception,exception:{}", e, e);
        return DataResult.getResult(BaseResCode.SYSTEM_BUSY);
    }

    /**
     * 自定义全局异常处理
     *
     * @param e
     * @return
     */
    public DataResult sysExceptionHandler(SysException e) {
        log.error("Exception,exception:{}", e, e);
        return new DataResult(e.getMessageCode(), e.getDetailMessage());
    }

    /**
     * 权限校验不通过，返回 403 视图
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = AuthorizationException.class)
    public DataResult errorPermission(AuthorizationException e) {
        log.error("Exception,exception:{}", e, e);
        return new DataResult(BaseResCode.UNAUTHORIZED_ERROR);
    }

    /**
     * 方法参数校验异常
     * 处理 validation 异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public DataResult methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        log.error("methodArgumentNotValidExceptionHandler bindingResult.allErrors():{},exception:{}", e.getBindingResult().getAllErrors(), e);
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        return DataResult.getResult(BaseResCode.METHOD_ARG_NOT_VALID_EXCEPTION);
    }

    /**
     * 校验 List<entity> 类型，需要 controller 添加 @Validation 注解
     * 处理 Validation List<entity> 异常
     *
     * @param exception
     * @return
     */
    @ExceptionHandler
    public DataResult handle(ConstraintViolationException exception) {
        log.error("methodArgumentNotValidExceptionHandler bindingResult.allErrors():{},exception:{}", exception, exception);
        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
        StringBuilder builder = new StringBuilder();
        for (ConstraintViolation<?> violation : violations) {
            builder.append(violation.getMessage());
            break;
        }
        return DataResult.getResult(BaseResCode.METHOD_ARG_NOT_VALID_EXCEPTION.getCode(), builder.toString());
    }
}
