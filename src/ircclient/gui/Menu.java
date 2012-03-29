package ircclient.gui;

import ircclient.gui.windows.JFontChooser;
import ircclient.gui.windows.RawWindow;
import ircclient.gui.windows.ChannelListWindow;
import ircclient.gui.windows.IRCWindow;
import ircclient.gui.windows.NetworksWindow;
import ircclient.irc.ServerConnection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 *
 * @author fc
 */
public class Menu extends JMenuBar {

    private JMenu file;
    private JMenu server;
    private JMenu tools;
    private JMenu help;
    private IRCWindow win;

    public Menu(IRCWindow win) {
        this.win = win;
        init();
    }

    public IRCWindow getWindow() {
        return win;
    }

    public void init() {
        file = new JMenu("File");
        server = new JMenu("Server");
        tools = new JMenu("Tools");
        help = new JMenu("Help");

        JMenuItem networks = new JMenuItem("Networks...");
        networks.addActionListener(networksListener);
        file.add(networks);

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(exitListener);
        file.add(exit);

        JMenuItem disconnect = new JMenuItem("Disconnect..");
        disconnect.addActionListener(disconnectListener);
        server.add(disconnect);

        JMenuItem joinchan = new JMenuItem("Join Channel...");
        joinchan.addActionListener(joinChanListener);
        server.add(joinchan);

        JMenuItem chanlist = new JMenuItem("Channel List...");
        chanlist.addActionListener(chatlistListener);
        server.add(chanlist);

        JMenuItem font = new JMenuItem("Font...");
        font.addActionListener(fontListener);
        tools.add(font);

        JMenuItem raw = new JMenuItem("Raw Log...");
        raw.addActionListener(rawListener);
        tools.add(raw);

        JMenuItem about = new JMenuItem("About...");
        about.addActionListener(aboutListener);
        help.add(about);

        this.add(file);
        this.add(server);
        this.add(tools);
        this.add(help);
    }

    public Menu getThis() {
        return this;
    }
    public ActionListener networksListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            new NetworksWindow(win);
        }
    };
    public ActionListener exitListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            win.exit();
        }
    };
    public ActionListener disconnectListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            try {
                int index = win.getTabbedPane().getSelectedIndex();
                win.quitServer(index);
            } catch (IOException ioe) {
            }
        }
    };
    public ActionListener joinChanListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            int index = win.getTabbedPane().getSelectedIndex();
            String chan = JOptionPane.showInputDialog(null, "Which channel do you wish to join\n(with # infront)", "Join Channel...:", JOptionPane.QUESTION_MESSAGE);

            if (chan != null && chan.startsWith("#")) {
                try {
                    win.getServerList().get(index).joinChan(chan);
                } catch (IOException ioe) {
                }
            }
        }
    };
    public ActionListener chatlistListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            ServerConnection serv = win.getServerList().get(win.getTabbedPane().getSelectedIndex()).getServer();
            ChannelListWindow win = new ChannelListWindow(serv);
            serv.getInput().setChanList(win);
            try {
                serv.getOutput().list();
            } catch (IOException ioe) {
            }
        }
    };
    public ActionListener fontListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            ChatArea ca = win.getServerList().get(0).getChannelList().get(0).getChat();
            JFontChooser fd = new JFontChooser(ca.getFont());
            fd.show();
            if (fd.getReturnStatus() == fd.RET_OK) {
                for (ServerPanel sp : win.getServerList()) {
                    for (ChannelPanel cp : sp.getChannelList()) {
                        cp.getChat().setFont(fd.getFont());
                    }
                }
            }
            fd.dispose();
        }
    };
    public ActionListener rawListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            new RawWindow();
        }
    };
    public ActionListener aboutListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            String msg = "Cookies IRC v0.2\nSome terrible\nNot Copyright 2010\n"
                    + "fc";
            JOptionPane.showMessageDialog(null, msg, "About", JOptionPane.INFORMATION_MESSAGE);
        }
    };
}
