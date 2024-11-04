package com.tweety.SwithT.common.config;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.redis.spring.RedisLockProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
public class ShedLockConfig {

    @Bean(name = "lockProvider5")
    public LockProvider lockProviderFor5(@Qualifier("5") RedisConnectionFactory redisTemplateConnectionFactory) {
        return new RedisLockProvider(redisTemplateConnectionFactory);
    }

    @Bean(name = "lockProvider14")
    public LockProvider lockProviderFor14(@Qualifier("14") RedisConnectionFactory redisOpenSearchSyncConnectionFactory) {
        return new RedisLockProvider(redisOpenSearchSyncConnectionFactory);
    }
}
