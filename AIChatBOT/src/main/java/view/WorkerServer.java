package view;

import DTO.DataClient;
import DTO.InfoClient;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextPane;
import utils.MyString;
import service.CallAPI;
import service.locationip.LocationIP;
import service.simsimi.Request;
import service.simsimi.Response;
import service.weather.Coordinates;
import service.weather.Daily;
import service.weather.WeatherForecast;
import utils.CipherUtils;
import utils.MyColor;
import utils.MyConvert;
import utils.weatherString;

/**
 *
 * @author HUNGVUONG
 */
public class WorkerServer extends Thread {

    private final Socket socket;
    private final JTextPane jTextPaneClientConnect;
    private ArrayList<InfoClient> listClientConnect = new ArrayList<>();
    private StringBuilder clientConn = new StringBuilder();
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private boolean isRunning = false;
    private String key;

    public WorkerServer(Socket socket, ArrayList listClientConnect, JTextPane jTextPaneClientConnect) {
        this.socket = socket;
        this.listClientConnect = listClientConnect;
        this.jTextPaneClientConnect = jTextPaneClientConnect;
    }

    public ObjectInputStream getOis() {
        return ois;
    }

    public void setOis(ObjectInputStream ois) {
        this.ois = ois;
    }

    public ObjectOutputStream getOos() {
        return oos;
    }

    public void setOos(ObjectOutputStream oos) {
        this.oos = oos;
    }
    
    @Override
    public void run() {
        Execute();
    }

