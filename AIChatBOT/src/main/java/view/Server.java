package view;

import client.DataClient;
import resource.MyColor;
import resource.MyString;
import service.simsimi.Request;
import service.simsimi.Respone;
import com.google.gson.Gson;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.DefaultListModel;
import javax.swing.JToggleButton;
import javax.swing.border.LineBorder;
import service.CallAPI;

/**
 *
 * @author HUNGVUONG
 */
public class Server extends javax.swing.JFrame {

    private boolean flagPort = false;
    private ServerSocket server;
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private final int numThread = 10;
    private final StringBuilder strContent = new StringBuilder();
    private final DefaultListModel<String> model = new DefaultListModel<>();
    private boolean flagConnect = false;
    private ExecutorService executor;

    public Server() {
        initComponents();
        setLocationRelativeTo(null);
        SetFont();

    }

    private void OpenServer(int port) {
        flagConnect = true;
        executor = Executors.newFixedThreadPool(numThread);
        try {
            server = new ServerSocket(port);
        } catch (IOException ex) {
            flagConnect = false;
            System.out.println(ex);
        }
    }

    private void ServerRunning() {
        new Thread(() -> {
            while (true) {
                try {
                    socket = server.accept();
                    executor.execute(new Thread(() -> {
                        model.addElement(socket.getInetAddress().getHostName() + " - " + socket.getInetAddress().getHostAddress());
                        jListClientConnect.setModel(model);
                        jListClientConnect.ensureIndexIsVisible(model.size() - 1);
                        while (true) {
                            ReturnResultToClient();
                        }
                    }));

                } catch (IOException ex) {
                    System.out.println(ex);
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (flagConnect == false) {
                    break;
                }
            }
        }).start();

    }

    private void CloseConnect() {
        try {
            flagConnect = false;
            strContent.append("Server Closed\n");
            jTextPaneContent.setText(strContent.toString());
            if (ois != null) {
                ois.close();
            }
            if (oos != null) {
                oos.close();
            }
            if (socket != null) {
                socket.close();
            }
            server.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Respone ParseSimsimi(String json) {
        Gson gson = new Gson();
        Respone respone = gson.fromJson(json, Respone.class);
        return respone;
    }

    private DataClient HandleInputFromClient() {
        DataClient result = null;
        try {
            ois = new ObjectInputStream(socket.getInputStream());
            DataClient dataClient = (DataClient) ois.readObject();
            String option = dataClient.getOption();
            switch (option) {
                case MyString.WEATHER -> {
                }
                case MyString.LOCATION_IP -> {
                }
                case MyString.SCAN_PORT -> {
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
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    private void ReturnResultToClient() {
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(HandleInputFromClient());
            oos.flush();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void SetFont() {
        jLabelStatus.setFont(new Font("Dialog", Font.BOLD, 12));
        jLabel4.setFont(new Font("Dialog", Font.BOLD, 12));
    }

    private void Validate() {
        Pattern patternPort = Pattern.compile("^[1-9]\\d+$");
        String portString = jTextFieldPort.getText().trim();

        if (patternPort.matcher(portString).matches()) {
            int portInt = Integer.parseInt(portString);
            if ((portInt < 1024 || portInt > 49151)) {
                jTextFieldPort.setBorder(new LineBorder(MyColor.red, 2));
                flagPort = false;
            } else {
                jTextFieldPort.setBorder(new LineBorder(MyColor.green, 2));
                flagPort = true;
            }
        }
        if (flagPort == true) {
            jToggleButtonOpenConnect.setEnabled(true);
        } else {
            jToggleButtonOpenConnect.setEnabled(false);
        }
    }

    private void HandleConnection() {
        int port = Integer.parseInt(jTextFieldPort.getText().trim());
        if (jToggleButtonOpenConnect.isSelected() == false) {
            jTextFieldPort.setEditable(true);
            jTextFieldPort.setFocusable(true);
            System.out.println("Server close");
            jLabelStatus.setText("Server đang đóng");
            jLabelStatus.setForeground(MyColor.orange);
            CloseConnect();
        } else {
            OpenServer(port);
            if (flagConnect == true) {
                System.out.println("Server open");
                jLabelStatus.setText("Server đang mở");
                jLabelStatus.setForeground(MyColor.green);

                strContent.replace(0, strContent.length(), "");
                strContent.append("Server Started\n");
                jTextPaneContent.setText(strContent.toString());
                jTextFieldPort.setEditable(false);

                ServerRunning();
            } else {
                System.out.println("Mở server thất bại");
                jLabelStatus.setText("Mở server thất bại");
                jLabelStatus.setForeground(MyColor.red);
                jToggleButtonOpenConnect.setSelected(false);
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldPort = new javax.swing.JTextField();
        jToggleButtonOpenConnect = new javax.swing.JToggleButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPaneContent = new javax.swing.JTextPane();
        jLabel4 = new javax.swing.JLabel();
        jLabelStatus = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jListClientConnect = new javax.swing.JList<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 25)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("SERVER");

        jLabel1.setText("Port:");

        jTextFieldPort.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldPortKeyReleased(evt);
            }
        });

        jToggleButtonOpenConnect.setText("Mở kết nối");
        jToggleButtonOpenConnect.setEnabled(false);
        jToggleButtonOpenConnect.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jToggleButtonOpenConnectStateChanged(evt);
            }
        });
        jToggleButtonOpenConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonOpenConnectActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldPort)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToggleButtonOpenConnect, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldPort, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jToggleButtonOpenConnect, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTextPaneContent.setEditable(false);
        jScrollPane1.setViewportView(jTextPaneContent);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Trạng thái: ");

        jLabelStatus.setForeground(new java.awt.Color(238, 118, 0));
        jLabelStatus.setText("Server đang đóng");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabelStatus))
                .addGap(11, 11, 11)
                .addComponent(jScrollPane1)
                .addContainerGap())
        );

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Client đang kết nối");

        jScrollPane2.setViewportView(jListClientConnect);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE))
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldPortKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldPortKeyReleased
        Validate();
    }//GEN-LAST:event_jTextFieldPortKeyReleased

    private void jToggleButtonOpenConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonOpenConnectActionPerformed
        HandleConnection();
    }//GEN-LAST:event_jToggleButtonOpenConnectActionPerformed

    private void jToggleButtonOpenConnectStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jToggleButtonOpenConnectStateChanged
        if (((JToggleButton) evt.getSource()).isSelected()) {
            jToggleButtonOpenConnect.setText("Ngắt kết nối");
            jToggleButtonOpenConnect.setBackground(MyColor.red);
            jToggleButtonOpenConnect.setForeground(Color.white);
        } else {
            jToggleButtonOpenConnect.setText("Mở kết nối");
            jToggleButtonOpenConnect.setBackground(MyColor.green);
            jToggleButtonOpenConnect.setForeground(Color.white);
        }
    }//GEN-LAST:event_jToggleButtonOpenConnectStateChanged

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
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Server().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelStatus;
    private javax.swing.JList<String> jListClientConnect;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jTextFieldPort;
    private javax.swing.JTextPane jTextPaneContent;
    private javax.swing.JToggleButton jToggleButtonOpenConnect;
    // End of variables declaration//GEN-END:variables
}
