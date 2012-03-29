package ircclient.gui;

import ircclient.irc.Output;
import java.awt.event.ActionEvent;
import java.io.IOException;

/**
 *
 * @author fc
 */
public class OperatorActions extends Thread {

    UserList ul;
    ActionEvent e;

    public OperatorActions(UserList ul, ActionEvent e) {
        this.ul = ul;
        this.e = e;
        this.start();

    }

    public OperatorActions(UserList ul) {
        this.ul = ul;
    }

    @Override
    public void run() {
        System.out.println(e.getActionCommand());
        try {
            String nick = ul.getList().getElementAt(ul.getSelectedIndex()).toString();
            if (nick.startsWith("@") || nick.startsWith("&") || nick.startsWith("+")
                    || nick.startsWith("%") || nick.startsWith("~")) {
                nick = nick.substring(1);
            }

            Output o = ul.getChannelPanel().getServPane().getServer().getOutput();
            if (e.getActionCommand().equals("Kick")) {
                kick(nick, o);

            } else if (e.getActionCommand().equals("Ban")) {
                ban(nick, o);

            } else if (e.getActionCommand().equals("Give op")) {
                o.setMode("+o", ul.getChannelPanel().getChannel(), nick);

            } else if (e.getActionCommand().equals("Take op")) {
                o.setMode("-o", ul.getChannelPanel().getChannel(), nick);

            } else if (e.getActionCommand().equals("Give voice")) {
                o.setMode("+v", ul.getChannelPanel().getChannel(), nick);

            } else if (e.getActionCommand().equals("Take voice")) {
                o.setMode("-v", ul.getChannelPanel().getChannel(), nick);

            } else {
                ban(nick, o);
                kick(nick, o);
            }

        } catch (IOException ioe) {
        }
    }

    public void kick(String nick, Output o) throws IOException {
        o.kick(nick, ul.getChannelPanel().getChannel());
    }

    public void ban(String nick, Output o) throws IOException {

        o.userhost(nick);
        while (!ul.getChannelPanel().getServPane().getServer().getInput().isUserHost()) {
        }
        String host = ul.getChannelPanel().getServPane().getServer().getInput().getUserHost();
        if (host != null) {
            String[] split = host.split("@");
            System.out.println(host);
            o.ban("*!*" + split[0] + "@" + split[1], ul.getChannelPanel().getChannel());
        }
    }
}


