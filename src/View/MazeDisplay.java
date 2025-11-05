package View;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import java.util.HashSet;
import java.util.Set;
import java.io.InputStream;

public class MazeDisplay extends Canvas {

    private Maze maze;
    private Solution solution;
    private int playerRow = 0;
    private int playerCol = 0;
    Image currentImage = new Image(getImageResourceAsStream("images/Red.png"));
    public static int CharColor = 1;
    private double cellHeight,cellWidth;
    StringProperty imageFileNameWall = new SimpleStringProperty("images/WallBlock.png");
    private Set<Position> playerPath = new HashSet<>();
    private boolean showPlayerPath = true;



    /**
     * Sets the character color based on the selected value (1 = Red, 2 = Blue, 3 = Yellow).
     *
     * @param charColor The selected character color code
     */
    public static void setCharacterColor(int charColor) { MazeDisplay.CharColor = charColor; }

    /**
     * Resizes the canvas and redraws its content.
     *
     * @param width  The new width of the canvas
     * @param height The new height of the canvas
     */
    @Override
    public void resize(double width, double height)
    {
        super.setWidth(width);
        super.setHeight(height);
        draw();
    }

    /**
     * Updates the player's position on the maze and redraws the canvas.
     * Also saves the player's path if enabled.
     *
     * @param row New player row index
     * @param col New player column index
     */
    public void setPlayerPosition(int row, int col) {
        if (maze != null && (playerRow != 0 || playerCol != 0)) {
            Position start = maze.getStartPosition();
            Position goal = maze.getGoalPosition();
            Position previousPosition = new Position(playerRow, playerCol);
            Position nextPosition = new Position(playerRow, playerCol);
            if (!previousPosition.equals(start) && !nextPosition.equals(goal)) {
                playerPath.add(previousPosition);
            }
        }

        if (CharColor == 1){
            currentImage = new Image(getImageResourceAsStream("images/Red.png"));
        }
        else if (CharColor == 2){
            currentImage = new Image(getImageResourceAsStream("images/Blue.png"));
        }
        else{
            currentImage = new Image(getImageResourceAsStream("images/Yellow.png"));
        }

        this.playerRow = row;
        this.playerCol = col;

        draw();
    }

    /**
     * Sets the maze solution to display and redraws the canvas.
     *
     * @param solution The maze solution
     */
    public void setSolution(Solution solution){
        this.solution = solution;
        draw();
    }

    /**
     * Sets the maze object and redraws it.
     *
     * @param maze The Maze to be displayed
     */
    public void drawMaze(Maze maze) {
        this.maze = maze;
        draw();
    }

    /**
     * Draws the entire maze including walls, solution path, player, and visited path.
     * Called internally whenever the display needs updating.
     */
    private void draw() {
        if(maze != null){
            int[][] mazeBody = maze.getMazeArray();
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            int rows = mazeBody.length;
            int cols = mazeBody[0].length;

            cellHeight = canvasHeight / rows;
            cellWidth = canvasWidth / cols;

            GraphicsContext graphicsContext = getGraphicsContext2D();
            graphicsContext.clearRect(0, 0, canvasWidth, canvasHeight);

            drawMazeWalls(graphicsContext, cellHeight, cellWidth, rows, cols);
            if(solution != null)
                drawSolution(graphicsContext, cellHeight, cellWidth);
            drawPlayer(graphicsContext, cellHeight, cellWidth);
            if(showPlayerPath)
                drawPlayerPath(graphicsContext, cellHeight, cellWidth);
        }
    }

    /**
     * Draws the solution path on the maze using a specific image or yellow overlay.
     *
     * @param graphicsContext The canvas graphics context
     * @param cellHeight Height of each cell
     * @param cellWidth Width of each cell
     */
    private void drawSolution(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        if (solution == null) {
            return;
        }
        java.util.List<AState> path = solution.getSolutionPath();
        if (path == null || path.size() < 1) {
            return;
        }
        Image solutionImage = null;
        try {
            solutionImage = new Image(getImageResourceAsStream("images/Solution.png"));
        } catch (Exception e) {
            solutionImage = null;
        }
        for (AState state : path) {
            Object obj = state.getObject();
            if (!(obj instanceof Position)) continue;
            Position pos = (Position) obj;
            drawSolutionCell(graphicsContext, pos.getRowIndex(), pos.getColumnIndex(), cellHeight, cellWidth, solutionImage);
        }
    }

    /**
     * Draws the player at the current position on the canvas.
     *
     * @param graphicsContext The canvas graphics context
     * @param cellHeight Height of each cell
     * @param cellWidth Width of each cell
     */
    private void drawPlayer(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        double x = getPlayerCol() * cellWidth;
        double y = getPlayerRow() * cellHeight;

        if(currentImage == null)
            graphicsContext.fillRect(x, y, cellWidth, cellHeight);
        else{
            graphicsContext.drawImage(currentImage, x, y, cellWidth, cellHeight);
        }
    }

    /**
     * Draws the maze walls and start/goal points using images.
     *
     * @param graphicsContext The canvas graphics context
     * @param cellHeight Height of each cell
     * @param cellWidth Width of each cell
     * @param rows Number of maze rows
     * @param cols Number of maze columns
     */
    private void drawMazeWalls(GraphicsContext graphicsContext, double cellHeight, double cellWidth, int rows, int cols) {
        int[][] mazeBody = maze.getMazeArray();

        graphicsContext.setFill(Color.RED);
        javafx.scene.image.Image wallImage = null;

        wallImage = new Image(getImageResourceAsStream(getImageFileNameWall()));
        if (wallImage == null) {
            System.out.println("There is no wall image file");
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if(mazeBody[i][j] == 1){
                    double x = j * cellWidth;
                    double y = i * cellHeight;
                    if(wallImage == null) {
                        graphicsContext.fillRect(x, y, cellWidth, cellHeight);
                    }
                    else
                        graphicsContext.drawImage(wallImage, x, y, cellWidth, cellHeight);
                }
            }
        }
        Image startingPoint = new Image(getImageResourceAsStream("images/Start.png"));
        Image endPoint = new Image(getImageResourceAsStream("images/End.png"));

