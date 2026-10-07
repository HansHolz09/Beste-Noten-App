package com.hansholz.bestenotenapp.components.enhanced

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
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
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.hansholz.bestenotenapp.main.LocalNativeKeyboardHandoff
import com.hansholz.bestenotenapp.main.LocalNativeTextInputOptions
import com.hansholz.bestenotenapp.main.LocalNativeTextInputTint

internal val LocalEnhancedTextFieldFocusOwner = compositionLocalOf<MutableState<Any?>?> { null }
internal val LocalNativeTextInputRefresh = compositionLocalOf<(() -> Unit)?> { null }

@Composable
fun EnhancedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
) {
    val resolvedInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val focused by resolvedInteractionSource.collectIsFocusedAsState()
    val cursorColor = colors.cursorColor(isError)
    val inputModifier = Modifier.enhancedNativeTextInput(cursorColor, visualTransformation)
    val options = enhancedKeyboardOptions(keyboardOptions)
    val actions = enhancedKeyboardActions(keyboardActions)
    if (LocalNativeTextInputOptions.current == null) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier.then(inputModifier),
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle,
            label = label,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            prefix = prefix,
            suffix = suffix,
            supportingText = supportingText,
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = options,
            keyboardActions = actions,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            interactionSource = resolvedInteractionSource,
            shape = shape,
            colors = colors,
        )
    } else {
        // Keep native input bounds inside the text slot, excluding Compose icon buttons.
        val focusRequester = remember { FocusRequester() }
        val labelPadding = with(LocalDensity.current) { typography.bodySmall.lineHeight.toDp() / 2 }
        CompositionLocalProvider(LocalTextSelectionColors provides colors.textSelectionColors) {
            Box(
                modifier
                    .semantics(mergeDescendants = true) {}
                    .focusNativeTextFieldOnTap(focusRequester, focused, enabled)
                    .padding(top = if (label != null) labelPadding else 0.dp)
                    .defaultMinSize(minWidth = OutlinedTextFieldDefaults.MinWidth, minHeight = OutlinedTextFieldDefaults.MinHeight),
                propagateMinConstraints = true,
            ) {
                OutlinedTextFieldDefaults.DecorationBox(
                    value = value,
                    innerTextField = {
                        BasicTextField(
                            value = value,
                            onValueChange = onValueChange,
                            modifier = inputModifier.focusRequester(focusRequester).fillMaxWidth(),
                            enabled = enabled,
                            readOnly = readOnly,
                            textStyle = textStyle.merge(TextStyle(color = textStyle.color.takeIf { it != Color.Unspecified } ?: colors.textColor(enabled, isError, focused))),
                            cursorBrush = SolidColor(cursorColor),
                            visualTransformation = visualTransformation,
                            singleLine = singleLine,
                            maxLines = maxLines,
                            minLines = minLines,
                            interactionSource = resolvedInteractionSource,
                            keyboardOptions = options,
                            keyboardActions = actions,
                        )
                    },
                    enabled = enabled,
                    singleLine = singleLine,
                    visualTransformation = visualTransformation,
                    interactionSource = resolvedInteractionSource,
                    label = label,
                    placeholder = placeholder,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    prefix = prefix,
                    suffix = suffix,
                    supportingText = supportingText,
                    isError = isError,
                    colors = colors,
                    container = {
                        OutlinedTextFieldDefaults.Container(enabled, isError, resolvedInteractionSource, colors = colors, shape = shape)
                    },
                )
            }
        }
    }
}

