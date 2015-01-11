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

		gvGameThread = new GameThread(holder, gvContext, backGroundRef);
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
		Log.d(TAG, "destroy: 1");
		gvGameThread.gtSetRunning(false);
		Log.d(TAG, "destroy: 2");
		while (retry) {
			Log.d(TAG, "destroy: 3");
			try {
				gvGameThread.join();
				Log.d(TAG, "destroy: 4");
				retry = false;
				Log.d(TAG, "destroy: 5");
			} catch (InterruptedException e) {
				Log.d(TAG, "destroy: catch");
			}
		}
		Log.d(TAG, "SurfaceDestroyed: end");
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		Log.d(TAG, "onWindowFocusChanged: start");
		if (!hasWindowFocus) gvGameThread.gtSetRunning(false);
		else gvGameThread.gtSetRunning(true);
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
	
	
	public void setRunning(Boolean b) {
		Log.d(TAG, "setRunning: start: " + b);
		gvGameThread.gtSetRunning(b);
		Log.d(TAG, "setRunning: end");
	}
	

	class GameThread extends Thread {

		private SurfaceHolder gtSurfaceHolder;
		private Context gvContext;
		private Bitmap gtBackgroundImage;
		int gtCanvasWidth = 1;
		int gtCanvasHeight = 1;
		

		private Boolean gtRun = false;
		private Boolean gtPause = false;

		public GameThread(SurfaceHolder surfaceHolder, Context context, int backGroundRef) {
			// get handles to some important objects
			Log.d(TAG, "GameThread: start");
			gtSurfaceHolder = surfaceHolder;
			gvContext = context;

			if(backGroundRef != -1) {
				Resources res = gvContext.getResources();
				gtBackgroundImage = BitmapFactory.decodeResource(res, backGroundRef);
			} else {
				gtBackgroundImage = null;
			}
			

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
				if(gtBackgroundImage != null){
					gtBackgroundImage = Bitmap.createScaledBitmap(
							gtBackgroundImage, width, height, true);
				}
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
			Log.d(TAG, "run: gameLoop");
			} // End of game loop

			Log.d(TAG, "run: end");
		}

		private void doDraw(Canvas canvas, int canvasWidth, int canvasHeight) {
			// Draw the background image. Operations on the Canvas accumulate
			// so this is like clearing the screen.
			if(gtBackgroundImage != null) {
				canvas.drawBitmap(gtBackgroundImage, 0, 0, null);
			}
			bespokeDraw(canvas, canvasWidth, canvasHeight);

		}

		private void update() {
			// Draw the background image. Operations on the Canvas accumulate
			// so this is like clearing the screen.

		}

		public void gtSetRunning(Boolean b) {
			Log.d(TAG, "gtSetRunning: start: " + b);
			gtRun = b;
			Log.d(TAG, "gtSetRunning: end");
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
