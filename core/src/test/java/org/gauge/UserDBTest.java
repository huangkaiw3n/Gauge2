package org.gauge;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.json.JSONArray;
import org.json.JSONObject;

import static org.junit.Assert.*;

public class UserDBTest {

    User monkey;
    User elephant;
    UserDB udb, udb2;

    @Before
    public void setUp() throws Exception {
        monkey = new User("monkey", "bananas", "monkey@zoo.com");
        elephant = new User("elephant", "stampede", "elepfun@safari.com");
        udb = new UserDB("src/test/UserDBtest.csv");
        udb.add("monkey", monkey, false);
        udb.add("elephant", elephant, false);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testToCSV() throws Exception {
        udb.toCSV();
    }

    @Test
    public void testFromCSV() throws Exception {
        udb2 = new UserDB("src/test/UserDBtest.csv");
        udb2.fromCSV();
    }

    @Test
    public void testToJSONArray() throws Exception{
        System.out.println(udb.toJSONArray());
    }
}