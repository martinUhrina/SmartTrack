package com.example.clickaplication

import android.R.attr.data
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class MainViewModel : ViewModel() {         //Priprava do budúcna, využitie maniViewModela
    private var _counter = MutableLiveData<Int>()
    val counter:LiveData<Int>get() = _counter
    private var _hightScore = MutableLiveData<Int>()
    val hightScore:LiveData<Int>get() = _hightScore
    private var _SeconLeft = MutableLiveData<Long>()
    val SecondLeft:LiveData<Long>get() = _SeconLeft


    private var _CountDownClick:Boolean = true
    val CountDownClick: Boolean
        get() = _CountDownClick
    lateinit var _cTimer: CountDownTimer
    var _isTimerStarted: Boolean = false
    val isTimerStarted: Boolean
        get() = _isTimerStarted


    init {
        _counter.value = 0
        _SeconLeft.value = 7
        _hightScore.value = 0
        setTimer(7000,1000)
    }

    fun addClick() {
        if (!_isTimerStarted) {
            _CountDownClick = true
            startTimer()
        }
        if (_CountDownClick) {
            _counter.value = _counter.value?.plus(1)
        }
    }
    fun resetfun() {
        _counter.value = 0
        _isTimerStarted = false
        stopTimer()
        _SeconLeft.value = 7
    }
    fun newHightScore ()
    {
        if(_counter.value!! > _hightScore.value!!) {
            _hightScore.value = _counter.value
        }
    }

    private fun setTimer(timer:Long , interval : Long) {
        _cTimer = object : CountDownTimer(timer,interval)  {
            override fun onFinish() {
                stopTimer()
            }
            override fun onTick(millisUntilFinished: Long) {
                _SeconLeft.value = millisUntilFinished/1000
            }
        }
     }

    private fun startTimer() {
        _isTimerStarted = true
        _cTimer.start()
    }
    private fun stopTimer() {
        _cTimer.cancel()
        _CountDownClick = false
    }

}
