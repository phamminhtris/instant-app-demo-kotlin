package com.trustcircle.internetmodule

import android.content.Context
import android.preference.PreferenceManager

/**
 * Created by tripham on 8/4/17.
 */
object QueryPreferences {
    private val PREF_SEARCH_QUERY = "searchQuery"
    fun getStoredQuery(context: Context): String {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_SEARCH_QUERY, "")
    }

    fun setStoredQuery(context: Context, query: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_SEARCH_QUERY, query)
                .apply()
    }
}