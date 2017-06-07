/**
 * May 5, 2017 4:25:52 PM 
 * Copyright(c) 2015-2017 深圳xxx电子商务科技有限公司. 
 */
package com.xxx.realm;

import java.util.Set;

import javax.annotation.Resource;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.util.Destroyable;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author 张永强
 */
public class CustomRedisCacheManager implements CacheManager, Destroyable {

    /**
     * redis cache key的前缀
     */
    private String cacheKeyPrefix;

    @Resource
    private RedisTemplate<String, Session> redisTemplate;

    @Override
    public void destroy() throws Exception {
        // 这里不用connection.flushDb(), 以免Session等其他缓存数据被连带删除
        Set<String> redisKeys = redisTemplate.keys(this.cacheKeyPrefix + "*");
        for (String redisKey : redisKeys) {
            redisTemplate.delete(redisKey);
        }
    }

    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        return new CustomRedisCache<K, V>(this.cacheKeyPrefix + name + ":");
    }

    /**
     * 需要spring注入，所以public访问权限
     * 
     * @param cacheKeyPrefix
     */
    public void setCacheKeyPrefix(String cacheKeyPrefix) {
        this.cacheKeyPrefix = cacheKeyPrefix;
    }
}
