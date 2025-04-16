package com.vector.omdbapp.data.model

object YearFilter {
    const val ALL = "All"
    fun generateYearOptions(): List<String> {
        val currentYear = java.time.Year.now().value
        val years = (currentYear downTo 1950).map { it.toString() }
        return listOf(ALL) + years
    }
}

