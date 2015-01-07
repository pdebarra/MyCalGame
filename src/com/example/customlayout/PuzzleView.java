package com.example.customlayout;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.view.MotionEvent;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

public class PuzzleView extends BaseGameView
{
	private static String TAG = "PuzzleView";
	
	Paint pt_Line;
	
	static private int gridDimension = 5;
	int paddingWidth;

	MainActivity mainActivity;

	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	
	public PuzzleView(Context context, AttributeSet attrs) {
		super(context, attrs, -1);
		// TODO Auto-generated constructor stub

		mainActivity = (MainActivity) getContext();


		gestureDetector = new GestureDetector(gvContext, new MyGestureDetector());
		gestureListener = new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return gestureDetector.onTouchEvent(event);
			}
		};

		setOnTouchListener(gestureListener);
		
		paddingWidth = 0;
		
		pt_Line = new Paint();
		pt_Line.setAntiAlias(true);
		pt_Line.setColor(Color.RED);
		pt_Line.setStyle(Paint.Style.FILL_AND_STROKE);
		
	}
	

	@Override
	public void bespokeDraw(Canvas canvas, int canvasWidth, int canvasHeight) {
		// TODO Auto-generated method stub
		super.bespokeDraw(canvas, canvasWidth, canvasHeight);
		paddingWidth = Math.min(canvasWidth, canvasHeight) / 20;
		int squareSize = Math.min(canvasWidth, canvasHeight) - 2 * paddingWidth;
		int top = paddingWidth + (canvasHeight - 2 * paddingWidth - squareSize) / 2;
		int bottom = top + squareSize;
		int left = paddingWidth + (canvasWidth - 2 * paddingWidth - squareSize) / 2;
		int right = left + squareSize;
		
		for(int i = 0; i <= gridDimension; i++){
			canvas.drawLine(left, top + i * (squareSize / gridDimension), right, top + i * (squareSize / gridDimension), pt_Line);
			canvas.drawLine(left + i * (squareSize / gridDimension), top, left + i * (squareSize / gridDimension), bottom, pt_Line);
		}
	}
	
	class MyGestureDetector extends SimpleOnGestureListener{

		private static final String TAG = "MyGuestureDector";

		private static final int SWIPE_MIN_DISTANCE = 120;
		private static final int SWIPE_MAX_OFF_PATH = 250;
		private static final int SWIPE_THRESHOLD_VELOCITY = 200;

		@Override
		public boolean onDown(MotionEvent e) {
			Log.d(TAG, "onDown: (" + e.getX()/(gvGameThread.gtCanvasWidth) + ", " + e.getY()/(gvGameThread.gtCanvasHeight) + ")");
			return true;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			super.onSingleTapUp(e);
			Log.d(TAG, "onLongPress: start");

			float xNorm = e.getX()/(gvGameThread.gtCanvasWidth);
			float yNorm = e.getY()/(gvGameThread.gtCanvasHeight);

			if(8.0f / 24.0f <= xNorm && xNorm <= 16.0f / 24.0f && 10.0f / 20.0f <= yNorm && yNorm <= 14.0f / 20.0f){
				Log.d(TAG, "onSingleTapUp: church");
				
			} else if(8.0f / 24.0f <= xNorm && xNorm <= 16.0f / 24.0f && 15.0f / 20.0f <= yNorm && yNorm <= 19.0f / 20.0f){
				Log.d(TAG, "onSingleTapUp: church");
				
			}
			return true;
		}

	}
	
	class MySpace {
		int value;
		int col;
		int row;
		
		
		MySpace(int val, int c, int r){
			value = val;
			col = c;
			row = r;
		}
		
		public void updateValue(int val){
			value = val;
		}
		
		public void mydraw(Canvas canvas){
			
		}
	}
	
	class MyStatus {
		String txt_status;
		
		MyStatus(String status){
			txt_status = status;
		}
		
		public void mydraw(Canvas canvas){

		}
	}
}
