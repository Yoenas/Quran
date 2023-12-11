package com.idn.quran.core.domain.model

import com.idn.quran.core.data.Resource

data class AdzanDataResult(
    val listLocation: List<String>,
    val dailyAdzan: Resource<DailyAdzan>,
    val listCalendar: List<String>
)
