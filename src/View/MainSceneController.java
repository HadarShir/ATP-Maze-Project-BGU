package View;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static View.MyViewController.isMusic;

public class MainSceneController implements Initializable {
    public Stage currStage;

    public Button Play_button;
    public TextField RowText;
    public TextField ColText;
    public ImageView mainImageView;
    public AnchorPane mainPane;
    public RadioButton rb1;
    public RadioButton rb2;
    public RadioButton rb3;

    public static MediaPlayer backgroundMusicPlayer;

    /**
     * Plays a mouse click sound if music is enabled in the settings.
     */
    public void mouseAudio(){
        if (isMusic()){
            Media mouseClicked = new Media(getClass().getResource("/music/Click.mp3").toString());
            MediaPlayer mediaPlayer2 = new MediaPlayer(mouseClicked);
            mediaPlayer2.setCycleCount(1);
            mediaPlayer2.play();
            mediaPlayer2.setVolume(0.3);
        }
    }

    /**
     * Handles the Play button logic:
     * - Validates character selection and input dimensions
     * - Sets maze configuration
     * - Transitions to the maze view scene
     *
     * @param event The action event triggered by the button click
     * @throws IOException If FXML loading fails
     */
    public void playButton(ActionEvent event) throws IOException{
        mouseAudio();
        if (rb1.isSelected())
            MazeDisplay.setCharacterColor(1);
        else if (rb2.isSelected())
            MazeDisplay.setCharacterColor(2);
        else if (rb3.isSelected())
            MazeDisplay.setCharacterColor(3);
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Character not chosen");
            alert.setContentText("Please choose your character");
            alert.setGraphic(null);
            alert.setHeaderText("");
            alert.show();
            return;
        }
        if ((isNumeric(RowText.getText()) && isNumeric(ColText.getText())) && (Integer.parseInt(RowText.getText()) >= 2 && Integer.parseInt(ColText.getText()) >= 2)){
            MyViewController.setMazeRows(Integer.parseInt(RowText.getText()));
            MyViewController.setMazeCols(Integer.parseInt(ColText.getText()));
            MyViewController.setMazeType(true);
            Parent root2 = FXMLLoader.load(getClass().getResource("/View/MyView.fxml"));
            Play_button.getScene().setRoot(root2);
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Wrong dimensions");
            alert.setContentText("Please enter whole numbers above 2 in the text fields");
            alert.setGraphic(null);
            alert.setHeaderText("");
            alert.show();
            return;
        }


    }

    /**
     * Utility method to check whether a given string is a numeric value.
     *
     * @param str The string to check
     * @return true if the string can be parsed as a number, false otherwise
     */
    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    /**
     * Starts background menu music if not already playing.
     */
    public static void startMenuMusic() {
        try {
            if (backgroundMusicPlayer == null) {
                Media music = new Media(MainSceneController.class.getResource("/music/ArcadeSound.mp3").toString());
                backgroundMusicPlayer = new MediaPlayer(music);
                backgroundMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                backgroundMusicPlayer.setVolume(0.2);
            }
            if (backgroundMusicPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
                backgroundMusicPlayer.play();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops the menu background music if it is currently playing.
     */
    public static void stopMenuMusic() {
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.stop();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mainImageView.fitWidthProperty().bind(mainPane.widthProperty());
        mainImageView.fitHeightProperty().bind(mainPane.heightProperty());
        startMenuMusic();
    }
}
