package view;

import DTO.DataClient;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTextPane;
import utils.MyString;
import service.CallAPI;
import service.simsimi.Request;
import service.simsimi.Respone;

/**
 *
 * @author HUNGVUONG
 */
public class WorkerServer extends Thread {

    private final Socket socket;
    private final JTextPane jTextPaneClientConnect;
    private StringBuilder clientConnect = new StringBuilder();
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private boolean isRunning = false;

    public WorkerServer(Socket socket, StringBuilder clientConnect, JTextPane jTextPaneClientConnect) {
        this.socket = socket;
        this.clientConnect = clientConnect;
        this.jTextPaneClientConnect = jTextPaneClientConnect;
    }

    @Override
    public void run() {
        xuLy();
    }

    private void xuLy() {
        try {
            System.out.println(socket.getInetAddress().getHostName() + " - " + socket.getInetAddress().getHostAddress() + " - " + socket.getPort());
            clientConnect.append(socket.getInetAddress().getHostName())
                    .append(" - ")
                    .append(socket.getInetAddress().getHostAddress())
                    .append(" - ")
                    .append(socket.getPort())
                    .append("\n");
            jTextPaneClientConnect.setText(clientConnect.toString());

            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
            isRunning = true;
            while (isRunning) {
                HandleInputFromClient();
            }
        } catch (IOException ex) {
            System.out.println(ex);
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void HandleInputFromClient() {
        try {
            DataClient result = null;
            DataClient dataClient = (DataClient) ois.readObject();//Đọc dữ liệu từ client trả về object DataClient
            System.out.println(dataClient.getMessage());
            String option = dataClient.getOption();//lấy option của client gửi
            switch (option) {//check option
                case MyString.WEATHER -> {
                    //Thời tiết
                }
                case MyString.LOCATION_IP -> {
                    //Vị trí IP
                }
                case MyString.SCAN_PORT -> {
                    Date date = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss dd/MM/yyyy");
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
                            scanPort += "Port " + port + " is opened<br/>";
                            socketPort.close();
                        } catch (IOException ex) {
                            scanPort += "Port " + port + " is closed<br/>";
                        } finally {
                            socketPort.close();
                        }
                    }
                    result = new DataClient("<html>Scan Port", scanPort + "</html>", "", "", format.format(date));
                }
                case MyString.SIMSIMI -> {
                    Date date = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss dd/MM/yyyy");
                    Respone res = ParseSimsimi(CallAPI.ResponeSimsimi(new Request(dataClient.getMessage(), dataClient.getOptionDetail())));
                    if (res.getStatus() == null) {
                        result = new DataClient("Simsimi", "Tôi không biết", "", "", format.format(date));
                    } else {
                        result = switch (res.getStatus()) {
                            case 200 ->
                                new DataClient("Simsimi", res.getAtext(), "", "", format.format(date));
                            case 429 ->
                                new DataClient("Simsimi", "Requesst đã đạt giới hạn", "", "", format.format(date));
                            default ->
                                new DataClient("Simsimi", "Tôi không biết", "", "", format.format(date));
                        };
                    }

                }
                default -> {
                    result = dataClient;
                    isRunning = false;
                }
            }
//            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(result);
            oos.flush();
        } catch (IOException ex) {
            System.out.println("Lỗiiiiii IO");
            System.out.println(ex);
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            System.out.println("not found");
            Logger.getLogger(WorkerServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Respone ParseSimsimi(String json) {
        Gson gson = new Gson();
        Respone respone = gson.fromJson(json, Respone.class);
        return respone;
    }
}
