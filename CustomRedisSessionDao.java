/**
 * May 5, 2017 2:20:53 PM 
 * Copyright(c) 2015-2017 深圳xxx电子商务科技有限公司. 
 */
package com.xxx.realm;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author 张永强
 */
public class CustomRedisSessionDao extends AbstractSessionDAO {

    /**
     * redis session key 前缀
     */
    private String sessionKeyPrefix;

    @Resource
    private RedisTemplate<String, Session> redisTemplateZero;

    /**
     * log4j
     */
    private static final Logger logger = Logger.getLogger(CustomRedisSessionDao.class);

    @Override
    public void update(Session session) throws UnknownSessionException {
        if (session == null || session.getId() == null) {
            logger.info("session or sessionId is null");
        }

        redisTemplateZero.opsForValue().set(this.sessionKeyPrefix + session.getId(), session, session.getTimeout(), TimeUnit.MILLISECONDS);
    }

    @Override
    public void delete(Session session) {
        if (session == null || session.getId() == null) {
            logger.info("session or sessionId is null");
        }
        redisTemplateZero.delete(this.sessionKeyPrefix + session.getId());
    }

    @Override
    public Collection<Session> getActiveSessions() {
        Set<String> keys = redisTemplateZero.keys(this.sessionKeyPrefix + "*");
        Set<Session> sessions = new HashSet<Session>();
        for (String key : keys) {
            Session session = redisTemplateZero.opsForValue().get(key);
            sessions.add(session);
        }
        return sessions;
    }

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = this.generateSessionId(session);
        this.assignSessionId(session, sessionId);

        // 刷新内存相同逻辑
        this.update(session);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        if (null == sessionId) {
            logger.info("session or sessionId is null");
            return null;
        }
        Session session = redisTemplateZero.opsForValue().get(this.sessionKeyPrefix + sessionId);
        return session;
    }

    /**
     * 需要spring注入，所以public访问权限
     * 
     * @param sessionKeyPrefix
     */
    public void setSessionKeyPrefix(String sessionKeyPrefix) {
        this.sessionKeyPrefix = sessionKeyPrefix;
    }
}
