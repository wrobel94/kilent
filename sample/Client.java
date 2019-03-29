package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Client {

    private FXMLLoader fxmlLoader;
    private Stage stage;
    private Parent root;

    private LoginController loginController;
    private ChatController chatController;

    private Sender sender;
    private Receiver receiver;
    private Connection connection;

    private String version = "100";
    private String userName;

    public String getVersion() {
        return version;
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }



    public Client(Stage primaryStage) throws IOException {
        this.stage = primaryStage;
        fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        root = fxmlLoader.load();
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.setResizable(false);
        primaryStage.show();
        LoginController loginController = (LoginController) fxmlLoader.getController();
        loginController.setClient(this,stage);
    }

    public void makeConnection(String server, Integer port) {
        connection = new Connection(server, port);
        connection.makeConnection();
    }

    public void makeSender() {
        if (isConnected()) {
            this.sender = new Sender(this.connection);
            this.receiver = new Receiver(this.connection);
        }

    }

    public boolean isConnected() {
        return connection.isConnected();
    }

    public void login() {
        fxmlLoader = new FXMLLoader(getClass().getResource("chat.fxml"));
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
        }
        stage.setTitle("Chat " + getUserName());
        stage.setScene(new Scene(root, 580, 400));
        stage.setResizable(false);
        stage.show();
        ChatController chatController = (ChatController) fxmlLoader.getController();
        chatController.setClient(this);
    }

    public void logout() {
        if (connection != null)
            connection.logout();
    }


    public Sender getSender(){
        return this.sender;
    }
}
