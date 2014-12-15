package com.example.customlayout;

import com.example.customlayout.MainActivity.GameManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.Toast;

public class CalcView extends BaseGameView{

	
	private static String TAG = "CalcView";

	MainActivity mainActivity;
	Paint textPaint;
	Rect txtBoundsRect;
	String numAtxt, numBtxt, interimtxt, anstxt, message;
	int posXtxt, posYtxt;
	
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	
	public CalcView(Context context, AttributeSet attrs) {
		super(context, attrs, R.drawable.calcviewbackground);
		// TODO Auto-generated constructor stub

		mainActivity = (MainActivity) getContext();
		
		textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setColor(Color.RED);
		txtBoundsRect = new Rect();
		
		numAtxt = new String("");
		numBtxt = new String("");
		interimtxt = new String("");
		anstxt = new String("");
		
		gestureDetector = new GestureDetector(gvContext, new MyGestureDetector());
		gestureListener = new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return gestureDetector.onTouchEvent(event);
			}
		};
		
		setOnTouchListener(gestureListener);
	}

	public void updateFigures(String numA, Boolean UnumA, 
							String numB, Boolean UnumB, 
							String interim, Boolean Uinterim, 
							String ans, Boolean Uans){
		if(UnumA) { 
			numAtxt = numA;
		}
		if(UnumB) { 
			numBtxt = numB;
		}
		if(Uinterim) { 
			interimtxt = interim;
		}
		if(Uans) {
			anstxt = ans;
		}
	}
	
	

	@Override
	public void bespokeDraw(Canvas canvas, int canvasWidth, int canvasHeight) {
		// TODO Auto-generated method stub
		super.bespokeDraw(canvas, canvasWidth, canvasHeight);
		

		textPaint.setTextSize(50);
		
		
		if(mainActivity.gameManager.getGameState() == GameManager.STATE_0_READY){
			// Press to Start
			message = "Press to Start";
			textPaint.getTextBounds(message, 0, message.length(), txtBoundsRect);
			posXtxt = (canvasWidth * 10) / 20 - txtBoundsRect.width() / 2;
			posYtxt = (canvasHeight * 12) / 20 + txtBoundsRect.height() / 2;
			canvas.drawText(message, posXtxt, posYtxt, textPaint);
			
		} else if(mainActivity.gameManager.getGameState() == GameManager.STATE_1_INPUT_A){
			// Write NumA
			textPaint.getTextBounds(numAtxt, 0, numAtxt.length(), txtBoundsRect);
			posXtxt = (canvasWidth * 5) / 20 - txtBoundsRect.width() / 2;
			posYtxt = (canvasHeight * 5) / 20 + txtBoundsRect.height() / 2;
			canvas.drawText(numAtxt, posXtxt, posYtxt, textPaint);
			// Write NumB
			textPaint.getTextBounds(numBtxt, 0, numBtxt.length(), txtBoundsRect);
			posXtxt = (canvasWidth * 15) / 20 - txtBoundsRect.width() / 2;
			posYtxt = (canvasHeight * 5) / 20 + txtBoundsRect.height() / 2;
			canvas.drawText(numBtxt, posXtxt, posYtxt, textPaint);
			// Link Ans to Keypad and write
			if (mainActivity.inputAns.isInputEmpty()){
				interimtxt = "0";
			}
			else {
				interimtxt = mainActivity.inputAns.getInputAns();
			}
			textPaint.getTextBounds(interimtxt, 0, interimtxt.length(), txtBoundsRect);
			posXtxt = (canvasWidth * 10) / 20 - txtBoundsRect.width() / 2;
			posYtxt = (canvasHeight * 12) / 20 + txtBoundsRect.height() / 2;
			canvas.drawText(interimtxt, posXtxt, posYtxt, textPaint);
		} else if(mainActivity.gameManager.getGameState() == GameManager.STATE_2_INPUT_B){
			// Write NumA
			textPaint.getTextBounds(numAtxt, 0, numAtxt.length(), txtBoundsRect);
			posXtxt = (canvasWidth * 5) / 20 - txtBoundsRect.width() / 2;
			posYtxt = (canvasHeight * 5) / 20 + txtBoundsRect.height() / 2;
			canvas.drawText(numAtxt, posXtxt, posYtxt, textPaint);
			// Write NumB
			textPaint.getTextBounds(numBtxt, 0, numBtxt.length(), txtBoundsRect);
			posXtxt = (canvasWidth * 15) / 20 - txtBoundsRect.width() / 2;
			posYtxt = (canvasHeight * 5) / 20 + txtBoundsRect.height() / 2;
			canvas.drawText(numBtxt, posXtxt, posYtxt, textPaint);
			// Write Interim
			textPaint.getTextBounds(interimtxt, 0, interimtxt.length(), txtBoundsRect);
			posXtxt = (canvasWidth * 10) / 20 - txtBoundsRect.width() / 2;
			posYtxt = (canvasHeight * 12) / 20 + txtBoundsRect.height() / 2;
			canvas.drawText(interimtxt, posXtxt, posYtxt, textPaint);
			// Link Ans to Keypad and write
			if (mainActivity.inputAns.isInputEmpty()){
				anstxt = "0";
			}
			else {
				anstxt = mainActivity.inputAns.getInputAns();
			}
			textPaint.getTextBounds(anstxt, 0, anstxt.length(), txtBoundsRect);
			posXtxt = (canvasWidth * 10) / 20 - txtBoundsRect.width() / 2;
			posYtxt = (canvasHeight * 17) / 20 + txtBoundsRect.height() / 2;
			canvas.drawText(anstxt, posXtxt, posYtxt, textPaint);
			
		} else if(mainActivity.gameManager.getGameState() == GameManager.STATE_3_RESULT){
			// Write NumA
			textPaint.getTextBounds(numAtxt, 0, numAtxt.length(), txtBoundsRect);
			posXtxt = (canvasWidth * 5) / 20 - txtBoundsRect.width() / 2;
			posYtxt = (canvasHeight * 5) / 20 + txtBoundsRect.height() / 2;
			canvas.drawText(numAtxt, posXtxt, posYtxt, textPaint);
			// Write NumB
			textPaint.getTextBounds(numBtxt, 0, numBtxt.length(), txtBoundsRect);
			posXtxt = (canvasWidth * 15) / 20 - txtBoundsRect.width() / 2;
			posYtxt = (canvasHeight * 5) / 20 + txtBoundsRect.height() / 2;
			canvas.drawText(numBtxt, posXtxt, posYtxt, textPaint);
			// Write Interim
			textPaint.getTextBounds(interimtxt, 0, interimtxt.length(), txtBoundsRect);
			posXtxt = (canvasWidth * 10) / 20 - txtBoundsRect.width() / 2;
			posYtxt = (canvasHeight * 12) / 20 + txtBoundsRect.height() / 2;
			canvas.drawText(interimtxt, posXtxt, posYtxt, textPaint);
			// Write Ans 
			textPaint.getTextBounds(anstxt, 0, anstxt.length(), txtBoundsRect);
			posXtxt = (canvasWidth * 10) / 20 - txtBoundsRect.width() / 2;
			posYtxt = (canvasHeight * 17) / 20 + txtBoundsRect.height() / 2;
			canvas.drawText(anstxt, posXtxt, posYtxt, textPaint);	
		} else {
			message = "Invalid State: " + String.valueOf(mainActivity.gameManager.getGameState());
			textPaint.getTextBounds(message, 0, message.length(), txtBoundsRect);
			posXtxt = (canvasWidth * 10) / 20 - txtBoundsRect.width() / 2;
			posYtxt = (canvasHeight * 17) / 20 + txtBoundsRect.height() / 2;
			canvas.drawText(message, posXtxt, posYtxt, textPaint);
		
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
				Log.d(TAG, "onLongPress: church");
				if(mainActivity.gameManager.getGameState() == GameManager.STATE_0_READY || mainActivity.gameManager.getGameState() == GameManager.STATE_3_RESULT){
					boolean temp = mainActivity.gameManager.receiveUpdate("");
				}
			}
			return true;
		}



		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			Log.d(TAG, "onFling: start");
			
			try{
				if( Math.abs( e1.getY() - e2.getY() ) > SWIPE_MAX_OFF_PATH){
					Log.d(TAG, "onFling: not greater than SWIPE_MAX_OFF_PATH");
					return false;
				}
				
				if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY ){
					Log.d(TAG, "onFling: Left");
					Toast.makeText(gvContext, "Left Swipe", Toast.LENGTH_SHORT).show();
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY ){
					Log.d(TAG, "onFling: Right");
					Toast.makeText(gvContext, "Right Swipe", Toast.LENGTH_SHORT).show();
				}
				
					
			} catch(Exception e) {
				Log.d(TAG, "onFling: catch");
				// ???
			}
			
			return false;
		}
		
	}
	
}
