package view;

import DTO.DataClient;
import resource.MyColor;
import utils.MyString;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.border.LineBorder;
import utils.PatternRegEx;

/**
 *
 * @author HUNGVUONG
 */
public class Client extends javax.swing.JFrame {

    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private boolean flagFullName = false;
    private boolean flagHost = false;
    private boolean flagPort = false;
    private final DefaultListModel<String> model = new DefaultListModel<>();
    private volatile boolean isRunning = false;

    public Client() {
        initComponents();
        setLocationRelativeTo(null);
        EventRadioButton();
        SetFont();
    }

    private void SetFont() {
        jLabelStatus.setFont(new Font("Dialog", Font.BOLD, 12));
        jLabelStatusTitle.setFont(new Font("Dialog", Font.BOLD, 12));
    }

    private void EventRadioButton() {
        String sWeather = "Chọn xem ngày hiện tại hoặc 7 ngày kế tiếp, sau đó nhập vào tên khu vực hoặc thành phố cần tra cứu\nVí dụ:Chọn Ngày hiện tại, nhập vào Long An, Ho Chi Minh City, Thủ Đức";
        String sIP = "Nhập vào địa chỉ IP cần xác định vị trí\nVí dụ: 103.129.191.96\n*Lưu ý: Địa chỉ IP phải là địa chỉ public";
        String sPort = "Nhập vào địa chỉ IP và khoảng giới hạn các port cần quét\nVí dụ: 192.168.123.123:1;10000";
        String sSimsimi = "Chọn ngôn ngữ mà muốn sử dụng để chat, sau đó nhập vào nội dung bạn muốn chat với BOT\nVí dụ: Chọn Tiếng Việt, nhập vào \"Xin chào\",\"Hôm nay tôi buồn quá\",\"Tôi có đẹp trai không?\"";

        jRadioButtonWeather.addItemListener((e) -> {
            CheckInputByRadio();
            jTextPaneTutorial.setText(sWeather);
        });
        jRadioButtonLocationIP.addItemListener((e) -> {
            CheckInputByRadio();
            jTextPaneTutorial.setText(sIP);
        });
        jRadioButtonPort.addItemListener((e) -> {
            CheckInputByRadio();
            jTextPaneTutorial.setText(sPort);
        });
        jRadioButtonSimsimi.addItemListener((e) -> {
            CheckInputByRadio();
            jTextPaneTutorial.setText(sSimsimi);
        });
    }

    private void Validate(int i) {
        switch (i) {
            case 1 -> {
                if (jTextFieldFullName.getText().trim().matches(PatternRegEx.patternCharacter)) {
                    jTextFieldFullName.setBorder(new LineBorder(MyColor.green, 2));
                    flagFullName = true;
                } else {
                    jTextFieldFullName.setBorder(new LineBorder(MyColor.red, 2));
                    flagFullName = false;

                }
            }//Validate FullName
            case 2 -> {
                if (jTextFieldServer.getText().trim().matches(PatternRegEx.patternIP)) {
                    jTextFieldServer.setBorder(new LineBorder(MyColor.green, 2));
                    flagHost = true;
                } else {
                    jTextFieldServer.setBorder(new LineBorder(MyColor.red, 2));
                    flagHost = false;
                }
            }//Validate Server Host
            case 3 -> {
                String portString = jTextFieldPort.getText().trim();

                if (portString.matches(PatternRegEx.patternPort)) {
                    int portInt = Integer.parseInt(portString);
                    if ((portInt < 1024 || portInt > 65535)) {
                        jTextFieldPort.setBorder(new LineBorder(MyColor.red, 2));
                        flagPort = false;
                    } else {
                        jTextFieldPort.setBorder(new LineBorder(MyColor.green, 2));
                        flagPort = true;
                    }
                } else {
                    jTextFieldPort.setBorder(new LineBorder(MyColor.red, 2));
                    flagPort = false;
                }
            }//Validate Port
        }
        if (flagFullName == true && flagHost == true && flagPort == true) {
            jToggleButtonConnect.setEnabled(true);
        } else {
            jToggleButtonConnect.setEnabled(false);
        }
    }

