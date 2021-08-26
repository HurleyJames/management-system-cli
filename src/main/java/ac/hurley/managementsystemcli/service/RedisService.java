package ac.hurley.managementsystemcli.service;

import ac.hurley.managementsystemcli.common.exception.SysException;
import ac.hurley.managementsystemcli.common.exception.code.BaseResCode;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * @author hurley
 */
@Service
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 判断是否存在
     * @param key
     * @return
     */
    public boolean exists(String key) {
        return this.redisTemplate.hasKey(key);
    }

    public Long getExpire(String key) {
        if (null == key) {
            throw new SysException(BaseResCode.DATA_ERROR.getCode(),
                    "Key or TomeUnit 不能为空");
        }
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    public void set(String key, String value) {
        this.redisTemplate.opsForValue().set(key, value);
    }

    public String get(String key) {
        return this.redisTemplate.opsForValue().get(key);
    }

    public void del(String key) {
        // 先判断是否存在
        if (this.exists(key)) {
            this.redisTemplate.delete(key);
        }
    }

    public void setAndExpire(String key, String value, long seconds) {
        this.redisTemplate.opsForValue().set(key, value);
        this.redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }

    public Set<String> keys(String pattern) {
        return redisTemplate.keys("*" + pattern);
    }

    public void delKeys(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (!CollectionUtils.isEmpty(keys)) {
            this.redisTemplate.delete(keys);
        }
    }
}
