package view;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 *
 * @author HUNGVUONG
 */
public class TitleBar extends javax.swing.JPanel {

    private int x, y;
    private int width, height, flag = 0;
    private Point point;

    public TitleBar(JFrame jframe) {
        initComponents();
        Event(jframe);
    }

    private void Event(JFrame jframe) {
        jLabelMinimize.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                jframe.setState(JFrame.ICONIFIED); // To minimize a frame                
            }
        });

        jLabelMaximize.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (flag == 0) {
                    point = jframe.getLocation();
                    width = jframe.getWidth();
                    height = jframe.getHeight();
                    jframe.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    jLabelMaximize.setIcon(new ImageIcon("src\\main\\java\\images\\icons8-restore-window-24.png"));
                    jLabelMaximize.setToolTipText("Restore Down");
                    flag = 1;
                } else {
                    jframe.setSize(width, height);
                    jframe.setLocation(point);
                    jLabelMaximize.setIcon(new ImageIcon("src\\main\\java\\images\\icons8-maximize-window-24.png"));
                    jLabelMaximize.setToolTipText("Maximize");
                    flag = 0;
                }
            }
        });

        jLabelClose.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelMinimize = new javax.swing.JLabel();
        jLabelMaximize = new javax.swing.JLabel();
        jLabelClose = new javax.swing.JLabel();

        setBackground(null);
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });
        java.awt.FlowLayout flowLayout1 = new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 5);
        flowLayout1.setAlignOnBaseline(true);
        setLayout(flowLayout1);

        jLabelMinimize.setIcon(new ImageIcon("src\\main\\java\\images\\icons8-minimize-window-24.png"));
        jLabelMinimize.setToolTipText("Minimize");
        add(jLabelMinimize);

        jLabelMaximize.setIcon(new ImageIcon("src\\main\\java\\images\\icons8-maximize-window-24.png"));
        jLabelMaximize.setToolTipText("Maximize");
        add(jLabelMaximize);

        jLabelClose.setIcon(new ImageIcon("src\\main\\java\\images\\icons8-close-window-24.png"));
        jLabelClose.setToolTipText("Close");
        add(jLabelClose);
    }// </editor-fold>//GEN-END:initComponents

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        x = evt.getX();
        y = evt.getY();
    }//GEN-LAST:event_formMousePressed

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        int xx = evt.getXOnScreen();
        int yy = evt.getYOnScreen();
        this.setLocation(xx - x, yy - y);
    }//GEN-LAST:event_formMouseDragged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelClose;
    private javax.swing.JLabel jLabelMaximize;
    private javax.swing.JLabel jLabelMinimize;
    // End of variables declaration//GEN-END:variables
}
