package ac.hurley.managementsystemcli.common.exception.code;

/**
 * 返回码的接口
 */
public interface ResCodeInterface {

    /**
     * 获取 Code
     *
     * @return
     */
    int getCode();

    /**
     * 获取信息
     *
     * @return
     */
    String getMessage();
}
