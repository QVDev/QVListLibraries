package qvdev.utils.libraries.lists;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

public class KinecticListView extends ListView implements AbsListView.OnScrollListener
{
    private static float RESET_ROTATION = 0.0f;
    private static final int MAX_Y_OVERSCROLL_DISTANCE = 200;

    private float rotationDegrees = 30.0f;
    private int rotationResetDelay = 200;
    private int rotationItems = 3;
    private boolean isScrolling;
    private boolean isKinectic;

    private boolean isBouncing;

    private Context mContext;
    private int mMaxYOverscrollDistance;

    public KinecticListView(Context context)
    {
        super(context);

        this.setOnScrollListener(this);
        mContext = context;
        initBounceListView();
    }

    public KinecticListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        this.setOnScrollListener(this);
        mContext = context;
        initBounceListView();
    }

    public KinecticListView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        this.setOnScrollListener(this);
        mContext = context;
        initBounceListView();
    }

    private void initBounceListView()
    {
        //get the density of the screen and do some maths with it on the max overscroll distance
        //variable so that you get similar behaviors no matter what the screen size

        final DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        final float density = metrics.density;

        mMaxYOverscrollDistance = (int) (density * MAX_Y_OVERSCROLL_DISTANCE);
    }


    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent)
    {
        if (isKinectic && !isBouncing)
        {
            if ((isTouchEvent && !isScrolling) || !isTouchEvent && !isScrolling)
            {
                isScrolling = true;
                // Get the first and last visible positions.
                int first = getFirstVisiblePosition();
                int last = getLastVisiblePosition();

                // Rotate half the entries.
                if (deltaY > 0)
                {
                    defineBottomOfListItems(last - first);

                } else
                {
                    defineTopOfListItems(first);
                }
            }
        }

        if(isBouncing)
        {
            //This is where the magic happens, we have replaced the incoming maxOverScrollY with our own custom variable mMaxYOverscrollDistance;
            return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, mMaxYOverscrollDistance, isTouchEvent);
        }
        else
        {
            return true;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int state)
    {
        resetViews();
    }

    @Override
    public void onScroll(AbsListView absListView, int first, int last, int total)
    {
    }

    private void defineTopOfListItems(int itemsOnScreen)
    {
        // Over-scrolled at the top.
        for (int i = itemsOnScreen; i <= itemsOnScreen + rotationItems; i++)
        {
            View item = getChildAt(i);
            if (item != null)
                rotate(item);
        }
    }

    private void defineBottomOfListItems(int itemsOnScreen)
    {
        // Over-scrolled at the bottom.
        for (int i = itemsOnScreen; i >= itemsOnScreen - rotationItems; i--)
        {
            View item = getChildAt(i);

            if (item != null)
                rotate(item);
        }
    }

    private void rotate(final View view)
    {
        if (view.getRotationX() == RESET_ROTATION)
        {
            view.setRotationX(rotationDegrees);

            if (!isScrolling)
            {
                resetRotation(view);
            }
        }
    }

    private void resetRotation(final View view)
    {
        // Reset the rotation.
        view.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                view.setRotationX(RESET_ROTATION);
            }
        }, rotationResetDelay);

    }

    private void resetViews()
    {
        for (int i = 0; i < getChildCount(); i++)
        {
            final View view = getChildAt(i);

            if (view.getRotationX() != RESET_ROTATION)
            {
                resetRotation(view);
                isScrolling = false;
            }
        }
    }

    public float getRotationDegrees()
    {
        return rotationDegrees;
    }

    public void setRotationDegrees(float rotationDegrees)
    {
        this.rotationDegrees = rotationDegrees;
    }

    public int getRotationResetDelay()
    {
        return rotationResetDelay;
    }

    public void setRotationResetDelay(int rotationResetDelay)
    {
        this.rotationResetDelay = rotationResetDelay;
    }

    public int getRotationItems()
    {
        return rotationItems;
    }

    public void setRotationItems(int rotationItems)
    {
        this.rotationItems = rotationItems;
    }

    public boolean isKinectic()
    {
        return isKinectic;
    }

    /**
     * Set the listview to kinect mode
     * ranges from <tt>0</tt> to <tt>length() - 1</tt>.
     * @param kinectic      true or false
     *
     */
    public void setKinectic(boolean kinectic)
    {
        isKinectic = kinectic;
    }

    public boolean isBouncing()
    {
        return isBouncing;
    }

    public void setBouncing(boolean bouncing)
    {
        isBouncing = bouncing;
    }
}