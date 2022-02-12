package com.davidread.dotty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * {@link DotsGame} is a model class for the game board and state of a Dotty game. It is implemented
 * using a singleton pattern, so call {@link #getInstance()} to use this class.
 */
public class DotsGame {

    /**
     * Int constant representing the number of colors dots may be assigned on the game board.
     */
    public static final int NUM_COLORS = 5;

    /**
     * Int constant representing the number of rows and columns the game board has.
     */
    public static final int GRID_SIZE = 6;

    /**
     * Int constant representing the initial value assigned to {@link #mMovesLeft} each time a new
     * game is started.
     */
    public static final int INIT_MOVES = 10;

    /**
     * Enum returned by {@link #processDot(Dot)}. It represents the dot's status when we are
     * attempting to add it to {@link #mSelectedDots}.
     */
    public enum DotStatus {Added, Rejected, Removed}

    /**
     * Static reference to this {@link DotsGame} so it can be implemented as a singleton.
     */
    private static DotsGame mDotsGame;

    /**
     * Tracks how many moves are left in a game.
     */
    private int mMovesLeft;

    /**
     * Tracks the game score.
     */
    private int mScore;

    /**
     * Stores all {@link Dot} objects that make up the game board.
     */
    private Dot[][] mDots;

    /**
     * Stores only the currently selected {@link Dot} objects.
     */
    private ArrayList<Dot> mSelectedDots;

    /**
     * Constructs a new {@link DotsGame}.
     */
    private DotsGame() {

        // Score is initially 0.
        mScore = 0;

        // Create dots for the 2D array.
        mDots = new Dot[GRID_SIZE][GRID_SIZE];
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                mDots[row][col] = new Dot(row, col);
            }
        }

        // No dots are initially selected.
        mSelectedDots = new ArrayList();
    }

    /**
     * Returns an instance of {@link DotsGame}.
     *
     * @return An instance of {@link DotsGame}.
     */
    public static DotsGame getInstance() {
        if (mDotsGame == null) {
            mDotsGame = new DotsGame();
        }
        return mDotsGame;
    }

    /**
     * Returns how many moves are left in the game.
     *
     * @return How many moves are left in the game.
     */
    public int getMovesLeft() {
        return mMovesLeft;
    }

    /**
     * Returns the game score.
     *
     * @return The game score.
     */
    public int getScore() {
        return mScore;
    }

    /**
     * Returns a {@link Dot} from {@link #mDots} given its row and column indices.
     *
     * @param row Row index.
     * @param col Column index.
     * @return A {@link Dot} from {@link #mDots} given its row and column indices.
     */
    public Dot getDot(int row, int col) {
        if (row >= GRID_SIZE || row < 0 || col >= GRID_SIZE || col < 0) {
            return null;
        } else {
            return mDots[row][col];
        }
    }

    /**
     * Returns an {@link ArrayList} of currently selected {@link Dot} objects.
     *
     * @return An {@link ArrayList} of currently selected {@link Dot} objects.
     */
    public ArrayList<Dot> getSelectedDots() {
        return mSelectedDots;
    }

    /**
     * Returns the {@link Dot} most recently selected.
     *
     * @return The {@link Dot} most recently selected.
     */
    public Dot getLastSelectedDot() {
        if (mSelectedDots.size() > 0) {
            return mSelectedDots.get(mSelectedDots.size() - 1);
        } else {
            return null;
        }
    }

    /**
     * Returns an {@link ArrayList} containing the lowest selected {@link Dot} from each column of
     * the game board.
     *
     * @return An {@link ArrayList} containing the lowest selected {@link Dot} from each column of
     * the game board.
     */
    public ArrayList<Dot> getLowestSelectedDots() {

        ArrayList<Dot> dots = new ArrayList<>();
        for (int col = 0; col < GRID_SIZE; col++) {
            for (int row = GRID_SIZE - 1; row >= 0; row--) {
                if (mDots[row][col].selected) {
                    dots.add(mDots[row][col]);
                    break;
                }
            }
        }

        return dots;
    }

    /**
     * Clears the currently selected dots.
     */
    public void clearSelectedDots() {

        // Reset board so none selected.
        for (Dot dot : mSelectedDots) {
            dot.selected = false;
        }

        mSelectedDots.clear();
    }

    /**
     * Attempt to add a {@link Dot} to {@link #mSelectedDots}. A {@link Dot} may only be added if
     * it is the first dot being selected or if it has the same color and is adjacent to the last
     * dot in {@link #mSelectedDots}. If the {@link Dot} is already selected, then the last dot in
     * {@link #mSelectedDots} is removed if backtracking.
     *
     * @param dot {@link Dot} we are attempting to add to {@link #mSelectedDots}.
     * @return The status of the {@link Dot} objects addition. Is either {@link DotStatus#Added},
     * {@link DotStatus#Rejected}, or {@link DotStatus#Removed}.
     */
    public DotStatus processDot(Dot dot) {
        DotStatus status = DotStatus.Rejected;

        // Check if first dot selected.
        if (mSelectedDots.size() == 0) {
            mSelectedDots.add(dot);
            dot.selected = true;
            status = DotStatus.Added;
        } else if (!dot.selected) {
            // Make sure new is same color and adjacent to last selected dot.
            Dot lastDot = getLastSelectedDot();
            if (lastDot.color == dot.color && lastDot.isAdjacent(dot)) {
                mSelectedDots.add(dot);
                dot.selected = true;
                status = DotStatus.Added;
            }
        } else if (mSelectedDots.size() > 1) {
            // Dot is already selected, so remove last dot if backtracking.
            Dot secondLast = mSelectedDots.get(mSelectedDots.size() - 2);
            if (secondLast.equals(dot)) {
                Dot removedDot = mSelectedDots.remove(mSelectedDots.size() - 1);
                removedDot.selected = false;
                status = DotStatus.Removed;
            }
        }

        return status;
    }

    /**
     * Sorts the {@link Dot} objects in {@link #mSelectedDots} by their row value ascending.
     */
    private void sortSelectedDots() {
        Collections.sort(mSelectedDots, new Comparator<Dot>() {
            public int compare(Dot dot1, Dot dot2) {
                return dot1.row - dot2.row;
            }
        });
    }

    /**
     * Should be called after completing a dot path to relocate dots and update game attributes.
     */
    public void finishMove() {
        if (mSelectedDots.size() > 1) {

            // Sort by row so dots are processed top-down.
            sortSelectedDots();

            // Move all dots above each selected dot down by changing color.
            for (Dot dot : mSelectedDots) {
                for (int row = dot.row; row > 0; row--) {
                    Dot dotCurrent = mDots[row][dot.col];
                    Dot dotAbove = mDots[row - 1][dot.col];
                    dotCurrent.color = dotAbove.color;
                }

                // Add new dot at top.
                Dot topDot = mDots[0][dot.col];
                topDot.setRandomColor();
            }

            mScore += mSelectedDots.size();
            mMovesLeft--;

            clearSelectedDots();
        }
    }

    /**
     * Resets this {@link DotsGame} with a new game.
     */
    public void newGame() {
        mScore = 0;
        mMovesLeft = INIT_MOVES;

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                mDots[row][col].setRandomColor();
            }
        }
    }

    /**
     * Returns true if this {@link DotsGame} is over.
     *
     * @return True if this {@link DotsGame} is over.
     */
    public boolean isGameOver() {
        return mMovesLeft == 0;
    }
}