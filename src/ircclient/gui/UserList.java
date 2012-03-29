package ircclient.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 *
 * @author fc
 */
public class UserList extends JList {

    private ChannelPanel cp;
    private SortedListModel userlistModel;
    private JPopupMenu menu;

    public UserList(ChannelPanel cp) {
        super();
        this.cp = cp;
        createMenu();

        DefaultListModel list = new DefaultListModel();
        userlistModel = new SortedListModel(list);
        setModel(userlistModel);
    }

    public void createMenu() {
        menu = new JPopupMenu();
        JMenuItem pm = new JMenuItem("Private Message..");
        JMenuItem whois = new JMenuItem("Whois");
        JMenu oper = new JMenu("Operator actions");
        JMenuItem giveop = new JMenuItem("Give op");
        JMenuItem takeop = new JMenuItem("Take op");
        JMenuItem giveVoice = new JMenuItem("Give voice");
        JMenuItem takeVoice = new JMenuItem("Take voice");
        JMenuItem kick = new JMenuItem("Kick");
        JMenuItem ban = new JMenuItem("Ban");
        JMenuItem kickban = new JMenuItem("Kickban");

        menu.add(whois);
        menu.add(pm);
        menu.add(oper);

        oper.add(giveop);
        oper.add(takeop);
        oper.add(giveVoice);
        oper.add(takeVoice);
        oper.addSeparator();
        oper.add(kick);
        oper.add(ban);
        oper.add(kickban);

        whois.addActionListener(whoisListener);
        pm.addActionListener(pmListener);
        giveop.addActionListener(operListener);
        takeop.addActionListener(operListener);
        giveVoice.addActionListener(operListener);
        takeVoice.addActionListener(operListener);
        kick.addActionListener(operListener);
        ban.addActionListener(operListener);
        kickban.addActionListener(operListener);
        addMouseListener(menuListener);
    }

    public JPopupMenu getMenu() {
        return menu;
    }

    public SortedListModel getList() {
        return userlistModel;
    }

    public UserList getThis() {
        return this;
    }

    public ChannelPanel getChannelPanel() {
        return cp;
    }
    public MouseAdapter menuListener = new MouseAdapter() {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e) && !isSelectionEmpty() && locationToIndex(e.getPoint()) == getSelectedIndex()) {
                getMenu().show(getThis(), e.getX(), e.getY());
            }
        }
    };
    public ActionListener pmListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            String nick = getList().getElementAt(getSelectedIndex()).toString();
            if (nick.startsWith("@") || nick.startsWith("&") || nick.startsWith("+")
                    || nick.startsWith("%") || nick.startsWith("~")) {
                nick = nick.substring(1);
            }

            getChannelPanel().getServPane().joinServer(nick);

        }
    };
    public ActionListener whoisListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            try {
                String nick = getList().getElementAt(getSelectedIndex()).toString();
                if (nick.startsWith("@") || nick.startsWith("&") || nick.startsWith("+")
                        || nick.startsWith("%") || nick.startsWith("~")) {
                    nick = nick.substring(1);
                }

                getChannelPanel().getServPane().getServer().getOutput().whois(nick);
            } catch (IOException ioe) {
            }
        }
    };
    public ActionListener operListener = new ActionListener() {

        public synchronized void actionPerformed(ActionEvent e) {
            new OperatorActions(getThis(), e);
        }
    };
}
