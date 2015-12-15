/**
 * Copyright 2015 CareNx Innovations Pvt. Ltd., Mumbai
 * 
 */
package com.carenx.myrecorder;

import java.io.File;
import java.util.concurrent.TimeUnit;

import com.carenx.myrecorder.AudioRecordHelper.State;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.Series;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
/**  
* This is the Activity Class that provides user interface.
* @author Avinash joshi
* 
* @version 1.0 Build May 30, 2015. Copyright 2015 CareNx Innovations Pvt. Ltd., Mumbai.
* @see AudioRecordHelper
*/
public class RecorderActivity extends Activity {

	private AudioRecordHelper mHelper;
	private MyGraphView graph;
	private Series mSeries1;
	private LinearLayout llanim;
	private TextView endTimeField;
	private TextView startTimeField;
	private SeekBar seekbar;
	private ImageButton playButton;
	private ImageButton stopButton;
	private Uri uri;
	private MediaPlayer mPlayer;
	private int startTime;
	private LinearLayout llList;
	public static Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext= getApplicationContext();
		
		mHelper = new AudioRecordHelper();
		mHelper.mState = State.INITIALIZING;
		
		GraphView mGraphView = new GraphView(RecorderActivity.this);
		graph = new MyGraphView(RecorderActivity.this, mHelper,mGraphView);
		llanim = (LinearLayout) findViewById(R.id.llanim);
		llanim.addView(mGraphView);
		llList = (LinearLayout) findViewById(R.id.llList);
		llList.setGravity(Gravity.TOP);
		
