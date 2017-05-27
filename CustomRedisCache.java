/**
 * May 5, 2017 4:43:10 PM 
 * Copyright(c) 2015-2017 深圳***电子商务科技有限公司. 
 */
package com.xxx.realm;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.data.redis.core.RedisTemplate;

import com.xxx.base.Springfactory;

/**
 * @author 张永强
 */
public class CustomRedisCache<K, V> implements Cache<K, V> {

    private RedisTemplate<String, V> redisTemplate;

    /**
     * redis key的前缀
     */
    public static final String KEY_PREFIX = "shiro-cache-";

    /**
     * shiro 缓存前缀
     */
    private String REDIS_SHIRO_CACHE;

    /**
     * doGetAuthorizationInfo 的过期时间, 10分钟。
     */
    private long expire = 600000L;

    /**
     * @param name
     */
    public CustomRedisCache(String name) {
        REDIS_SHIRO_CACHE = KEY_PREFIX + name + ":";
    }

    @Override
    public V get(K key) throws CacheException {
        if (null == redisTemplate) {
            // 加载顺序原因，如果初始化时候获取，那么redisTemplate为null
            redisTemplate = Springfactory.getBean("redisTemplate");
        }

        return redisTemplate.opsForValue().get(this.REDIS_SHIRO_CACHE + key);
    }

    @Override
    public V put(K key, V value) throws CacheException {
        if (null == redisTemplate) {
            redisTemplate = Springfactory.getBean("redisTemplate");
        }

        V previos = get(key);
        redisTemplate.opsForValue().set(this.REDIS_SHIRO_CACHE + key, value, this.expire, TimeUnit.MILLISECONDS);
        return previos;
    }

    @Override
    public V remove(K key) throws CacheException {
        if (null == redisTemplate) {
            redisTemplate = Springfactory.getBean("redisTemplate");
        }

        V previos = get(key);
        redisTemplate.delete(this.REDIS_SHIRO_CACHE + key);
        return previos;
    }

    @Override
    public void clear() throws CacheException {
        if (null == redisTemplate) {
            redisTemplate = Springfactory.getBean("redisTemplate");
        }

        // 这里不用connection.flushDb(), 以免Session等其他缓存数据被连带删除
        Set<String> redisKeys = redisTemplate.keys(this.REDIS_SHIRO_CACHE + "*");
        for (String redisKey : redisKeys) {
            redisTemplate.delete(redisKey);
        }
    }

    @Override
    public int size() {
        if (keys() == null)
            return 0;
        return keys().size();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<K> keys() {
        if (null == redisTemplate) {
            redisTemplate = Springfactory.getBean("redisTemplate");
        }

        Set<String> redisKeys = redisTemplate.keys(this.REDIS_SHIRO_CACHE + "*");
        Set<K> keys = new HashSet<K>();
        for (String redisKey : redisKeys) {
            keys.add((K) redisKey.substring(this.REDIS_SHIRO_CACHE.length()));
        }
        return keys;
    }

    @Override
    public Collection<V> values() {
        if (null == redisTemplate) {
            redisTemplate = Springfactory.getBean("redisTemplate");
        }

        Set<String> redisKeys = redisTemplate.keys(this.REDIS_SHIRO_CACHE + "*");
        Set<V> values = new HashSet<V>();
        for (String redisKey : redisKeys) {
            V value = redisTemplate.opsForValue().get(redisKey);
            values.add(value);
        }
        return values;
    }
}
