package yuki.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import yuki.Yuki;

/**
 * Controls Yuki's main graphical interface.
 */
public class MainWindow extends AnchorPane {
    /** Avatar displayed beside messages sent by the user. */
    private final Image userImage = new Image(
            getClass().getResourceAsStream("/images/UserAvatar.png"));
    /** Avatar displayed beside responses sent by Yuki. */
    private final Image yukiImage = new Image(
            getClass().getResourceAsStream("/images/YukiLogo.jpg"));

    /** Provides a scrollable view of the conversation. */
    @FXML
    private ScrollPane scrollPane;
    /** Contains the dialog boxes in the conversation. */
    @FXML
    private VBox dialogContainer;
    /** Accepts commands entered by the user. */
    @FXML
    private TextField userInput;
    /** Submits the command currently in the input field. */
    @FXML
    private Button sendButton;

    /** Generates responses to commands entered through the GUI. */
    private Yuki yuki;

    /** Configures behavior that depends on controls injected from FXML. */
    @FXML
    private void initialize() {
        dialogContainer.heightProperty().addListener(
                observable -> scrollPane.setVvalue(1.0));
    }

    /**
     * Supplies the chatbot used to process commands.
     *
     * @param yuki Chatbot backing this window.
     */
    public void setYuki(Yuki yuki) {
        this.yuki = yuki;
    }

    /** Displays the user's input and Yuki's response, then clears the input field. */
    @FXML
    private void handleUserInput() {
        String userText = userInput.getText();
        String yukiText = yuki.getResponse(userText);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(userText, userImage),
                DialogBox.getYukiDialog(yukiText, yukiImage));
        userInput.clear();
    }
}
