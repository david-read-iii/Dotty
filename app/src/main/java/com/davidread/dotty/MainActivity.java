package com.davidread.dotty;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

/**
 * {@link MainActivity} represents a user interface with a move counter, a score counter, a Dotty
 * game board, and a new game button.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * {@link DotsGame} holding the logic and state of this Dotty game.
     */
    private DotsGame mGame;

    /**
     * {@link DotsGrid} displaying the game board and path of selected dots for this Dotty game.
     */
    private DotsGrid mDotsGrid;

    /**
     * {@link TextView} indicating how many moves are remaining.
     */
    private TextView mMovesRemaining;

    /**
     * {@link TextView} indicating the game score.
     */
    private TextView mScore;

    /**
     * Invoked once when this {@link MainActivity} is initially created. It initializes this
     * activity's member variables and initializes the user interface.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMovesRemaining = findViewById(R.id.movesRemaining);
        mScore = findViewById(R.id.score);
        mDotsGrid = findViewById(R.id.gameGrid);
        mDotsGrid.setGridListener(mGridListener);

        mGame = DotsGame.getInstance();
        startNewGame();
    }

    /**
     * {@link DotsGrid.DotsGridListener} that specifies what to do when a dot is touched on
     * {@link #mDotsGrid} and what to do after {@link DotsGrid#animateDots()} is called. It
     * registers each dot click from {@link #mDotsGrid} to {@link #mGame}.
     */
    private final DotsGrid.DotsGridListener mGridListener = new DotsGrid.DotsGridListener() {

        @Override
        public void onDotSelected(Dot dot, DotsGrid.DotSelectionStatus selectionStatus) {

            // Ignore selections when game is over.
            if (mGame.isGameOver()) return;

            // Add/remove dot to/from selected dots.
            DotsGame.DotStatus addStatus = mGame.processDot(dot);

            if (selectionStatus == DotsGrid.DotSelectionStatus.Last) {
                if (mGame.getSelectedDots().size() > 1) {
                    mDotsGrid.animateDots();
                } else {
                    mGame.clearSelectedDots();
                }
            }

            // Display changes to the game.
            mDotsGrid.invalidate();
        }

        @Override
        public void onAnimationFinished() {
            mGame.finishMove();
            mDotsGrid.invalidate();
            updateMovesAndScore();
        }
    };

    /**
     * Invoked when the new game {@link android.widget.Button} is clicked in {@link MainActivity}.
     * It animates the removal of {@link #mDotsGrid} from the screen, resets the state of
     * {@link #mGame}, and finally animates the addition of {@link #mDotsGrid} with the new game
     * state.
     */
    public void newGameClick(View view) {

        // Animate mDotsGrid down off screen.
        int screenHeight = this.getWindow().getDecorView().getHeight();
        ObjectAnimator moveBoardOff = ObjectAnimator.ofFloat(
                mDotsGrid,
                "translationY",
                screenHeight
        );
        moveBoardOff.setDuration(500);
        moveBoardOff.start();

        moveBoardOff.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                startNewGame();

                // Animate mDotsGrid from above the screen down to default location.
                ObjectAnimator moveBoardOn = ObjectAnimator.ofFloat(
                        mDotsGrid,
                        "translationY",
                        -screenHeight,
                        0
                );
                moveBoardOn.setDuration(500);
                moveBoardOn.start();
            }
        });
    }

    /**
     * Resets the game state in {@link #mGame}, resets the UI of {@link #mDotsGrid}, and resets the
     * UI of {@link #mMovesRemaining} and {@link #mScore}.
     */
    private void startNewGame() {
        mGame.newGame();
        mDotsGrid.invalidate();
        updateMovesAndScore();
    }

    /**
     * Updates {@link #mMovesRemaining} and {@link #mScore} with the latest game data from
     * {@link #mGame}.
     */
    private void updateMovesAndScore() {
        mMovesRemaining.setText(String.format(Locale.getDefault(), "%d", mGame.getMovesLeft()));
        mScore.setText(String.format(Locale.getDefault(), "%d", mGame.getScore()));
    }
}