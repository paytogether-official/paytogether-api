package kr.paytogether.shared.slack

import org.springframework.context.ApplicationEvent

data class SlackEvent (
    val topic: Topic,
    val title: String,
    val message: String? = null,
    val color: Color = Color.GOOD,
    val fields: List<Field> = emptyList(),
) : ApplicationEvent(title)
