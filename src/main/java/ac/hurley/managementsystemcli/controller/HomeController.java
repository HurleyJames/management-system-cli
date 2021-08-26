package ac.hurley.managementsystemcli.controller;

import ac.hurley.managementsystemcli.common.DataResult;
import ac.hurley.managementsystemcli.service.HomeService;
import ac.hurley.managementsystemcli.service.HttpSessionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 首页
 *
 * @author hurley
 */
@RestController
@RequestMapping("/sys")
@Api(tags = "首页数据")
public class HomeController {

    @Resource
    private HomeService homeService;
    @Resource
    private HttpSessionService httpSessionService;

    @GetMapping("/home")
    @ApiOperation(value = "获取首页数据接口")
    public DataResult getHomeInfo() {
        // 通过 access_token 获取当前用户的 userId
        String userId = httpSessionService.getCurrentUserId();
        DataResult result = DataResult.success();
        result.setData(homeService.getHomeInfo(userId));
        return result;
    }
}
