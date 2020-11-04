package view;

import DTO.DataClient;
import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author HUNGVUONG
 */
public class Item extends javax.swing.JPanel implements ListCellRenderer<DataClient> {

    private final FlowLayout flowLayout;
    public Item() {
        initComponents();
        flowLayout = (FlowLayout) this.getLayout();        
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setAutoscrolls(true);
        java.awt.FlowLayout flowLayout1 = new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT);
        flowLayout1.setAlignOnBaseline(true);
        setLayout(flowLayout1);

        jLabel1.setForeground(new java.awt.Color(0, 102, 102));
        jLabel1.setText("jLabel1");
        add(jLabel1);
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public Component getListCellRendererComponent(JList<? extends DataClient> list, DataClient value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value.getType() == 1) {
            jLabel1.setText(value.getMessage());
            flowLayout.setAlignment(FlowLayout.LEFT);
//            setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 0), timeTmp, TitledBorder.CENTER, TitledBorder.ABOVE_TOP, new Font("Dialog", 0, 12), new Color(153, 153, 153)));
        } else {
            flowLayout.setAlignment(FlowLayout.RIGHT);
            jLabel1.setText(value.getMessage());
//            setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 0), timeTmp, TitledBorder.CENTER, TitledBorder.ABOVE_TOP, new Font("Dialog", 0, 12), new Color(153, 153, 153)));

        }
        return this;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
