package com.tweety.SwithT.common.configs;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

	@Value("${spring.redis.host}")
	public String host;

	@Value("${spring.redis.port}")
	public int port;

	@Bean
	@Qualifier("2") //아마 디폴트가 1일 것 이다. 밑에는 1이라고하지만 여기는 2라고 표시하면 된다
	public RedisConnectionFactory redisConnectionFactory() {

		RedisStandaloneConfiguration configuration
				= new RedisStandaloneConfiguration();

		configuration.setHostName(host);
		configuration.setPort(port);
		//1번 db 사용한다 여기서 db 사용할 방 명시한다. 1번,2번,3번 무슨 용도 등등으로 쓰면 된다.
		configuration.setDatabase(1);

		return new LettuceConnectionFactory(configuration);

	}

	@Qualifier("2")
	@Bean //여기에도 다 Qualifier를 설정해야한다.
	public RedisTemplate<String, Object> redisTemplate(@Qualifier("2") RedisConnectionFactory redisConnectionFactory){
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		return redisTemplate;

	}

}