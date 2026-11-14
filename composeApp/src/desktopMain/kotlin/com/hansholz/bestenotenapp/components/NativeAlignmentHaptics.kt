package com.hansholz.bestenotenapp.components

import java.lang.foreign.Arena
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.Linker
import java.lang.foreign.MemorySegment
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout.ADDRESS
import java.lang.foreign.ValueLayout.JAVA_LONG

internal object NativeAlignmentHaptics {
    private val linker = Linker.nativeLinker()
    private val objc = SymbolLookup.libraryLookup("/usr/lib/libobjc.A.dylib", Arena.global())
    private val msgSend = objc.find("objc_msgSend").get()
    private val getClass = linker.downcallHandle(objc.find("objc_getClass").get(), FunctionDescriptor.of(ADDRESS, ADDRESS))
    private val registerName = linker.downcallHandle(objc.find("sel_registerName").get(), FunctionDescriptor.of(ADDRESS, ADDRESS))
    private val perform = linker.downcallHandle(msgSend, FunctionDescriptor.ofVoid(ADDRESS, ADDRESS, JAVA_LONG, JAVA_LONG))
    private val performSelector = selector("performFeedbackPattern:performanceTime:")
    private val performer =
        linker.downcallHandle(msgSend, FunctionDescriptor.of(ADDRESS, ADDRESS, ADDRESS)).invoke(
            getClass.invoke(Arena.global().allocateFrom("NSHapticFeedbackManager")) as MemorySegment,
            selector("defaultPerformer"),
        ) as MemorySegment

    private fun selector(name: String) = registerName.invoke(Arena.global().allocateFrom(name)) as MemorySegment

    fun alignmentOccurred() {
        perform.invoke(performer, performSelector, 1L, 1L)
    }
}
