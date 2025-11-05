package View;

import ViewModel.MyViewModel;
import javafx.event.ActionEvent;

public interface IView {

    /**
     * Triggered when the player reaches the goal in the maze.
     */
    void goalReached();

    /**
     * Connects the ViewModel to the View layer.
     *
     * @param myViewModel The ViewModel instance
     */
    void setMazeViewModel(MyViewModel myViewModel);

    /**
     * Solves the maze upon user request.
     *
     * @param actionEvent Event triggered by the Solve button
     */
    void solveMaze(ActionEvent actionEvent);

    /**
     * Saves the current maze state to a file.
     *
     * @param actionEvent Event triggered by the Save button
     */
    void SaveMaze(ActionEvent actionEvent);

    /**
     * Sets the player's current position in the maze.
     *
     * @param row The player's row index
     * @param col The player's column index
     */
    void setPlayerPosition(int row, int col);

    /**
     * Sets the player's new row after movement.
     *
     * @param updatePlayerRow New row for the player
     */
    void setUpdatePlayerRow(int updatePlayerRow);

    /**
     * Sets the player's new column after movement.
     *
     * @param updatePlayerCol New column for the player
     */
    void setUpdatePlayerCol(int updatePlayerCol);

    /**
     * Generates a new maze randomly.
     */
    void generateNewMaze();
}
