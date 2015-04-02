package org.gauge.middleware;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joel on 3/19/15.
 */
public class BodyParser {

  public static JSONObject parseJSONObject(String target) throws JSONException {
    return new JSONObject(target);
  }

  public static JSONArray parseJSONArray(String target) {
    try {
      JSONArray result = new JSONArray(target);
      return result;
    } catch (JSONException e) {
      return null;
    }
  }

}
