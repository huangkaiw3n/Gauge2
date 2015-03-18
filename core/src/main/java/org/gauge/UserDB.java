package org.gauge;

import java.util.Hashtable;
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
     * @param username key for removal from userData
     * @return bool whether removed
     * @throws NullPointerException - if username is null
     */
    public boolean delete(String username) {
        if(userData.remove(username) != null)
            return true;
        else
            return false;
    }

    


}
