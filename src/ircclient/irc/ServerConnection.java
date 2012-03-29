package ircclient.irc;

import ircclient.gui.ServerPanel;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.JOptionPane;

/**
 *
 * @author fc
 */
public class ServerConnection extends Thread {

    private String server, nick, nickPass;
    private String[] channels;
    private int port;
    private Socket sock;
    private ServerPanel sp;
    private BufferedReader br;
    private BufferedWriter bw;
    private Input in;
    public Output out;

    public ServerConnection(String server, int port, String nick, String[] channels, String nickPass, ServerPanel sp) {
        this.server = server;
        this.port = port;
        this.nick = nick;
        this.channels = channels;
        this.nickPass = nickPass;
        this.sp = sp;
        this.start();
    }

    public ServerConnection(String nick) {
        this.nick = nick;
    }

    @Override
    public void run() {
        try {
            if (sp.isSSL()) {
                SocketFactory socketFactory = SSLSocketFactory.getDefault();
                sock = socketFactory.createSocket(server, port);
            } else {
                sock = new Socket(server, port);
            }


            bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
            br = new BufferedReader(new InputStreamReader(sock.getInputStream()));

            out = new Output(bw);
            out.login(getNick());
            in = new Input(this);

            String currLine;
            while ((currLine = br.readLine()) != null) {
                System.out.println(currLine);
                in.parse(currLine);
            }

        } catch (UnknownHostException uhe) {
            JOptionPane.showMessageDialog(null, "Unknown host:\n" + server + ":" + port,
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(null, "Error connecting to:\n" + server + ":" + port,
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public String getServer() {
        return server;
    }

    public int getPort() {
        return port;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String[] getAutojoin() {
        return channels;
    }

    public String nickPass() {
        return nickPass;
    }

    public ServerPanel getServerPanel() {
        return sp;
    }

    public Output getOutput() {
        return out;
    }

    public Input getInput() {
        return in;
    }

    public BufferedReader getReader() {
        return br;
    }
}
