package com.vector.omdbapp.data.model

/**
 * Enum class for search type filter
 */
enum class TypeFilter(val displayName: String, val value: String) {
    ALL("All", ""),
    MOVIE("Movie", "movie"),
    SERIES("Series", "series"),
    EPISODE("Episode", "episode");

    companion object {
        fun fromDisplayName(name: String): TypeFilter {
            return entries.firstOrNull { it.displayName.equals(name, ignoreCase = true) } ?: ALL
        }

        fun displayNames(): List<String> = entries.map { it.displayName }
    }
}
