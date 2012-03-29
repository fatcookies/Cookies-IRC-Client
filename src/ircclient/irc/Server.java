/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ircclient.irc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author fc
 */
public class Server {

    private String name, server, nick, nickPass;
    private String[] channels;
    private int port;
    private boolean autoConnect, log, ssl;

    public Server(String name, String server, int port, String nick, String[] channels, String nickPass, boolean autoConnect, boolean log, boolean ssl) {
        this.name = name;
        this.server = server;
        this.port = port;
        this.nick = nick;
        this.channels = channels;
        this.nickPass = nickPass;
        this.autoConnect = autoConnect;
        this.log = log;
        this.ssl = ssl;
    }

    public Server() {
    }

    public void write() {
        Properties p = new Properties();

        p.setProperty("name", name);
        p.setProperty("server", server);
        p.setProperty("port", Integer.toString(port));
        p.setProperty("nick", nick);
        p.setProperty("nickPass", nickPass);
        p.setProperty("channels", channelsToString(channels));
        p.setProperty("autoConnect", Boolean.toString(autoConnect));
        p.setProperty("log", Boolean.toString(log));
        p.setProperty("ssl", Boolean.toString(ssl));

        try {
            File f = new File("networks/" + name + "/" + name + ".xml");

            if (!f.exists() && new File("networks/" + name + "/").mkdir()
                    && new File("networks/" + name + "/log").mkdir()) {
                f.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream("networks/" + name + "/" + name + ".xml");
            p.storeToXML(fos, name);
            fos.close();
        } catch (FileNotFoundException fnfe) {
        } catch (IOException ioe) {
        }
    }

    public void read(String network) {
        Properties p = new Properties();
        try {
            FileInputStream fis = new FileInputStream("networks/" + network + "/" + network + ".xml");
            p.loadFromXML(fis);

            this.name = p.getProperty("name");
            this.server = p.getProperty("server");
            this.port = Integer.parseInt(p.getProperty("port"));
            this.nick = p.getProperty("nick");
            this.nickPass = p.getProperty("nickPass");
            this.channels = channelsToArray(p.getProperty("channels"));
            this.autoConnect = stringToBoolean(p.getProperty("autoConnect"));
            this.log = stringToBoolean(p.getProperty("log"));
            this.ssl = stringToBoolean(p.getProperty("ssl"));

            fis.close();
        } catch (FileNotFoundException fnfe) {
        } catch (IOException ioe) {
        }
    }

    public boolean delete() {
        return deleteDir(new File("networks/" + name + "/"));
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    public static String channelsToString(String[] chan) {
        StringBuilder sb = new StringBuilder();
        for (String s : chan) {
            sb.append(s + ",");
        }
        return sb.toString();
    }

    public static String[] channelsToArray(String channels) {
        return channels.split(",");
    }

    public static boolean stringToBoolean(String s) {
        return s.equals("true") ? true : false;
    }

    public static String booleanToString(boolean b) {
        return b ? "true" : "false";
    }

    public String getName() {
        return name;
    }

    public String getServer() {
        return server;
    }

    public String getNick() {
        return nick;
    }

    public String getNickPass() {
        return nickPass;
    }

    public String[] getChannels() {
        return channels;
    }

    public int getPort() {
        return port;
    }

    public boolean isAutoConnect() {
        return autoConnect;
    }

    public boolean isLogged() {
        return log;
    }

    public boolean isSSL() {
        return ssl;
    }
}
