package com.idn.quran.presentation.adzan

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.idn.quran.core.data.AdzanRepository
import com.idn.quran.core.data.Resource
import com.idn.quran.core.domain.model.AdzanDataResult

class AdzanViewModel(private val adzanRepository: AdzanRepository) : ViewModel() {

    fun getDailyAdzanTime(): LiveData<Resource<AdzanDataResult>> = adzanRepository.getResultDailyAdzanTime()
}