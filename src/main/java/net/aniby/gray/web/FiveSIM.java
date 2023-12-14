package net.aniby.gray.web;

import net.aniby.gray.Main;
import net.aniby.utils.IOHelper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.http.HttpResponse;
import java.util.Map;

public class FiveSIM {
    private static final JSONParser parser = new JSONParser();

    public static JSONObject buy() throws Exception {
        HttpResponse<String> response = IOHelper.get(Main.getConfig().getFiveSimLink() + "/v1/user/buy/activation/turkey/virtual32/ozan", Map.of(
                "Authorization", "Bearer " + Main.getConfig().getFiveSimApiKey()
        ));
        return (JSONObject) parser.parse(response.body());
    }

    public static void ban(long id) throws Exception {
        IOHelper.get(Main.getConfig().getFiveSimLink() + "/v1/user/ban/" + id, Map.of(
                "Authorization", "Bearer " + Main.getConfig().getFiveSimApiKey()
        ));
    }

    public static void end(long id) throws Exception {
        IOHelper.get(Main.getConfig().getFiveSimLink() + "/v1/user/finish/" + id, Map.of(
                "Authorization", "Bearer " + Main.getConfig().getFiveSimApiKey()
        ));
    }

    private static final long oneMinute = 60*1000L;
    public static String getCode(long id) throws Exception {
        long layout = 5000L;
        long start = System.currentTimeMillis();
        long newMillis;
        String check_url = Main.getConfig().getFiveSimLink() + "/v1/user/check/" + id;
        do {
            String callback_response = IOHelper.get(check_url, Map.of(
                    "Authorization", "Bearer " + Main.getConfig().getFiveSimApiKey()
            )).body();
            JSONObject callback_object = (JSONObject) parser.parse(callback_response);
            JSONArray sms_list = (JSONArray) callback_object.get("sms");
            if (!sms_list.isEmpty()) {
                JSONObject sms = (JSONObject) sms_list.get(0);
                return (String) sms.get("code");
            }
            newMillis = System.currentTimeMillis();
            //////
            System.out.println("Time: " + (newMillis - start) + " | " + (newMillis - start - oneMinute));
            //////
            Thread.sleep(layout);
        } while (newMillis - start < oneMinute);
        return null;
    }
}
