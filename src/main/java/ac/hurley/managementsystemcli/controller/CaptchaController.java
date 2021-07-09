package ac.hurley.managementsystemcli.controller;

import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.utils.CaptchaUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequestMapping("/sys")
@RestController
@Api(tags = "验证码相关")
@Slf4j
public class CaptchaController {

    /**
     * 获取验证码图片
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/verify")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 48);
        captcha.setLen(2);
        CaptchaUtil.out(captcha, request, response);
    }
}
