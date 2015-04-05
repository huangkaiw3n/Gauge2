package org.gauge;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by Kaiwen on 19/3/2015.
 * Username
 IP
 */
public class UserStatusDB {

    private HashSet<User> userSet;

    public UserStatusDB(){
        userSet = new HashSet<>();
    }

    public synchronized boolean insert(User u1){
        return userSet.add(u1);
    }

    public synchronized boolean delete(User u1){
        return userSet.remove(u1);
    }

    public int size(){
        return userSet.size();
    }

    public synchronized JSONArray toJSONArray(){
        JSONArray users = new JSONArray();

        for(User it : userSet){
            users.put(it.toJSON());
        }
        return users;
    }
}
