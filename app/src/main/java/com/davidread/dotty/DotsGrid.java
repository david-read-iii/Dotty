package com.davidread.dotty;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import java.util.ArrayList;

/**
 * {@link DotsGrid} is a custom view for a Dotty game board. It draws dots for each dot in
 * {@link #mGame} and paths between all dots selected by {@link #mGame}.
 */
public class DotsGrid extends View {

    /**
     * Enum returned by {@link DotsGridListener#onDotSelected(Dot, DotSelectionStatus)} to represent
     * the selection status of the {@link Dot} that invoked this callback method.
     */
    public enum DotSelectionStatus {First, Additional, Last}

    /**
     * {@link DotsGridListener} defines an interface that a controller class should implement and
     * pass into this {@link DotsGrid} to specify what to do when a dot is selected.
     */
    public interface DotsGridListener {
        void onDotSelected(Dot dot, DotSelectionStatus status);
    }

    /**
     * Constant int representing the pixel radius of each dot on screen.
     */
    private final int DOT_RADIUS = 40;

    /**
     * {@link DotsGame} holding the logic and state of this Dotty game.
     */
    private DotsGame mGame;

    /**
     * {@link Path} used to define lines between selected dots.
     */
    private Path mDotPath;

    /**
     * {@link DotsGridListener} defined by a controller class that defines what to do when a dot
     * is selected.
     */
    private DotsGridListener mGridListener;

    /**
     * Int array holding the colors that may be assigned to each dot.
     */
    private int[] mDotColors;

    /**
     * Int representing the width of a single dot in pixels.
     */
    private int mCellWidth;

    /**
     * Int representing the height of a single dot in pixels.
     */
    private int mCellHeight;

    /**
     * {@link Paint} used to draw dots.
     */
    private Paint mDotPaint;

    /**
     * {@link Paint} used to draw paths.
     */
    private Paint mPathPaint;

    /**
     * {@link AnimatorSet} to animate dots within this {@link DotsGrid}.
     */
    private AnimatorSet mAnimatorSet;

    /**
     * Constructs a new {@link DotsGrid}.
     *
     * @param context {@link Context} for the superclass.
     * @param attrs   {@link AttributeSet} for the superclass.
     */
    public DotsGrid(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Used to access the game state.
        mGame = DotsGame.getInstance();

        // Get color resources.
        mDotColors = getResources().getIntArray(R.array.dotColors);

        // For drawing dots.
        mDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        // For drawing the path between connected dots.
        mPathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPathPaint.setStrokeWidth(10);
        mPathPaint.setStyle(Paint.Style.STROKE);

        // The path between connected dots.
        mDotPath = new Path();

        // For animating dots.
        mAnimatorSet = new AnimatorSet();
    }

