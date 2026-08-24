package dev.gaphunter.webhooksignaturecompanion.detect

import com.intellij.psi.JavaRecursiveElementWalkingVisitor
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiLiteralExpression
import com.intellij.psi.PsiMethod
import dev.gaphunter.webhooksignaturecompanion.model.WebhookHit

/**
 * Finds Java Spring MVC endpoint methods whose mapping path contains
 * "webhook" (case-insensitive -- the real, common naming convention for
 * this kind of endpoint: `/webhooks/stripe`, `/api/webhook/github`,
 * etc.) with no signature-verification signal anywhere in the method
 * body ([SignatureVerificationSignals]).
 */
object JavaWebhookFinder {

    private val MAPPING_ANNOTATIONS = setOf("PostMapping", "RequestMapping", "PutMapping")

    fun findAll(file: PsiFile): List<WebhookHit> {
        val hits = mutableListOf<WebhookHit>()
        file.accept(object : JavaRecursiveElementWalkingVisitor() {
            override fun visitMethod(method: PsiMethod) {
                super.visitMethod(method)
                if (!hasWebhookPath(method)) return
                val bodyText = method.body?.text ?: return
                if (SignatureVerificationSignals.bodyLooksVerified(bodyText)) return
                val nameIdentifier = method.nameIdentifier ?: return
                hits += WebhookHit(nameIdentifier)
            }
        })
        return hits
    }

    private fun hasWebhookPath(method: PsiMethod): Boolean {
        for (annotation in method.modifierList?.annotations.orEmpty()) {
            val simpleName = annotation.nameReferenceElement?.referenceName ?: continue
            if (simpleName !in MAPPING_ANNOTATIONS) continue
            val pathValue = annotation.findAttributeValue("value") ?: annotation.findAttributeValue("path")
            val pathText = (pathValue as? PsiLiteralExpression)?.value as? String ?: continue
            if (pathText.contains("webhook", ignoreCase = true)) return true
        }
        return false
    }
}
