package kr.paytogether.shared.logging

import ch.qos.logback.classic.Level.ERROR_INT
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.ThrowableProxyUtil
import ch.qos.logback.core.filter.Filter
import ch.qos.logback.core.spi.FilterReply
import ch.qos.logback.core.spi.FilterReply.NEUTRAL
import kr.paytogether.shared.slack.Color
import kr.paytogether.shared.slack.Field
import kr.paytogether.shared.slack.SlackEvent
import kr.paytogether.shared.slack.Topic
import kr.paytogether.shared.utils.BeanUtils


class LogbackSlackAppender: Filter<ILoggingEvent>() {
    override fun decide(event: ILoggingEvent?): FilterReply {
        if (event?.level?.levelInt != ERROR_INT) return NEUTRAL

        val publisher = BeanUtils.getApplicationEventPublisher()

        publisher.publishEvent(SlackEvent(
            topic = Topic.ERROR,
            title = "${event.level}",
            message = "> ${event.formattedMessage}",
            color = Color.DANGER,
            details = ThrowableProxyUtil.asString(event.throwableProxy),
            fields = listOf(
                Field("Logger Name", event.loggerName),
                Field("Error Type", event.throwableProxy?.className ?: "Unknown"),
                Field("Error Message", event.message),
            ),
        ))


        return NEUTRAL
    }
}