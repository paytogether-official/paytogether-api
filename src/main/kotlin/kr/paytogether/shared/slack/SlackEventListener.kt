package kr.paytogether.shared.slack

import com.slack.api.Slack
import com.slack.api.model.Attachments.attachment
import com.slack.api.webhook.Payload
import com.slack.api.webhook.WebhookPayloads.payload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kr.paytogether.shared.ProfileConfig
import kr.paytogether.shared.exception.ErrorCode
import kr.paytogether.shared.exception.PaytogetherException
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class SlackEventListener(
    private val applicationScope: CoroutineScope,
    private val slackProperties: SlackProperties,
    private val profileConfig: ProfileConfig,
) {
    private val slack: Slack = Slack.getInstance()

    @EventListener(SlackEvent::class)
    fun slackEventListener(event: SlackEvent) = applicationScope.launch {
        val url = slackProperties.webhook[event.topic]?.url ?: throw PaytogetherException(ErrorCode.SERVER_ERROR, "No webhook url for ${event.topic}")

        val payload: Payload = payload { p -> p
//            .blocks(
//                withBlocks {
//                    context {
//                        elements {
//                            markdownText(event.details ?: "")
//                        }
//                    }
//                }
//            )
            .attachments(
                listOf(attachment { a -> a
                    .pretext(event.title)
                    .title(event.message)
                    .authorName("Paytogether")
                    .color(event.color.value)
                    .mrkdwnIn(listOf("text"))
                    .text("``` ${event.details?.slice(0..680)} ```")
                    .fields(event.fields.map { it.toField() })
                })
            )
        }

        slack.send(url, payload)
    }

    @ConfigurationProperties(prefix = "paytogether.slack")
    data class SlackProperties(
        val webhook: Map<Topic, WebhookProperty>,
    ) {
        data class WebhookProperty(
            val url: String,
        )
    }
}