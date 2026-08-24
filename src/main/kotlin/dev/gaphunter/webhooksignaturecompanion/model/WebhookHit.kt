package dev.gaphunter.webhooksignaturecompanion.model

import com.intellij.psi.PsiElement

/** One Spring endpoint whose path looks like a webhook receiver, with no signature-verification signal found anywhere in its method body. */
data class WebhookHit(val methodNameElement: PsiElement)
