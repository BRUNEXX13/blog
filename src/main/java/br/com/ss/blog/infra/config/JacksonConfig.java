// java
package br.com.ss.blog.infra.config;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

@Configuration
public class JacksonConfig {

    private static final DateTimeFormatter LOCAL_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            builder.serializers(new LocalDateSerializer(LOCAL_DATE_FORMATTER));
            builder.deserializers(new LocalDateDeserializer(LOCAL_DATE_FORMATTER));
        };
    }
}
