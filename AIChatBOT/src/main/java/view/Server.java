package view;

import utils.MyColor;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.border.LineBorder;
import utils.PatternRegEx;

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
    private StringBuilder strContent = new StringBuilder();
    private ExecutorService executor;
    private boolean isRunning = false;
    private ArrayList<String> clientConnect=new ArrayList<>();
    private int x,y;
    public Server() {
        initComponents();
        jPanelTop.add(new TitleBar(this));
        setLocationRelativeTo(null);
        SetFont();

    }

    private boolean OpenServer(int port) {
        executor = Executors.newCachedThreadPool();
        try {
            server = new ServerSocket(port);
            return true;
        } catch (IOException ex) {
            System.out.println(ex);
            return false;
        }
    }

    private void ServerRunning() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                isRunning = true;
                while (isRunning) {
                    try {
                        Socket socket = server.accept();
                        executor.execute(new WorkerServer(socket,clientConnect, jTextPaneClientConnect));
                    } catch (IOException ex) {
                        break;
                    }
                }
                executor.shutdown();
                System.out.println("finish");
            }
        }).start();
    }

    private void CloseConnect() {
        try {
            isRunning = false;
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

    private void SetFont() {
        jLabelStatus.setFont(new Font("Dialog", Font.BOLD, 12));
        jLabel4.setFont(new Font("Dialog", Font.BOLD, 12));
    }

    private void Validate() {
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
        }
        if (flagPort == true) {
            jToggleButtonOpenConnect.setEnabled(true);
        } else {
            jToggleButtonOpenConnect.setEnabled(false);
        }
    }

    private void HandleConnection() {
        int port = Integer.parseInt(jTextFieldPort.getText().trim());
        if (!jToggleButtonOpenConnect.isSelected()) {
            jTextFieldPort.setEditable(true);
            jTextFieldPort.setFocusable(true);
            System.out.println("Server close");
            jLabelStatus.setText("Server đang đóng");
            jLabelStatus.setForeground(MyColor.orange);
            CloseConnect();
        } else {
            if (OpenServer(port)) {
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

        jPanelMain = new javax.swing.JPanel();
        jPanelTop = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextPaneClientConnect = new javax.swing.JTextPane();
        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldPort = new javax.swing.JTextField();
        jToggleButtonOpenConnect = new javax.swing.JToggleButton();
        jLabel4 = new javax.swing.JLabel();
        jLabelStatus = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPaneContent = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        setIconImage(new ImageIcon("src\\main\\java\\images\\icons8-server-48.png").getImage());
        setUndecorated(true);

        jPanelMain.setBackground(MyColor.white);
        jPanelMain.setBorder(javax.swing.BorderFactory.createLineBorder(MyColor.titleBar));

        jPanelTop.setBackground(MyColor.titleBar);
        jPanelTop.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jPanelTopMouseDragged(evt);
            }
        });
        jPanelTop.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanelTopMousePressed(evt);
            }
        });
        jPanelTop.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 5));

        jPanel2.setBackground(MyColor.white);

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Client đang kết nối");

        jTextPaneClientConnect.setEditable(false);
        jScrollPane3.setViewportView(jTextPaneClientConnect);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane3)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 25)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("SERVER");

        jPanel1.setBackground(MyColor.white);

        jPanel3.setBackground(MyColor.white);

        jLabel1.setText("Port:");

        jTextFieldPort.setBackground(MyColor.input);
        jTextFieldPort.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldPortKeyReleased(evt);
            }
        });

        jToggleButtonOpenConnect.setText("Mở kết nối");
        jToggleButtonOpenConnect.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
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

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Trạng thái: ");

        jLabelStatus.setForeground(new java.awt.Color(238, 118, 0));
        jLabelStatus.setText("Server đang đóng");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldPort, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jToggleButtonOpenConnect, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextFieldPort, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToggleButtonOpenConnect, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabelStatus))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTextPaneContent.setEditable(false);
        jScrollPane1.setViewportView(jTextPaneContent);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 482, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanelMainLayout = new javax.swing.GroupLayout(jPanelMain);
        jPanelMain.setLayout(jPanelMainLayout);
        jPanelMainLayout.setHorizontalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 798, Short.MAX_VALUE)
            .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanelTop, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
                .addGroup(jPanelMainLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE))
        );
        jPanelMainLayout.setVerticalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 506, Short.MAX_VALUE)
            .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanelMainLayout.createSequentialGroup()
                    .addComponent(jPanelTop, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGap(3, 3, 3)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelMain, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void jPanelTopMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanelTopMousePressed
        x = evt.getX();
        y = evt.getY();
    }//GEN-LAST:event_jPanelTopMousePressed

    private void jPanelTopMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanelTopMouseDragged
        int xx = evt.getXOnScreen();
        int yy = evt.getYOnScreen();
        this.setLocation(xx - x, yy - y);
    }//GEN-LAST:event_jPanelTopMouseDragged

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
            @Override
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelTop;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField jTextFieldPort;
    private javax.swing.JTextPane jTextPaneClientConnect;
    private javax.swing.JTextPane jTextPaneContent;
    private javax.swing.JToggleButton jToggleButtonOpenConnect;
    // End of variables declaration//GEN-END:variables
}
