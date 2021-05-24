package ac.hurley.managementsystemcli.common;

import ac.hurley.managementsystemcli.common.exception.code.BaseResCode;
import ac.hurley.managementsystemcli.common.exception.code.ResCodeInterface;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 返回值 DataResult
 */
@Data
public class DataResult {

    @ApiModelProperty(value = "请求响应状态码，0 成功，其它失败", name = "code")
    private int code;

    @ApiModelProperty(value = "响应状态码的详细信息", name = "message")
    private String message;

    @ApiModelProperty(value = "需要返回的数据", name = "data")
    private Object data;

    public DataResult(int code, Object data) {
        this.code = code;
        this.data = data;
        this.message = null;
    }

    public DataResult(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public DataResult(int code, String message) {
        this.code = code;
        this.message = message;
        this.data = null;
    }

    public DataResult() {
        this.code = BaseResCode.SUCCESS.getCode();
        this.message = BaseResCode.SUCCESS.getMessage();
        this.data = null;
    }

    public DataResult(Object data) {
        this.code = BaseResCode.SUCCESS.getCode();
        this.message = BaseResCode.SUCCESS.getMessage();
        this.data = data;
    }

    public DataResult(ResCodeInterface resCodeInterface) {
        this.data = null;
        this.code = resCodeInterface.getCode();
        this.message = resCodeInterface.getMessage();
    }

    /**
     * 操作成功，data 为 null
     *
     * @return
     */
    public static DataResult success() {
        return new DataResult();
    }

    /**
     * 操作失败，data 不为 null
     *
     * @param data
     * @return
     */
    public static DataResult success(Object data) {
        return new DataResult(data);
    }

    /**
     * 操作失败，data 不为 null
     *
     * @param message
     * @return
     */
    public static DataResult fail(String message) {
        return new DataResult(BaseResCode.OPERATION_ERROR.getCode(), message);
    }

    /**
     * 操作失败，data 不为 null
     *
     * @param code
     * @param message
     * @return
     */
    public static DataResult getResult(int code, String message) {
        return new DataResult(code, message);
    }

    /**
     * 自定义返回，入参一般是异常 code 枚举 data 为空
     *
     * @param resCode
     * @return
     */
    public static DataResult getResult(BaseResCode resCode) {
        return new DataResult(resCode);
    }
}
