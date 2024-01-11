package com.idn.quran.presentation.adzan

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.idn.quran.core.data.Resource
import com.idn.quran.databinding.FragmentAdzanBinding
import com.idn.quran.presentation.ViewModelFactory

class AdzanFragment : Fragment() {
    private var _binding: FragmentAdzanBinding? = null
    private val binding get() = _binding as FragmentAdzanBinding

    private val adzanViewModel: AdzanViewModel by viewModels { ViewModelFactory(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdzanBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adzanViewModel.getDailyAdzanTime().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    binding.apply {
                        it.data?.let { adzanDataResult ->
                            tvLocation.text = adzanDataResult.listLocation[1]
                            tvDate.text = adzanDataResult.listCalendar[3]
                        }
                    }
                    when (val adzanTime = it.data?.dailyAdzan) {
                        is Resource.Loading -> showLoading(true)
                        is Resource.Success -> {
                            binding.apply {
                                adzanTime.data?.let { time ->
                                    tvTimeImsak.text = time.imsak
                                    tvTimeSubuh.text = time.subuh
                                    tvTimeDzuhur.text = time.dzuhur
                                    tvTimeAshar.text = time.ashar
                                    tvTimeMaghrib.text = time.maghrib
                                    tvTimeIsya.text = time.isya
                                }
                            }
                            showLoading(false)
                        }

                        is Resource.Error -> {
                            showLoading(false)
                            Log.e("AdzanFragment", "error getting schedule: ${adzanTime.message}", )
                            Toast.makeText(context, "Error: ${adzanTime.message}", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            showLoading(false)
                            Log.e("AdzanFragment", "error getting location: ${it.message}", )
                            Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                is Resource.Error -> {
                    Log.e("AdzanFragment", "error observing AdzanViewModel: ${it.message}", )
                    Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            if (isLoading) {
                progressBar.visibility = View.VISIBLE
                cvAdzanTime.visibility = View.GONE
            } else {
                progressBar.visibility = View.GONE
                cvAdzanTime.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}