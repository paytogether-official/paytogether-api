package kr.paytogether.exchange.feign.twelvedata

import com.google.gson.GsonBuilder
import feign.Logger
import feign.RequestInterceptor
import feign.Response
import feign.codec.ErrorDecoder
import feign.gson.GsonDecoder
import feign.gson.GsonEncoder
import feign.kotlin.CoroutineFeign
import feign.slf4j.Slf4jLogger
import kr.paytogether.exchange.feign.twelvedata.dto.TwelvedataErrorResponse
import kr.paytogether.shared.exception.FeignException
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.nio.charset.StandardCharsets

@Configuration
class TwelvedataFeignConfig(
    private val properties: TwelvedataProperties,
) {
    private val gson = GsonBuilder()
        .enableComplexMapKeySerialization()
        .create()

    @Bean
    fun twelvedataRest(): TwelvedataRest = CoroutineFeign.builder<Void>()
        .decoder(GsonDecoder(gson))
        .encoder(GsonEncoder(gson))
        .requestInterceptors(listOf(
            requestInterceptor(),
        ))
        .logger(Slf4jLogger())
        .errorDecoder(errorDecoder())
        .logLevel(Logger.Level.FULL)
        .target(TwelvedataRest::class.java, properties.url)

    private fun requestInterceptor(): RequestInterceptor = RequestInterceptor { template ->
        template.header("Content-Type", "application/json")
        template.header("Accept", "application/json")
        template.header("Authorization", "apikey ${properties.apiKey}")
    }

    fun errorDecoder(): ErrorDecoder = ErrorDecoder { _: String, r: Response ->
        val response = r.body().asReader(StandardCharsets.UTF_8).use { it.readText() }

        val twelveError: TwelvedataErrorResponse = try {
            gson.fromJson(response, TwelvedataErrorResponse::class.java)
        } catch (e: Exception) {
            throw FeignException(
                message = e.message ?: "Failed to parse error response",
                details = response,
            )
        }

        throw twelveError.toFeignException()
    }


    @ConfigurationProperties(prefix = "paytogether.feign.twelvedata")
    data class TwelvedataProperties(
        val url: String,
        val apiKey: String,
    )
}