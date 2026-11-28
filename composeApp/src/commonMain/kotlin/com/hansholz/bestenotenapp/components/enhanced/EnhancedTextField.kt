package com.hansholz.bestenotenapp.components.enhanced

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldDecorator
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldLabelPosition
import androidx.compose.material3.TextFieldLabelScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.hansholz.bestenotenapp.main.LocalNativeKeyboardHandoff
import com.hansholz.bestenotenapp.main.LocalNativeTextInputOptions

internal val LocalEnhancedTextFieldFocusOwner = compositionLocalOf<MutableState<Any?>?> { null }
internal val LocalNativeTextInputRefresh = compositionLocalOf<(() -> Unit)?> { null }

@Composable
fun EnhancedTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    labelPosition: TextFieldLabelPosition = TextFieldLabelPosition.Cutout(),
    label: @Composable (TextFieldLabelScope.() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    inputTransformation: InputTransformation? = null,
    outputTransformation: OutputTransformation? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.Default,
    scrollState: ScrollState = rememberScrollState(),
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
    textObfuscationMode: TextObfuscationMode? = null,
) {
    val resolvedInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val focused by resolvedInteractionSource.collectIsFocusedAsState()
    val cursorColor = colors.cursorColor(isError)
    val inputModifier = Modifier.enhancedNativeTextInput(cursorColor, outputTransformation ?: textObfuscationMode)
    val options = enhancedKeyboardOptions(keyboardOptions)
    val keyboardAction = enhancedKeyboardAction(options, onKeyboardAction)
    if (LocalNativeTextInputOptions.current == null && textObfuscationMode != null) {
        OutlinedSecureTextField(
            state = state,
            modifier = modifier.then(inputModifier),
            enabled = enabled,
            textStyle = textStyle,
            labelPosition = labelPosition,
            label = label,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            isError = isError,
            inputTransformation = inputTransformation,
            textObfuscationMode = textObfuscationMode,
            keyboardOptions = options,
            onKeyboardAction = keyboardAction,
            shape = shape,
            colors = colors,
            interactionSource = resolvedInteractionSource,
        )
    } else if (LocalNativeTextInputOptions.current == null) {
        OutlinedTextField(
            state = state,
            modifier = modifier.then(inputModifier),
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle,
            labelPosition = labelPosition,
            label = label,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            isError = isError,
            inputTransformation = inputTransformation,
            outputTransformation = outputTransformation,
            keyboardOptions = options,
            onKeyboardAction = keyboardAction,
            lineLimits = lineLimits,
            scrollState = scrollState,
            shape = shape,
            colors = colors,
            interactionSource = resolvedInteractionSource,
        )
    } else {
        val focusRequester = remember { FocusRequester() }
        val labelPadding = with(LocalDensity.current) { typography.bodySmall.lineHeight.toDp() / 2 }
        CompositionLocalProvider(
            LocalTextSelectionColors provides TextSelectionColors(cursorColor, colors.textSelectionColors.backgroundColor),
        ) {
            Box(
                modifier
                    .semantics(mergeDescendants = true) {}
                    .focusNativeTextFieldOnTap(focusRequester, focused, enabled)
                    .padding(top = if (label != null) labelPadding else 0.dp)
                    .defaultMinSize(minWidth = OutlinedTextFieldDefaults.MinWidth, minHeight = OutlinedTextFieldDefaults.MinHeight),
                propagateMinConstraints = true,
            ) {
                OutlinedTextFieldDefaults
                    .decorator(
                        state = state,
                        enabled = enabled,
                        lineLimits = if (textObfuscationMode != null) TextFieldLineLimits.SingleLine else lineLimits,
                        outputTransformation = outputTransformation,
                        interactionSource = resolvedInteractionSource,
                        labelPosition = labelPosition,
                        label = label,
                        placeholder = placeholder,
                        leadingIcon = leadingIcon?.redirectFocusOnEnter(focusRequester) { focused },
                        trailingIcon = trailingIcon?.redirectFocusOnEnter(focusRequester) { focused },
                        isError = isError,
                        colors = colors,
                        container = {
                            OutlinedTextFieldDefaults.Container(enabled, isError, resolvedInteractionSource, colors = colors, shape = shape)
                        },
                    ).Decoration {
                        val fieldModifier = inputModifier.focusRequester(focusRequester).fillMaxWidth()
                        val fieldTextStyle = textStyle.merge(TextStyle(color = textStyle.color.takeIf { it != Color.Unspecified } ?: colors.textColor(enabled, isError, focused)))
                        if (textObfuscationMode != null) {
                            BasicSecureTextField(
                                state = state,
                                modifier = fieldModifier,
                                enabled = enabled,
                                readOnly = readOnly,
                                inputTransformation = inputTransformation,
                                textStyle = fieldTextStyle,
                                keyboardOptions = options,
                                onKeyboardAction = keyboardAction,
                                interactionSource = resolvedInteractionSource,
                                cursorBrush = SolidColor(cursorColor),
                                textObfuscationMode = textObfuscationMode,
                                scrollState = scrollState,
                            )
                        } else {
                            BasicTextField(
                                state = state,
                                modifier = fieldModifier,
                                enabled = enabled,
                                readOnly = readOnly,
                                textStyle = fieldTextStyle,
                                cursorBrush = SolidColor(cursorColor),
                                inputTransformation = inputTransformation,
                                outputTransformation = outputTransformation,
                                keyboardOptions = options,
                                onKeyboardAction = keyboardAction,
                                lineLimits = lineLimits,
                                scrollState = scrollState,
                                interactionSource = resolvedInteractionSource,
                            )
                        }
                    }
            }
        }
    }
}

