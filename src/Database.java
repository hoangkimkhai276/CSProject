import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Database {
    // read Json
    //
    //
    static JSONParser jsonParser = new JSONParser();
    static FileReader reader;
    static ArrayList<String> validIds = new ArrayList<>();
    static {
        try {
            reader = new FileReader("src\\clients.json");
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
            for (Object client : CLIENT_LIST) {
                JSONObject c = (JSONObject) client;
                String curId = (String) c.get("id");
                validIds.add(curId);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}
