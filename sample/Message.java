package sample;

public class Message {
    private String message;

    public Message(String nick) {
        this.nick = nick;
    }

    public String getNick() {
        return nick;
    }

    private String nick;
    private String enter = "/D";

    public void setMessageSend(String message) {
        this.message = convertToMessage(message);
    }

    public void setMessageRecive(String message) {
        this.message = decodeMessage(message);
    }

    private String convertToMessage(String message) {
        String[] msg = message.split("\n");
        System.out.println(msg.length);
        int n=1;
        String convertmessage = "";
        for (String i : msg) {
            if(n<msg.length){
                convertmessage = convertmessage + i + enter;
            }else {
                System.out.println(n);
                convertmessage = convertmessage + i;
            }
            n++;
        }
        return convertmessage;
    }

    private String decodeMessage(String message) {
        String[] msg = message.split(enter);
        String convertmessage = "";
        int n = 1;
        for (String i : msg) {
            if(n<msg.length) {
                convertmessage = convertmessage + i + "\n";
            }else {
                convertmessage = convertmessage + i;
            }
        }
        return convertmessage;
    }


    public String getMessage() {
        return message;
    }
}
