package dev.gaphunter.webhooksignaturecompanion.detect

import com.intellij.psi.PsiFile
import dev.gaphunter.webhooksignaturecompanion.model.WebhookHit
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid

/** Kotlin counterpart of [JavaWebhookFinder]. */
object KotlinWebhookFinder {

    private val MAPPING_ANNOTATIONS = setOf("PostMapping", "RequestMapping", "PutMapping")

    fun findAll(file: PsiFile): List<WebhookHit> {
        if (file !is KtFile) return emptyList()
        val hits = mutableListOf<WebhookHit>()
        file.accept(object : KtTreeVisitorVoid() {
            override fun visitNamedFunction(function: KtNamedFunction) {
                super.visitNamedFunction(function)
                if (!hasWebhookPath(function)) return
                val bodyText = function.bodyExpression?.text ?: return
                if (SignatureVerificationSignals.bodyLooksVerified(bodyText)) return
                val nameIdentifier = function.nameIdentifier ?: return
                hits += WebhookHit(nameIdentifier)
            }
        })
        return hits
    }

    private fun hasWebhookPath(function: KtNamedFunction): Boolean {
        for (entry in function.annotationEntries) {
            val simpleName = entry.shortName?.asString() ?: continue
            if (simpleName !in MAPPING_ANNOTATIONS) continue
            val named = entry.valueArguments.firstOrNull {
                val argName = it.getArgumentName()?.asName?.asString()
                argName == "value" || argName == "path"
            }
            val positional = entry.valueArguments.firstOrNull { it.getArgumentName() == null }
            val pathText = (named ?: positional)?.getArgumentExpression()?.text ?: continue
            if (pathText.contains("webhook", ignoreCase = true)) return true
        }
        return false
    }
}
