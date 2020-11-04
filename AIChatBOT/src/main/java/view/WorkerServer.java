package view;

import DTO.DataClient;
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
import service.simsimi.Request;
import service.simsimi.Respone;

/**
 *
 * @author HUNGVUONG
 */
public class WorkerServer extends Thread {

    private final Socket socket;
    private final JTextPane jTextPaneClientConnect;
    private ArrayList<String> listClientConnect = new ArrayList<>();
    private StringBuilder clientConn = new StringBuilder();
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private boolean isRunning = false;

    public WorkerServer(Socket socket, ArrayList listClientConnect, JTextPane jTextPaneClientConnect) {
        this.socket = socket;
        this.listClientConnect = listClientConnect;
        this.jTextPaneClientConnect = jTextPaneClientConnect;
    }

    @Override
    public void run() {
        Execute();
    }

    private void Execute() {
        try {
            String infoClient = socket.getInetAddress().getHostName() + " - " + socket.getInetAddress().getHostAddress() + " - " + socket.getPort() + "\n";
            System.out.println(infoClient);
            listClientConnect.add(infoClient);
            listClientConnect.forEach((client) -> {
                clientConn.append(client);
            });

            jTextPaneClientConnect.setText(clientConn.toString());
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
            isRunning = true;
            while (isRunning) {                                
                HandleInputFromClient();
            }
            if (!isRunning) {
                listClientConnect.remove(infoClient);
                clientConn.replace(0, clientConn.length(), "");
                listClientConnect.forEach((client) -> {
                    clientConn.append(client);
                });
                System.out.println(clientConn);
                jTextPaneClientConnect.setText(clientConn.toString());
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
            SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
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
                    result = new DataClient(0, scanPort, "", "", format.format(date));
                }
                case MyString.SIMSIMI -> {
                    if (CheckConnect("google.com")) {
                        Date date = new Date();
                        Respone res = ParseSimsimi(CallAPI.ResponeSimsimi(new Request(dataClient.getMessage(), dataClient.getOptionDetail())));
                        if (res.getStatus() == null) {
                            result = new DataClient(0, "Tôi không biết", "", "", format.format(date));
                        } else {
                            result = switch (res.getStatus()) {
                                case 200 ->
                                    new DataClient(0, res.getAtext(), "", "", format.format(date));
                                case 429 ->
                                    new DataClient(0, "Requesst đã đạt giới hạn", "", "", format.format(date));
                                default ->
                                    new DataClient(0, "Tôi không biết", "", "", format.format(date));
                            };
                        }
                    } else {
                        result = new DataClient(0, "Server mất kết nối Internet", "", "", "");
                    }
                }
                default -> {
                    result = dataClient;
                    isRunning = false;
                }
            }
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

    private boolean CheckConnect(String host) {
        try {
            if (InetAddress.getByName(host).isReachable(5000)) {
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
