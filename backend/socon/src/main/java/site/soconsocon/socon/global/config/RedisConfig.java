package site.soconsocon.socon.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableCaching
@RequiredArgsConstructor
@EnableRedisRepositories(basePackages = {
        "site.soconsocon.socon.*.repository.redis"
})
public class RedisConfig {

    private final RedisProperties redisProperties;

    // lettuce
    @Bean
    @Profile("dev")
    public RedisConnectionFactory redisConnectionFactoryDev() {
        return new LettuceConnectionFactory(
                new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort()));
    }

    @Bean
    @Profile("dev")
    public RedisTemplate<String, Object> redisTemplateDev() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactoryDev());

        // 키 직렬화 방식
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // 값 직렬화 방식을 Jackson2JsonRedisSerializer로 설정
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashValueSerializer(serializer);  // 해시 값 직렬화 방식도 설정

        return redisTemplate;
    }

    @Bean
    @Profile("prod")
    public RedisConnectionFactory redisConnectionFactoryProd() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisProperties.getHost());
        config.setPort(redisProperties.getPort());
        config.setPassword(RedisPassword.of(redisProperties.getPassword()));
        return new LettuceConnectionFactory(config);
    }

    // 생산 환경을 위한 RedisTemplate 설정
    @Bean
    @Profile("prod")
    public RedisTemplate<String, Object> redisTemplateProd() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactoryProd());

        // 키 직렬화 방식
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // 값 직렬화 방식을 Jackson2JsonRedisSerializer로 설정
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashValueSerializer(serializer);  // 해시 값 직렬화 방식도 설정

        return redisTemplate;
    }
}