    private void Execute() {
        try {
            InfoClient infoClient = new InfoClient(socket.getInetAddress().getHostName(), socket.getInetAddress().getHostAddress(), socket.getPort());
//            String infoClient = socket.getInetAddress().getHostName() + " - " + socket.getInetAddress().getHostAddress() + " - " + socket.getPort() + "\n";

            listClientConnect.add(infoClient);
            listClientConnect.forEach((client) -> {
                clientConn.append(client.getHostName() + " - " + client.getHostAddress() + " - " + client.getPort() + "\n");
            });
            System.out.println(clientConn + "\n");
            jTextPaneClientConnect.setText(clientConn.toString());
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
            key = ois.readObject().toString();
            System.out.println("key: " + key);
            isRunning = true;
            while (isRunning) {
                HandleInputFromClient();
            }
//            if (!isRunning) { 
            listClientConnect.remove(infoClient);
//                clientConn.replace(0, clientConn.length(), "");
            clientConn.setLength(0);
            listClientConnect.forEach((client) -> {
                clientConn.append(client.getHostName() + " - " + client.getHostAddress() + " - " + client.getPort() + "\n");
            });
            System.out.println(clientConn + "\n");
            jTextPaneClientConnect.setText(clientConn.toString());
            ois.close();
            oos.close();
            socket.close();
//            }
        } catch (IOException ex) {
            System.out.println(ex);
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(WorkerServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void HandleInputFromClient() {
        try {
            DataClient result = null;
            DataClient dataClient = (DataClient) ois.readObject();//Đọc dữ liệu từ client trả về object DataClient
            System.out.println(dataClient.getName() + ": " + dataClient.getMessage());
            dataClient.setMessage(CipherUtils.deString(dataClient.getMessage(), key));
            System.out.println(dataClient.getName() + ": deScriptMess:" + dataClient.getMessage());
            SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
            String option = dataClient.getOption();//lấy option của client gửi
            boolean flagWeather = true;
            switch (option) {//check option
                case MyString.WEATHER -> {
                    if (CheckConnect("google.com")) {
                        Date date = new Date();
                        try {
                            String code = CallAPI.GetCoordinates(dataClient.getMessage()).getCode();
                            if (code.equals("200")) {
                                System.out.println(CallAPI.GetWeatherForecast(dataClient.getMessage()));
                                WeatherForecast weather = ParseWeather(CallAPI.GetWeatherForecast(dataClient.getMessage()));
                                Coordinates coordinates = CallAPI.GetCoordinates(dataClient.getMessage());
                                String forecast = "";
                                switch (dataClient.getOptionDetail()) {
                                    case weatherString.NOW -> {
                                        forecast = "<html><b style=\"text-align: center;color:blue\">" + coordinates.getName() + "</b><br>"
                                                + rowWeather("Thời gian", MyConvert.convertTimeToHours(weather.getCurrent().getDt().toString()))
                                                + rowWeather("Nhiệt độ", weather.getCurrent().getTemp() + " độ C")
                                                + rowWeather("Thời tiết", weather.getCurrent().getWeather().get(0).getDescription())
                                                + rowWeather("Cảm giác như", weather.getCurrent().getFeelsLike() + " độ C")
                                                + rowWeather("Tầm nhìn", weather.getCurrent().getVisibility() + "m")
                                                + rowWeather("Độ ẩm", weather.getCurrent().getHumidity() + "%")
                                                + rowWeather("Áp suất", weather.getCurrent().getPressure() + "hPa")
                                                + rowWeather("Tốc độ gió", weather.getCurrent().getWindSpeed() + "m/s")
                                                + rowWeather("Chỉ số UV", weather.getCurrent().getUvi().toString())
                                                + "</html>";
                                        result = new DataClient(0, "Weather", forecast, "", "", format.format(date));
                                    }
                                    case weatherString.TODAY -> {
                                        forecast = "<html><b style=\"text-align: center;color:blue\">" + coordinates.getName() + "</b><br>"
                                                + rowWeather("Thời gian", MyConvert.convertTime(weather.getDaily().get(0).getDt().toString()))
                                                + rowWeather("Bình minh", MyConvert.convertTimeToHours(weather.getDaily().get(0).getSunrise().toString()))
                                                + rowWeather("Hoàng hôn", MyConvert.convertTimeToHours(weather.getDaily().get(0).getSunset().toString()))
                                                + rowWeather("Nhiệt độ ban ngày", weather.getDaily().get(0).getTemp().getDay() + " độ C")
                                                + rowWeather("Nhiệt độ cao nhất", weather.getDaily().get(0).getTemp().getMax() + " độ C")
                                                + rowWeather("Nhiệt độ thấp nhất", weather.getDaily().get(0).getTemp().getMin() + " độ C")
                                                + rowWeather("Cảm giác như", weather.getDaily().get(0).getFeelsLike().getDay() + " độ C")
                                                + rowWeather("Thời tiết", weather.getDaily().get(0).getWeather().get(0).getDescription())
                                                + rowWeather("Độ ẩm", weather.getDaily().get(0).getHumidity() + "%")
                                                + rowWeather("Áp suất", weather.getDaily().get(0).getPressure() + "hPa")
                                                + rowWeather("Tốc độ gió", weather.getDaily().get(0).getWindSpeed() + "m/s")
                                                + rowWeather("Chỉ số UV", weather.getDaily().get(0).getUvi().toString())
                                                + "</html>";

                                        result = new DataClient(0, "Weather", forecast, "", "", format.format(date));
                                    }
                                    case weatherString.THREEDAY -> {
                                        for (int i = 1; i <= 3; i++) {
                                            forecast = "<html><b style=\"text-align: center;color:blue\">" + coordinates.getName() + "</b><br>"
                                                    + rowWeather("Thời gian", MyConvert.convertTime(weather.getDaily().get(i).getDt().toString()))
                                                    + rowWeather("Thời tiết", weather.getDaily().get(i).getWeather().get(0).getDescription())
                                                    + rowWeather("Nhiệt độ cao nhất", weather.getDaily().get(i).getTemp().getMax() + " độ C")
                                                    + rowWeather("Nhiệt độ thấp nhất", weather.getDaily().get(i).getTemp().getMin() + " độ C")
                                                    + rowWeather("Độ ẩm", weather.getDaily().get(i).getHumidity() + "%")
                                                    + rowWeather("Tốc độ gió", weather.getDaily().get(i).getWindSpeed() + "m/s")
                                                    + "</html>";
                                            result = new DataClient(0, "Weather", forecast, "", "", format.format(date));
                                            oos.writeObject(result);
                                            oos.flush();
                                        }
                                        flagWeather = false;
                                    }
                                    case weatherString.FIVEDAY -> {
                                        for (int i = 1; i <= 5; i++) {
                                            forecast = "<html><b style=\"text-align: center;color:blue\">" + coordinates.getName() + "</b><br>"
                                                    + rowWeather("Thời gian", MyConvert.convertTime(weather.getDaily().get(i).getDt().toString()))
                                                    + rowWeather("Thời tiết", weather.getDaily().get(i).getWeather().get(0).getDescription())
                                                    + rowWeather("Nhiệt độ cao nhất", weather.getDaily().get(i).getTemp().getMax() + " độ C")
                                                    + rowWeather("Nhiệt độ thấp nhất", weather.getDaily().get(i).getTemp().getMin() + " độ C")
                                                    + rowWeather("Độ ẩm", weather.getDaily().get(i).getHumidity() + "%")
                                                    + rowWeather("Tốc độ gió", weather.getDaily().get(i).getWindSpeed() + "m/s")
                                                    + "</html>";
                                            result = new DataClient(0, "Weather", forecast, "", "", format.format(date));
                                            oos.writeObject(result);
                                            oos.flush();
                                        }
                                        flagWeather = false;
                                    }
                                    case weatherString.SEVENDAY -> {
                                        for (int i = 1; i < weather.getDaily().size(); i++) {
                                            forecast = "<html><b style=\"text-align: center;color:blue;\">" + coordinates.getName() + "</b><br>"
                                                    + rowWeather("Thời gian", MyConvert.convertTime(weather.getDaily().get(i).getDt().toString()))
                                                    + rowWeather("Thời tiết", weather.getDaily().get(i).getWeather().get(0).getDescription())
                                                    + rowWeather("Nhiệt độ cao nhất", weather.getDaily().get(i).getTemp().getMax() + " độ C")
                                                    + rowWeather("Nhiệt độ thấp nhất", weather.getDaily().get(i).getTemp().getMin() + " độ C")
                                                    + rowWeather("Độ ẩm", weather.getDaily().get(i).getHumidity() + "%")
                                                    + rowWeather("Tốc độ gió", weather.getDaily().get(i).getWindSpeed() + "m/s")
                                                    + "</html>";
                                            result = new DataClient(0, "Weather", forecast, "", "", format.format(date));
                                            oos.writeObject(result);
                                            oos.flush();
                                        }
                                        flagWeather = false;
                                    }
                                }
                            } else {
                                result = new DataClient(0, "Weather", "Không tìm thấy địa điểm", "", "", "");
                            }
                        } catch (Exception e) {
                            result = new DataClient(0, "Weather", "Server mất quá nhiều thời gian để phản hồi", "", "", "");
                        }
                    } else {
                        result = new DataClient(0, "Weather", "Server mất kết nối Internet", "", "", "");
                    }
                    if (flagWeather == true) {
                        oos.writeObject(result);
                        oos.flush();
                    }
                }
                case MyString.LOCATION_IP -> {
                    if (CheckConnect("google.com")) {
                        Date date = new Date();
                        try {
                            LocationIP locationIP = ParseLocationIP(CallAPI.GetLocationIP(dataClient.getMessage()));
                            if (locationIP.getType() == null) {
                                result = new DataClient(0, "Location IP", "Không tìm thấy vị trí của IP này", "", "", format.format(date));
                            } else {
                                String location = "<html>" + rowWeather("IP", dataClient.getMessage()) + rowWeather("Kinh độ", locationIP.getLongitude().toString()) + rowWeather("Vĩ độ", locationIP.getLatitude().toString()) + rowWeather("Địa điểm", locationIP.getCity());
                                result = new DataClient(0, "Location IP", location, "", "", format.format(date));
                            }
                        } catch (Exception e) {
                            result = new DataClient(0, "Location IP", "Server mất quá nhiều thời gian để phản hồi", "", "", "");
                        }
                    } else {
                        result = new DataClient(0, "Location IP", "Server mất kết nối Internet", "", "", "");
                    }
                    oos.writeObject(result);
                    oos.flush();
                }
                case MyString.SCAN_PORT -> {
                    Date date = new Date();
                    StringTokenizer token = new StringTokenizer(dataClient.getMessage(), ":");
                    String hostname = token.nextToken();
                    StringTokenizer portst = new StringTokenizer(token.nextToken(), ";");
                    int beginPort = Integer.parseInt(portst.nextToken());
                    int endPort = Integer.parseInt(portst.nextToken());
                    String scanPort = "";
                    for (int port = beginPort; port <= endPort; port++) {
                        Socket socketPort = new Socket();
                        try {
                            socketPort.connect(new InetSocketAddress(hostname, port), 200);
                            scanPort = "Port " + port + " <b style=\"color:#31255A\" >is opened</b><br/>";
                            result = new DataClient(0, "Port", scanPort, "", "", format.format(date));
                            socketPort.close();
                        } catch (IOException ex) {
                            scanPort = "Port " + port + " <b style=\"color:#C60000\">is closed</b><br/>";
                            result = new DataClient(0, "Port", scanPort, "", "", format.format(date));
                        } finally {
                            socketPort.close();
                        }
                        System.out.println(scanPort);
                        oos.writeObject(result);
                        oos.flush();
                    }

                }
                case MyString.SIMSIMI -> {
                    if (CheckConnect("google.com")) {
                        Date date = new Date();
                        Request request = new Request(dataClient.getMessage(), dataClient.getOptionDetail());
                        try {
                            Response res = ParseSimsimi(CallAPI.GetSimsimi(request));
                            result = switch (CallAPI.GetStatusCodeSimsimi(request)) {
                                case 200 ->
                                    new DataClient(0, "Simsimi", res.getAtext(), "", "", format.format(date));
                                case 429 ->
                                    new DataClient(0, "Simsimi", "Request đã đạt giới hạn", "", "", format.format(date));
                                default ->
                                    new DataClient(0, "Simsimi", "Tôi không biết", "", "", format.format(date));
                            };
                        } catch (Exception e) {
                            result = new DataClient(0, "Simsimi", "Server mất quá nhiều thời gian để phản hồi", "", "", "");
                        }
                    } else {
                        result = new DataClient(0, "Simsimi", "Server mất kết nối Internet", "", "", "");
                    }
                    System.out.println(result.getMessage());
                    oos.writeObject(result);
                    oos.flush();
                }
                default -> {
//                    result = dataClient;
//                    oos.writeObject(result);
//                    oos.flush();
                    isRunning = false;
                }
            }
        } catch (IOException ex) {
//            System.out.println("Lỗiiiiii IO");
//            System.out.println(ex);
            isRunning = false;
        } catch (ClassNotFoundException ex) {
            System.out.println("not found");
            Logger.getLogger(WorkerServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String rowWeather(String title, String content) {
        return title + ": <b style=\"color:#31255A\">" + content + "</b><br>";
    }

    private Response ParseSimsimi(String json) {
        Gson gson = new Gson();
        Response respone = gson.fromJson(json, Response.class);
        return respone;
    }

    private LocationIP ParseLocationIP(String json) {
        Gson gson = new Gson();
        LocationIP locationIP = gson.fromJson(json, LocationIP.class);
        return locationIP;
    }

    private WeatherForecast ParseWeather(String json) {
        Gson gson = new Gson();
        WeatherForecast weatherForecast = gson.fromJson(json, WeatherForecast.class);
        return weatherForecast;
    }

    private boolean CheckConnect(String host) {
        try {
            if (InetAddress.getByName(host).isReachable(10000)) {
                System.out.println(host + " is reachable");
                return true;
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return false;
    }
}
