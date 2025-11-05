package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;

import java.util.Observer;

public interface IModel {

    /**
     * Generates a new maze with the given dimensions.
     *
     * @param rows Number of rows in the maze
     * @param cols Number of columns in the maze
     */
    void generateMaze(int rows, int cols);

    /**
     * Returns the current maze.
     *
     * @return The current Maze object
     */
    Maze getMaze();

    /**
     * Solves the current maze using a search algorithm.
     */
    void solveMaze();

    /**
     * Returns the solution of the current maze.
     *
     * @return The Solution object for the maze
     */
    Solution getSolution();

    /**
     * Gets the current row of the player.
     *
     * @return Player's row index
     */
    int getPlayerRow();

    /**
     * Gets the current column of the player.
     *
     * @return Player's column index
     */
    int getPlayerCol();

    /**
     * Sets the player's row position.
     *
     * @param row New row index
     */
    void setPlayerRow(int row);

    /**
     * Sets the player's column position.
     *
     * @param col New column index
     */
    void setPlayerCol(int col);

    /**
     * Assigns an observer to receive updates from the model.
     *
     * @param o The observer to register
     */
    void assignObserver(Observer o);

    /**
     * Updates the player's position based on the given movement direction.
     *
     * @param direction The direction in which to move the player
     */
    void updatePlayerLocation(MovementDirection direction);

    /**
     * Saves the current maze with the given name.
     *
     * @param name The name to assign to the saved maze file
     */
    void saveMaze(String name);
}