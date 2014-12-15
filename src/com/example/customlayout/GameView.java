package com.example.customlayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	
	private static String TAG = "GameView";

	GameThread gvGameThread;
	Context gvContext;
	
	
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d(TAG, "GameView: start");

		gvContext = context;

		gestureDetector = new GestureDetector(gvContext, new MyGestureDetector());
		gestureListener = new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return gestureDetector.onTouchEvent(event);
			}
		};
		
		setOnTouchListener(gestureListener);
		
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);

		gvGameThread = new GameThread(holder, context);
		setFocusable(true);
		
		Log.d(TAG, "GameView: end");
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.d(TAG, "onSurfaceChanged: start");
		gvGameThread.setSurfaceSize(width, height);
		Log.d(TAG, "onSurfaceChanged: end");

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		// start the thread here so that we don't busy-wait in run()
		// waiting for the surface to be created
		Log.d(TAG, "surfaceCreated: start");
		gvGameThread.gtSetRunning(true);
		gvGameThread.start();
		Log.d(TAG, "surfaceCreated: end");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// we have to tell thread to shut down & wait for it to finish, or else
		// it might touch the Surface after we return and explode
		Log.d(TAG, "SurfaceDestroyed: start");
		boolean retry = true;
		gvGameThread.gtSetRunning(false);
		while (retry) {
			try {
				gvGameThread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
		Log.d(TAG, "SurfaceDestroyed: end");
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		Log.d(TAG, "onWindowFocusChanged: start");
		if (!hasWindowFocus) gvGameThread.pause();
		else gvGameThread.unpause();
		Log.d(TAG, "onWindowFocusChanged: end");
	}

	public void pause() {
		// TODO Auto-generated method stub
		Log.d(TAG, "pause: start");
		gvGameThread.pause();
		Log.d(TAG, "pause: end");
	}

	class GameThread extends Thread {

		private SurfaceHolder gtSurfaceHolder;
		private Context gtContext;
		private Bitmap gtBackgroundImage;
		int gtCanvasWidth = 1;
		int gtCanvasHeight = 1;
		
		//GameHero elaine;

		private Boolean gtRun = false;
		private Boolean gtPause = false;

		public GameThread(SurfaceHolder surfaceHolder, Context context) {
			// get handles to some important objects
			Log.d(TAG, "GameThread: start");
			gtSurfaceHolder = surfaceHolder;
			gtContext = context;

			Resources res = gtContext.getResources();
			gtBackgroundImage = BitmapFactory.decodeResource(res,
					R.drawable.multilandmap);
			
			//elaine = new GameHero(gtContext, 100, 100);

			Log.d(TAG, "GameThread: end");
		}

		/* Callback invoked when the surface dimensions change. */
		public void setSurfaceSize(int width, int height) {
			Log.d(TAG, "setSurfaceSize: start");
			// synchronized to make sure these all change atomically
			synchronized (gtSurfaceHolder) {
				gtCanvasWidth = width;
				gtCanvasHeight = height;

				// don't forget to resize the background image
				gtBackgroundImage = Bitmap.createScaledBitmap(
						gtBackgroundImage, width, height, true);
			}

			Log.d(TAG, "setSurfaceSize: end");
		}

		@Override
		public void run() {

			Log.d(TAG, "run: start");
			while (gtRun) {
				if(!gtPause){
					Canvas c = null;
					try {
						c = gtSurfaceHolder.lockCanvas(null);
						synchronized (gtSurfaceHolder) {
							update();
							doDraw(c);
						}
					} finally {
						// do this in a finally so that if an exception is thrown
						// during the above, we don't leave the Surface in an
						// inconsistent state
						if (c != null) {
							gtSurfaceHolder.unlockCanvasAndPost(c);
						}
					} // End of catch statement to draw graphics
				} else {
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} // End of game loop

			Log.d(TAG, "run: end");
		}

		private void doDraw(Canvas canvas) {
			// Draw the background image. Operations on the Canvas accumulate
			// so this is like clearing the screen.
			canvas.drawBitmap(gtBackgroundImage, 0, 0, null);
			//elaine.ondraw(canvas);

		}

		private void update() {
			// Draw the background image. Operations on the Canvas accumulate
			// so this is like clearing the screen.
			//elaine.update(System.currentTimeMillis());

		}

		public void gtSetRunning(Boolean b) {
			Log.d(TAG, "setRunning: start");
			gtRun = b;
			Log.d(TAG, "setRunning: end");
		}

		public void pause() {
			Log.d(TAG, "GameThread.pause: start");
			gtPause = true;
			Log.d(TAG, "GameThread.pause: end");
		}

		public void unpause() {
			Log.d(TAG, "GameThread.unpause: start");
			gtPause= false;
			Log.d(TAG, "GameThread.unpause: end");
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
		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub
			super.onLongPress(e);
			Log.d(TAG, "onLongPress: start");
			
			float xNorm = e.getX()/(gvGameThread.gtCanvasWidth);
			float yNorm = e.getY()/(gvGameThread.gtCanvasHeight);
			
			if(0.75f <= xNorm && xNorm <= 0.85f && 0.3 <= yNorm && yNorm <= 0.4){
				Log.d(TAG, "onLongPress: church");
				new AlertDialog.Builder(gvContext)
				.setTitle("Enter Church?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Log.d(TAG, "onLongPress: Entered Church");
						Toast.makeText(gvContext, "Church Scene Not Ready", Toast.LENGTH_SHORT).show();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Log.d(TAG, "onLongPress: Did not Enter Church");
					}
				})
				.show();
			}
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