		final Button btnToggel = (Button) findViewById(R.id.btntoggelbutton);
		btnToggel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				mHelper.toggleRecording();
				if (btnToggel.getText().equals("Start"))
				{
					AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
					if (am.isWiredHeadsetOn()) {
						btnToggel.setText("Stop");
						btnToggel.setBackgroundResource(R.drawable.round_bg_red);
						graph.startGraph();
					}else{
						Toast.makeText(RecorderActivity.this,"Please connect the Stetho",Toast.LENGTH_SHORT).show();
					}
				}
				else{
					btnToggel.setText("Start");
					btnToggel.setBackgroundResource(R.drawable.round_bg);
					graph.stopGraph();
				}
			}
		});
		prepareRecordeingCompleteListners();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent intent = new Intent(RecorderActivity.this,SettingsActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mHelper.mState == State.RECORDING) {
			mHelper.toggleRecording();
			// updateViews();
		}
		unregisterReceiver(receiver);
	}

	/**
	 * Starting the activity will toggle the recording state. When this starts
	 * recording.
	 */
	private Handler myHandler = new Handler();
	private BroadcastReceiver receiver;
	private void prepareRecordeingCompleteListners() {
		/* Logout receiver */
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.carenx.ACTION_RECORDING_COMPLETE");

		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				
				final String path = intent.getStringExtra("url");
				TextView txt = new TextView(context);
				LayoutParams parms = new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
				parms.setMargins(5, 5, 5, 5);
				txt.setTypeface(txt.getTypeface(), Typeface.BOLD);
				txt.setTextSize(20);
				txt.setTextColor(Color.BLACK);
				txt.setText("Rec_"+System.currentTimeMillis());
				txt.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
						if (am.isWiredHeadsetOn()) {
							showSoungPlayer(path);
						}else{
							Toast.makeText(RecorderActivity.this,"Please connect the Headphone",Toast.LENGTH_SHORT).show();
						}
					}
				});
				llList.addView(txt,parms);
			}
		};
		registerReceiver(receiver, filter);
	}

	private void showSoungPlayer(String path)
	{
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_sound_player, null);
		final Dialog dialog = new Dialog(RecorderActivity.this,
				android.R.style.Theme_DeviceDefault_Dialog_NoActionBar_MinWidth);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		endTimeField = (TextView)view.findViewById(R.id.textView2);
		startTimeField= (TextView)view.findViewById(R.id.textView1);
		seekbar =(SeekBar) view.findViewById(R.id.seekBar1);
		
		playButton = (ImageButton)view.findViewById(R.id.ibPlay);
		playButton.setVisibility(View.GONE);
	    stopButton = (ImageButton)view.findViewById(R.id.ibStop);
	    stopButton.setVisibility(View.VISIBLE);
	    seekbar.setClickable(false);
	    
	    OnClickListener click = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.ibStop:
						stopPalyback();
						dialog.dismiss();
					break;

		
				case R.id.ibPlay:
					play();
				break;

				default:
					break;
				}
				
			}
		};
		playButton.setOnClickListener(click);
		stopButton.setOnClickListener(click);
		dialog.show();
		releaseMediaPlayer();
		startPlayback(path);
		
	}
	
	private void startPlayback(String path){
		File rec=new File(path);
		if(rec.exists())
			uri=Uri.fromFile(rec);
		else{
			Toast.makeText(RecorderActivity.this, "File Does Not Exist", Toast.LENGTH_SHORT).show();
			
			return;
		}
		
		mPlayer=MediaPlayer.create(getApplicationContext(), uri);
		
		mPlayer.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				playButton.setVisibility(View.VISIBLE);
				stopButton.setVisibility(View.GONE);
				 playButton.setEnabled(true);
			     stopButton.setEnabled(true);
			     endTimeField.setVisibility(View.VISIBLE);
			     startTimeField.setVisibility(View.GONE);
			     myHandler.removeCallbacks(UpdateSongTime);
			     seekbar.setProgress(0);
				
			    
			}
		});
		play();
	}
	public void play(){
		
		
		   //Toast.makeText(getApplicationContext(), "Playing sound",  Toast.LENGTH_SHORT).show();
		      mPlayer.start();
		      int finalTime = mPlayer.getDuration();
		      startTime = mPlayer.getCurrentPosition();
		      seekbar.setMax(finalTime);
		         endTimeField.setText(String.format("%2d:%2d", 
		         TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
		         TimeUnit.MILLISECONDS.toSeconds((long) finalTime) - 
		         TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
		         toMinutes((long) finalTime)))
		      );
		         endTimeField.setVisibility(View.GONE);
		      startTimeField.setText(String.format("%2d:%2d", 
		         TimeUnit.MILLISECONDS.toMinutes((long) startTime),
		         TimeUnit.MILLISECONDS.toSeconds((long) startTime) - 
		         TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
		         toMinutes((long) startTime)))
		      );
		      startTimeField.setVisibility(View.VISIBLE);
		      seekbar.setProgress((int)startTime); 
		      myHandler .postDelayed(UpdateSongTime,100);
		      stopButton.setEnabled(true);
		      playButton.setEnabled(false);
		   }
	private Runnable UpdateSongTime = new Runnable() {
	      public void run() {
	         startTime = mPlayer.getCurrentPosition();
	         startTimeField.setText(String.format("%2d:%2d", (int)
	            TimeUnit.MILLISECONDS.toMinutes((long) startTime),(int)
	            TimeUnit.MILLISECONDS.toSeconds((long) startTime) - 
	            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
	            toMinutes((long) startTime)))
	         );
	         seekbar.setProgress((int)startTime);
	         myHandler.postDelayed(this, 100);
	      }
	   };
 
	   
	private void stopPalyback() {
		if(mPlayer!=null)
			mPlayer.stop();
		
		//Toast.makeText(getApplicationContext(),"Playback Stoped", Toast.LENGTH_SHORT).show();
		
	}
	
	private void releaseMediaPlayer() {
		if(mPlayer!=null)
		{
			try{
				mPlayer.release();
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
		
	}

	@Override
	protected void onStop() {
		stopPalyback();
		releaseMediaPlayer();
		super.onStop();

	}
}
