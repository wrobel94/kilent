package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class LoginController {

    Client client;

    @FXML
    public TextField server;

    @FXML
    public TextField port;

    @FXML
    Button loginButton;

    @FXML
    Label connectedLabel;

    @FXML
    TextField userNameTextField;

    @FXML
    Label loginLabel;

    Stage stage;

    public void initialize() {

    }

    public void setClient(Client client, Stage stage) {
        this.client = client;
        this.stage = stage;
    }

    public void login() {
        Runnable task = new Runnable() {
            Boolean error = false;
            Boolean rejOrAck = false;

            @Override
            public void run() {
                if (client.isConnected()) {
                    client.getSender().login(userNameTextField.getText());
                    error = Interpreter.isError(client.getReceiver().getLastResponse());
                    rejOrAck = Interpreter.rejectOrAcknowledge(client.getReceiver().getLastResponse());
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (client.isConnected()) {
                            if (error) {
                                popOutRecivedError();
                            } else {
                                if (rejOrAck) {
                                    client.setUserName(userNameTextField.getText());
                                    client.login();
                                } else {
                                    loginLabel.setText("Try again");
                                    loginButton.setDisable(true);
                                }
                            }
                        }
                    }
                });
            }
        };
        new Thread(task).start();
    }

    public void makeConnection() {
        Runnable task = new Runnable() {
            Boolean error = false;
            Boolean rejOrAck = false;

            @Override
            public void run() {
                client.makeConnection(server.getText(), Integer.parseInt(port.getText()));
                client.makeSender();
                if (client.isConnected()) {
                    client.getSender().hello(client.getVersion());
                    error = Interpreter.isError(client.getReceiver().getLastResponse());
                    rejOrAck = Interpreter.rejectOrAcknowledge(client.getReceiver().getLastResponse());
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (client.isConnected()) {
                            if (error) {
                                popOutRecivedError();
                            } else {
                                if (rejOrAck) {
                                    connectedLabel.setText("Connected");
                                    loginButton.setDisable(false);
                                }
                            }
                        } else {
                            connectedLabel.setText("Disconnected");
                            loginButton.setDisable(true);
                        }

                    }
                });
            }
        };
        new Thread(task).start();
    }

    public void popOutRecivedError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setContentText("Ooops, there was an error!");
        Optional<ButtonType> result = alert.showAndWait();
        if (!result.isPresent())
            Platform.exit();
        else if (result.get() == ButtonType.OK)
            Platform.exit();

    }


}
