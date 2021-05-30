package ac.hurley.managementsystemcli.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.util.UUID;

/**
 * 密码工具类
 */
public class PasswordUtils {

    private static Logger logger = LoggerFactory.getLogger(PasswordUtils.class);

    private final static String[] HEX_DIGITS = {"0", "1", "2", "3", "4", "5", "6",
            "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    private final static String MD5 = "md5";

    /**
     * 加盐
     */
    private Object salt;

    /**
     * 加密算法
     */
    private String algo;

    public PasswordUtils(Object salt) {
        this(salt, MD5);
    }

    public PasswordUtils(Object salt, String algo) {
        this.salt = salt;
        this.algo = algo;
    }

    /**
     * 明文密码加密
     *
     * @param rawPwd 明文
     * @param salt   盐
     * @return
     */
    public static String encode(String rawPwd, String salt) {
        return new PasswordUtils(salt).encode(rawPwd);
    }

    /**
     * 加密
     *
     * @param newPwd
     * @return
     */
    public String encode(String newPwd) {
        String result = null;
        try {
            MessageDigest md = MessageDigest.getInstance(algo);
            // 加密后的字符串

        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return result;
    }

    /**
     * 密码匹配验证
     *
     * @param salt   盐
     * @param rawPwd 明文
     * @param encPwd 密文
     * @return
     */
    public static boolean matches(String salt, String rawPwd, String encPwd) {
        return new PasswordUtils(salt).matches(encPwd, rawPwd);
    }

    /**
     * 密码匹配验证
     *
     * @param encPwd 密文
     * @param rawPwd 明文
     * @return
     */
    public boolean matches(String encPwd, String rawPwd) {
        String pwd1 = "" + encPwd;
        String pwd2 = encode(rawPwd);

        return pwd1.equals(pwd2);
    }

    /**
     * 合并密码和盐
     *
     * @param pwd
     * @return
     */
    private String mergePwdAndSalt(String pwd) {
        if (pwd == null) {
            pwd = "";
        }

        if ((salt == null) || "".equals(salt)) {
            return pwd;
        } else {
            return pwd + "{" + salt.toString() + "}";
        }
    }

    /**
     * 转换字节数组为 16 进制字符串
     *
     * @param b
     * @return
     */
    public String byteArrayToHexString(byte[] b) {
        StringBuilder result = new StringBuilder();
        for (byte value : b) {
            result.append(byteToHexString(value));
        }
        return result.toString();
    }

    /**
     * 将字节转化为 16 进制
     *
     * @param b
     * @return
     */
    public static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n += 256;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return HEX_DIGITS[d1] + HEX_DIGITS[d2];
    }

    /**
     * 获取加密盐
     *
     * @return
     */
    public static String getSalt() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 20);
    }
}
