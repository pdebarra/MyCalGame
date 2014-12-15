package com.example.customlayout;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class BaseGameView extends SurfaceView implements SurfaceHolder.Callback {
	
	private static String TAG = "BaseGameView";

	GameThread gvGameThread;
	Context gvContext;
	
	

	public BaseGameView(Context context, AttributeSet attrs, int backGroundRef) {
		super(context, attrs);
		Log.d(TAG, "GameView: start");

		gvContext = context;


		
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);

		gvGameThread = new GameThread(holder, context, backGroundRef);
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
	public void bespokeDraw(Canvas canvas, int canvasWidth, int canvasHeight) {
		// TODO Auto-generated method stub
	}
	

	class GameThread extends Thread {

		private SurfaceHolder gtSurfaceHolder;
		private Context gtContext;
		private Bitmap gtBackgroundImage;
		int gtCanvasWidth = 1;
		int gtCanvasHeight = 1;
		

		private Boolean gtRun = false;
		private Boolean gtPause = false;

		public GameThread(SurfaceHolder surfaceHolder, Context context, int backGroundRef) {
			// get handles to some important objects
			Log.d(TAG, "GameThread: start");
			gtSurfaceHolder = surfaceHolder;
			gtContext = context;

			Resources res = gtContext.getResources();
			gtBackgroundImage = BitmapFactory.decodeResource(res, backGroundRef);
			//		R.drawable.multilandmap);
			

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
							doDraw(c, gtCanvasWidth, gtCanvasHeight);
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

		private void doDraw(Canvas canvas, int canvasWidth, int canvasHeight) {
			// Draw the background image. Operations on the Canvas accumulate
			// so this is like clearing the screen.
			canvas.drawBitmap(gtBackgroundImage, 0, 0, null);
			bespokeDraw(canvas, canvasWidth, canvasHeight);

		}

		private void update() {
			// Draw the background image. Operations on the Canvas accumulate
			// so this is like clearing the screen.

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
	

}
