package kr.paytogether.shared

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Configuration
import org.springframework.data.web.ReactivePageableHandlerMethodArgumentResolver
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer

@Configuration
@ConditionalOnClass(WebFluxConfigurer::class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
class WebFluxConfig : WebFluxConfigurer {

    override fun configureArgumentResolvers(resolvers: ArgumentResolverConfigurer) {
        resolvers.addCustomResolver(ReactivePageableHandlerMethodArgumentResolver())
    }
}