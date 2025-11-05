package Model;

import Client.Client;
import Client.IClientStrategy;
import IO.MyCompressorOutputStream;
import IO.MyDecompressorInputStream;
import algorithms.mazeGenerators.Maze;
import algorithms.search.AState;
import algorithms.search.Solution;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class MyModel extends Observable implements IModel{
    private Maze maze;
    private int playerRow;
    private int playerCol;
    private Solution solution;

    /**
     * Generates a new maze using the server at port 5400.
     * Initializes the player position at the maze's start.
     *
     * @param rows Number of rows in the maze
     * @param cols Number of columns in the maze
     */
    @Override
    public void generateMaze(int rows, int cols) {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{rows, cols}; toServer.writeObject(mazeDimensions); //send maze dimensions to server
                        toServer.flush();
                        byte[] compressedMaze = (byte[]) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[(rows * cols) + 100 /*CHANGE SIZE ACCORDING TO YOU MAZE SIZE*/]; //allocating byte[] for the decompressed maze -
                        is.read(decompressedMaze); //Fill decompressedMaze with bytes
                        maze = new Maze(decompressedMaze);
                        playerRow = maze.getStartPosition().getRowIndex();
                        playerCol = maze.getStartPosition().getColumnIndex();
                    } catch (Exception e) { e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) { e.printStackTrace();
        }
        setChanged();
        notifyObservers("maze generated");
        movePlayer(playerRow,playerCol);
    }

    /**
     * Returns the currently generated maze.
     *
     * @return Maze object
     */
    @Override
    public Maze getMaze() {
        return maze;
    }

    /**
     * Sends the current maze to the solving server (port 5401)
     * and retrieves the solution.
     * Notifies observers once the solution is received.
     */
    @Override
    public void solveMaze() {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new
                    IClientStrategy() {
                        @Override
                        public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                            try {
                                ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                                ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                                toServer.flush();
                                toServer.writeObject(maze); //send maze to server
                                toServer.flush();
                                Solution mazeSolution = (Solution) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                                ArrayList<AState> mazeSolutionSteps = mazeSolution.getSolutionPath();
                                //solution = new Solution(mazeSolutionSteps);
                                solution = new Solution(mazeSolution.getSolutionPath());
                            } catch (Exception e) { e.printStackTrace();
                            }
                        }
                    });
            client.communicateWithServer();
        } catch (UnknownHostException e) { e.printStackTrace();
        }
        //since we have the solve maze server, we will have to ask it to solve it.
        setChanged();
        notifyObservers("maze solved");
    }

    /**
     * Returns the solution for the current maze.
     *
     * @return Solution object
     */
    @Override
    public Solution getSolution() {
        return solution;
    }

    /**
     * Gets the player's current row index.
     *
     * @return Player's row
     */
    @Override
    public int getPlayerRow() {
        return playerRow;
    }

    /**
     * Gets the player's current column index.
     *
     * @return Player's column
     */
    @Override
    public int getPlayerCol() {
        return playerCol;
    }

    /**
     * Sets the player's row index.
     *
     * @param playerRow New row index
     */
    public void setPlayerRow(int playerRow) {
        this.playerRow = playerRow;
    }

    /**
     * Sets the player's column index.
     *
     * @param playerCol New column index
     */
    public void setPlayerCol(int playerCol) {
        this.playerCol = playerCol;
    }

    /**
     * Assigns an observer to the model.
     *
     * @param o Observer to register
     */
    @Override
    public void assignObserver(Observer o) {
        this.addObserver(o);
    }

    /**
     * Updates the player's location in the maze based on the given direction.
     * Performs boundary and wall checks before moving.
     *
     * @param direction Direction to move the player (UP, DOWN, LEFT, RIGHT)
     */
    @Override
    public void updatePlayerLocation(MovementDirection direction) {
        int[][] mazeBody = maze.getMazeArray();
        switch (direction){
            case UP -> {
                if (playerRow > 0 && (mazeBody[playerRow - 1][playerCol]  == 0))
                    movePlayer(playerRow - 1, playerCol);
            }
            case DOWN -> {
                if (playerRow < mazeBody.length - 1 && (mazeBody[playerRow + 1][playerCol]  == 0) )
                    movePlayer(playerRow + 1, playerCol);
            }
            case LEFT -> {
                if (playerCol > 0 && (mazeBody[playerRow][playerCol- 1]  == 0))
                    movePlayer(playerRow, playerCol - 1);
            }
            case RIGHT -> {
                if (playerCol < mazeBody[0].length - 1 && (mazeBody[playerRow ][playerCol + 1]  == 0))
                    movePlayer(playerRow, playerCol + 1);
            }
        }
    }

    /**
     * Moves the player to the given row and column, then notifies observers.
     *
     * @param playerRow New row index
     * @param playerCol New column index
     */
    private void movePlayer(int playerRow, int playerCol) {
        this.playerRow = playerRow;
        this.playerCol = playerCol;
        setChanged();
        notifyObservers("player moved");
    }

    /**
     * Saves the current maze to a file using compression.
     *
     * @param name The name of the file to save the maze under
     */
    public void saveMaze(String name){
        try {
            File theDir = new File("Saved_Mazes");
            if (!theDir.exists()) {
                theDir.mkdirs();
            }
            byte[] list = maze.toByteArray();
            String filename = "Saved_Mazes/" + name;
            OutputStream out = new MyCompressorOutputStream(new FileOutputStream("tempMaze.maze"));
            out.write(list);
            out.flush();
            Path p = Paths.get("tempMaze.maze");
            byte[] compressedList = Files.readAllBytes(p);

            FileOutputStream fos = new FileOutputStream(filename);

            try {
                fos.write(compressedList);
            } catch (Throwable var13) {
                try {
                    fos.close();
                } catch (Throwable var12) {
                    var13.addSuppressed(var12);
                }
                throw var13;
            }
            fos.close();
        } catch (IOException var14) {
            var14.printStackTrace();
        }
    }

}
