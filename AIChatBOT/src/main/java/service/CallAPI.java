package service;

import com.google.gson.Gson;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import service.weather.Coordinates;
import service.weather.WeatherForecast;

/**
 *
 * @author HUNGVUONG
 */
public class CallAPI {

    private static HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static HttpResponse<String> Simsimi(Request request) {
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
            //System.out.println(httpResponse.statusCode());
            // print response body
//            System.out.println(httpResponse.body());
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(CallAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return httpResponse;
    }

    public static String GetSimsimi(Request request) {
        return Simsimi(request).body();
    }

    public static int GetStatusCodeSimsimi(Request request) {
        System.out.println(Simsimi(request).statusCode());
        return Simsimi(request).statusCode();
    }

    public static String GetLocationIP(String ip) {
        String API_KEY = MyString.API_KEY_LOCATION_IP;
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://api.ipstack.com/" + ip + "?access_key=" + API_KEY))
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

    public static Coordinates GetCoordinates(String location) {
        String code = "";
        double lon = 0;
        double lat = 0;
        String name = "";
        try {
            Document doc = Jsoup.connect("https://api.openweathermap.org/data/2.5/weather?q=" + location + "&appid=" + MyString.API_KEY_WEATHER)
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .get();
            JSONObject object = (JSONObject) JSONValue.parse(doc.text());
            try {
                long codeLong = (long) object.get("cod");
                code = String.valueOf(codeLong);
            } catch (Exception e) {
                code = (String) object.get("cod");
            }

            if (code.equals("200")) {
                JSONObject coord = (JSONObject) object.get("coord");
                try {
                    lon = (double) coord.get("lon");
                    lat = (double) coord.get("lat");
                } catch (Exception e) {
                    long lo = (long) coord.get("lon");
                    long la = (long) coord.get("lat");
                    lon = lo;
                    lat = la;
                }
                name = (String) object.get("name");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Coordinates(code, lon, lat, name);
    }

    public static String GetWeatherForecast(String location) {
        Coordinates coordinates = GetCoordinates(location);
        double lat = coordinates.getLatitude();
        double lon = coordinates.getLongitude();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("https://api.openweathermap.org/data/2.5/onecall?lat=" + lat + "&lon=" + lon + "&lang=vi&exclude=hourly,minutely,alerts&units=metric&appid=" + MyString.API_KEY_WEATHER))
                .setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
                .build();
        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // print status code
            System.out.println(response.statusCode());
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(CallAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response.body();
    }

    private static WeatherForecast ParseWeather(String json) {
        Gson gson = new Gson();
        WeatherForecast weatherForecast = gson.fromJson(json, WeatherForecast.class);
        return weatherForecast;
    }

    public static void main(String[] args) {
//        System.out.println(GetCoordinates("longan").getLatitude()+"--"+GetCoordinates("longan").getLongitude()+"--"+GetCoordinates("longan").getName());
//        System.out.println(GetWeatherForecast("saigon"));
//        System.out.println(GetCoordinates("bangkok").getLatitude());
        System.out.println(ParseWeather(GetWeatherForecast("saigon")));
    }

}
