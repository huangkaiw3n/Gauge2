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

    public UserDB() {
        userData = new Hashtable<String, User>();
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
        String csv = "C:\\Users\\Kaiwen\\Desktop\\test.csv";
        CSVWriter writer = new CSVWriter(new FileWriter(csv));
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
        String csv = "C:\\Users\\Kaiwen\\Desktop\\test.csv";
        CSVReader reader = new CSVReader(new FileReader(csv));
        String [] row = null;

        while((row = reader.readNext()) != null) {
            User aUser = new User(row[0],row[1],row[2]);
            this.add(row[0], aUser, false);
        }
        reader.close();
        return true;
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
