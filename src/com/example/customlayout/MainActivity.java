package com.example.customlayout;

import java.util.Random;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static String TAG = "MainActivity";
	public InputAns inputAns;
	public GameManager gameManager;
	public CalcView calcView;
	public PopView popView;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Log.d(TAG, "Entered OnCreate");
		
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.my_custom_layout);
		Log.d(TAG, "Exited OnCreate");
		
		inputAns = new InputAns();
		gameManager = new GameManager();
		calcView = (CalcView) findViewById(R.id.CV_calcView);
		
		popView = (PopView) findViewById(R.id.PV_pop);
		
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
		Log.d(TAG, "onCreateOptionsMenu");
        return true;
    }

	@Override
	protected void onResume()
	{
		// TODO: Implement this method
		super.onResume();
		//popView.resume();
	}

	@Override
	protected void onPause()
	{
		// TODO: Implement this method
		super.onPause();
		//popView.pause();
	}
	
	
	
	
    
    public void keypadEntry(View v){
		Log.d(TAG, "keyPadEntry");
		
    	switch(v.getId()){
    	case R.id.num0:
    		//Toast.makeText(this, "Keypad 0 Pressed", Toast.LENGTH_SHORT).show();
    		inputAns.addToInput("0");
    		break;
    	case R.id.num1:
    		//Toast.makeText(this, "Keypad 1 Pressed", Toast.LENGTH_SHORT).show();
    		inputAns.addToInput("1");
    		break;
    	case R.id.num2:
    		//Toast.makeText(this, "Keypad 2 Pressed", Toast.LENGTH_SHORT).show();
    		inputAns.addToInput("2");
    		break;
    	case R.id.num3:
    		//Toast.makeText(this, "Keypad 3 Pressed", Toast.LENGTH_SHORT).show();
    		inputAns.addToInput("3");
    		break;
    	case R.id.num4:
    		//Toast.makeText(this, "Keypad 4 Pressed", Toast.LENGTH_SHORT).show();
    		inputAns.addToInput("4");
    		break;
    	case R.id.num5:
    		//Toast.makeText(this, "Keypad 5 Pressed", Toast.LENGTH_SHORT).show();
    		inputAns.addToInput("5");
    		break;
    	case R.id.num6:
    		//Toast.makeText(this, "Keypad 6 Pressed", Toast.LENGTH_SHORT).show();
    		inputAns.addToInput("6");
    		break;
    	case R.id.num7:
    		//Toast.makeText(this, "Keypad 7 Pressed", Toast.LENGTH_SHORT).show();
    		inputAns.addToInput("7");
    		break;
    	case R.id.num8:
    		//Toast.makeText(this, "Keypad 8 Pressed", Toast.LENGTH_SHORT).show();
    		inputAns.addToInput("8");
    		break;
    	case R.id.num9:
    		//Toast.makeText(this, "Keypad 9 Pressed", Toast.LENGTH_SHORT).show();
    		inputAns.addToInput("9");
    		break;
    	case R.id.clear:
    		//Toast.makeText(this, "Keypad clear Pressed", Toast.LENGTH_SHORT).show();
    		inputAns.deletelast();
    		break;
    	case R.id.enter:
    		//Toast.makeText(this, "Keypad enter Pressed", Toast.LENGTH_SHORT).show();
    		//Toast.makeText(this, "Logic is:" + String.valueOf(gameManager.receiveUpdate(inputAns.getInputAns())), Toast.LENGTH_SHORT).show();
    		if (gameManager.getGameState() == GameManager.STATE_1_INPUT_A && gameManager.receiveUpdate(inputAns.getInputAns())) {
    			calcView.updateFigures(gameManager.NumA, true, gameManager.NumB, true, gameManager.Interim, true, gameManager.Ans, false);
    		} else if (gameManager.getGameState() == GameManager.STATE_2_INPUT_B && gameManager.receiveUpdate(inputAns.getInputAns())){
    			calcView.updateFigures(gameManager.NumA, true, gameManager.NumB, true, "Correct", true, gameManager.Ans, true);
    		}
    		inputAns.clearInputAns();
    		break;
    	}
    }
    
    class InputAns {
    	String ansText;
    	
    	InputAns(){
    		ansText = new String("");
    	}
    	
	    public boolean isInputEmpty(){
	    	return ansText.isEmpty();
	    }
	    
	    public boolean addToInput(String c){   		
	    	try {
	    		Integer.parseInt(c.toString());
	    		ansText = ansText + c;
		    	return true;
	    	} 
	    	catch(NumberFormatException nfe) {
	    	   System.out.println("Could not parse " + nfe);
		    	return false;
	    	}	
	    }
	    
	    public void clearInputAns(){
	    	ansText = "";
	    }
	    
	    public String getInputAns(){
	    	return ansText;
	    }
	    
	    public void deletelast(){
	    	if (! ansText.isEmpty()){
	    		ansText = ansText.substring(0, ansText.length() - 1);
	    	}
	    }
    }
    
    class GameManager{

    	Random r;
    	
    	public int currentState;
    	public static final int STATE_0_READY = 0;
    	public static final int STATE_1_INPUT_A = 1;
    	public static final int STATE_2_INPUT_B = 2;
    	public static final int STATE_3_RESULT = 3;
    	public static final int STATE_4_READY = 4;
    	
    	public int level;
    	
    	public String NumA;
    	public String NumB;
    	public String Interim;
    	public String Ans;
    	
    	GameManager(){
    		r = new Random();
    		
    		level = 10;
    		
    		currentState = STATE_0_READY;
    		NumA = "";
    		NumB = "";
    		Interim = "";
    		Ans = "";
    		
    	}
    	
    	public int getGameState(){
    		return currentState;
    	}
    	
    	public boolean receiveUpdate(String input){
    		return gameLoop(input);
    	}
    	
    	private boolean gameLoop(String input){
    		boolean success = true;
    	
    		if(currentState == STATE_0_READY){
    			currentState = STATE_1_INPUT_A;  
    			setupProblem(level);
    			calcView.updateFigures(NumA, true, NumB, true, Interim, false, Ans, false);
    		} else if(currentState == STATE_1_INPUT_A){
    			if(input.equals(Interim)){
    				currentState = STATE_2_INPUT_B; 
    			} else if(input.equals("skip")){
					currentState = STATE_2_INPUT_B; 
				} else {
    				success = false;
    			}
    		} else if(currentState == STATE_2_INPUT_B){
    			if(input.equals(Ans)){
    				currentState = STATE_3_RESULT;  
    			} else {
    				success = false;
    			}
    		} else if(currentState == STATE_3_RESULT){
    			popView.addOneBullet();
    			currentState = STATE_0_READY;  
				success = false;
    		} else {
    			currentState = STATE_0_READY;  
    		}
    		return success;
    	}

    	public void setupProblem(int Level){
    		int a = Level + (r.nextInt(11) - 5);
    		int b = Level + (r.nextInt(11) - 5);
    		
    		NumA = String.valueOf(a);
    		NumB = String.valueOf(b);
    		Interim = String.valueOf( (a+(b-Level)) * Level);
    		Ans = String.valueOf(a*b);
    	}
    }
}
