package service;

import utils.MyString;
import service.simsimi.Request;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author HUNGVUONG
 */
public class CallAPI {

    private static HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static String ResponeSimsimi(Request request) {
        String json = new StringBuilder()
                .append("{").append("\"utext\":\"")
                .append(request.getUtext())
                .append("\",")
                .append("\"lang\":\"")
                .append(request.getLang())
                .append("\"")
                .append("}").toString();

        // add json header
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("https://wsapi.simsimi.com/190410/talk"))
                .setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
                .header("Content-Type", "application/json")
                .header("x-api-key", MyString.API_KEY_SIMSIMI)
                .build();

        HttpResponse<String> httpResponse = null;
        try {
            httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println(httpResponse.statusCode());
            // print response body
            System.out.println(httpResponse.body());
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(CallAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return httpResponse.body();

    }

    public String ResponeLocationIP(String ip) {
        String API_KEY=MyString.API_KEY_LOCATION_IP;
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://api.ipstack.com/"+ip+"?access_key="+API_KEY))
                .setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
                .build();

        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // print response headers
//            var headers = response.headers();
//            headers.map().forEach((k, v) -> System.out.println(k + ":" + v));

            // print status code
            System.out.println(response.statusCode());

            // print response body
            System.out.println(response.body());
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(CallAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response.body();
    }
    public  static String jsonGetRequest(String location){
        try {

            Document doc=Jsoup.connect("https://api.openweathermap.org/data/2.5/weather?q="+location+"&appid=0d0ea27651db38d1d4fb5ffbb571b33a")
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .get();
            JSONObject object= (JSONObject) JSONValue.parse(doc.text());
            JSONArray weather= (JSONArray) object.get("weather");
            JsonObject weatherObj= (JsonObject) weather.get(0);
            return weatherObj.get("description").toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "location not found";
    }
//    public static void main(String[] args) {
//        ResponeLocationIP("107.129.191.47");
//    }
}
