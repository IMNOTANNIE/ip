package yuki.gui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Displays one message together with the speaker's avatar.
 */
public class DialogBox extends HBox {
    /** Displays the message text. */
    @FXML
    private Label dialog;
    /** Displays the avatar of the message's speaker. */
    @FXML
    private ImageView displayPicture;

    private DialogBox(String message, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load a dialog box.", e);
        }

        dialog.setText(message);
        displayPicture.setImage(image);
    }

    /**
     * Creates a dialog box for a message sent by the user.
     *
     * @param message Message to display.
     * @param image Avatar of the user.
     * @return A right-aligned dialog box for the user.
     */
    public static DialogBox getUserDialog(String message, Image image) {
        return new DialogBox(message, image);
    }

    /**
     * Creates a dialog box for a response sent by Yuki.
     *
     * @param message Message to display.
     * @param image Avatar of Yuki.
     * @return A left-aligned dialog box for Yuki.
     */
    public static DialogBox getYukiDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.flip();
        return dialogBox;
    }

    /** Places the avatar on the left and aligns the dialog box to the left. */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }
}
