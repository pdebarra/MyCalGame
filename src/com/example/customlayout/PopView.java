package com.example.customlayout;

import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.graphics.Paint;
import android.util.Log;
import android.content.Context;
import java.util.List;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import com.example.customlayout.Input.TouchEvent;
import android.widget.Toast;
import android.graphics.Color;
import android.graphics.Canvas;
import java.util.Random;


public class PopView extends SurfaceView implements Runnable, SurfaceHolder.Callback
{

	private static String TAG = "PopView";
	
	Context context;
	
	
	Paint paint;
	
	int screenheight;
	int screenwidth;
	float density;
	
	int paddingWidth;
	final static int gridDimension = 5;
	final static int gridOffset = 1;
	
	volatile int cx;
	volatile int cy;
	int radius;
	volatile int opx = 1;
	volatile int opy = 1;
	
	volatile boolean running = false;
	SurfaceHolder holder;
	Thread renderThread = null;
	
	PuzzleGrid puzzleGrid;
	Input input;
	
	volatile private Boolean readyToFire;
	
	public PopView(Context context, AttributeSet attrs){
		super(context, attrs);
		
		this.context = context;
		
		holder = getHolder();
		holder.addCallback(this);
		
		readyToFire = false;
		
		DisplayMetrics dm = this.getResources().getDisplayMetrics();
		screenheight = dm.heightPixels;
		screenwidth = dm.widthPixels;
		density = dm.density;
		radius = Math.round(20*density);
		
		paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.GREEN);
		paint.setStrokeWidth(10);
		
		puzzleGrid = new PuzzleGrid(gridDimension, gridOffset, screenwidth, screenheight);
		
