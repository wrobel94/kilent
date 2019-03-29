package sample;

public class SenderMessageMaker {

    public static String hello(String version){
        return "HEL#" + version;
    }

    public static String check(){
        return "CHK";
    }

    public static String check(String room){
        return "CHK#" + room;
    }

    public static String send(String contact, Message message) {
        return "SND#" + contact + "#" + message.getMessage();
    }

    public static String publish(String room, Message message) {
        return "PUB#" + room + "#" + message.getMessage();
    }

    public static String login(String userName){
        return "LGN#" + userName;
    }
}
