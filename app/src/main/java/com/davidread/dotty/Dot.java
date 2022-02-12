package com.davidread.dotty;

import java.util.Random;

/**
 * {@link Dot} represents a single dot with a color, a row value, a column value,
 * a center x-coordinate, a center y-coordinate, a radius, and whether the dot is currently
 * selected.
 */
public class Dot {

    /**
     * Color of this {@link Dot}.
     */
    public int color;

    /**
     * Row index where this {@link Dot} is located on the game board.
     */
    public int row;

    /**
     * Column index where this {@link Dot} is located on the game board.
     */
    public int col;

    /**
     * Center x-coordinate where this {@link Dot} is located on screen.
     */
    public float centerX;

    /**
     * Center y-coordinate where this {@link Dot} is located on screen.
     */
    public float centerY;

    /**
     * Radius of this {@link Dot} on screen.
     */
    public float radius;

    /**
     * Whether this {@link Dot} has been selected.
     */
    public boolean selected;

    /**
     * {@link Random} for assigning random colors to {@link #color}.
     */
    private Random randomGen;

    /**
     * Constructs a new {@link Dot} with a row and column index.
     *
     * @param row Row index on the game board.
     * @param col Column index on the game board.
     */
    public Dot(int row, int col) {
        randomGen = new Random();
        setRandomColor();
        selected = false;
        radius = 1;
        this.row = row;
        this.col = col;
    }

    /**
     * Assigns a random color to this {@link Dot}.
     */
    public void setRandomColor() {
        color = randomGen.nextInt(DotsGame.NUM_COLORS);
    }

    /**
     * Returns true if this {@link Dot} is adjacent to a passed {@link Dot} on the game board.
     *
     * @param dot The {@link Dot} we are checking for adjacency with.
     * @return Whether this {@link Dot} is adjacent with the passed {@link Dot}.
     */
    public boolean isAdjacent(Dot dot) {
        int colDiff = Math.abs(col - dot.col);
        int rowDiff = Math.abs(row - dot.row);
        return colDiff + rowDiff == 1;
    }
}