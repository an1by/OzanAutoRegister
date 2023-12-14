package net.aniby.utils;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class IOHelper {
    public enum RequestType {
        GET, POST, PUT, DELETE
    }

    public static HttpResponse<String> generate(String url, Map<String, String> headers, RequestType requestType, HttpRequest.BodyPublisher publisher) throws URISyntaxException, IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(new URI(url));
        if (headers != null) {
            for (String key : headers.keySet())
                requestBuilder = requestBuilder.header(key, headers.get(key));
        }
        switch (requestType) {
            case GET: {
                requestBuilder = requestBuilder.GET();
                break;
            }
            case POST: {
                requestBuilder = requestBuilder.POST(publisher);
                break;
            }
            case PUT: {
                requestBuilder = requestBuilder.PUT(publisher);
                break;
            }
            case DELETE: {
                requestBuilder.DELETE();
                break;
            }
        }
        return httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
    }

    public static HttpResponse<String> get(String url, Map<String, String> headers) throws URISyntaxException, IOException, InterruptedException {
        return generate(url, headers, RequestType.GET, null);
    }
}
