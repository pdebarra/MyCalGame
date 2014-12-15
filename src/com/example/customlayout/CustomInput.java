package com.example.customlayout;



import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;

@RemoteViews.RemoteView
public class CustomInput extends ViewGroup{
	
	private Rect mTempContainerRect = new Rect();
	private Rect mTempChildRect = new Rect();

	public CustomInput(Context context) {
		super(context);
	}

    public CustomInput(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomInput(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		final int count = getChildCount();
		
		int leftPos = getPaddingLeft();
		int rightPos = r - l - getPaddingRight();
		int topPos = getPaddingTop();
		int bottomPos = b - t - getPaddingBottom();
		
		int verticalBorder1 = leftPos + (1 * (rightPos - leftPos) ) / 6;
		int verticalBorder2 = leftPos + (2 * (rightPos - leftPos) ) / 6;
		int verticalBorder3 = leftPos + (3 * (rightPos - leftPos) ) / 6;
		int verticalBorder4 = leftPos + (4 * (rightPos - leftPos) ) / 6;
		int verticalBorder5 = leftPos + (5 * (rightPos - leftPos) ) / 6;
		
		int horizontalBorder = topPos + (bottomPos - topPos) / 2;
		
		int width = (rightPos - leftPos) / 6;
		int height = (bottomPos - topPos) / 2;
		
		for(int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			final InputParams lp = (InputParams) child.getLayoutParams();
			
			switch(lp.key){
			case InputParams.KEY_1:
				mTempContainerRect.left = leftPos;
				mTempContainerRect.right = verticalBorder1;
				mTempContainerRect.top = topPos;
				mTempContainerRect.bottom = horizontalBorder;
				break;
			case InputParams.KEY_2:
				mTempContainerRect.left = verticalBorder1;
				mTempContainerRect.right = verticalBorder2;
				mTempContainerRect.top = topPos;
				mTempContainerRect.bottom = horizontalBorder;
				break;
			case InputParams.KEY_3:
				mTempContainerRect.left = verticalBorder2;
				mTempContainerRect.right = verticalBorder3;
				mTempContainerRect.top = topPos;
				mTempContainerRect.bottom = horizontalBorder;
				break;
			case InputParams.KEY_4:
				mTempContainerRect.left = verticalBorder3;
				mTempContainerRect.right = verticalBorder4;
				mTempContainerRect.top = topPos;
				mTempContainerRect.bottom = horizontalBorder;
				break;
			case InputParams.KEY_5:
				mTempContainerRect.left = verticalBorder4;
				mTempContainerRect.right = verticalBorder5;
				mTempContainerRect.top = topPos;
				mTempContainerRect.bottom = horizontalBorder;
				break;
			case InputParams.KEY_6:
				mTempContainerRect.left = leftPos;
				mTempContainerRect.right = verticalBorder1;
				mTempContainerRect.top = horizontalBorder;
				mTempContainerRect.bottom = bottomPos;
				break;
			case InputParams.KEY_7:
				mTempContainerRect.left = verticalBorder1;
				mTempContainerRect.right = verticalBorder2;
				mTempContainerRect.top = horizontalBorder;
				mTempContainerRect.bottom = bottomPos;
				break;
			case InputParams.KEY_8:
				mTempContainerRect.left = verticalBorder2;
				mTempContainerRect.right = verticalBorder3;
				mTempContainerRect.top = horizontalBorder;
				mTempContainerRect.bottom = bottomPos;
				break;
			case InputParams.KEY_9:
				mTempContainerRect.left = verticalBorder3;
				mTempContainerRect.right = verticalBorder4;
				mTempContainerRect.top = horizontalBorder;
				mTempContainerRect.bottom = bottomPos;
				break;
			case InputParams.KEY_0:
				mTempContainerRect.left = verticalBorder4;
				mTempContainerRect.right = verticalBorder5;
				mTempContainerRect.top = horizontalBorder;
				mTempContainerRect.bottom = bottomPos;
				break;
			case InputParams.KEY_C:
				mTempContainerRect.left = verticalBorder5;
				mTempContainerRect.right = rightPos;
				mTempContainerRect.top = topPos;
				mTempContainerRect.bottom = horizontalBorder;
				break;
			case InputParams.KEY_E:
				mTempContainerRect.left = verticalBorder5;
				mTempContainerRect.right = rightPos;
				mTempContainerRect.top = horizontalBorder;
				mTempContainerRect.bottom = bottomPos;
				break;
			default:
				break;
			}
			

        	Gravity.apply(lp.gravity, width, height, mTempContainerRect, mTempChildRect);
        	child.layout(mTempChildRect.left, mTempChildRect.top, mTempChildRect.right, mTempChildRect.bottom);
		}
		
	}

    @Override
    public InputParams generateLayoutParams(AttributeSet attrs) {
        return new CustomInput.InputParams(getContext(), attrs);
    }

    @Override
    protected InputParams generateDefaultLayoutParams() {
        return new InputParams(InputParams.MATCH_PARENT, InputParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new InputParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof InputParams;
    }

	
	public static class InputParams extends MarginLayoutParams {
		
        public int gravity = Gravity.TOP | Gravity.START;
    	
    	final static int KEY_1 = 0;
    	final static int KEY_2 = 1;
    	final static int KEY_3 = 2;
    	final static int KEY_4 = 3;
    	final static int KEY_5 = 4;
    	final static int KEY_6 = 5;
    	final static int KEY_7 = 6;
    	final static int KEY_8 = 7;
    	final static int KEY_9 = 8;
    	final static int KEY_0 = 9;
    	final static int KEY_C = 10;
    	final static int KEY_E = 11;
    	
    	public int key = KEY_1;

		public InputParams(Context c, AttributeSet attrs) {
			super(c, attrs);
			// TODO Auto-generated constructor stub
			TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.CustomInput);
			gravity = a.getInt(R.styleable.CustomInput_android_layout_gravity, gravity);
			key = a.getInt(R.styleable.CustomInput_layout_key, key);
			a.recycle();
		}

        public InputParams(int width, int height) {
            super(width, height);
        }

        public InputParams(ViewGroup.LayoutParams source) {
            super(source);
        }
		
	}

}
