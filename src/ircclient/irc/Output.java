/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ircclient.irc;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 *
 * @author fc
 */
public class Output {

    private BufferedWriter bw;

    public Output(BufferedWriter writer) {
        this.bw = writer;
    }

    public void pong(String s) throws IOException {
        bw.write("PONG " + s + "\n");
        bw.flush();
    }

    public void away(String s) throws IOException {
        bw.write("AWAY " + s + "\n");
        bw.flush();
    }

    public void away() throws IOException {
        bw.write("AWAY" + "\n");
        bw.flush();
    }

    public void say(String reciep, String message) throws IOException {
        bw.write("PRIVMSG " + reciep + " :" + message + "\n");
        bw.flush();
    }

    public void ctcpsay(String reciep, String message) throws IOException {
        bw.write("PRIVMSG " + reciep + " :\u0001ACTION " + message + "\u0001\n");
        bw.flush();
    }

    public void ctcp(String reciep, String ctcp) throws IOException {
        bw.write("PRIVMSG " + reciep + " :\u0001" + ctcp + "\u0001\n");
        bw.flush();
    }

    public void sendNotice(String reciep, String message) throws IOException {
        bw.write("NOTICE " + reciep + " " + message + "\n");
        bw.flush();
    }

    public void join(String chan) throws IOException {
        bw.write("JOIN " + chan + "\n");
        bw.flush();
    }

    public void part(String chan) throws IOException {
        bw.write("PART " + chan + "\n");
        bw.flush();
    }

    public void quit() throws IOException {
        bw.write("QUIT " + "fuck ya all" + "\n");
        bw.flush();
    }

    public void nick(String nick) throws IOException {
        bw.write("NICK " + nick + "\n");
        bw.flush();
    }

    public void login(String nick) throws IOException {
        bw.write("NICK " + nick + "\n");
        bw.write("USER  " + nick + " 8 *  : Cookies IRC\n");
        bw.flush();
    }

    public void topic(String chan, String topic) throws IOException {
        bw.write("TOPIC " + chan + " :" + topic + "\n");
        bw.flush();
    }

    public void ban(String host, String chan) throws IOException {
        bw.write("MODE " + chan + " +b " + host + "\n");
        bw.flush();
    }

    public void unban(String host, String chan) throws IOException {
        bw.write("MODE " + chan + " -b " + host + "\n");
        bw.flush();
    }

    public void kick(String nick, String chan) throws IOException {
        bw.write("KICK " + chan + " " + nick + "\n");
        bw.flush();
    }

    public void setMode(String mode, String channel) throws IOException {
        bw.write("MODE " + channel + " " + mode + "\n");
        bw.flush();
    }

    public void setMode(String mode, String channel, String nick) throws IOException {
        bw.write("MODE " + channel + " " + mode + " " + nick + "\n");
        bw.flush();
    }

    public void whois(String nick) throws IOException {
        bw.write("WHOIS " + nick + "\n");
        bw.flush();
    }

    public void getNames(String channel) throws IOException {
        bw.write("NAMES " + channel + "\n");
        bw.flush();
    }

    public void userhost(String nick) throws IOException {
        bw.write("USERHOST " + nick + "\n");
        bw.flush();
    }

    public void list() throws IOException {
        bw.write("LIST\n");
        bw.flush();
    }
}




