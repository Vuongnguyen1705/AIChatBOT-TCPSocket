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
import resource.MyString;
import service.CallAPI;
import service.simsimi.Request;
import service.simsimi.Respone;

/**
 *
 * @author HUNGVUONG
 */
public class WorkerServer extends Thread {

    private Socket socket;
    private final DefaultListModel<String> model;
    private final JList jListClientConnect;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private String close = "";
    private boolean isRunning = false;

    public WorkerServer(Socket socket, DefaultListModel<String> model, JList jListClientConnect) {
        this.socket = socket;
        this.model = model;
        this.jListClientConnect = jListClientConnect;
    }

    @Override
    public void run() {
        xuLy();
    }

    private void xuLy() {
        try {
            System.out.println(socket.getInetAddress().getHostName() + " - " + socket.getInetAddress().getHostAddress() + " - " + socket.getPort());
            model.addElement(socket.getInetAddress().getHostName() + " - " + socket.getInetAddress().getHostAddress() + " - " + socket.getPort());
            jListClientConnect.setModel(model);
            jListClientConnect.ensureIndexIsVisible(model.size() - 1);

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
//            ois = new ObjectInputStream(socket.getInputStream());
            DataClient result = null;
            if (ois != null) {
                System.out.println("không null");
            } else {
                System.out.println("nulllllll");
            }
            DataClient dataClient = (DataClient) ois.readObject();
            System.out.println(dataClient.getMessage());
            String option = dataClient.getOption();
            switch (option) {
                case MyString.WEATHER -> {
                }
                case MyString.LOCATION_IP -> {
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
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("Lỗiiiiii");
            System.out.println(ex);
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Respone ParseSimsimi(String json) {
        Gson gson = new Gson();
        Respone respone = gson.fromJson(json, Respone.class);
        return respone;
    }
}
