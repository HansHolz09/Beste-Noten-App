package com.hansholz.bestenotenapp.data

actual suspend fun readBesteSchuleCache(
    student: String,
    key: String,
): String? = null

actual suspend fun writeBesteSchuleCache(
    student: String,
    key: String,
    value: String,
) = Unit

actual suspend fun clearBesteSchuleCache() = Unit

actual suspend fun besteSchuleCacheSize() = 0L
