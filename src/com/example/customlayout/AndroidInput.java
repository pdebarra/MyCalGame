package com.example.customlayout;
import android.view.View;
import android.content.Context;
import java.util.List;
import com.example.customlayout.Input;

public class AndroidInput implements Input {
	MultiTouchHandler touchHandler;
	
	public AndroidInput(Context context, View view, float scaleX, float scaleY) {
		touchHandler = new MultiTouchHandler(view, scaleX, scaleY);
	}

	@Override
	public boolean isTouchDown(int pointer)
	{
		return touchHandler.isTouchDown(pointer);
	}

	@Override
	public int getTouchX(int pointer)
	{
		return touchHandler.getTouchX(pointer);
	}

	@Override
	public int getTouchY(int pointer)
	{
		return touchHandler.getTouchY(pointer);
	}

	@Override
	public List<TouchEvent> getTouchEvents()
	{
		// TODO: Implement this method
		return touchHandler.getTouchEvents();
	}
}