    private boolean ConnectToServer(String host, int port) {
        host = jTextFieldServer.getText().trim();
        port = Integer.parseInt(jTextFieldPort.getText().trim());
        try {
            socket = new Socket(host, port);
            if (socket.isConnected() && !socket.isClosed()) {
                try {
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    ois = new ObjectInputStream(socket.getInputStream());
                } catch (IOException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
                jLabelStatus.setText("Đã kết nối");
                jLabelStatus.setForeground(MyColor.green);
                return true;
            } else {
                jLabelStatus.setText("Kết nối thất bại");
                jLabelStatus.setForeground(MyColor.red);
                return false;
            }
        } catch (IOException ex) {
            System.out.println(ex);
            return false;
        }
    }

    private void CloseConnect() {
        try {
            if (ois != null) {
                ois.close();
            }
            if (oos != null) {
                oos.close();
            }
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void HandleConnection() {
        int port = Integer.parseInt(jTextFieldPort.getText().trim());
        String host = jTextFieldServer.getText();
        if (jToggleButtonConnect.isSelected() == false) {
            jTextFieldFullName.setEditable(true);
            jTextFieldServer.setEditable(true);
            jTextFieldPort.setEditable(true);

            System.out.println("Client close");
            jLabelStatus.setText("Chưa kết nối");
            jLabelStatus.setForeground(MyColor.orange);
            try {
                oos.writeObject(new DataClient("", MyString.EXIT_PROGRAM, "", "", ""));
                oos.flush();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {

            if (ConnectToServer(host, port)) {
                System.out.println("open");
                jLabelStatus.setText("Đã kết nối");
                jLabelStatus.setForeground(MyColor.green);

                jTextFieldFullName.setEditable(false);
                jTextFieldServer.setEditable(false);
                jTextFieldPort.setEditable(false);

                ClientRunning();

            } else {
                System.out.println("thất bại");
                jLabelStatus.setText("Kết nối thất bại");
                jLabelStatus.setForeground(MyColor.red);
                jToggleButtonConnect.setSelected(false);
            }
        }
    }

    private String OptionRadioSelected() {
        if (jRadioButtonWeather.isSelected()) {
            return MyString.WEATHER;
        } else if (jRadioButtonLocationIP.isSelected()) {
            return MyString.LOCATION_IP;
        } else if (jRadioButtonPort.isSelected()) {
            return MyString.SCAN_PORT;
        } else {
            return MyString.SIMSIMI;
        }
    }

    private void CheckInputByRadio() {
        String input = jTextFieldInputChat.getText().trim();
        if (jRadioButtonWeather.isSelected()) {
            CheckSystaxInput(input);
        } else if (jRadioButtonLocationIP.isSelected()) {
            CheckSystaxInput(input);
        } else if (jRadioButtonPort.isSelected()) {
            CheckSystaxInput(input);
        } else {
            CheckSystaxInput(input);
        }
    }

    private boolean CheckSystaxInput(String s) {
        String option = OptionRadioSelected();
        switch (option) {
            case MyString.WEATHER -> {
                if (s.matches(PatternRegEx.patternCharacter)) {
                    jTextFieldInputChat.setBorder(new LineBorder(MyColor.green, 2));
                    return true;
                } else {
                    jTextFieldInputChat.setBorder(new LineBorder(MyColor.red, 2));
                    return false;
                }
            }
            case MyString.LOCATION_IP -> {
                if (s.matches(PatternRegEx.patternIP)) {
                    jTextFieldInputChat.setBorder(new LineBorder(MyColor.green, 2));
                    return true;
                } else {
                    jTextFieldInputChat.setBorder(new LineBorder(MyColor.red, 2));
                    return false;
                }
            }
            case MyString.SCAN_PORT -> {
                if (s.matches(PatternRegEx.patternInputScanPort)) {
                    jTextFieldInputChat.setBorder(new LineBorder(MyColor.green, 2));
                    return true;
                } else {
                    jTextFieldInputChat.setBorder(new LineBorder(MyColor.red, 2));
                    return false;
                }
            }
            default -> {
                if (!jTextFieldInputChat.getText().trim().equals("")) {
                    jTextFieldInputChat.setBorder(new LineBorder(MyColor.green, 2));
                    return true;
                } else {
                    jTextFieldInputChat.setBorder(new LineBorder(MyColor.red, 2));
                    return false;
                }
            }
        }
    }

    private void SetModelMessage(String s) {
        model.addElement(s);//thêm dữ liệu vào model chat
        jListMessage.setModel(model);//set dữ liệu hiển thị lên list chat
        jListMessage.ensureIndexIsVisible(model.size() - 1);//cuộn list xuống tin nhắn mới nhất
    }

    private void SendData() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss dd/MM/yyyy");

        String fullName = jTextFieldFullName.getText().trim();
        String message = jTextFieldInputChat.getText().trim();
        String dateTime = format.format(date);

        String option = OptionRadioSelected();//kiểm tra tùy chọn chat
        SetModelMessage(fullName + ": " + message + " --> " + dateTime);//set hiển thị tin nhắn     
        SetModelMessage("......");
        try {
            DataClient data = null;
            switch (option) {
                case MyString.WEATHER -> {
                }
                case MyString.LOCATION_IP -> {
                }
                case MyString.SCAN_PORT -> {
                    data = new DataClient(fullName, message, option, "", dateTime);
                }
                case MyString.SIMSIMI -> {
                    String lang = "";
                    if (jComboBoxLanguage.getSelectedItem().equals("Tiếng Việt")) {
                        lang = "vn";
                    } else {
                        lang = "en";
                    }
                    data = new DataClient(fullName, message, option, lang, dateTime);
                }
            }
            oos.writeObject(data);
            oos.flush();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void ReceiveData() {
        try {
            DataClient data = (DataClient) ois.readObject();//đọc dữ liệu từ server
            if (data.getMessage().equals(MyString.EXIT_PROGRAM)) {
                isRunning = false;
                CloseConnect();
            } else {
                model.remove(model.getSize() - 1);
                SetModelMessage(data.getFullName() + ": " + data.getMessage() + " --> " + data.getDate());
            }
        } catch (IOException | ClassNotFoundException ex) {
            System.err.println("Lỗiiii");
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void ClientRunning() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                isRunning = true;
                while (isRunning) {
                    ReceiveData();
                }
                System.out.println("finish");
            }
        }).start();

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupOptionChat = new javax.swing.ButtonGroup();
        jLabel6 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldFullName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldServer = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldPort = new javax.swing.JTextField();
        jToggleButtonConnect = new javax.swing.JToggleButton();
        jLabelStatusTitle = new javax.swing.JLabel();
        jLabelStatus = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPaneMessage = new javax.swing.JScrollPane();
        jListMessage = new javax.swing.JList<>();
        jPanel5 = new javax.swing.JPanel();
        jTextFieldInputChat = new javax.swing.JTextField();
        jButtonSend = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jRadioButtonWeather = new javax.swing.JRadioButton();
        jRadioButtonLocationIP = new javax.swing.JRadioButton();
        jRadioButtonPort = new javax.swing.JRadioButton();
        jRadioButtonSimsimi = new javax.swing.JRadioButton();
        jComboBoxLanguage = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPaneTutorial = new javax.swing.JTextPane();
        jLabel5 = new javax.swing.JLabel();
        jComboBoxDate = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel6.setFont(new java.awt.Font("Dialog", 1, 25)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("CLIENT");

        jLabel1.setText("Full Name:");

        jTextFieldFullName.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jTextFieldFullName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldFullNameKeyReleased(evt);
            }
        });

        jLabel2.setText("Host:");

        jTextFieldServer.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jTextFieldServer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldServerKeyReleased(evt);
            }
        });

