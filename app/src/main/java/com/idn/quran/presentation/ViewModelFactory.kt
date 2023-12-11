package com.idn.quran.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.idn.quran.core.di.Injection.provideAdzanRepository
import com.idn.quran.core.di.Injection.provideQuranRepository
import com.idn.quran.presentation.adzan.AdzanViewModel
import com.idn.quran.presentation.quran.QuranViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(val context: Context) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(QuranViewModel::class.java) -> {
                QuranViewModel(provideQuranRepository()) as T
            }
            modelClass.isAssignableFrom(AdzanViewModel::class.java) -> {
                AdzanViewModel(provideAdzanRepository(context)) as T
            }
            else -> throw Throwable("Unknown ViewModel Class: " + modelClass.name)
    }
}