@Composable
fun EnhancedBasicTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    inputTransformation: InputTransformation? = null,
    textStyle: TextStyle = TextStyle.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.Default,
    cursorBrush: Brush = SolidColor(colorScheme.primary),
    outputTransformation: OutputTransformation? = null,
    decorator: TextFieldDecorator? = null,
) {
    val cursorColor = (cursorBrush as? SolidColor)?.value ?: colorScheme.primary
    val options = enhancedKeyboardOptions(keyboardOptions)
    EnhancedNativeTextSelectionColors(cursorColor) {
        BasicTextField(
            state = state,
            modifier = modifier.enhancedNativeTextInput(cursorColor, outputTransformation),
            enabled = enabled,
            inputTransformation = inputTransformation,
            textStyle = textStyle,
            keyboardOptions = options,
            onKeyboardAction = enhancedKeyboardAction(options, onKeyboardAction),
            lineLimits = lineLimits,
            cursorBrush = cursorBrush,
            outputTransformation = outputTransformation,
            decorator = decorator,
        )
    }
}

@Composable
private fun EnhancedNativeTextSelectionColors(
    cursorColor: Color,
    content: @Composable () -> Unit,
) {
    if (LocalNativeTextInputOptions.current == null) {
        content()
    } else {
        val selectionColors = LocalTextSelectionColors.current
        CompositionLocalProvider(
            LocalTextSelectionColors provides TextSelectionColors(cursorColor, selectionColors.backgroundColor),
            content = content,
        )
    }
}

@Composable
private fun enhancedKeyboardOptions(options: KeyboardOptions): KeyboardOptions =
    options.copy(
        platformImeOptions = options.platformImeOptions ?: LocalNativeTextInputOptions.current,
        showKeyboardOnFocus = options.showKeyboardOnFocus,
        hintLocales = options.hintLocales,
    )

@Composable
private fun enhancedKeyboardAction(
    options: KeyboardOptions,
    onKeyboardAction: KeyboardActionHandler?,
): KeyboardActionHandler? {
    val handoff = LocalNativeKeyboardHandoff.current
    if (handoff == null || options.imeAction != ImeAction.Next) return onKeyboardAction
    return KeyboardActionHandler { defaultAction ->
        handoff { onKeyboardAction?.onKeyboardAction(defaultAction) ?: defaultAction() }
    }
}

private fun (@Composable () -> Unit).redirectFocusOnEnter(
    focusRequester: FocusRequester,
    focused: () -> Boolean,
): @Composable () -> Unit =
    {
        Box(Modifier.onFocusChanged { if (it.hasFocus && !focused()) focusRequester.requestFocus() }) {
            this@redirectFocusOnEnter()
        }
    }

@Composable
private fun Modifier.focusNativeTextFieldOnTap(
    focusRequester: FocusRequester,
    focused: Boolean,
    enabled: Boolean,
): Modifier {
    if (!enabled) return this
    val handoff = LocalNativeKeyboardHandoff.current
    val focusOwner = LocalEnhancedTextFieldFocusOwner.current
    return pointerInput(focusRequester, focused, handoff, focusOwner) {
        detectTapGestures {
            if (!focused && focusOwner?.value != null && handoff != null) {
                handoff { focusRequester.requestFocus() }
            } else {
                focusRequester.requestFocus()
            }
        }
    }
}

@Composable
private fun Modifier.enhancedNativeTextInput(
    cursorColor: Color,
    transformation: Any?,
): Modifier {
    if (LocalNativeTextInputOptions.current == null) return this
    val owner = remember { Any() }
    val focusOwner = LocalEnhancedTextFieldFocusOwner.current
    val handoff = LocalNativeKeyboardHandoff.current
    val refresh = LocalNativeTextInputRefresh.current
    val focusManager = LocalFocusManager.current
    var focused by remember { mutableStateOf(false) }
    LaunchedEffect(focused, cursorColor, transformation) {
        if (focused) {
            withFrameNanos {}
            withFrameNanos {}
            // UIKit otherwise keeps the old caret geometry after a visual transformation.
            refresh?.invoke()
        }
    }
    DisposableEffect(focusOwner) {
        onDispose { if (focusOwner?.value === owner) focusOwner.value = null }
    }
    return this
        .onPreviewKeyEvent { event ->
            if (event.key == Key.Tab && handoff != null) {
                if (event.type == KeyEventType.KeyDown) {
                    focusManager.moveFocus(if (event.isShiftPressed) FocusDirection.Previous else FocusDirection.Next)
                }
                true
            } else {
                false
            }
        }.pointerInput(handoff, focusOwner) {
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                val up = waitForUpOrCancellation(PointerEventPass.Initial)
                if (up != null && focusOwner?.value != null && focusOwner.value !== owner) handoff?.invoke {}
            }
        }.onFocusChanged {
            focused = it.isFocused
            if (focused) {
                focusOwner?.value = owner
            } else if (focusOwner?.value === owner) {
                focusOwner.value = null
            }
        }
}
