package ircclient.gui;

import ircclient.gui.windows.IRCWindow;
import ircclient.irc.ServerConnection;
import java.awt.BorderLayout;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 *
 * @author fc
 */
public class ServerPanel extends JPanel {

    private JTabbedPane channels;
    private ArrayList<ChannelPanel> chanList = new ArrayList<ChannelPanel>();
    private ServerConnection serv;
    private IRCWindow win;
    public static final String SERVER_PANEL = "+Server";
    private String name;
    private boolean log, ssl, away;

    public ServerPanel(IRCWindow win, String name, String server, int port, String nick, String[] autojoin, String nickPass, boolean log, boolean ssl) {
        ServerConnection s = new ServerConnection(server, port, nick, autojoin, nickPass, this);
        this.serv = s;
        this.win = win;
        this.name = name;
        this.log = log;
        this.ssl = ssl;

        channels = new JCloseTabbedPane(this);
        this.setLayout(new BorderLayout());

        this.add(channels, BorderLayout.CENTER);
        ChannelPanel cp = joinServer(SERVER_PANEL);
    }

    public ChannelPanel getChannelPane(int i) {
        return chanList.get(i);
    }

    public JTabbedPane getTabbedPane() {
        return channels;
    }

    public IRCWindow getWindow() {
        return win;
    }

    public ChannelPanel getChannelPane(String chan) {
        String chann = chan.toLowerCase();
        ChannelPanel cpp = null;
        for (ChannelPanel cp : chanList) {
            if (cp.getChannel().toLowerCase().equals(chann)) {
                cpp = cp;
            }
        }
        return (cpp != null) ? cpp : chanList.get(0);
    }

    public String getChannel(int i) {
        return chanList.get(i).getChannel();
    }

    public ChannelPanel joinServer(String chan) {
        ChannelPanel ca = new ChannelPanel(chan, this);
        channels.add(chan, ca);
        chanList.add(ca);
        channels.setSelectedComponent(ca);
        return ca;
    }

    public ArrayList<ChannelPanel> getChannelList() {
        return chanList;
    }

    public ChannelPanel joinChan(String chan) throws IOException {
        if (!chan.equalsIgnoreCase(SERVER_PANEL) && !isInChannel(chan)) {
            getServer().getOutput().join(chan);
            ChannelPanel ca = new ChannelPanel(chan, this, true);
            channels.add(chan, ca);
            chanList.add(ca);
            channels.setSelectedComponent(ca);
            return ca;
        } else if (isInChannel(chan)) {
            return null;
        } else {
            return null;
        }
    }

    public boolean isInChannel(String chan) {
        boolean inChan = false;
        for (ChannelPanel cp : chanList) {
            if (chan.equalsIgnoreCase(cp.getChannel())) {
                inChan = true;
                break;
            } else {
                inChan = false;
            }
        }
        return inChan;
    }

    public void partChan(String chan) throws IOException {
        if (!getChannelPane(chan).getChannel().equalsIgnoreCase(SERVER_PANEL)) {
            getServer().getOutput().part(chan);
            channels.remove(getChannelPane(chan));
        }
    }

    public void partChan(int i) throws IOException {
        if (i != 0) {
            getServer().getOutput().part(getChannel(i));
            channels.remove(chanList.get(i));
            chanList.remove(i);
        }
    }

    public void partChat(int i) {
        if (i != 0) {
            channels.remove(chanList.get(i));
            chanList.remove(i);
        }
    }

    public void changeNick(String nick) {
        getServer().setNick(nick);
        for (ChannelPanel cp : chanList) {
            cp.getNickButton().setText(nick);
        }
    }

    public void away(String s) throws IOException {
        if (!away) {
            serv.getOutput().away(s);
            away = true;
        } else {
            serv.getOutput().away();
            away = false;
        }
    }

    public void setServer(ServerConnection serv) {
        this.serv = serv;
    }

    public ServerConnection getServer() {
        return serv;
    }

    public String getServerName() {
        return name;
    }

    public boolean isLogged() {
        return log;
    }

    public boolean isSSL() {
        return ssl;
    }
}
