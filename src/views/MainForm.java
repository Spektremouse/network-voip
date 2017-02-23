package views;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by thomaspachico on 22/02/2017.
 */
public class MainForm
{
    private JButton btn_connect;
    public JPanel mainPanel;
    private JRadioButton rbtn_generic;
    private JRadioButton rbtn_repetition;
    private JPanel pnl_strategy;
    private JRadioButton rbtn_fill;

    public MainForm()
    {
        groupButton();
        btn_connect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Hello World!");
            }
        });
    }

    private void groupButton( ) {

        ButtonGroup bg1 = new ButtonGroup( );

        bg1.add(rbtn_generic);
        bg1.add(rbtn_repetition);
        bg1.add(rbtn_fill);
    }
}
