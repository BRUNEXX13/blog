package br.com.ss.blog.infra.cache;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

public class FallbackRedisSerializer implements RedisSerializer<Object> {

    private final GenericJackson2JsonRedisSerializer primary;
    private final GenericJackson2JsonRedisSerializer fallback;
    private final ObjectMapper fallbackMapper;

    public FallbackRedisSerializer(GenericJackson2JsonRedisSerializer primary,
                                   GenericJackson2JsonRedisSerializer fallback,
                                   ObjectMapper fallbackMapper) {
        this.primary = primary;
        this.fallback = fallback;
        this.fallbackMapper = fallbackMapper;
    }

    @Override
    public byte[] serialize(Object t) throws SerializationException {
        if (t == null) {
            return new byte[0];
        }
        try {
            return primary.serialize(t);
        } catch (Exception ex) {
            // fallback to a safer serialization (no typing)
            return fallback.serialize(t);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            // try primary (expects type metadata)
            return primary.deserialize(bytes);
        } catch (Exception primaryEx) {
            try {
                // try fallback: plain JSON -> Map/List -> convertValue later by ObjectMapper consumer
                Object raw = fallback.deserialize(bytes);
                // raw might be Map or List; return as-is and let caller convert if needed
                return raw;
            } catch (Exception fallbackEx) {
                // last attempt: try to read as string to surface helpful message
                String payload = new String(bytes, StandardCharsets.UTF_8);
                throw new SerializationException("Both primary and fallback deserialization failed. payload=" + payload, fallbackEx);
            }
        }
    }

    /**
     * Helper to convert a raw Map/List (result from fallback) into target class.
     * Use where you know the target type (e.g. in service after reading from cache).
     */
    public <T> T convertRawTo(Object raw, Class<T> target) {
        if (raw == null) return null;
        return fallbackMapper.convertValue(raw, target);
    }
}
