package com.lost.domain.assistance.policy

import com.lost.domain.detection.AssistanceLevel
import com.lost.domain.detection.DetectionResult
import com.lost.domain.fakes.DomainFixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AssistanceResponsePolicyTest {
    private val policy = AssistanceResponsePolicy.standard()

    private fun context(
        result: DetectionResult,
        guidance: Boolean = false,
        timedOut: Boolean = false,
        contact: Boolean = true
    ) = AssistanceContext(result, guidance, timedOut, contact)

    @Test
    fun `sin desvio no hace nada`() {
        assertEquals(AssistanceAction.NONE, policy.decide(context(DetectionResult.CALM)))
    }

    @Test
    fun `el primer eslabon vibra y pregunta en nivel medio`() {
        val action = policy.decide(context(DomainFixtures.result(AssistanceLevel.MEDIUM)))
        assertEquals(AssistanceAction.VIBRATE_AND_ASK, action)
    }

    @Test
    fun `un desvio voluntario solo pregunta suave`() {
        val action = policy.decide(context(DomainFixtures.result(AssistanceLevel.LOW, isVoluntary = true)))
        assertEquals(AssistanceAction.SOFT_ASK, action)
    }

    @Test
    fun `si pidio ayuda el segundo eslabon muestra la ruta`() {
        val action = policy.decide(context(DomainFixtures.result(AssistanceLevel.MEDIUM), guidance = true))
        assertEquals(AssistanceAction.SHOW_ALTERNATIVE_ROUTE, action)
    }

    @Test
    fun `nivel alto llega al ultimo eslabon y comparte`() {
        val action = policy.decide(context(DomainFixtures.result(AssistanceLevel.HIGH)))
        assertEquals(AssistanceAction.SHARE_LOCATION, action)
    }

    @Test
    fun `sin respuesta tambien comparte`() {
        val action = policy.decide(context(DomainFixtures.result(AssistanceLevel.MEDIUM), timedOut = true))
        assertEquals(AssistanceAction.SHARE_LOCATION, action)
    }

    @Test
    fun `una cadena personalizada respeta el orden de sus eslabones`() {
        val custom = AssistanceResponsePolicy(listOf(ShareLocationLink(), VibrateAndAskLink()))
        val action = custom.decide(context(DomainFixtures.result(AssistanceLevel.MEDIUM)))
        assertEquals(AssistanceAction.VIBRATE_AND_ASK, action)
    }
}