        jLabel3.setText("Port:");

        jTextFieldPort.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jTextFieldPort.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldPortKeyReleased(evt);
            }
        });

        jToggleButtonConnect.setText("Kết nối");
        jToggleButtonConnect.setEnabled(false);
        jToggleButtonConnect.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jToggleButtonConnectStateChanged(evt);
            }
        });
        jToggleButtonConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonConnectActionPerformed(evt);
            }
        });

        jLabelStatusTitle.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelStatusTitle.setText("Trạng thái:");

        jLabelStatus.setForeground(new java.awt.Color(238, 118, 0));
        jLabelStatus.setText("Chưa kết nối");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabelStatusTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(6, 6, 6))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldFullName)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldServer)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldPort)
                        .addGap(18, 18, 18)
                        .addComponent(jToggleButtonConnect, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextFieldFullName, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jTextFieldServer, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jTextFieldPort, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToggleButtonConnect, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelStatusTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelStatus))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPaneMessage.setViewportView(jListMessage);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPaneMessage)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPaneMessage)
                .addContainerGap())
        );

        jTextFieldInputChat.setEditable(false);
        jTextFieldInputChat.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jTextFieldInputChat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldInputChatKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldInputChatKeyReleased(evt);
            }
        });

        jButtonSend.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jButtonSend.setIcon(new ImageIcon("src\\main\\java\\resource\\send32px.png"));
        jButtonSend.setEnabled(false);
        jButtonSend.setHideActionText(true);
        jButtonSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSendActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextFieldInputChat, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonSend, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldInputChat, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSend, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Tùy chọn Chat");

        buttonGroupOptionChat.add(jRadioButtonWeather);
        jRadioButtonWeather.setText("Tra cứu thời tiết");
        jRadioButtonWeather.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonWeatherItemStateChanged(evt);
            }
        });

        buttonGroupOptionChat.add(jRadioButtonLocationIP);
        jRadioButtonLocationIP.setText("Xác định vị trí IP");

        buttonGroupOptionChat.add(jRadioButtonPort);
        jRadioButtonPort.setText("Quét Port");

        buttonGroupOptionChat.add(jRadioButtonSimsimi);
        jRadioButtonSimsimi.setSelected(true);
        jRadioButtonSimsimi.setText("Chat BOT Simsimi");
        jRadioButtonSimsimi.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonSimsimiItemStateChanged(evt);
            }
        });

        jComboBoxLanguage.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tiếng Việt", "Tiếng Anh" }));
        jComboBoxLanguage.setFocusable(false);

        jTextPaneTutorial.setEditable(false);
        jTextPaneTutorial.setFont(new java.awt.Font("DialogInput", 0, 14)); // NOI18N
        jTextPaneTutorial.setText("Chọn ngôn ngữ mà muốn sử dụng sau đó nhập vào nội dung bạn muốn chat với BOT\nVí dụ: Chọn Tiếng Việt, nhập vào \"Xin chào\", \"Hôm nay tôi buồn quá\"");
        jScrollPane2.setViewportView(jTextPaneTutorial);

        jLabel5.setText("Ngôn ngữ:");

        jComboBoxDate.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ngày hiện tại", "7 ngày" }));
        jComboBoxDate.setEnabled(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jRadioButtonWeather)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBoxDate, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jRadioButtonLocationIP)
                            .addComponent(jRadioButtonPort)
                            .addComponent(jRadioButtonSimsimi)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBoxLanguage, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButtonWeather)
                    .addComponent(jComboBoxDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonLocationIP)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonPort)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonSimsimi)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxLanguage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jToggleButtonConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonConnectActionPerformed
        HandleConnection();
    }//GEN-LAST:event_jToggleButtonConnectActionPerformed

    private void jTextFieldFullNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldFullNameKeyReleased
        Validate(1);
    }//GEN-LAST:event_jTextFieldFullNameKeyReleased

    private void jTextFieldServerKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldServerKeyReleased
        Validate(2);
    }//GEN-LAST:event_jTextFieldServerKeyReleased

    private void jTextFieldPortKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldPortKeyReleased
        Validate(3);
    }//GEN-LAST:event_jTextFieldPortKeyReleased

    private void jToggleButtonConnectStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jToggleButtonConnectStateChanged
        if (((JToggleButton) evt.getSource()).isSelected()) {
            jToggleButtonConnect.setText("Ngắt kết nối");
            jToggleButtonConnect.setBackground(MyColor.red);
            jToggleButtonConnect.setForeground(Color.white);
            jTextFieldInputChat.setEditable(true);
        } else {
            jToggleButtonConnect.setText("Kết nối");
            jToggleButtonConnect.setBackground(MyColor.green);
            jToggleButtonConnect.setForeground(Color.white);
            jTextFieldInputChat.setEditable(false);
        }
    }//GEN-LAST:event_jToggleButtonConnectStateChanged

    private void jTextFieldInputChatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldInputChatKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (CheckSystaxInput(jTextFieldInputChat.getText())) {
                SendData();
                jTextFieldInputChat.setText("");
            }
        }
    }//GEN-LAST:event_jTextFieldInputChatKeyPressed

    private void jButtonSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSendActionPerformed
        SendData();
        jTextFieldInputChat.setText("");
    }//GEN-LAST:event_jButtonSendActionPerformed

    private void jRadioButtonWeatherItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonWeatherItemStateChanged
        jComboBoxDate.setEnabled(jRadioButtonWeather.isSelected());
    }//GEN-LAST:event_jRadioButtonWeatherItemStateChanged

    private void jRadioButtonSimsimiItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonSimsimiItemStateChanged
        jComboBoxLanguage.setEnabled(jRadioButtonSimsimi.isSelected());
    }//GEN-LAST:event_jRadioButtonSimsimiItemStateChanged

    private void jTextFieldInputChatKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldInputChatKeyReleased
        if (CheckSystaxInput(jTextFieldInputChat.getText())) {
            jButtonSend.setEnabled(true);
        } else
            jButtonSend.setEnabled(false);
    }//GEN-LAST:event_jTextFieldInputChatKeyReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Client().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupOptionChat;
    private javax.swing.JButton jButtonSend;
    private javax.swing.JComboBox<String> jComboBoxDate;
    private javax.swing.JComboBox<String> jComboBoxLanguage;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabelStatus;
    private javax.swing.JLabel jLabelStatusTitle;
    private javax.swing.JList<String> jListMessage;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JRadioButton jRadioButtonLocationIP;
    private javax.swing.JRadioButton jRadioButtonPort;
    private javax.swing.JRadioButton jRadioButtonSimsimi;
    private javax.swing.JRadioButton jRadioButtonWeather;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPaneMessage;
    private javax.swing.JTextField jTextFieldFullName;
    private javax.swing.JTextField jTextFieldInputChat;
    private javax.swing.JTextField jTextFieldPort;
    private javax.swing.JTextField jTextFieldServer;
    private javax.swing.JTextPane jTextPaneTutorial;
    private javax.swing.JToggleButton jToggleButtonConnect;
    // End of variables declaration//GEN-END:variables
}
