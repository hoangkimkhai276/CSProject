package group5;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
public class Database {
    // read Json

    static JSONParser jsonParser = new JSONParser();
    static FileReader reader;
    static ArrayList<String> validIds = new ArrayList<>();
    static ArrayList<String> encryptedPasswords = new ArrayList<>();
    static {
        try {
            reader = new FileReader("src\\group5\\clients.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String SHA_256(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] passwordInBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, passwordInBytes);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 32)
        {
            hexString.insert(0, '0');
        }

        return hexString.toString();
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
                String password = (String) c.get("password");
                encryptedPasswords.add(SHA_256(password));
                validIds.add(curId);
            }
        } catch (IOException | ParseException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
