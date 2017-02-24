package views;

import interfaces.IStrategy;
import interfaces.IThreadCallback;
import models.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by thomaspachico on 22/02/2017.
 */
public class MainForm implements IThreadCallback
{
    private JButton btn_connect;
    public JPanel mainPanel;
    private JRadioButton rbtn_generic;
    private JRadioButton rbtn_repetition;
    private JPanel pnl_strategy;
    private JRadioButton rbtn_fill;
    private JRadioButton rbtn_test;
    private JRadioButton rbtn_voice;
    private JRadioButton rbtn_default;
    private JRadioButton rbtn_socket2;
    private JRadioButton rbtn_socket3;
    private JRadioButton rbtn_socket4;
    private JTextField txt_host;
    private JRadioButton rbtn_on;
    private JRadioButton rbtn_off;
    private JProgressBar progressBar;

    //Model related stuff
    private IStrategy mStrategy = new GenericStrategy();
    private boolean mIsInterleave = true;
    private TransmissionType mTransmissionType = TransmissionType.VOICE;
    private DatagramType mDatagramType = DatagramType.DEFAULT;
    private String mHostname = "localhost";
    private ReceiverThread receiver;
    private SenderThread sender;
    private IThreadCallback form = this;

    public MainForm()
    {
        btn_connect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mHostname = txt_host.getText();

                try
                {
                    receiver = new ReceiverThread(mStrategy, mDatagramType, form);
                    sender = new SenderThread(mHostname, mDatagramType,
                            mTransmissionType,mIsInterleave);

                    receiver.start();
                    sender.start();

                    btn_connect.setEnabled(false);
                }
                catch (Exception ex)
                {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        });

        rbtn_generic.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mStrategy = new GenericStrategy();
            }
        });
        rbtn_repetition.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mStrategy = new RepetitionStrategy();
            }
        });
        rbtn_fill.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mStrategy = new FillingStrategy();
            }
        });
        rbtn_on.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mIsInterleave = true;
            }
        });
        rbtn_off.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mIsInterleave = false;
            }
        });
        rbtn_test.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mTransmissionType = TransmissionType.TEST;
            }
        });
        rbtn_voice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mTransmissionType = TransmissionType.VOICE;
            }
        });
        rbtn_default.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mDatagramType = DatagramType.DEFAULT;
            }
        });
        rbtn_socket2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mDatagramType = DatagramType.SOCKET2;
            }
        });
        rbtn_socket3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mDatagramType = DatagramType.SOCKET3;
            }
        });
        rbtn_socket4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mDatagramType = DatagramType.SOCKET4;
            }
        });

        groupButton();
    }

    private void groupButton()
    {
        ButtonGroup strategy = new ButtonGroup();

        strategy.add(rbtn_generic);
        strategy.add(rbtn_repetition);
        strategy.add(rbtn_fill);
        rbtn_generic.setSelected(true);

        ButtonGroup type = new ButtonGroup();

        type.add(rbtn_test);
        type.add(rbtn_voice);
        rbtn_voice.setSelected(true);

        ButtonGroup interleave = new ButtonGroup();

        interleave.add(rbtn_on);
        interleave.add(rbtn_off);
        rbtn_on.setSelected(true);

        ButtonGroup socket = new ButtonGroup();

        socket.add(rbtn_default);
        socket.add(rbtn_socket2);
        socket.add(rbtn_socket3);
        socket.add(rbtn_socket4);
        rbtn_default.setSelected(true);

        progressBar.setMaximum(1000);
    }

    @Override
    public void onComplete()
    {
        if(mTransmissionType == TransmissionType.TEST)
        {
            JOptionPane.showMessageDialog(null, receiver.mBuilder.toString());
        }
        else if(mTransmissionType == TransmissionType.VOICE)
        {
            JOptionPane.showMessageDialog(null, "Transmission complete!");
        }
        btn_connect.setEnabled(true);
    }

    @Override
    public void onCanceled()
    {
        JOptionPane.showMessageDialog(null, "Transmission canceled.");
        btn_connect.setEnabled(true);
    }

    @Override
    public void onUpdate(String message, int progress) {
        progressBar.setValue(progress);
        progressBar.setStringPainted(true);
    }
}
