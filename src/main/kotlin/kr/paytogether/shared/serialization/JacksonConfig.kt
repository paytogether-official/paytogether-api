package kr.paytogether.shared.serialization

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.IOException
import java.math.BigDecimal
import java.text.SimpleDateFormat

@Configuration
class JacksonConfig {
    @Bean
    fun objectMapper(): ObjectMapper {
        return ObjectMapper().apply {
            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
            configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)
            setSerializationInclusion(JsonInclude.Include.ALWAYS)
            setDateFormat(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX"))
            registerModule(JavaTimeModule())
            registerModule(SimpleModule().apply {
                addSerializer(BigDecimal::class.java, BigDecimalPlainStringSerializer())
            })
            writer().with(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
        }
    }

    class BigDecimalPlainStringSerializer : JsonSerializer<BigDecimal>() {
        @Throws(IOException::class)
        override fun serialize(value: BigDecimal?, gen: JsonGenerator, serializers: SerializerProvider) {
            if (value == null) gen.writeString("")
            else gen.writeString(value.toPlainString())
        }
    }
}