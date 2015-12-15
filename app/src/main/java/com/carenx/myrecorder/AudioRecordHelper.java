package com.carenx.myrecorder;

import java.io.File;

import android.content.Context;
import android.media.AudioFormat;
import android.media.MediaRecorder.AudioSource;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
/**  
* This class is an intermediate class between the actual activity and the AudioRecorder Class
* @author Avinash joshi
* 
* @version 1.0 Build May 30, 2015. Copyright 2015 CareNx Innovations Pvt. Ltd., Mumbai.
* @see AudioRecorder
*/
public class AudioRecordHelper {
	
	public State mState;
	long mTimeAtStart;

	AudioRecorder mRecorder = null;

	long mTimeAtRecordingStart;
	String mOutputFile;
	String mTitle;


	/**
	 * INITIALIZING : Class is initializing; no session selected 
	 * READY : session has been selected, session is not started 
	 * STARTED : session has been started, not currently recording 
	 * RECORDING : recording
	 */
	public enum State {
		INITIALIZING, READY, STARTED, RECORDING
	};

	private final static int[] sampleRates = { 44100, 22050, 11025, 8000 };

	/**
	 * Toggles recording.
	 *
	 * Calls startRecording or stopRecording depending on the current state.
	 *
	 * @see startRecording
	 * @see stopRecording
	 */
	public void toggleRecording() {
		if (mState == State.INITIALIZING)
			mState = State.STARTED;
		if (mState == State.STARTED) {
			startRecording();
		} else {
			stopRecording();
		}

		// updateViews();
	}

	/**
	 * Starts recording.
	 *
	 * Changes state to RECORDING.
	 *
	 */
	private void startRecording() {

		// session must be in STARTED state
		if (mState != State.STARTED)
			return;

		// make sure the SD card is present for the recording
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			// create the directory
			File external = Environment.getExternalStorageDirectory();
			File audio = new File(external.getAbsolutePath()
					+ "/CareNx/Recordings");
			audio.mkdirs();
			Log.w("MyRecorder",
					"writing to directory " + audio.getAbsolutePath());

			// get the recording type from preferences compressed/UnCompressed
			boolean uncompressed = MyRecorder.getApplication()
					.getSharedPreferences().getBoolean("unCompressed", false);
			//

			// construct file name
			mOutputFile = audio.getAbsolutePath() + "/audio_"
					+ System.currentTimeMillis()
					+ (uncompressed ? ".wav" : ".3gp");
			Log.w("MyRecorder", "writing to file " + mOutputFile);

			// start the recording
			if (!uncompressed) {
				mRecorder = new AudioRecorder(false, 0, 0, 0, 0);
			} else {
				int i = 0;
				do {
					if (mRecorder != null)
						mRecorder.release();
					mRecorder = new AudioRecorder(true, AudioSource.DEFAULT,
							sampleRates[i], AudioFormat.CHANNEL_IN_MONO,
							AudioFormat.ENCODING_PCM_16BIT);
				} while ((++i < sampleRates.length)
						& !(mRecorder.getState() == AudioRecorder.State.INITIALIZING));
			}
			mRecorder.setOutputFile(mOutputFile);
			mRecorder.setGain(1);
			mRecorder.prepare();
			mRecorder.start(); // Recording is now started
			mTimeAtRecordingStart = System.currentTimeMillis() - mTimeAtStart;
			if (mRecorder.getState() == AudioRecorder.State.ERROR) {
				mOutputFile = null;
			}

		} else {
			mOutputFile = null;
			mTimeAtRecordingStart = System.currentTimeMillis() - mTimeAtStart;
		}
		mState = State.RECORDING;
		// updateViews();
	}

	/**
	 * Stops recording.
	 *
	 * Changes state to STARTED.
	 *
	 */
	private void stopRecording() {
		// state must be RECORDING
		if (mState != State.RECORDING)
			return;

		if (mRecorder != null) {
			mRecorder.stop();
			mRecorder.release();
		}

		mState = State.STARTED;
		// updateViews();
	}

	/**
	 * 
	 * Returns the duration of the recorded clip.
	 * 
	 * @return returns the duration of the recorded clip. 
	 * 
	 */
	private long timeInRecording() {
		if (mState != State.RECORDING)
			return 0;
		return System.currentTimeMillis() - mTimeAtRecordingStart;
	}

	/**
	 * 
	 * Returns the largest amplitude sampled since the last call to this method.
	 * 
	 * @return returns the largest amplitude since the last call, or 0 when not in recording state. 
	 * 
	 */
	public int getMaxAmplitude() {
		if (mRecorder == null || mState != State.RECORDING)
			return 0;
		return mRecorder.getMaxAmplitude();
	}

	
}
