package net.aniby.utils.dropmail;

import net.aniby.utils.IOHelper;
import net.aniby.utils.PasswordGenerator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class DropMailAPI {
    private static final JSONParser parser = new JSONParser();

    private final String authToken;
    private final String source;
    private String emailAddress = null;

    public String emailAddress() {
        return emailAddress;
    }

    private String sessionId = null;

    public String sessionId() {
        return sessionId;
    }

    public DropMailAPI() {
        this.source = "https://mirror2.dropmail.info";
        this.authToken = PasswordGenerator.generate(12);
    }

    public DropMailAPI(String source) {
        this.source = source;
        this.authToken = PasswordGenerator.generate(12);
    }

    public void create() throws URISyntaxException, IOException, InterruptedException, ParseException {
        String query = "/api/graphql/" + this.authToken + "?query=mutation%20%7BintroduceSession%20%7Bid%2C%20expiresAt%2C%20addresses%20%7Baddress%7D%7D%7D";
        HttpResponse<String> result = IOHelper.get(source + query, null);
        JSONObject introduceSession = (JSONObject) ((JSONObject) ((JSONObject) parser.parse(result.body())).get("data")).get("introduceSession");

        this.sessionId = (String) introduceSession.get("id");
        this.emailAddress = (String) ((JSONObject) ((JSONArray) introduceSession.get("addresses")).get(0)).get("address");
    }

    public List<IncomingMail> fetchMails() throws ParseException, URISyntaxException, IOException, InterruptedException {
        String query = "/api/graphql/" + this.authToken + "?query=query%20%7Bsessions%20%7Bid%2C%20expiresAt%2C%20mails%20%7BrawSize%2C%20fromAddr%2C%20toAddr%2C%20downloadUrl%2C%20text%2C%20headerSubject%7D%7D%7D";
        HttpResponse<String> result = IOHelper.get(source + query, null);
        JSONArray sessions = (JSONArray) ((JSONObject) ((JSONObject) parser.parse(result.body())).get("data")).get("sessions");

        List<IncomingMail> incomingMails = new ArrayList<>();
        for (Object sessionObject : sessions) {
            JSONObject session = (JSONObject) sessionObject;
            if (session.get("id").equals(this.sessionId)) {
                for (Object mailObject : (JSONArray) session.get("mails")) {
                    JSONObject mail = (JSONObject) mailObject;
                    incomingMails.add(new IncomingMail(
                            (String) mail.get("text"),
                            (String) mail.get("headerSubject"),
                            (String) mail.get("fromAddr"),
                            (String) mail.get("toAddr"),
                            (String) mail.get("downloadUrl"),
                            (long) mail.get("rawSize")
                    ));
                }
            }
        }
        return incomingMails;
    }
}
