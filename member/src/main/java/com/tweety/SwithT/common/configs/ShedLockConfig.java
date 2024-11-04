package com.tweety.SwithT.common.configs;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.redis.spring.RedisLockProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
public class ShedLockConfig {

    @Primary
    @Bean(name = "lockProvider16")
    public LockProvider lockProviderFor16(@Qualifier("16") RedisConnectionFactory redisTemplateConnectionFactory) {
        return new RedisLockProvider(redisTemplateConnectionFactory);
    }

}
