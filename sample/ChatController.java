package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

import java.util.*;

public class ChatController {

    Client client;
    List<String> loginRooms  = new ArrayList<>();
    String currentRoom = "";

    final KeyCombination keyCombinationShiftEnter = new KeyCodeCombination(
            KeyCode.ENTER, KeyCombination.SHIFT_DOWN);

    @FXML
    ComboBox roomsContactsComboBox;

    @FXML
    ComboBox roomOrContactComboBox;

    @FXML
    TextArea myMessageTextArea;

    @FXML
    TextArea chatTextArea;

    @FXML
    Button sendPublishButton;

    @FXML
    Button refreshButton;

    Thread checker;
    Timer timerChecker = new Timer();

    public void initialize() {
        myMessageTextArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (keyCombinationShiftEnter.match(event)) {
                    myMessageTextArea.setText(myMessageTextArea.getText() + "\n");
                    event.consume();
                    myMessageTextArea.requestFocus();
                    myMessageTextArea.end();
                } else if (event.getCode().equals(KeyCode.CONTROL)) {
                    checking();
                } else if (event.getCode().equals(KeyCode.ENTER)) {
                    onSendPublish();
                }
            }
        });
    }


    public void endOfCheaking() {
        timerChecker.cancel();
        timerChecker = new Timer();
        checker = null;
    }

    public void print(Message message) {
        if (chatTextArea.getText().length() < 1) {
            chatTextArea.setText(message.getNick() + ": " + message.getMessage());
        } else {
            chatTextArea.setText(chatTextArea.getText() + message.getNick() + ": " + message.getMessage());
        }
    }

    public void checkingByPeriodTime() {
        timerChecker.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checking();
            }
        }, 0, 1000);
    }

    public void checking() {
        List<Message> messages = new ArrayList<>();
        Runnable task = new Runnable() {
            Boolean error = false;
            Boolean clearChat = false;
            @Override
            public void run() {
                Thread thisThread = Thread.currentThread();
                if (checker == thisThread) {
                    if (roomOrContactComboBox.getValue().equals("rooms")) {
                        if (client.isConnected()) {
                            if (loginRooms.contains(roomsContactsComboBox.getValue().toString())){
                                if(!currentRoom.equals(roomsContactsComboBox.getValue().toString())){
                                    clearChat = true;
                                }
                                client.getSender().check(roomsContactsComboBox.getValue().toString());
                                error = Interpreter.isError(client.getReceiver().getLastResponse());
                                if (!error) {
                                    messages.addAll(client.getReceiver().checkingRoomResponse());
                                }
                            }
                        }
                    } else if (roomOrContactComboBox.getValue().equals("contacts")) {
                        if (client.isConnected()) {
                            client.getSender().check();
                            error = Interpreter.isError(client.getReceiver().getLastResponse());
                            if (!error) {
                                messages.addAll(client.getReceiver().checkingResponse());
                            }
                        }
                    } else {
                        messages.clear();
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            roomsContactsComboBox.setDisable(false);
                            if (error) {
                                popOutRecivedError();
                            }
                            if (!messages.isEmpty()) {
                                if (messages.get(0).getNick().equals("ERR")) {
                                    client.getSender().error();
                                    popOutRecivedError();
                                }
                                if (clearChat){
                                    chatTextArea.setText("");
                                }
                                for (Message message : messages) {
                                    if (message.getMessage().length() > 0) {
                                        print(message);
                                        chatTextArea.setText(chatTextArea.getText() + "\n");
                                    }
                                }
                            }
                        }
                    });
                }
            }
        };
        checker = new Thread(task);
        checker.start();

    }

    public void refreshList() {
        checker = null;
        endOfCheaking();
        Runnable task = new Runnable() {
            Boolean error = false;
            @Override
            public void run() {
                List<String> list = new ArrayList<>();
                list.clear();
                if (roomOrContactComboBox.getValue().equals("rooms")) {
                    roomsContactsComboBox.getEditor().setText("");
                    if (client.isConnected()) {
                        client.getSender().listOfRooms();
                        error = Interpreter.isError(client.getReceiver().getLastResponse());
                        list.addAll(client.getReceiver().getRoomsResponse());
                    }
                }
                if (roomOrContactComboBox.getValue().equals("contacts")) {
                    if (client.isConnected()) {
                        client.getSender().list();
                        error = Interpreter.isError(client.getReceiver().getLastResponse());
                        list.addAll(client.getReceiver().getContactsResponse(client.getUserName()));
                        for (String s : list) {
                            System.out.println(s);
                        }
                    }
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        roomsContactsComboBox.setDisable(false);
                        if (error) {
                            popOutRecivedError();
                        }
                        roomsContactsComboBox.setDisable(false);
                        roomsContactsComboBox.setItems(FXCollections.observableArrayList(list));
                        roomsContactsComboBox.setValue(""); // jesli by nie dzialolo
                        if (roomOrContactComboBox.getValue().equals("rooms")) {
                            roomsContactsComboBox.setEditable(true);
                        } else {
                            roomsContactsComboBox.setEditable(false);
                        }
                    }
                });
            }
        };
        checker = new Thread(task);
        checker.start();
    }

    public void comboBoxValue() {
        refreshButton.setDisable(false);
        chatTextArea.setText("");
        checker = null;
        endOfCheaking();
        roomsContactsComboBox.setEditable(false);
        roomsContactsComboBox.setDisable(true);
        refreshList();
    }


    public void setClient(Client client) {
        this.client = client;
        roomOrContactComboBox.setItems(FXCollections.observableArrayList("rooms", "contacts"));
    }

    public void onSendPublish() {
        Runnable task = new Runnable() {
            Boolean error = false;
            Boolean rejOrAck = false;
            @Override
            public void run() {
                if (myMessageTextArea.getText().length() > 0) {
                    Message message = new Message(client.getUserName());
                    message.setMessageSend(myMessageTextArea.getText());
                    boolean isRoomsContactsComboBoxEmpty;
                    if (roomOrContactComboBox.getValue().equals("rooms")) {
                        isRoomsContactsComboBoxEmpty = roomsContactsComboBox.getEditor().getText().isEmpty();
                        if (!isRoomsContactsComboBoxEmpty) {
                            String room = roomsContactsComboBox.getValue().toString();
                            if (room.length() > 0) {
                                client.getSender().publish(room, message);
                                error = Interpreter.isError(client.getReceiver().getLastResponse());
                                rejOrAck = Interpreter.rejectOrAcknowledge(client.getReceiver().getLastResponse());
                            }
                        }
                    } else if (roomOrContactComboBox.getValue().equals("contacts")) {
                        isRoomsContactsComboBoxEmpty = roomsContactsComboBox.getSelectionModel().isEmpty();
                        if (!isRoomsContactsComboBoxEmpty) {
                            String contact = roomsContactsComboBox.getValue().toString();
                            if (contact.length() > 0) {
                                client.getSender().sendMessage(contact, message);
                                error = Interpreter.isError(client.getReceiver().getLastResponse());
                                rejOrAck = Interpreter.rejectOrAcknowledge(client.getReceiver().getLastResponse());
                            }
                        }
                    }
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        roomsContactsComboBox.setDisable(false);
                        if (error) {
                            popOutRecivedError();
                        }
                        if (rejOrAck) {
                            if (roomOrContactComboBox.getValue().equals("rooms")) {
                                if (!currentRoom.equals(roomsContactsComboBox.getValue().toString())){
                                    chatTextArea.setText("");
                                }
                                if(!loginRooms.contains(roomsContactsComboBox.getValue().toString())){
                                    loginRooms.add(roomsContactsComboBox.getValue().toString());
                                }
                                currentRoom = roomsContactsComboBox.getValue().toString();
                            }
                            chatTextArea.setText(chatTextArea.getText() + client.getUserName() + ": " + myMessageTextArea.getText());
                            myMessageTextArea.setText("");
                        } else {
                            myMessageTextArea.setText("");
                            myMessageTextArea.setText("Nie udalo sie wyslac wiadomosci");
                        }
                    }
                });
            }
        };
        new Thread(task).start();
        checkingByPeriodTime();
    }

    public void popOutRecivedError() {
        timerChecker.cancel();
        checker = null;
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
