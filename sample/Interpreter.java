package sample;

import java.util.Arrays;

public class Interpreter {

    public static boolean isError(String text){
        if (text.equals("ERR")){
            return true;
        }else if(Arrays.asList(text.split("0")).get(0).equals("ERR")){
            return true;
        }else if (text.length()<3){
            return true;
        }
        return false;
    }

    public static boolean rejectOrAcknowledge(String text){
        if (text.equals("ACK")){
            return true;
        }else if(Arrays.asList(text.split("0")).get(0).equals("ACK")){
            return true;
        }
        return false;
    }
}