        double x,y;
        x = maze.getStartPosition().getColumnIndex()*cellWidth;
        y = maze.getStartPosition().getRowIndex()*cellHeight;
        graphicsContext.drawImage(startingPoint,x,y,cellWidth,cellHeight);
        x = maze.getGoalPosition().getColumnIndex()*cellWidth;
        y = maze.getGoalPosition().getRowIndex()*cellHeight;
        graphicsContext.drawImage(endPoint ,x,y,cellWidth,cellHeight);

    }

    /**
     * Draws the path previously taken by the player.
     *
     * @param graphicsContext The canvas graphics context
     * @param cellHeight Height of each cell
     * @param cellWidth Width of each cell
     */
    private void drawPlayerPath(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        if (playerPath == null || playerPath.isEmpty()) {
            return;
        }
        Image pathImage = new Image(getImageResourceAsStream("images/Path.png"));
        for (Position pos : playerPath) {
            graphicsContext.drawImage(pathImage, pos.getColumnIndex()*cellWidth, pos.getRowIndex()*cellHeight, cellWidth, cellHeight);
        }
    }

    /**
     * Clears the player's path and refreshes the display.
     */
    public void clearPlayerPath() {
        if (playerPath != null) playerPath.clear();
        draw();
    }

    /**
     * Enables or disables the display of the player's path.
     *
     * @param show Whether to show the path
     */
    public void setShowPlayerPath(boolean show) {
        this.showPlayerPath = show;
        draw();
    }

    /**
     * Returns whether the player's path is currently shown.
     *
     * @return true if path is shown, false otherwise
     */
    public boolean isShowPlayerPath() {
        return showPlayerPath;
    }

    /**
     * Indicates that this canvas can be resized.
     *
     * @return true, indicating the canvas is resizable
     */
    @Override
    public boolean isResizable(){
        return true;
    }

    /**
     * Returns the minimum height allowed for this canvas.
     *
     * @param width The width context
     * @return The minimum height (0)
     */
    @Override
    public double minHeight(double width)
    {
        return 0;
    }

    /**
     * Returns the maximum height allowed for this canvas.
     *
     * @param width The width context
     * @return Maximum height (infinite)
     */
    @Override
    public double maxHeight(double width)
    {
        return Double.MAX_VALUE;
    }

    /**
     * Returns the preferred height of the canvas.
     *
     * @param width The width context
     * @return Preferred height (400)
     */
    @Override
    public double prefHeight(double width)
    {
        return 400;
    }

    /**
     * Returns the minimum width allowed for this canvas.
     *
     * @param height The height context
     * @return The minimum width (0)
     */
    @Override
    public double minWidth(double height)
    {
        return 0;
    }

    /**
     * Returns the maximum width allowed for this canvas.
     *
     * @param height The height context
     * @return Maximum width (infinite)
     */
    @Override
    public double maxWidth(double height)
    {
        return Double.MAX_VALUE;
    }

    /**
     * Returns the preferred width of the canvas.
     *
     * @param height The height context
     * @return Preferred width (600)
     */
    @Override
    public double prefWidth(double height)
    {
        return 600;
    }

    /**
     * Loads an image resource from the classpath.
     *
     * @param imagePath The relative path to the image resource
     * @return InputStream for the image file
     */
    private InputStream getImageResourceAsStream(String imagePath){
        return getClass().getClassLoader().getResourceAsStream(imagePath);
    }

    /**
     * Gets the player's current column index.
     *
     * @return Player's column
     */
    public int getPlayerCol(){
        return playerCol;
    }

    /**
     * Gets the player's current row index.
     *
     * @return Player's row
     */
    public int getPlayerRow(){
        return playerRow;
    }

    /**
     * Gets the file name of the wall image.
     *
     * @return Path to the wall image file
     */
    public String getImageFileNameWall() {
        return imageFileNameWall.get();
    }

    /**
     * Draws a single cell of the solution path.
     * Uses an image if available, otherwise fills it with a yellow overlay.
     *
     * @param graphicsContext Graphics context of the canvas
     * @param row Row index of the cell
     * @param col Column index of the cell
     * @param cellHeight Height of the cell
     * @param cellWidth Width of the cell
     * @param solutionImage Image used for solution visualization (optional)
     */
    private void drawSolutionCell(GraphicsContext graphicsContext, int row, int col, double cellHeight, double cellWidth, Image solutionImage) {
        if (solutionImage != null) {
            graphicsContext.drawImage(solutionImage, col * cellWidth, row * cellHeight, cellWidth, cellHeight);
        } else {
            graphicsContext.setFill(Color.YELLOW);
            graphicsContext.setGlobalAlpha(0.7);
            graphicsContext.fillRect(col * cellWidth, row * cellHeight, cellWidth, cellHeight);
            graphicsContext.setGlobalAlpha(1.0);
        }
    }

    /**
     * Returns the current solution object for the maze.
     *
     * @return Solution of the maze
     */
    public Solution getSolution() {
        return solution;
    }

}