    /**
     * Invoked when the size of this {@link DotsGrid} is changed. It updates the values for
     * {@link #mCellWidth} and {@link #mCellHeight}, and the view-related attributes of each
     * {@link Dot} in {@link #mGame}.
     *
     * @param width     Current width of this {@link DotsGrid}.
     * @param height    Current height of this {@link DotsGrid}.
     * @param oldWidth  Old width of this {@link DotsGrid}.
     * @param oldHeight Old height of this {@link DotsGrid}.
     */
    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        int boardWidth = (width - getPaddingLeft() - getPaddingRight());
        int boardHeight = (height - getPaddingTop() - getPaddingBottom());
        mCellWidth = boardWidth / DotsGame.GRID_SIZE;
        mCellHeight = boardHeight / DotsGame.GRID_SIZE;
        resetDots();
    }

    /**
     * Invoked when the {@link DotsGrid} should render its content. First, it draws all dots stored
     * in {@link #mGame} given their view-related attributes. Then, it draws a path between all
     * dots currently selected in {@link #mGame}.
     *
     * @param canvas The {@link Canvas} on which the background will be drawn.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw dots.
        for (int row = 0; row < DotsGame.GRID_SIZE; row++) {
            for (int col = 0; col < DotsGame.GRID_SIZE; col++) {
                Dot dot = mGame.getDot(row, col);
                mDotPaint.setColor(mDotColors[dot.color]);
                canvas.drawCircle(dot.centerX, dot.centerY, dot.radius, mDotPaint);
            }
        }

        // Draw connector.
        ArrayList<Dot> selectedDots = mGame.getSelectedDots();
        if (!selectedDots.isEmpty()) {
            mDotPath.reset();
            Dot dot = selectedDots.get(0);
            mDotPath.moveTo(dot.centerX, dot.centerY);

            for (int i = 1; i < selectedDots.size(); i++) {
                dot = selectedDots.get(i);
                mDotPath.lineTo(dot.centerX, dot.centerY);
            }

            mPathPaint.setColor(mDotColors[dot.color]);
            canvas.drawPath(mDotPath, mPathPaint);
        }
    }

    /**
     * Invoked when this {@link DotsGrid}'s {@link View.OnClickListener} should be called.
     *
     * @return True if there was an assigned {@link View.OnClickListener} assigned and it was
     * called. False otherwise.
     */
    @Override
    public boolean performClick() {
        return super.performClick();
    }

    /**
     * Invoked when a touch screen motion event occurs. It determines which dot was clicked and
     * notifies the controller class of this via {@link #mGridListener}.
     *
     * @param event The {@link MotionEvent} that invoked this callback.
     * @return True if the event was handled. False otherwise.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // Only execute when a listener exists.
        if (mGridListener == null) return true;

        // Determine which dot is pressed.
        int x = (int) event.getX();
        int y = (int) event.getY();
        int col = x / mCellWidth;
        int row = y / mCellHeight;
        Dot selectedDot = mGame.getDot(row, col);

        // Return previously selected dot if touch moves outside the grid.
        if (selectedDot == null) {
            selectedDot = mGame.getLastSelectedDot();
        }

        // Notify activity that a dot is selected.
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mGridListener.onDotSelected(selectedDot, DotSelectionStatus.First);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            mGridListener.onDotSelected(selectedDot, DotSelectionStatus.Additional);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            mGridListener.onDotSelected(selectedDot, DotSelectionStatus.Last);
        }

        // Allow accessibility services to perform this action.
        performClick();

        return true;
    }

    /**
     * Initializes {@link #mGridListener} of this {@link DotsGrid}.
     *
     * @param gridListener A {@link DotsGridListener} that specifies what to do when a dot is
     *                     clicked.
     */
    public void setGridListener(DotsGridListener gridListener) {
        mGridListener = gridListener;
    }

    /**
     * Resets the view-related attributes of each dot in {@link #mGame} given the attributes of this
     * {@link DotsGrid}.
     */
    private void resetDots() {
        for (int row = 0; row < DotsGame.GRID_SIZE; row++) {
            for (int col = 0; col < DotsGame.GRID_SIZE; col++) {
                Dot dot = mGame.getDot(row, col);
                dot.radius = DOT_RADIUS;
                dot.centerX = col * mCellWidth + (mCellWidth / 2f);
                dot.centerY = row * mCellHeight + (mCellHeight / 2f);
            }
        }
    }

    /**
     * Animates the disappearance of dots selected in {@link #mGame}.
     */
    public void animateDots() {

        // For storing many animations.
        ArrayList<Animator> animations = new ArrayList<>();

        // Get an animation to make selected dots disappear.
        animations.add(getDisappearingAnimator());

        // Play animations (just one right now) together, then reset radius to full size .
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(animations);
        mAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                resetDots();
            }
        });
        mAnimatorSet.start();
    }

    /**
     * Returns a {@link ValueAnimator} that makes all dots selected by {@link #mGame} shrink and
     * disappear by reducing each individual {@link Dot#radius} field.
     *
     * @return A {@link ValueAnimator} that shrinks and disappears selected dots.
     */
    private ValueAnimator getDisappearingAnimator() {
        ValueAnimator animator = ValueAnimator.ofFloat(1, 0);
        animator.setDuration(100);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(animation -> {
            for (Dot dot : mGame.getSelectedDots()) {
                dot.radius = DOT_RADIUS * (float) animation.getAnimatedValue();
            }
            invalidate();
        });
        return animator;
    }
}