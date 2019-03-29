package sample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Receiver {

    Connection connection;

    public Receiver(Connection connection) {
        this.connection = connection;
    }

    public List<String> getRoomsResponse() {
        String response = getLastResponse();
        List<String> rooms = new ArrayList<>();
        rooms.addAll(Arrays.asList(response.split("#")));
        if (rooms.get(0).equals("ROR")){
            rooms.remove(0);
        }else {
            rooms.clear();
        }
        return rooms;
    }

    public String getLastResponse() {
        return connection.getLastResponse();
    }

    public List<Message> checkingResponse() {
        String response = getLastResponse();
        List<String> splited = new ArrayList<>();
        List<Message> messages = new ArrayList<>();
        splited.addAll(Arrays.asList(response.split("#")));
        if (splited.get(0).equals("CHR")){
            splited.remove(0);
            if (splited.size()%2==0){
                for (int i = 0;i<splited.size();i+=2){
                    Message currentMessage = new Message(splited.get(i));
                    currentMessage.setMessageRecive(splited.get(i+1));
                    messages.add(currentMessage);
                }
            }
        }else {
            Message error = new Message("ERR");
            error.setMessageRecive(response);
            messages.add(error);
            splited.clear();
        }
        return messages;
    }


    public List<Message> checkingRoomResponse() {
        String response = getLastResponse();
        List<String> splited = new ArrayList<>();
        List<Message> messages = new ArrayList<>();
        splited.addAll(Arrays.asList(response.split("#")));
        if (splited.get(0).equals("CHR")){
            splited.remove(0);
            if (splited.size() % 2 == 0) {
                for (int i = 0; i < splited.size(); i += 2) {
                    Message currentMessage = new Message(splited.get(i));
                    currentMessage.setMessageRecive(splited.get(i + 1));
                    messages.add(currentMessage);
                }
            } else {
                Message error = new Message("ERR");
                error.setMessageRecive(response);
                messages.add(error);
                splited.clear();
            }

        }
        return messages;
    }

    public List<String> getContactsResponse(String userName) {
        List<String> contacts = new ArrayList<>();
        String response = getLastResponse();
        contacts.addAll(Arrays.asList(response.split("#")));
        if (contacts.get(0).equals("LSR")){
            contacts.remove(0);
        }else {
            contacts.clear();
        }
        for (int i = 0; i < contacts.size(); i++) {
            System.out.println(contacts.get(i));
            if (contacts.get(i).equals(userName)) {
                contacts.remove(i);
            }
        }
        return contacts;
    }

}
