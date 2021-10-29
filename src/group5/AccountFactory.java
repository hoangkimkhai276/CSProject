package group5;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class AccountFactory {
    static JSONParser jsonParser = new JSONParser();
    static FileReader reader;

    static {
        try {
            reader = new FileReader("src\\group5\\clients.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static Object JSON_CLIENTS;
    static JSONArray CLIENT_LIST;

    static {
        try {
            JSON_CLIENTS = jsonParser.parse(reader);
            CLIENT_LIST = (JSONArray) JSON_CLIENTS;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        for (Object client : CLIENT_LIST) {
//            JSONObject c = (JSONObject) client;
//            String id = (String) c.get("id");
//            makeAccount(id);
//        }
//    }

    public static Account makeAccount(String id) {
        for (Object client : CLIENT_LIST) {
            JSONObject c = (JSONObject) client;
            String curId = (String) c.get("id");
            if (curId.equals(id)) {
                return makeAccount(c);
            }
        }
        return null;
    }

    private static Account makeAccount(JSONObject object) {
        String id =  getTag("id",object);
        String password = getTag("password",object);
        String serverIp = getTag("ip",(JSONObject) object.get("server"));
        String serverPort = getTag("port",(JSONObject) object.get("server"));
        JSONObject actions =(JSONObject) object.get("actions");
        String delayString = getTag("delay",actions);

        int delay = getDelaySecondFromString(delayString);

        JSONArray stepsJson = (JSONArray) actions.get("steps");

        Step[] steps = getStepsFromJson(stepsJson);

        Account account = new Account(id, password, serverIp, serverPort, delay, steps);

        return account;
    }
    private static Step[] getStepsFromJson(JSONArray stepsJson){
        int n = stepsJson.size();
        Step[] steps = new Step[n];
        int i = 0;
        for(Object step : stepsJson){
            String s = (String) step;
            String[] action = s.split(" ");
            boolean isIncrease = action[0].equals("INCREASE");
            double amount = Double.parseDouble(action[1]);
            Step ss = new Step(isIncrease, amount);
            steps[i++] = ss;
        }
        return steps;
    }

    private static int getDelaySecondFromString(String s){
        s = s.replaceAll("[^0-9]+", " ");
        String[] seconds = s.trim().split(" ");
        return Integer.parseInt(seconds[0]);
    }

    private static String getTag(String tag, JSONObject object){
        String value = (String) object.get(tag);
        if (value == null){
            try {
                throw  new WrongJsonFormatException("Tag not found");
            } catch (WrongJsonFormatException wrongJsonFormatException) {
                wrongJsonFormatException.printStackTrace();
            }
        }
        return value;
    }
}


