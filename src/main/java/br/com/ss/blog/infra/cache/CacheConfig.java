package br.com.ss.blog.infra.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Set;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        // primary mapper: uses polymorphic typing (WRAPPER_ARRAY is default for GenericJackson2JsonRedisSerializer)
        ObjectMapper primaryMapper = new ObjectMapper();
        primaryMapper.registerModule(new JavaTimeModule());
        primaryMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // attach polymorphic typing for NON_FINAL types (keeps metadata for objects like PaginatedResponse<T>)
        primaryMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_ARRAY);

        GenericJackson2JsonRedisSerializer primarySerializer = new GenericJackson2JsonRedisSerializer(primaryMapper);

        // fallback mapper: no default typing, useful to read older entries that lack type metadata
        ObjectMapper fallbackMapper = new ObjectMapper();
        fallbackMapper.registerModule(new JavaTimeModule());
        fallbackMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        GenericJackson2JsonRedisSerializer fallbackSerializer = new GenericJackson2JsonRedisSerializer(fallbackMapper);

        // composite serializer that tries primary then fallback
        FallbackRedisSerializer composite = new FallbackRedisSerializer(primarySerializer, fallbackSerializer, fallbackMapper);

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(composite));
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = redisCacheConfiguration();
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .initialCacheNames(Set.of(CacheNames.USERS))
                .build();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        // reuse mappers as above
        ObjectMapper primaryMapper = new ObjectMapper();
        primaryMapper.registerModule(new JavaTimeModule());
        primaryMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        primaryMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_ARRAY);

        GenericJackson2JsonRedisSerializer primarySerializer = new GenericJackson2JsonRedisSerializer(primaryMapper);

        ObjectMapper fallbackMapper = new ObjectMapper();
        fallbackMapper.registerModule(new JavaTimeModule());
        fallbackMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        GenericJackson2JsonRedisSerializer fallbackSerializer = new GenericJackson2JsonRedisSerializer(fallbackMapper);

        FallbackRedisSerializer composite = new FallbackRedisSerializer(primarySerializer, fallbackSerializer, fallbackMapper);

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(composite);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(composite);

        template.afterPropertiesSet();
        return template;
    }
}
