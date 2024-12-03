package com.aramonp.workly.domain.repository

import com.aramonp.workly.domain.model.User

interface ResourceProvider {
    fun getString(resId: Int): String
    fun getString(resId: Int, vararg formatArgs: Any): String
}