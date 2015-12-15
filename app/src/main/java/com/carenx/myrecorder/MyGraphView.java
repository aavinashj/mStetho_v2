package com.carenx.myrecorder;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer.GridStyle;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

/**  
* This class contains code for displaying amplitude graph while recording
* @author Avinash joshi
* 
* @version 1.0 Build May 30, 2015. Copyright 2015 CareNx Innovations Pvt. Ltd., Mumbai.
* @see AudioRecordHelper
*/
public class MyGraphView {

	private Context mContext;
	private LineGraphSeries<DataPoint> mSeries1;
	private int mYaxis;
	private int mXaxis;
	private AudioRecordHelper mAudioHelper;
	public Handler mHandler;
	private Graph mGraph;
	private GraphView mGraphView;

	/**
	 * 
	 * 
	 * Default constructor
	 * 
	 * Instantiates a new GraphView
	 * 
	 * @param context
	 *            of the invoking activity
	 * @param object
	 *            of the AudioRecordHelper class that is currently used to
	 *            record the audio
	 * @param object
	 *            of the GraphView class to be initialized
	 * 
	 * @see AudioRecordHelper class
	 * @see GraphView Library
	 * 
	 */
	public MyGraphView(Context context, AudioRecordHelper helper,
			GraphView graphView) {
		mContext = context;
		mAudioHelper = helper;

		mGraphView = graphView;
		mGraph = new Graph();
		mHandler = new Handler();
		mYaxis = 20;
		mXaxis = 20;

		initializeGraphView();

	}

	/**
	 * Initializes the Graph to particular Style
	 *
	 * Changes state to STARTED.
	 *
	 */
	private void initializeGraphView() {

		// initialization of the x and y axis data points

		DataPoint[] values = reSetGraphValues();
		// setting the view of graph

		mGraphView.getGridLabelRenderer().setHorizontalLabelsVisible(false);
		mGraphView.getGridLabelRenderer().setVerticalLabelsVisible(false);
		mGraphView.getGridLabelRenderer().setGridStyle(GridStyle.NONE);
		mGraphView.getViewport().setMinY(-100);
		mGraphView.getViewport().setMaxY(100);
		mGraphView.getViewport().setYAxisBoundsManual(true);

		mSeries1 = new LineGraphSeries<DataPoint>(values);
		mGraphView.addSeries(mSeries1);

	}

	/**
	 * 
	 * Returns an array of DataPoint of size 20 with x values in increment
	 * 0,1,2,3... and all y values initialized to ZERO
	 * 
	 * @return returns an array of DataPoint of size 20 with x and y values
	 *         initialized
	 * 
	 */
	private DataPoint[] reSetGraphValues() {
		DataPoint[] values = new DataPoint[20];
		for (int i = 0; i < 20; i++) {
			double x = i;
			// double f = mRand.nextDouble()*0.15+0.3;
			double y = 1;
			DataPoint v = new DataPoint(x, y);
			values[i] = v;
		}
		return values;

	}

	/**
	 * Starts the graph thread
	 *
	 * @see Graph
	 *
	 */
	public void startGraph() {
		mHandler.postDelayed(mGraph, 100);
	}

	/**
	 * Stops the graph thread
	 *
	 * @see Graph
	 *
	 */
	public void stopGraph() {
		mHandler.removeCallbacks(mGraph);
		mSeries1.resetData(reSetGraphValues());
	}

	/**
	 * A thread that calls the getMaxAmplitude function of AudioRecordHelper
	 * class and generates a new DataPoint object and appends it to the graph in
	 * a regular interval.
	 *
	 * @see getMaxAmplitude
	 *
	 */
	public class Graph extends Thread {
		public void run() {

			DataPoint dp = new DataPoint(mXaxis++,
					(20 * Math.log10(mAudioHelper.getMaxAmplitude() / 100)));

			mSeries1.appendData(dp, true, 20); // resetData(generateData());
			mHandler.postDelayed(this, 200);
		}
	}

}
