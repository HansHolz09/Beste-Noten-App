package com.hansholz.bestenotenapp.data

expect suspend fun readBesteSchuleCache(
    student: String,
    key: String,
): String?

expect suspend fun writeBesteSchuleCache(
    student: String,
    key: String,
    value: String,
)

expect suspend fun clearBesteSchuleCache()

expect suspend fun besteSchuleCacheSize(): Long
