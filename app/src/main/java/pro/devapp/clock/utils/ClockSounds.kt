package pro.devapp.clock.utils

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import android.os.Handler
import pro.devapp.clock.R
import java.util.HashMap

class ClockSounds(context: Context) {
    private val soundD = R.raw.timer_end

    private var soundPool: SoundPool = SoundPool(2, AudioManager.STREAM_MUSIC, 100)
    private var soundPoolMap: HashMap<Int, Int>
    private var streamIds: ArrayList<Int> = ArrayList()
    private var isLoaded = false

    init {
        soundPool.setOnLoadCompleteListener { soundPool, sampleId, status ->
            isLoaded = true
        }
        soundPoolMap = HashMap(4)
        soundPoolMap[soundD] = soundPool.load(context, R.raw.timer_end, 1)
    }

    /**
     * Play sound by Id
     */
    fun playSound(context: Context, soundID: Int) {
        val volume = 0.9f// whatever in the range = 0.0 to 1.0
        if (isLoaded) {
            var streamId = soundPool.play(soundPoolMap[soundID] as Int, volume, volume, 1, 0, 1f)
            streamIds.add(streamId)
        } else {
            soundPool.setOnLoadCompleteListener { soundPool, sampleId, status ->
                // play sound with same right and left volume, with a priority of 1,
                // zero repeats (i.e play once), and a playback rate of 1f
                soundPool.play(soundPoolMap[soundID] as Int, volume, volume, 1, 0, 1f)
                isLoaded = true
            }
        }
    }

    fun stopAll(){
        for (streamId in streamIds)
            soundPool.stop(streamId)
    }

    fun release() {
        soundPool.release()
        isLoaded = false
    }
}