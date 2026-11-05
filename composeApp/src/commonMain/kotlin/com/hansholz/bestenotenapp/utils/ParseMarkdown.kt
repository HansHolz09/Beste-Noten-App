package com.hansholz.bestenotenapp.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.mikepenz.markdown.annotator.DefaultAnnotatorSettings
import com.mikepenz.markdown.annotator.buildMarkdownAnnotatedString
import com.mikepenz.markdown.model.markdownAnnotator
import com.mikepenz.markdown.model.markdownAnnotatorConfig
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.CancellationToken
import org.intellij.markdown.parser.MarkdownParser

private val annotatorSettings =
    DefaultAnnotatorSettings(
        linkTextSpanStyle = TextLinkStyles(SpanStyle(textDecoration = TextDecoration.Underline)),
        codeSpanStyle = SpanStyle(fontFamily = FontFamily.Monospace),
        annotator = markdownAnnotator(markdownAnnotatorConfig(eolAsNewLine = true)) { _, child -> child.type == MarkdownElementTypes.IMAGE },
        highlightSpanStyle = SpanStyle(background = Color.Yellow.copy(alpha = 0.4f)),
    )

private val headings =
    setOf(
        MarkdownElementTypes.ATX_1,
        MarkdownElementTypes.ATX_2,
        MarkdownElementTypes.ATX_3,
        MarkdownElementTypes.ATX_4,
        MarkdownElementTypes.ATX_5,
        MarkdownElementTypes.ATX_6,
        MarkdownElementTypes.SETEXT_1,
        MarkdownElementTypes.SETEXT_2,
    )

fun String.parseMarkdown(): AnnotatedString {
    val tree = MarkdownParser(flavour = GFMFlavourDescriptor(), cancellationToken = CancellationToken.NonCancellable).buildMarkdownTreeFromString(this as CharSequence)
    val result = buildAnnotatedString { appendMarkdownBlocks(this@parseMarkdown, tree.children) }
    return result.subSequence(0, result.text.trimEnd().length)
}

private fun AnnotatedString.Builder.appendMarkdownBlocks(
    content: String,
    nodes: List<ASTNode>,
) {
    nodes.forEach { node ->
        when (node.type) {
            MarkdownElementTypes.PARAGRAPH -> {
                buildMarkdownAnnotatedString(content, node, annotatorSettings)
            }

            in headings -> {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    node.children
                        .firstOrNull { it.type == MarkdownTokenTypes.ATX_CONTENT || it.type == MarkdownTokenTypes.SETEXT_CONTENT }
                        ?.let { buildMarkdownAnnotatedString(content, it.children.dropWhile { child -> child.type == MarkdownTokenTypes.WHITE_SPACE }, annotatorSettings) }
                }
            }

            MarkdownElementTypes.UNORDERED_LIST,
            MarkdownElementTypes.ORDERED_LIST,
            MarkdownElementTypes.LIST_ITEM,
            MarkdownElementTypes.BLOCK_QUOTE,
            -> {
                appendMarkdownBlocks(content, node.children)
            }

            MarkdownElementTypes.CODE_FENCE, MarkdownElementTypes.CODE_BLOCK -> {
                withStyle(annotatorSettings.codeSpanStyle) {
                    append(
                        node.children
                            .filter { it.type in setOf(MarkdownTokenTypes.CODE_FENCE_CONTENT, MarkdownTokenTypes.CODE_LINE, MarkdownTokenTypes.EOL) }
                            .joinToString("") { it.getTextInNode(content) }
                            .trim('\n'),
                    )
                }
            }

            MarkdownTokenTypes.LIST_BULLET -> {
                append("• ")
            }

            MarkdownTokenTypes.LIST_NUMBER -> {
                append("${node.getTextInNode(content).trim()} ")
            }

            MarkdownTokenTypes.EOL -> {
                append('\n')
            }

            MarkdownTokenTypes.BLOCK_QUOTE, MarkdownTokenTypes.WHITE_SPACE, MarkdownTokenTypes.HORIZONTAL_RULE -> {}

            else -> {
                append(node.getTextInNode(content))
            }
        }
    }
}
