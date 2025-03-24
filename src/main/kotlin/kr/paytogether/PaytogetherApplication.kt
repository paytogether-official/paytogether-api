package kr.paytogether

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
@ConfigurationPropertiesScan
class PaytogetherApplication

fun main(args: Array<String>) {
    runApplication<PaytogetherApplication>(*args)
}
