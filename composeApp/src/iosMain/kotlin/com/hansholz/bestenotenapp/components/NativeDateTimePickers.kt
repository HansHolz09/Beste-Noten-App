package com.hansholz.bestenotenapp.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropInteractionMode
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitHour
import platform.Foundation.NSCalendarUnitMinute
import platform.Foundation.NSDate
import platform.Foundation.NSLocale
import platform.Foundation.NSRunLoop
import platform.Foundation.NSRunLoopCommonModes
import platform.Foundation.NSSelectorFromString
import platform.QuartzCore.CADisplayLink
import platform.UIKit.UIColor
import platform.UIKit.UIControlEventValueChanged
import platform.UIKit.UIDatePicker
import platform.UIKit.UIDatePickerMode
import platform.UIKit.UIDatePickerStyle
import platform.UIKit.UIFont
import platform.UIKit.UILabel
import platform.UIKit.UIView
import platform.darwin.NSObject
import kotlin.math.abs

private const val REFERENCE_DATE_UNIX_OFFSET_SECONDS = 978_307_200.0
private const val APP_BODY_FONT_NAME = "Sniglet-Regular"

private val activeNativeDateTimePickers = mutableMapOf<UIDatePicker, Int>()
private var nativeDateTimePickerGeneration = 0

@OptIn(ExperimentalForeignApi::class)
private class AppDatePicker : UIDatePicker {
    private val applyCalendarTypography: Boolean
    private var typographyDisplayLink: CADisplayLink? = null

    constructor(applyCalendarTypography: Boolean) : super(frame = CGRectMake(0.0, 0.0, 0.0, 0.0)) {
        this.applyCalendarTypography = applyCalendarTypography
    }

    override fun layoutSubviews() {
        super.layoutSubviews()
        tintColor = window?.tintColor ?: superview?.tintColor ?: UIColor.yellowColor
        if (applyCalendarTypography) applyCalendarTypography()
    }

    override fun didMoveToWindow() {
        super.didMoveToWindow()
        if (!applyCalendarTypography) return
        if (window != null) startTypographyDisplayLink() else stopTypographyDisplayLink()
    }

    private fun startTypographyDisplayLink() {
        if (typographyDisplayLink != null) return
        val target = DisplayLinkTarget { applyCalendarTypography() }
        val link = CADisplayLink.displayLinkWithTarget(target, NSSelectorFromString("tick"))
        link.addToRunLoop(NSRunLoop.mainRunLoop, NSRunLoopCommonModes)
        typographyDisplayLink = link
    }

    private fun stopTypographyDisplayLink() {
        typographyDisplayLink?.invalidate()
        typographyDisplayLink = null
    }
}

@OptIn(BetaInteropApi::class, ExperimentalForeignApi::class)
private class DisplayLinkTarget(
    private val onTick: () -> Unit,
) : NSObject() {
    @ObjCAction
    fun tick() {
        onTick()
    }
}

private fun UIView.applyCalendarTypography() {
    (this as? UILabel)?.let { label ->
        val currentFontName = label.font.fontName
        if (currentFontName != APP_BODY_FONT_NAME) {
            UIFont.fontWithName(APP_BODY_FONT_NAME, label.font.pointSize)?.let { label.font = it }
        }
    }
    subviews.forEach { (it as UIView).applyCalendarTypography() }
}

internal fun hideVisibleNativeDateTimePickers() {
    nativeDateTimePickerGeneration++
    activeNativeDateTimePickers.keys.forEach { it.hidden = true }
}

private fun <T : UIDatePicker> T.registerAsVisiblePicker(): T =
    also {
        activeNativeDateTimePickers[it] = nativeDateTimePickerGeneration
        tintColor = superview?.tintColor ?: UIColor.yellowColor
    }

private fun UIDatePicker.updateNativeAppearance() {
    hidden = activeNativeDateTimePickers[this] != nativeDateTimePickerGeneration
    tintColor = window?.tintColor ?: superview?.tintColor ?: UIColor.yellowColor
}

