package com.example.customlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;

/**
 * Example of writing a custom layout manager.  This is a fairly full-featured
 * layout manager that is relatively general, handling all layout cases.  You
 * can simplify it for more specific cases.
 */
@RemoteViews.RemoteView
public class CustomLayout extends ViewGroup {
    /** The amount of space used by children in the left gutter. */
    private int primaryBorder;

    /** The amount of space used by children in the right gutter. */
    private int secondaryBorder;

    /** These are used for computing child frames based on their gravity. */
    private final Rect mTmpContainerRect = new Rect();
    private final Rect mTmpChildRect = new Rect();

    public CustomLayout(Context context) {
        super(context);
    }

    public CustomLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Any layout manager that doesn't scroll will want this.
     */
    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    /**
     * Ask all children to measure themselves and compute the measurement of this
     * layout based on the children.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();

        // These keep track of the space we are using on the left and right for
        // views positioned there; we need member variables so we can also use
        // these for layout later.
        

        // Measurement will ultimately be computing these values.
        int inputHeight = 0;
        int detailHeight = 0;
        int actionHeight = 0;
        int inputWidth = 0;
        int detailWidth = 0;
        int actionWidth = 0;
		
		int maxHeight = 0;
		int maxWidth = 0;
		
        int childState = 0;

        // Iterate through all children, measuring them and computing our dimensions
        // from their size.
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                // Measure the child.
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);

                // Update our size information based on the layout params.  Children
                // that asked to be positioned on the left or right go in those gutters.
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (lp.position == LayoutParams.INPUT_BOX) {
					inputWidth += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
					inputHeight += child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
                } else if (lp.position == LayoutParams.DETAILS_BOX) {
					detailWidth += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
					detailHeight += child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
                } else {
					actionWidth += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
					actionHeight += child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
                }
                childState = combineMeasuredStates(childState, child.getMeasuredState());
            }
        }

        // Total width is the maximum width of all inner children plus the gutters.
        maxWidth += Math.max(inputWidth + detailWidth, actionWidth + detailWidth);
        maxHeight += Math.max(inputHeight + actionHeight, detailHeight);

        // Check against our minimum height and width
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

        // Report our final dimensions.
        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec,
                        childState << MEASURED_HEIGHT_STATE_SHIFT));
    }

    /**
     * Position all children within this layout.
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int count = getChildCount();

        // These are the far left and right edges in which we are performing layout.
        int leftPos = getPaddingLeft();
        int rightPos = right - left - getPaddingRight();

        // These are the top and bottom edges in which we are performing layout.
        final int parentTop = getPaddingTop();
        final int parentBottom = bottom - top - getPaddingBottom();
		

        primaryBorder = Math.min( leftPos + bottom - parentTop - getPaddingBottom(), parentTop + right - left - getPaddingRight());
		secondaryBorder = (2*primaryBorder)/3;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();

                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();

				if(right > bottom){
                	// Compute the frame in which we are placing this child.
                	if (lp.position == LayoutParams.INPUT_BOX) {
                	    mTmpContainerRect.left = leftPos;
                	    mTmpContainerRect.right = primaryBorder;
						mTmpContainerRect.top = parentTop + secondaryBorder;
						mTmpContainerRect.bottom = parentBottom;
						width = mTmpContainerRect.right - mTmpContainerRect.left;
						height = mTmpContainerRect.bottom - mTmpContainerRect.top;
                	} else if (lp.position == LayoutParams.DETAILS_BOX) {
                	    mTmpContainerRect.left = primaryBorder;
                	    mTmpContainerRect.right = rightPos;
						mTmpContainerRect.top = parentTop;
						mTmpContainerRect.bottom = parentBottom;
						width = mTmpContainerRect.right - mTmpContainerRect.left;
						height = mTmpContainerRect.bottom - mTmpContainerRect.top;
                	} else {
                	    mTmpContainerRect.left = leftPos;
                	    mTmpContainerRect.right = primaryBorder;
						mTmpContainerRect.top = parentTop;
						mTmpContainerRect.bottom = parentTop + secondaryBorder;
						width = mTmpContainerRect.right - mTmpContainerRect.left;
						height = mTmpContainerRect.bottom - mTmpContainerRect.top;
                	}
                	// Use the child's gravity and size to determine its final
                	// frame within its container.
                	Gravity.apply(lp.gravity, width, height, mTmpContainerRect, mTmpChildRect);

                	// Place the child.
                	child.layout(mTmpChildRect.left, mTmpChildRect.top,
                       	 mTmpChildRect.right, mTmpChildRect.bottom);
				} else {
                	// Compute the frame in which we are placing this child.
                	if (lp.position == LayoutParams.INPUT_BOX) {
                	    mTmpContainerRect.left = leftPos;
                	    mTmpContainerRect.right = rightPos;
						mTmpContainerRect.top = parentTop + secondaryBorder;
						mTmpContainerRect.bottom = parentTop + primaryBorder;
						width = mTmpContainerRect.right - mTmpContainerRect.left;
						height = mTmpContainerRect.bottom - mTmpContainerRect.top;
                	} else if (lp.position == LayoutParams.DETAILS_BOX) {
                	    mTmpContainerRect.left = leftPos;
                	    mTmpContainerRect.right = rightPos;
						mTmpContainerRect.top = parentTop + primaryBorder;
						mTmpContainerRect.bottom = parentBottom;
						width = mTmpContainerRect.right - mTmpContainerRect.left;
						height = mTmpContainerRect.bottom - mTmpContainerRect.top;
                	} else {
                	    mTmpContainerRect.left = leftPos;
                	    mTmpContainerRect.right = rightPos;
						mTmpContainerRect.top = parentTop;
						mTmpContainerRect.bottom = parentTop + secondaryBorder;
						width = mTmpContainerRect.right - mTmpContainerRect.left;
						height = mTmpContainerRect.bottom - mTmpContainerRect.top;
                	}
                	// Use the child's gravity and size to determine its final
                	// frame within its container.
                	Gravity.apply(lp.gravity, width, height, mTmpContainerRect, mTmpChildRect);

                	// Place the child.
                	child.layout(mTmpChildRect.left, mTmpChildRect.top,
								 mTmpChildRect.right, mTmpChildRect.bottom);
						
				}
            }
        }
    }

    // ----------------------------------------------------------------------
    // The rest of the implementation is for custom per-child layout parameters.
    // If you do not need these (for example you are writing a layout manager
    // that does fixed positioning of its children), you can drop all of this.

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new CustomLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    /**
     * Custom per-child layout information.
     */
    public static class LayoutParams extends MarginLayoutParams {
        /**
         * The gravity to apply with the View to which these layout parameters
         * are associated.
         */
        public int gravity = Gravity.TOP | Gravity.START;

        public static int INPUT_BOX = 0;
        public static int DETAILS_BOX = 1;
        public static int GAME_BOX = 2;

        public int position = INPUT_BOX;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            // Pull the layout param values from the layout XML during
            // inflation.  This is not needed if you don't care about
            // changing the layout behavior in XML.
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.CustomLayout);
            gravity = a.getInt(R.styleable.CustomLayout_android_layout_gravity, gravity);
            position = a.getInt(R.styleable.CustomLayout_layout_position, position);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}
