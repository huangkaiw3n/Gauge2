package org.gauge;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by Kaiwen on 19/3/2015.
 */
public class UserDB {

    private Hashtable<String, User> userData;
    private String csvPath;//Eg "C:\\Users\\Kaiwen\\Desktop\\test.csv";

    public UserDB(String csvPath) {
        userData = new Hashtable<String, User>();
        this.csvPath = csvPath;
    }

    /**
     * Adds username as key to userData table, with User class as value.
     *
     * @param username key for adding into userData
     * @param u1       User class with username, password, email, ip
     * @param forceAdd if asserted, overwrite existing user even if key exists
     * @return bool whether added
     * @throws NullPointerException - if username is null
     */
    public boolean add(String username, User u1, boolean forceAdd) {
        if (userData.containsKey(username) && !forceAdd)
            return false;

        userData.put(username, u1);
        return true;
    }

    /**
     * Deletes entry with username from userData table
     *
     * @param username key for removal from userData
     * @return bool whether removed
     * @throws NullPointerException - if username is null
     */
    public boolean delete(String username) {
        if (userData.remove(username) != null)
            return true;
        else
            return false;
    }

    public boolean toCSV() throws java.io.IOException{
        CSVWriter writer = new CSVWriter(new FileWriter(csvPath));
        List<String[]> csvData = new ArrayList<String[]>();

        for(Map.Entry<String, User> it : userData.entrySet()){
            User u1 = it.getValue();
            csvData.add(new String[] {u1.getUsername(), u1.getPassword()
            , u1.getEmail()});
        }
        writer.writeAll(csvData);
        writer.close();
        return true;
    }

    public boolean fromCSV() throws java.io.IOException{
        CSVReader reader = new CSVReader(new FileReader(csvPath));
        String [] row = null;

        while((row = reader.readNext()) != null) {
            User aUser = new User(row[0],row[1],row[2]);
            this.add(row[0], aUser, false);
        }
        reader.close();
        return true;
    }

    public boolean authenticate(String username, String password){
        if(!userData.containsKey(username))
            return false;
        if(userData.get(username).getPassword().compareTo(password) == 0)
            return true;
        else
            return false;
    }

    public JSONArray toJSONArray(){
        JSONArray users = new JSONArray();

        for(Map.Entry<String, User> it : userData.entrySet()){
            User u1 = it.getValue();
            users.put(u1.toJSON());
        }
        return users;
    }

}