@OptIn(BetaInteropApi::class, ExperimentalForeignApi::class, ExperimentalComposeUiApi::class)
@Composable
internal fun NativeDatePicker(
    selectedDateMillis: Long?,
    onSelectedDateChanged: (Long) -> Unit,
    modifier: Modifier,
) {
    val target = remember { DatePickerTarget(onSelectedDateChanged) }
    target.onSelectedDateChanged = onSelectedDateChanged
    UIKitView(
        factory = {
            AppDatePicker(applyCalendarTypography = true)
                .apply {
                    datePickerMode = UIDatePickerMode.UIDatePickerModeDate
                    preferredDatePickerStyle = UIDatePickerStyle.UIDatePickerStyleInline
                    addTarget(target, NSSelectorFromString("valueChanged:"), UIControlEventValueChanged)
                }.registerAsVisiblePicker()
        },
        update = { picker ->
            picker.updateNativeAppearance()
            selectedDateMillis?.let { millis ->
                val seconds = millis.toDouble() / 1000.0
                val currentSeconds = picker.date.timeIntervalSinceReferenceDate + REFERENCE_DATE_UNIX_OFFSET_SECONDS
                if (abs(currentSeconds - seconds) > 1.0) {
                    picker.setDate(
                        NSDate(timeIntervalSinceReferenceDate = seconds - REFERENCE_DATE_UNIX_OFFSET_SECONDS),
                        animated = false,
                    )
                }
            }
        },
        modifier = modifier,
        onRelease = { activeNativeDateTimePickers.remove(it) },
        properties =
            UIKitInteropProperties(
                interactionMode = UIKitInteropInteractionMode.Cooperative(),
                isNativeAccessibilityEnabled = true,
                placedAsOverlay = true,
            ),
    )
}

@OptIn(BetaInteropApi::class, ExperimentalForeignApi::class, ExperimentalComposeUiApi::class)
@Composable
internal fun NativeTimePicker(
    hour: Int,
    minute: Int,
    onTimeChanged: (Int, Int) -> Unit,
    modifier: Modifier,
) {
    val target = remember { TimePickerTarget(onTimeChanged) }
    target.onTimeChanged = onTimeChanged
    UIKitView(
        factory = {
            AppDatePicker(applyCalendarTypography = true)
                .apply {
                    datePickerMode = UIDatePickerMode.UIDatePickerModeTime
                    preferredDatePickerStyle = UIDatePickerStyle.UIDatePickerStyleWheels
                    locale = NSLocale(localeIdentifier = "de_DE")
                    addTarget(target, NSSelectorFromString("valueChanged:"), UIControlEventValueChanged)
                }.registerAsVisiblePicker()
        },
        update = { picker ->
            picker.updateNativeAppearance()
            val calendar = NSCalendar.currentCalendar
            val currentHour = calendar.component(NSCalendarUnitHour, picker.date).toInt()
            val currentMinute = calendar.component(NSCalendarUnitMinute, picker.date).toInt()
            if (currentHour != hour || currentMinute != minute) {
                calendar
                    .dateBySettingHour(hour.toLong(), minute.toLong(), 0, picker.date, 0u)
                    ?.let { picker.setDate(it, animated = false) }
            }
        },
        modifier = modifier,
        onRelease = { activeNativeDateTimePickers.remove(it) },
        properties =
            UIKitInteropProperties(
                interactionMode = UIKitInteropInteractionMode.Cooperative(),
                isNativeAccessibilityEnabled = true,
                placedAsOverlay = true,
            ),
    )
}

@OptIn(BetaInteropApi::class, ExperimentalForeignApi::class)
private class DatePickerTarget(
    var onSelectedDateChanged: (Long) -> Unit,
) : NSObject() {
    @ObjCAction
    fun valueChanged(sender: UIDatePicker) {
        val unixSeconds = sender.date.timeIntervalSinceReferenceDate + REFERENCE_DATE_UNIX_OFFSET_SECONDS
        onSelectedDateChanged((unixSeconds * 1000.0).toLong())
    }
}

@OptIn(BetaInteropApi::class, ExperimentalForeignApi::class)
private class TimePickerTarget(
    var onTimeChanged: (Int, Int) -> Unit,
) : NSObject() {
    @ObjCAction
    fun valueChanged(sender: UIDatePicker) {
        val calendar = NSCalendar.currentCalendar
        onTimeChanged(
            calendar.component(NSCalendarUnitHour, sender.date).toInt(),
            calendar.component(NSCalendarUnitMinute, sender.date).toInt(),
        )
    }
}
