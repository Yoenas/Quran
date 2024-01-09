package com.idn.quran.presentation.quran

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.idn.quran.R
import com.idn.quran.adapter.SurahAdapter
import com.idn.quran.core.data.Resource
import com.idn.quran.core.domain.model.Ayah
import com.idn.quran.core.domain.model.Surah
import com.idn.quran.databinding.ActivityDetailSurahBinding
import com.idn.quran.databinding.CustomViewAlertdialogBinding
import com.idn.quran.presentation.ViewModelFactory

class DetailSurahActivity : AppCompatActivity() {
    private var _binding: ActivityDetailSurahBinding? = null
    private val binding get() = _binding as ActivityDetailSurahBinding

    private var _surah: Surah? = null
    private val surah get() = _surah as Surah

    private var _mediaPlayer: MediaPlayer? = null
    private val mediaPlayer get() = _mediaPlayer as MediaPlayer

    private val quranViewModel: QuranViewModel by viewModels { ViewModelFactory(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailSurahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        _surah = when {
            Build.VERSION.SDK_INT >= 33 -> intent.getParcelableExtra(EXTRA_DATA, Surah::class.java)
            else -> @Suppress("DEPRECATION") intent.getParcelableExtra(EXTRA_DATA)
        }

        initView()

        val mAdapter = SurahAdapter()
        mAdapter.setOnItemClickCallback(object : SurahAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Ayah) {
                showCustomAlertDialog(data, surah)
            }
        })

        val numberSurah = surah.number
        if (numberSurah != null) {
            quranViewModel.getDetailSurahWithQuranEdition(numberSurah).observe(this) {
                when (it) {
                    is Resource.Loading -> showLoading(true)
                    is Resource.Success -> {
                        binding.rvSurah.apply {
                            mAdapter.setData(it.data?.get(0)?.listAyahs, it.data)
                            adapter = mAdapter
                            layoutManager = LinearLayoutManager(this@DetailSurahActivity)
                        }
                        showLoading(false)
                    }

                    is Resource.Error -> {
                        Toast.makeText(this, "Error ${it.message}", Toast.LENGTH_SHORT).show()
                        showLoading(false)
                    }
                }
            }
        } else {
            Toast.makeText(this, "Number Surah not Found.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            if (isLoading) {
                progressBar.visibility = View.VISIBLE
                cvDetailSurah.visibility = View.GONE
                rvSurah.visibility = View.GONE
            } else {
                progressBar.visibility = View.GONE
                cvDetailSurah.visibility = View.VISIBLE
                rvSurah.visibility = View.VISIBLE
            }
        }
    }

    private fun initView() {
        binding.apply {
            val revelationType = surah.revelationType
            val numberOfAyahs = surah.numberOfAyahs
            val resultOfAyah = "$revelationType - $numberOfAyahs Ayahs"
            tvDetailAyah.text = resultOfAyah
            tvDetailName.text = surah.name
            tvDetailSurah.text = surah.englishName
            tvDetailNameTranslation.text = surah.englishNameTranslation
        }
    }

    private fun showCustomAlertDialog(dataAudio: Ayah, surah: Surah) {
        _mediaPlayer = MediaPlayer()
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog).create()
        val view = CustomViewAlertdialogBinding.inflate(layoutInflater)
        builder.setView(view.root)
        view.apply {
            tvSurah.text = surah.englishName
            tvName.text = surah.name
            val numberInSurah = dataAudio.numberInSurah
            val resultNumberText = "Ayah $numberInSurah"
            tvNumberAyah.text = resultNumberText
        }
        builder.setOnShowListener {
            view.btnPlay.apply {
                text = getString(R.string.loading_audio)
                isEnabled = false
                mediaPlayer.apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    try {
                        setDataSource(dataAudio.audio)
                        prepareAsync()
                        setOnPreparedListener {
                            isEnabled = true
                            text = getString(R.string.play_audio)
                            setOnClickListener {
                                it.isEnabled = false
                                text = getString(R.string.playing_audio)
                                start()
                            }
                            setOnCompletionListener {
                                isEnabled = true
                                text = getString(R.string.play_audio)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        view.btnStop.setOnClickListener {
            mediaPlayer.stop()
            builder.dismiss()
        }
        builder.setCanceledOnTouchOutside(false)
        builder.show()
        builder.setOnDismissListener {
            mediaPlayer.stop()
        }
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer.stop()
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
    }

    companion object {
        const val EXTRA_DATA = "number"
    }
}