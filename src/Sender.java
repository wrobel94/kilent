package sample;

import java.io.IOException;
import java.io.PrintWriter;

public class Sender {
    Connection connection;

    public Sender(Connection connection) {
        this.connection = connection;
    }


    public void hello(String version) {
        connection.send(SenderMessageMaker.hello(version));
    }

    public void login(String login) {
        connection.send(SenderMessageMaker.login(login));
    }

    public void list() {
        connection.send("LST");
    }

    public void sendMessage(String contact, Message message) {
        connection.send(SenderMessageMaker.send(contact, message));
    }

    public void check() {
        connection.send("CHK");
    }

    public void check(String room) {
        connection.send(SenderMessageMaker.check(room));
    }

    public void publish(String room, Message message) {
        connection.send(SenderMessageMaker.publish(room, message));
    }

    public void logout() {
        connection.send("LGO");
    }

    public void listOfRooms() {
        connection.send("LOR");
    }

    public void error() {
        connection.send("ERR");
    }

}
