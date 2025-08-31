package kr.paytogether.shared.exception

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.autoconfigure.web.WebProperties
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.Order
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono

@Component
@Order(-2)
class GlobalErrorWebExceptionHandler(
    errorAttributes: ErrorAttributes,
    applicationContext: ApplicationContext,
    serverCodecConfigurer: ServerCodecConfigurer,
) : AbstractErrorWebExceptionHandler(
    errorAttributes,
    WebProperties.Resources(),
    applicationContext
) {
    private val logger = KotlinLogging.logger {}

    init {
        super.setMessageWriters(serverCodecConfigurer.writers)
        super.setMessageReaders(serverCodecConfigurer.readers)
    }

    override fun getRoutingFunction(errorAttributes: ErrorAttributes?): RouterFunction<ServerResponse> =
        RouterFunctions.route(
            RequestPredicates.all(),
            this::renderErrorResponse
        )

    private fun renderErrorResponse(request: ServerRequest): Mono<ServerResponse> {
        val error = getError(request)
        val attributes = getErrorAttributes(request, ErrorAttributeOptions.defaults())

        logger.error(error) {
            buildString {
                appendLine("Global Exception Caught")
                appendLine("Request: ${request.method()} ${request.path()}")
                appendLine("Attributes: $attributes")
            }
        }

        return ServerResponse
            .status(attributes["status"] as Int)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(attributes)
    }
}