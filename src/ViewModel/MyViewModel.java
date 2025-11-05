package ViewModel;

import Model.IModel;
import Model.MovementDirection;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.scene.input.KeyEvent;

import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {

    /**
     * Constructor for MyViewModel.
     * Connects this ViewModel to the underlying model and registers as an observer.
     *
     * @param model The model instance implementing IModel
     */
    public MyViewModel(IModel model){
        this.model = model;
        this.model.assignObserver(this);
    }
    private final IModel model;

    /**
     * Requests the model to generate a new maze with given dimensions.
     *
     * @param rows Number of maze rows
     * @param cols Number of maze columns
     */
    public void generateMaze(int rows, int cols){
        model.generateMaze(rows,cols);
    }

    /**
     * Retrieves the current maze from the model.
     *
     * @return Maze object
     */
    public Maze getMaze(){
        return model.getMaze();
    }

    /**
     * Requests the model to solve the current maze.
     */
    public void solveMaze(){
        model.solveMaze();
    }

    /**
     * Retrieves the current maze solution from the model.
     *
     * @return Solution object
     */
    public Solution getSolution(){
        return model.getSolution();
    }

    /**
     * Gets the current row index of the player.
     *
     * @return Player's row index
     */
    public int getPlayerRow(){
        return model.getPlayerRow();
    }

    /**
     * Gets the current column index of the player.
     *
     * @return Player's column index
     */
    public int getPlayerCol(){ return model.getPlayerCol(); }

    /**
     * Sets the player's row index.
     *
     * @param r New row index
     */
    public void setPlayerRow(int r){
        this.model.setPlayerRow(r);
    }

    /**
     * Sets the player's column index.
     *
     * @param c New column index
     */
    public void setPlayerCol(int c){
        this.model.setPlayerCol(c);
    }

    /**
     * Receives updates from the model and notifies observers (e.g., the View).
     *
     * @param o   The observable object (model)
     * @param arg The argument passed by the observable (update message)
     */
    @Override
    public void update(Observable o, Object arg) {
        setChanged();
        notifyObservers(arg);
    }

    /**
     * Gets the starting position of the maze.
     *
     * @return Position object representing the start
     */
    public Position getStartPosition() {
        return model.getMaze().getStartPosition();
    }

    /**
     * Gets the goal position of the maze.
     *
     * @return Position object representing the goal
     */
    public Position getGoalPosition() {
        return model.getMaze().getGoalPosition();
    }

    /**
     * Interprets key input and requests the model to move the player accordingly.
     *
     * @param keyEvent Key input event from the user
     */
    public void movePlayer(KeyEvent keyEvent) {
        MovementDirection direction;
        switch (keyEvent.getCode()) {
            case NUMPAD8, UP -> direction = MovementDirection.UP;
            case NUMPAD2, DOWN -> direction = MovementDirection.DOWN;
            case NUMPAD4, LEFT -> direction = MovementDirection.LEFT;
            case NUMPAD6, RIGHT -> direction = MovementDirection.RIGHT;
            default -> {
                return;
            }
        }
        model.updatePlayerLocation(direction);
    }

    /**
     * Requests the model to save the current maze to a file.
     *
     * @param name The filename to save the maze under
     */
    public void saveMaze(String name){
        model.saveMaze(name);
    }
}

