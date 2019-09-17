package pro.devapp.clock.utils

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import android.os.Handler
import pro.devapp.clock.R
import java.util.HashMap

class ClockSounds(context: Context) {
    private var soundPool: SoundPool = SoundPool(2, AudioManager.STREAM_MUSIC, 100)
    private var soundPoolMap: HashMap<Int, Int> = HashMap(4)
    private var streamIds: ArrayList<Int> = ArrayList()

    init {
        soundPoolMap[R.raw.timer_end] = soundPool.load(context, R.raw.timer_end, 1)
    }

    /**
     * Play sound by Id
     */
    fun playSound(soundID: Int) {
        val volume = 0.9f
        var streamId = soundPool.play(soundPoolMap[soundID] as Int, volume, volume, 1, -1, 1f)
        streamIds.add(streamId)
    }

    fun stopAll(){
        for (streamId in streamIds)
            soundPool.stop(streamId)
    }

    fun release() {
        soundPool.release()
    }
}