package ac.hurley.managementsystemcli.common.exception;


import ac.hurley.managementsystemcli.common.exception.code.BaseResCode;
import ac.hurley.managementsystemcli.common.exception.code.ResCodeInterface;

/**
 * 管理系统异常类
 */
public class SysException extends RuntimeException {

    /**
     * 异常编号
     */
    private final int messageCode;

    /**
     * 对 messageCode 异常信息进行补充说明
     */
    private final String detailMessage;

    public SysException(int messageCode, String detailMessage) {
        super(detailMessage);
        this.messageCode = messageCode;
        this.detailMessage = detailMessage;
    }

    public SysException(String detailMessage) {
        super(detailMessage);
        this.messageCode = BaseResCode.OPERATION_ERROR.getCode();
        this.detailMessage = detailMessage;
    }

    public SysException(ResCodeInterface codeInterface) {
        this(codeInterface.getCode(), codeInterface.getMessage());
    }

    public int getMessageCode() {
        return messageCode;
    }

    public String getDetailMessage() {
        return detailMessage;
    }
}
