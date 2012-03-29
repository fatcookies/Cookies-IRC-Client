package ircclient;

import ircclient.gui.Tray;
import ircclient.gui.windows.IRCWindow;

/**
 *
 * @author fc
 */
public class Main {

    public static void main(String[] args) {
        IRCWindow w = new IRCWindow("Cookies IRC V0.2");
        Tray t = new Tray(w);
        w.setTray(t);
        w.setVisible(true);
        // finger licking good
    }
}
