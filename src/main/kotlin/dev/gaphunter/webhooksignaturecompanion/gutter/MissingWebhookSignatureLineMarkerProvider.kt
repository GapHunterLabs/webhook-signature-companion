package dev.gaphunter.webhooksignaturecompanion.gutter

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProviderDescriptor
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import dev.gaphunter.webhooksignaturecompanion.detect.JavaWebhookFinder
import dev.gaphunter.webhooksignaturecompanion.detect.KotlinWebhookFinder
import dev.gaphunter.webhooksignaturecompanion.model.WebhookHit
import dev.gaphunter.webhooksignaturecompanion.review.ReviewPrompt

class MissingWebhookSignatureLineMarkerProvider : LineMarkerProviderDescriptor(), DumbAware {

    override fun getName(): String = "Missing webhook signature verification"

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? = null

    override fun collectSlowLineMarkers(elements: MutableList<out PsiElement>, result: MutableCollection<in LineMarkerInfo<*>>) {
        val file = elements.firstOrNull()?.containingFile ?: return
        val hits = when (file.language.id) {
            "JAVA" -> JavaWebhookFinder.findAll(file)
            "kotlin" -> KotlinWebhookFinder.findAll(file)
            else -> emptyList()
        }
        if (hits.isEmpty()) return

        val hitsByElement = hits.associateBy { it.methodNameElement }
        for (element in elements) {
            val hit = hitsByElement[element] ?: continue
            result.add(buildMarker(hit))

            val path = file.virtualFile?.path ?: continue
            val lineNumber = file.viewProvider.document?.getLineNumber(element.textRange.startOffset) ?: -1
            ReviewPrompt.recordHit(file.project, "$path:$lineNumber")
        }
    }

    private fun buildMarker(hit: WebhookHit): LineMarkerInfo<PsiElement> {
        val tooltip = "This webhook endpoint has no signature-verification signal in its body -- anyone who guesses the URL can send a forged event"
        return LineMarkerInfo(
            hit.methodNameElement,
            hit.methodNameElement.textRange,
            WebhookIcons.RISK,
            { _: PsiElement -> tooltip },
            null,
            GutterIconRenderer.Alignment.RIGHT,
            { tooltip },
        )
    }
}
