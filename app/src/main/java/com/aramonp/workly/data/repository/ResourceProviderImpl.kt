package com.aramonp.workly.data.repository

import android.content.Context
import com.aramonp.workly.domain.repository.ResourceProvider
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ResourceProviderImpl @Inject constructor (
    @ApplicationContext private val context: Context
): ResourceProvider {
    override fun getString(resId: Int): String = context.getString(resId)
    override fun getString(resId: Int, vararg formatArgs: Any): String = context.getString(resId, *formatArgs)
}