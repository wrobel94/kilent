package sample;

import javafx.beans.binding.MapExpression;
import javafx.scene.control.Alert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Connection {
    private Server server;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String lastResponse;

    public Connection(String server, Integer port) {
        socket = new Socket();
        this.server = new Server(server,port); // pozniej do przerobienia na interfejs
    }

    public void makeConnection(){
        try {
            socket = new Socket(server.getName(), server.getPort());
            setPrintWriter();
            setBufferedReader();
        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        }
    }

    private void getResponse(){
        String response;
        try {
            response = in.readLine();
            lastResponse = response;
        } catch (IOException e) {
            response = "ERR";
            lastResponse = response;
        }
    }

    private void setBufferedReader() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        }
    }

    public boolean isConnected(){
        return socket.isConnected();
    }


    public void send(String message){
        out.print(message);
        out.flush();
        getResponse();
    }


    public String getLastResponse(){
        return lastResponse;
    }


    private void setPrintWriter() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        }
    }


    public void logout(){
        out.print("LGO");
        out.flush();
        try {
            out.close();
            in.close();
            socket.close();
        }catch(IOException e){

        }
    }
}
