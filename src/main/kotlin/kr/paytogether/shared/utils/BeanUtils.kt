package kr.paytogether.shared.utils

import kr.paytogether.shared.utils.BeanUtils.ApplicationContextProvider.Companion.applicationContext
import kr.paytogether.shared.utils.BeanUtils.ApplicationEventPublisherProvider.Companion.applicationEventPublisher
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationEventPublisherAware
import org.springframework.context.annotation.Configuration
import kotlin.reflect.KClass

object BeanUtils {


    fun <T : Any> getBean(type: KClass<T>): T = applicationContext.getBean(type.java)

    fun getApplicationContext(): ApplicationContext = applicationContext

    fun getApplicationEventPublisher(): ApplicationEventPublisher = applicationEventPublisher

    fun getApplicationName(defaultName: String): String = applicationContext.id ?: defaultName

    fun getApplicationName(): String {
        assert(applicationContext.id != null)
        return applicationContext.id!!
    }

    @Configuration(proxyBeanMethods = false)
    class ApplicationContextProvider : ApplicationContextAware {
        override fun setApplicationContext(context: ApplicationContext) {
            applicationContext = context
        }

        companion object {
            lateinit var applicationContext: ApplicationContext
        }
    }

    @Configuration(proxyBeanMethods = false)
    class ApplicationEventPublisherProvider : ApplicationEventPublisherAware {
        companion object {
            lateinit var applicationEventPublisher: ApplicationEventPublisher
        }

        override fun setApplicationEventPublisher(context: ApplicationEventPublisher) {
            applicationEventPublisher = context
        }
    }
}