		input = new AndroidInput(context, this, 1, 1); 
	}
	
	public void resume() {
		running = true;
		renderThread = new Thread(this);
		renderThread.start();
	}

	@Override
	public void run() {
		while(running){
			handleInput();
			update();
			if(!holder.getSurface().isValid())
				continue;
			updateGrahics();
			
		}
	}
	
	private void updateGrahics(){
		Canvas canvas = holder.lockCanvas();
		
		canvas.drawARGB(0xff, 0, 0, 0xff);
		puzzleGrid.draw(canvas);
		
		holder.unlockCanvasAndPost(canvas);
	}
	
	private void handleInput(){
		List<TouchEvent> touchEvents = input.getTouchEvents();
		int len = touchEvents.size();
		
		for(int i = 0; i < len; i++){
			TouchEvent event = touchEvents.get(i);
			if(event.type == TouchEvent.TOUCH_UP){
				//Toast.makeText(context, "Popview Touched", Toast.LENGTH_SHORT).show();
				Log.d(TAG, "Handling View Touched Event");
				puzzleGrid.handleInput(event.x, event.y);
			}
		}
	}
	
	private void update(){
		puzzleGrid.update();
	}

	public void pause(){
		running = false;
		boolean retry = true;
		while(retry){
			try{
				renderThread.join();
				retry = false;
			} catch(InterruptedException e){
				// retry
			}
		}
	}
	


	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		setSurfaceSize(width, height);
		puzzleGrid.resetRefs(width, height);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// start the thread here so that we don't busy-wait in run()
		// waiting for the surface to be created
		resume();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// we have to tell thread to shut down & wait for it to finish, or else
		// it might touch the Surface after we return and explode
		pause();
	}

	/* Callback invoked when the surface dimensions change. */
	public void setSurfaceSize(int width, int height) {
		// synchronized to make sure these all change atomically
		synchronized (this) {
			screenwidth = width;
			screenheight = height;
			paddingWidth = Math.min(screenwidth, screenheight) / 20;
		}
	}
	
	public void addOneBullet(){
		puzzleGrid.addOneBullet();
	}
	
	private class PuzzleGrid {
		int gridDimension;
		int gridOffset;
		volatile int top, left, bottom, right, paddingSize;
		volatile int squareSize;
		Paint pt_Line;
		volatile Boolean[][] isPressed;
		volatile int [] colFloor;
		final int squarePadding = 3;
		volatile int fallingSquareCol;
		volatile int fallingSquareDist;
		volatile int fallingSquareVel;
		volatile int fallingSquareInterval;
		Long lastTime, thisTime;
		Random random;
		volatile private int bulletCount;
		volatile private boolean isWider;
		
		
		public PuzzleGrid(int gridDimension, int gridOffset, int viewWidth, int viewHeight){
			this.gridDimension = gridDimension;
			this.gridOffset = gridOffset;
			
			random = new Random();
			
			pt_Line = new Paint();
			pt_Line.setAntiAlias(true);
			pt_Line.setColor(Color.RED);
			pt_Line.setStyle(Paint.Style.FILL_AND_STROKE);	
			
			isPressed  = new Boolean[gridDimension][gridDimension-2*gridOffset];
			colFloor = new int[gridDimension-2*gridOffset];
			
			lastTime = thisTime = System.currentTimeMillis();
			
			resetGame();
			resetRefs(viewWidth, viewHeight);
		}
		
		public void resetRefs(int viewWidth, int viewHeight){
			synchronized(this){
				if(viewWidth > viewHeight){
					isWider = true;
				} else{
					isWider = false;
				}
				squareSize = (Math.min(viewWidth, viewHeight) - 2 * paddingSize) / gridDimension;
				top = paddingWidth + (screenheight - 2 * paddingWidth - squareSize*gridDimension) / 2;
				bottom = top + squareSize*gridDimension;
				left = paddingWidth + (screenwidth - 2 * paddingWidth - squareSize*gridDimension) / 2;
				right = left + squareSize*gridDimension;
				clearGrid();
				initiateFallingSquare();
			}
		}
		
		public void resetGame(){
			bulletCount = 3;
			clearGrid();
			initiateFallingSquare();	
		}
		
		private void clearGrid(){
			for(int i = 0; i < gridDimension; i++){
				for(int j = 0; j < gridDimension - 2 * gridOffset; j++){
					isPressed[i][j] = false;
				}
				
				for(int j = 0; j < gridDimension - 2 * gridOffset; j++){
					colFloor[j] = squareSize - squarePadding;
				}
			}
		}
		
		private void initiateFallingSquare(){
			fallingSquareCol = random.nextInt(gridDimension - 2 * gridOffset);
			fallingSquareDist = squareSize * (gridDimension + 1);
			fallingSquareVel = squareSize / 40;
			fallingSquareInterval = 20;
		}
		
		public void draw(Canvas canvas){
			synchronized(this){
				// draw grid
				for(int i = 0; i <= gridDimension; i++){
					canvas.drawLine(left + squareSize*gridOffset, top + i * squareSize, right - squareSize*gridOffset, top + i * squareSize, pt_Line);
				}

				for(int i = gridOffset; i <= gridDimension - gridOffset; i++){
					canvas.drawLine(left + i * squareSize, top, left + i * squareSize, bottom, pt_Line);
				}
				
				// draw squares
				for(int i = 0; i < gridDimension; i++){
					for(int j = 0; j < gridDimension - 2 * gridOffset; j++){
						if(isPressed[i][j]){
							drawSquare(canvas, i, j);
						}
					}
				}
				drawFallingSquare(canvas);
				drawBullets(canvas);
			}
		}
		
		public void update(){
			updateFallingSquare();
		}
		
		private void updateFallingSquare(){
			thisTime = System.currentTimeMillis();
			if(thisTime - lastTime > fallingSquareInterval){
				lastTime = thisTime;
				fallingSquareDist -= fallingSquareVel;
				if(fallingSquareDist < colFloor[fallingSquareCol]){
					//fallingSquareDist = squareSize * (gridDimension + 1);
					addSquaretoCol(fallingSquareCol);
					initiateFallingSquare();
				}
			}
		}
		
		private void addSquaretoCol(int col){
			int i = 0;
			boolean exitLoop = false;
			while(!exitLoop && i < gridDimension){
				if(isPressed[gridDimension - 1 - i][col] == false){
					isPressed[gridDimension - 1 - i][col] = true;
					exitLoop = true;
					colFloor[col] += squareSize;
				} 
				i++;
			}
			if(isGameOver()){
				resetGame();
			}
		}
		
		private void drawSquare(Canvas canvas, int row, int col){
			int x = left + (gridOffset + col) * squareSize + squarePadding;
			int y = top + row * squareSize + squarePadding;
			int size = squareSize - 2 * squarePadding;
			canvas.drawRect(x, y, x + size, y + size, pt_Line);
		}
		
		private void drawFallingSquare(Canvas canvas){
			synchronized(this){
				int x = left + (gridOffset + fallingSquareCol) * squareSize + squarePadding;
				int y = bottom - fallingSquareDist;
				int size = squareSize - 2 * squarePadding;
				canvas.drawRect(x, y, x + size, y + size, pt_Line);
			}
		}
		
		private void drawBullets(Canvas canvas){
			if(isWider){
				for(int i = 0; i <  bulletCount; i++){
					canvas.drawRect(0 + squarePadding, (gridDimension - i) * squareSize - squarePadding, 
									left + squareSize - squarePadding, (gridDimension - 1 - i) * squareSize + squarePadding,
									pt_Line);	
				}
			} else {
				
			}
		}
		
		private boolean touchedFallingSquare(int xTouch, int yTouch){
			synchronized(this){
				int x = left + (gridOffset + fallingSquareCol) * squareSize + squarePadding;
				int y = bottom - fallingSquareDist;
				int size = squareSize - 2 * squarePadding;
			
				if(		   xTouch > x 
						&& xTouch < x + size
						&& yTouch > y 
						&& yTouch < y + size ){
					return true;
				}
				else {
					return false;
				}
			}
		}
		
		public void handleInput(int x, int y){
			/*
			int row = findRow(y);
			int col = findCol(x);
			
			synchronized(this){
				if(Math.min(row, col) >= 0){
					isPressed[row][col] = true; 
				}
			}
			*/
			
			if(touchedFallingSquare(x, y) && bulletCount > 0){
				bulletCount = Math.max(bulletCount - 1, 0);
				initiateFallingSquare();
			}
		}
		
		private int findRow(int y){
			if(y < top || y > bottom) {
				return -1;
			} else {
				return (y - top) / squareSize;
			}			
		}

		private int findCol(int x){
			if(x < left + gridOffset*squareSize || x > right - gridOffset*squareSize) {
				return -1;
			} else {
				return (x - (left + gridOffset*squareSize)) / squareSize;
			}					
		}
		
		public void setBulletCount(int num){
			bulletCount = num;
		}
		
		public void addOneBullet(){
			bulletCount = Math.min(bulletCount + 1, gridDimension);
		}
		
		private boolean isGameOver(){
			for(int i = 0; i < gridDimension - 2 * gridOffset; i++){
				if(!isPressed[0][i]) {
					return false;
				}
			}
			return true;
		}
	}
}
