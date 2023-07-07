package xmpp;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smackx.mam.MamManager;
import org.jivesoftware.smackx.mam.element.MamElements;
import org.jxmpp.stringprep.XmppStringprepException;


import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;   
import java.util.Set;


public class mainL extends JFrame {
    private JPanel aks;
    private JList<String> list1;
    private JPanel chatPanel;
    private JList list2;
    private JTextArea textArea1;
    private JButton sendButton;
    private JScrollPane jsc;
    String curSel;
    MamManager mamManager;
    String username, password;

    public void scroll() {
        JScrollBar v = jsc.getVerticalScrollBar();
        v.setValue(v.getMaximum());
        v.setValue(v.getMaximum());
    }

    void login() {
        username = JOptionPane.showInputDialog("Enter Username ");
        password = JOptionPane.showInputDialog("Enter Password");
    }

    public mainL() throws HeadlessException {
        login();
        setContentPane(aks);
        setTitle("Hi");
        //    setSize(450,450);
        pack();
        DefaultListModel<String> list = (DefaultListModel<String>) list1.getModel();
        DefaultListModel<String> msgss = (DefaultListModel<String>) list2.getModel();

        list1.setPreferredSize(new Dimension(300,600));

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        XConnection xConnection = new XConnection(new RECallback() {
            @Override
            public void setRE(Set<RosterEntry> rosterEntries) {
                System.out.println("HEHE");
                list.clear();
                for (RosterEntry x : rosterEntries
                ) {
                    list.addElement(x.getJid().toString());

                }
            }

            @Override
            public void setLM(List<MamElements.MamResultExtension> ak, List<Message> messages) {
                msgss.clear();
                for (Message m : messages
                ) {
                    System.out.println(m.getBody());
                    msgss.addElement("From : " + m.getFrom().asBareJid().toString() + " To : " + m.getTo().asBareJid().toString() + " Message : " + m.getBody());
                }
                list2.ensureIndexIsVisible(msgss.getSize() - 1);


            }

            @Override
            public void newMsg(String username, Message msg) {
                if (username.contains(curSel)) {
                    msgss.addElement("From : " + msg.getFrom().asBareJid().toString() + " To : " + msg.getTo().asBareJid().toString() + " Message : " + msg.getBody());
                    list2.ensureIndexIsVisible(msgss.getSize() - 1);
                }
            }

        });

        list1.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    curSel = list1.getSelectedValue();
                    System.out.println(curSel);
                    try {
                        xConnection.loadmsg(curSel);
                    } catch (XmppStringprepException ex) {
                        ex.printStackTrace();
                    } catch (XMPPException.XMPPErrorException ex) {
                        ex.printStackTrace();
                    } catch (SmackException.NotConnectedException ex) {
                        ex.printStackTrace();
                    } catch (SmackException.NoResponseException ex) {
                        ex.printStackTrace();
                    } catch (SmackException.NotLoggedInException ex) {
                        ex.printStackTrace();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        setVisible(true);

        xConnection.setUP(username, password);
        if (xConnection.connect()) {
            JOptionPane.showConfirmDialog(aks, "Logged In !");
        } else {
            JOptionPane.showConfirmDialog(aks, "Error ! ");

        }

        try {
            xConnection.loadroster();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (curSel.length() > 0 && textArea1.getText().length() > 0) {
                    try {
                        xConnection.sendMsg(curSel, textArea1.getText());
                        textArea1.setText("");
                        xConnection.loadmsg(curSel);
                    } catch (XmppStringprepException ex) {
                        ex.printStackTrace();
                    } catch (SmackException.NotConnectedException ex) {
                        ex.printStackTrace();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    } catch (XMPPException.XMPPErrorException ex) {
                        ex.printStackTrace();
                    } catch (SmackException.NoResponseException ex) {
                        ex.printStackTrace();
                    } catch (SmackException.NotLoggedInException ex) {
                        ex.printStackTrace();
                    }

                }
            }
        });
    }


    public static void main(String ar[]) {
        mainL mainL_ = new mainL();

    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }


}