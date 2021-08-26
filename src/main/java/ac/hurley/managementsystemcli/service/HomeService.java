package ac.hurley.managementsystemcli.service;

import ac.hurley.managementsystemcli.vo.res.HomeResVO;

/**
 * 首页 Service 类
 *
 * @author hurley
 */
public interface HomeService {

    /**
     * 获取首页信息
     *
     * @param userId
     * @return
     */
    HomeResVO getHomeInfo(String userId);
}
