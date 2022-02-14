package com.davidread.dotty;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import java.util.ArrayList;

/**
 * {@link SoundEffects} provides a singleton class for playing sound effects during a Dotty game.
 */
public class SoundEffects {

    /**
     * Static reference to return for this class in {@link #getInstance(Context)}.
     */
    private static SoundEffects mSoundEffects;

    /**
     * {@link SoundPool} for retrieving audio resources and playing them.
     */
    private SoundPool mSoundPool;

    /**
     * {@link ArrayList} containing the audio resource ids of sounds to play during dot selections.
     */
    private ArrayList<Integer> mSelectSoundIds;

    /**
     * Int index to switch between audio resource ids in {@link #mSelectSoundIds}.
     */
    private int mSoundIndex;

    /**
     * Audio resource id of game over sound.
     */
    private int mEndGameSoundId;

    /**
     * Constructs a new {@link SoundEffects}.
     *
     * @param context {@link Context} for getting audio resources.
     */
    private SoundEffects(Context context) {

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        mSoundPool = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build();

        mSelectSoundIds = new ArrayList<>();
        mSelectSoundIds.add(mSoundPool.load(context, R.raw.note_e, 1));
        mSelectSoundIds.add(mSoundPool.load(context, R.raw.note_f, 1));
        mSelectSoundIds.add(mSoundPool.load(context, R.raw.note_f_sharp, 1));
        mSelectSoundIds.add(mSoundPool.load(context, R.raw.note_g, 1));

        mEndGameSoundId = mSoundPool.load(context, R.raw.game_over, 1);

        resetTones();
    }

    /**
     * Returns an instance of {@link SoundEffects}.
     *
     * @param context {@link Context} for getting audio resources.
     * @return An instance of {@link SoundEffects}.
     */
    public static SoundEffects getInstance(Context context) {
        if (mSoundEffects == null) {
            mSoundEffects = new SoundEffects(context);
        }
        return mSoundEffects;
    }

    /**
     * Resets the tone played for {@link #playTone(boolean)}.
     */
    public void resetTones() {
        mSoundIndex = -1;
    }

    /**
     * Plays a dot selection sound. Each time a dot is added to the selection, a higher note tone is
     * played. Each time a dot is removed from the selection, a lower tone note is played.
     *
     * @param advance True if this dot selection adds a dot. False if this dot selection removes a
     *                dot.
     */
    public void playTone(boolean advance) {
        if (advance) {
            mSoundIndex++;
        } else {
            mSoundIndex--;
        }

        if (mSoundIndex < 0) {
            mSoundIndex = 0;
        } else if (mSoundIndex >= mSelectSoundIds.size()) {
            mSoundIndex = 0;
        }

        mSoundPool.play(mSelectSoundIds.get(mSoundIndex), 1, 1, 1, 0, 1);
    }

    /**
     * Plays the game over sound.
     */
    public void playGameOver() {
        mSoundPool.play(mEndGameSoundId, 0.5f, 0.5f, 1, 0, 1);
    }
}