@Composable
fun EnhancedTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
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
) {
    val resolvedInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val focused by resolvedInteractionSource.collectIsFocusedAsState()
    val cursorColor = colors.cursorColor(isError)
    val inputModifier = Modifier.enhancedNativeTextInput(cursorColor, outputTransformation)
    val options = enhancedKeyboardOptions(keyboardOptions)
    val handoff = LocalNativeKeyboardHandoff.current
    val keyboardAction =
        if (handoff != null && options.imeAction == ImeAction.Next) {
            KeyboardActionHandler { defaultAction ->
                handoff { onKeyboardAction?.onKeyboardAction(defaultAction) ?: defaultAction() }
            }
        } else {
            onKeyboardAction
        }
    if (LocalNativeTextInputOptions.current == null) {
        OutlinedTextField(
            state = state,
            modifier = modifier.then(inputModifier),
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle,
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
        CompositionLocalProvider(LocalTextSelectionColors provides colors.textSelectionColors) {
            Box(
                modifier
                    .semantics(mergeDescendants = true) {}
                    .focusNativeTextFieldOnTap(focusRequester, focused, enabled)
                    .defaultMinSize(minWidth = OutlinedTextFieldDefaults.MinWidth, minHeight = OutlinedTextFieldDefaults.MinHeight),
                propagateMinConstraints = true,
            ) {
                OutlinedTextFieldDefaults
                    .decorator(
                        state = state,
                        enabled = enabled,
                        lineLimits = lineLimits,
                        outputTransformation = outputTransformation,
                        interactionSource = resolvedInteractionSource,
                        placeholder = placeholder,
                        leadingIcon = leadingIcon,
                        trailingIcon = trailingIcon,
                        isError = isError,
                        colors = colors,
                        container = {
                            OutlinedTextFieldDefaults.Container(enabled, isError, resolvedInteractionSource, colors = colors, shape = shape)
                        },
                    ).Decoration {
                        BasicTextField(
                            state = state,
                            modifier = inputModifier.focusRequester(focusRequester).fillMaxWidth(),
                            enabled = enabled,
                            readOnly = readOnly,
                            textStyle = textStyle.merge(TextStyle(color = textStyle.color.takeIf { it != Color.Unspecified } ?: colors.textColor(enabled, isError, focused))),
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

@Composable
fun EnhancedBasicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = TextStyle.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    interactionSource: MutableInteractionSource? = null,
    cursorBrush: Brush = SolidColor(colorScheme.primary),
    decorationBox: @Composable (@Composable () -> Unit) -> Unit = { it() },
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.enhancedNativeTextInput(if ((cursorBrush as? SolidColor)?.value == Color.Transparent) Color.Transparent else colorScheme.primary, visualTransformation),
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        keyboardOptions = enhancedKeyboardOptions(keyboardOptions),
        keyboardActions = enhancedKeyboardActions(keyboardActions),
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        visualTransformation = visualTransformation,
        onTextLayout = onTextLayout,
        interactionSource = interactionSource,
        cursorBrush = cursorBrush,
        decorationBox = decorationBox,
    )
}

@Composable
fun EnhancedBasicTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = TextStyle.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    interactionSource: MutableInteractionSource? = null,
    cursorBrush: Brush = SolidColor(colorScheme.primary),
    decorationBox: @Composable (@Composable () -> Unit) -> Unit = { it() },
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.enhancedNativeTextInput(if ((cursorBrush as? SolidColor)?.value == Color.Transparent) Color.Transparent else colorScheme.primary, visualTransformation),
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        keyboardOptions = enhancedKeyboardOptions(keyboardOptions),
        keyboardActions = enhancedKeyboardActions(keyboardActions),
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        visualTransformation = visualTransformation,
        onTextLayout = onTextLayout,
        interactionSource = interactionSource,
        cursorBrush = cursorBrush,
        decorationBox = decorationBox,
    )
}

@Composable
private fun enhancedKeyboardOptions(options: KeyboardOptions): KeyboardOptions =
    options.copy(
        platformImeOptions = options.platformImeOptions ?: LocalNativeTextInputOptions.current,
        showKeyboardOnFocus = options.showKeyboardOnFocus,
        hintLocales = options.hintLocales,
    )

@Composable
private fun enhancedKeyboardActions(actions: KeyboardActions): KeyboardActions {
    val handoff = LocalNativeKeyboardHandoff.current ?: return actions
    return KeyboardActions(
        onDone = actions.onDone,
        onGo = actions.onGo,
        onNext = { handoff { actions.onNext?.invoke(this) ?: defaultKeyboardAction(ImeAction.Next) } },
        onPrevious = { handoff { actions.onPrevious?.invoke(this) ?: defaultKeyboardAction(ImeAction.Previous) } },
        onSearch = actions.onSearch,
        onSend = actions.onSend,
    )
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
    val updateTint = LocalNativeTextInputTint.current
    val refresh = LocalNativeTextInputRefresh.current
    val focusManager = LocalFocusManager.current
    var focused by remember { mutableStateOf(false) }
    LaunchedEffect(focused, cursorColor, transformation) {
        if (focused) {
            withFrameNanos {}
            updateTint?.invoke(cursorColor)
            withFrameNanos {}
            updateTint?.invoke(cursorColor)
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
                    val direction = if (event.isShiftPressed) FocusDirection.Previous else FocusDirection.Next
                    handoff { focusManager.moveFocus(direction) }
                }
                true
            } else {
                false
            }
        }.pointerInput(handoff, focusOwner) {
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                if (focusOwner?.value != null && focusOwner.value !== owner) handoff?.invoke {}
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
