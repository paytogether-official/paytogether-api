package kr.paytogether.shared

import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class ProfileConfig(
    private val environment: Environment,
) {
    enum class Profile { LOCAL }

    fun getActiveProfiles(): Array<String> {
        return environment.activeProfiles
    }

    fun isLocal(): Boolean = getActiveProfiles().map { it.uppercase() }.contains(Profile.LOCAL.name)
}