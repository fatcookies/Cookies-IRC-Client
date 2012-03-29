package ircclient.irc;

import ircclient.gui.windows.ChannelListWindow;
import ircclient.gui.ChannelPanel;
import ircclient.gui.ServerPanel;
import ircclient.gui.SortedListModel;
import ircclient.gui.Tray;
import java.io.IOException;

/**
 *
 * @author fc
 */
public class Input {

    private ServerConnection serv;
    private String currUserHost;
    private boolean isUserHost;
    private boolean log;
    private ChannelListWindow chanlist;

    public Input(ServerConnection serv) {
        this.serv = serv;
        this.log = serv.getServerPanel().isLogged();
    }

    public void parse(String in) {
        String split[] = in.split(" ");

        if (in.startsWith("PING")) { // Handles PING command and sends PONG
            parsePing(in);

        } else if (split[1].equals("PRIVMSG")) { // Handles all msgs.
            parsePrivmsg(in, split);

        } else if (split[1].equals("NOTICE")) { // Prints Notice messages
            parseNotice(in, split);

        } else if (split[1].equals("PART") || split[1].equals("QUIT")) { // Handles joins,parts and quits
            parseLeave(in, split);

        } else if (split[1].equals("JOIN")) { // Handles user joins to channels
            parseJoin(in, split);

        } else if (split[1].equals("KICK")) { // Handles kicks (Including user)
            parseKick(in, split);

        } else if (split[1].equals("MODE")) { //Handles all modes for channel and users
            parseMode(in, split);

        } else if (split[1].equals("NICK")) { // Handles nick changes
            parseNick(in, split);

        } else if (split[1].equals("311") || split[1].equals("307") || split[1].equals("319") //Handles whois message
                || split[1].equals("312") || split[1].equals("317") || split[1].equals("318")) {
            parseWhois(in, split);

        } else if (split[1].equals("353")) { // Handles userlist at join
            parseUserlist(in, split);

        } else if (split[1].equals("372") // Info msg codes
                || split[1].equals("001") || split[1].equals("002")
                || split[1].equals("003") || split[1].equals("004")
                || split[1].equals("005") || split[1].equals("251")
                || split[1].equals("252") || split[1].equals("254")
                || split[1].equals("255") || split[1].equals("265")
                || split[1].equals("266") || split[1].equals("375")) {
            parseInfoLine(in, split);

        } else if (split[1].equals("TOPIC")) { // Handles topic when user is in channel
            parseTopic(in, split);

        } else if (split[1].equals("332")) { // Handles topic when user joins channel
            parseStartTopic(in, split);

        } else if (split[1].equals("333")) { // Handles 2nd part of topic when user joins channel
            parseTopicInfo(in, split);

        } else if (split[1].equals("376")) { // Handles actions after end of /MOTD (Autojoin etc)
            autoJoin();

        } else if (split[1].equals("482")) { // Handles Not op message
            parseNotOp(in, split);

        } else if (split[1].equals("302")) { // Handles Userhost message
            parseUserhost(in, split);

        } else if (split[1].equals("322")) { //Handles List request messages
            parseChannelList(in, split);
        }
    }

    private boolean parseUserlist(String in, String[] split) {
        String users[] = in.split(split[4] + " :")[1].split(" ");
        SortedListModel d = serv.getServerPanel().getChannelPane(split[4]).getUserlist().getList();
        for (String user : users) {
            d.add(user);
        }
        serv.getServerPanel().getChannelPane(split[4]).getOnlineCount().setText(d.getSize() + " users");
        return true;
    }

    private boolean parseChannelList(String in, String[] split) {
        int offset = split[0].length() + split[1].length()
                + split[2].length() + split[3].length()
                + split[4].length() + 6;
        // System.out.println("chan: " + split[3] + "\nusers: " + split[4]+"\ntopic: "+in.substring(offset));
        if (getChanList() != null) {
            getChanList().addChannel(split[3], split[4], in.substring(offset));
        }
        return true;
    }

    private boolean parseKick(String in, String[] split) {
        String kicker = in.split("!")[0].substring(1);
        String channel = split[2];
        String kicked = split[3];
        String reason = in.split("KICK " + channel + " " + kicked + " :")[1];
        serv.getServerPanel().getChannelPane(channel).getChat().appendKick(kicker, kicked, channel, reason);

        serv.getServerPanel().getChannelPane(channel).getUserlist().getList().removeElement(kicked);
        String[] ops = {"@", "&", "%", "+", "~"};
        for (String op : ops) {
            serv.getServerPanel().getChannelPane(channel).getUserlist().getList().removeElement(op + kicked);
        }

        if (kicked.equals(serv.getNick())) {
            serv.getServerPanel().getChannelPane(channel).getTopic().setText("");
            serv.getServerPanel().getChannelPane(channel).getUserlist().getList().clear();
        }
        return true;
    }

    private boolean parseJoin(String in, String[] split) {
        String names[] = in.toString().split("!");

        String name = names[0].substring(1);
        String host = names[1].split(" ")[0];
        String channel = split[2].split(":")[1];

        serv.getServerPanel().getChannelPane(channel).getChat().appendJoin(name
                + " (" + host + ")", channel);

        if (!name.equals(serv.getNick())) {
            serv.getServerPanel().getChannelPane(channel).getUserlist().getList().add(name);
        }
        return true;
    }

    private boolean parseTopic(String in, String[] split) {
        String name = in.toString().split("!")[0].substring(1);
        String channel = split[2];

        try {
            String topic = in.split("TOPIC " + split[2] + " :")[1];
            serv.getServerPanel().getChannelPane(channel).getTopic().setText(topic);
            serv.getServerPanel().getChannelPane(channel).getChat().appendTopic("*" + name + " has changed the topic to: " + topic);

            Tray t = serv.getServerPanel().getWindow().getTray();
            if (t.isTray()) {
                t.message("Topic change: " + channel, topic);
            }
        } catch (Exception e) {
        }
        return true;
    }

    private boolean parseNotOp(String in, String[] split) {
        String notice = in.split(serv.getNick() + " " + split[3] + " :")[1];
        String channel = split[3];
        serv.getServerPanel().getChannelPane(channel).getChat().appendNotice(channel, notice);
        return true;
    }

    private boolean parseUserhost(String in, String[] split) {
        try {
            String[] sp = in.split("=+");
            for (String s : sp) {
                System.out.println(s);
            }
            currUserHost = sp[1].substring(1);
            isUserHost = true;
        } catch (Exception e) {
            isUserHost = false;
        }
        return true;
    }

    private boolean parseStartTopic(String in, String[] split) {
        try {
            String topic = in.split("332 " + serv.getNick() + " " + split[3] + " :")[1];
            String channel = split[3];
            serv.getServerPanel().getChannelPane(channel).getTopic().setText(topic);
            serv.getServerPanel().getChannelPane(channel).getChat().appendTopic("*Topic for " + channel + ": " + topic);
        } catch (Exception e) {
        }
        return true;
    }

    private boolean parseTopicInfo(String in, String[] split) {
        String user = split[4];
        String channel = split[3];
        Long time = Long.parseLong(split[5]);

        String date = new java.text.SimpleDateFormat("HH:mm:ss dd/MM/yyyy ").format(new java.util.Date(time * 1000));
        serv.getServerPanel().getChannelPane(channel).getChat().appendTopic("*Topic for " + channel + " set by " + user + " at " + date);
        return true;
    }

    private boolean parseLeave(String in, String[] split) {
        String names[] = in.toString().split("!");

        String name = names[0].substring(1);
        String host = names[1].split(" ")[0];
        String channel = split[2];


        serv.getServerPanel().getChannelPane(channel).getUserlist().getList().removeElement(name);
        String[] ops = {"@", "&", "%", "+", "~"};
        for (String op : ops) {
            serv.getServerPanel().getChannelPane(channel).getUserlist().getList().removeElement(op + name);
        }

        if (split[1].equals("PART")) {
            serv.getServerPanel().getChannelPane(channel).getChat().appendPart(name
                    + " (" + host + ")", channel);
        } else {
            for (ChannelPanel cp : serv.getServerPanel().getChannelList()) {

                if (cp.getUserlist().getList().contains(name)) {
                    cp.getChat().appendQuit(name + " (" + host + ")", channel);
                }
                for (String op : ops) {
                    if (cp.getUserlist().getList().contains(op + name)) {
                        cp.getChat().appendQuit(name + " (" + host + ")", channel);
                    }
                }

            }
        }
        try {
            if (log) {
                new Log(serv.getServerPanel().getServerName(), channel).writeLine(name + "(" + host + ") has left " + channel);
            }
        } catch (IOException ioe) {
        }

        return true;
    }

    private boolean parseNick(String in, String[] split) {
        String name = in.toString().split("!")[0].substring(1);
        String newnick = in.split(":")[2];
        if (name.equalsIgnoreCase(serv.getNick())) {
            serv.getServerPanel().changeNick(newnick);
        }

        for (ChannelPanel cp : serv.getServerPanel().getChannelList()) {

            String[] ops = {"@", "&", "%", "+", "~"};
            for (String op : ops) {
                if (cp.getUserlist().getList().contains(op + name)) {
                    cp.getChat().appendLine("* " + name + " is now known as " + newnick);
                    cp.getUserlist().getList().removeElement(op + name);
                    cp.getUserlist().getList().add(op + newnick);
                }
            }

            if (cp.getUserlist().getList().contains(name)) {
                cp.getChat().appendLine("* " + name + " is now known as " + newnick);
                cp.getUserlist().getList().removeElement(name);
                cp.getUserlist().getList().add(newnick);
            }
            System.out.println(cp.getChannel());
        }
        return true;
    }

    private boolean parseInfoLine(String in, String[] split) {
        String line = in.split(split[1] + " " + serv.getNick() + " ")[1];
        serv.getServerPanel().getChannelPane(0).getChat().appendLine(line);
        return true;
    }

    private boolean parseWhois(String in, String[] split) {
        String line = in.split(split[1] + " " + split[2] + " ")[1];
        serv.getServerPanel().getChannelPane(0).getChat().appendLine(line);
        return true;
    }

    private boolean parsePing(String in) {
        String pingsplit[] = in.split(" :");
        try {
            serv.getOutput().pong(pingsplit[1]);
        } catch (IOException ioe) {
        }
        return true;
    }

    private boolean parseNotice(String in, String[] split) {
        String line = getLine(in);
        String name = in.toString().split("!")[0].substring(1);
        ServerPanel sp = serv.getServerPanel();
        try {
            sp.getChannelPane(sp.getTabbedPane().getSelectedIndex()).getChat().appendNotice(name, line);
        } catch (Exception e) {
        }
        return true;
    }

    private boolean autoJoin() {
        try {
            if (serv.nickPass() != null) {
                serv.getOutput().say("NickServ", "id " + serv.nickPass());
            }

            if (serv.getAutojoin() != null) {
                for (String chan : serv.getAutojoin()) {
                    serv.getOutput().join(chan);
                    serv.getServerPanel().joinChan(chan);
                }
            }
        } catch (IOException ioe) {
        }
        return true;
    }

    private boolean parsePrivmsg(String in, String[] split) {  // Handles all msgs.
        String line = getLine(in);
        String name = in.toString().split("!")[0].substring(1);
        String channel = split[2].split(":")[0];

        if (!line.contains("\u0001")) { // If normal msg

            if (!channel.equals(serv.getNick())) { // If channel message
                if (line.toLowerCase().contains(serv.getNick().toLowerCase())) { // If highlight
                    Tray t = serv.getServerPanel().getWindow().getTray();
                    if (t.isTray() && !serv.getServerPanel().getWindow().isFocused()) {
                        t.message("Highlight", name + ": \n" + line);
                    }
                    serv.getServerPanel().getChannelPane(channel).getChat().appendHighlight(name, line);
                } else { // If normal message
                    serv.getServerPanel().getChannelPane(channel).getChat().appendLine(name, line);
                }

            } else { // If private message
                if (!serv.getServerPanel().isInChannel(name)) {
                    serv.getServerPanel().joinServer(name);
                }
                serv.getServerPanel().getChannelPane(name).getChat().appendLine(name, line);
            }
        } else { // If CTCP message
            try {
                parseCTCP(in, split);
            } catch (IOException ioe) {
            }
        }
        try {
            if (log) {
                new Log(serv.getServerPanel().getServerName(), channel).writeLine("<" + name + "> " + line);
            }
        } catch (IOException ioe) {
        }

        return true;
    }

    private String getLine(String in) {
        String lines[] = in.split(" :");
        String line;
        if (lines.length > 2) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < lines.length - 1; i++) {
                sb.append(lines[i] + " :");
            }
            return sb.toString();
        } else {
            return lines[1];
        }
    }

    private boolean parseMode(String in, String[] split) {
        String name = in.toString().split("!")[0].substring(1);
        String channel = split[2];
        String mode = split[3];
        ChannelPanel cp = serv.getServerPanel().getChannelPane(channel);

        if (split.length == 5) {
            String victim = split[4];
            if (mode.equals("+v")) {
                cp.getChat().appendLine("*" + name + " gives voice to " + victim);
                cp.getUserlist().getList().removeElement(victim);
                cp.getUserlist().getList().add("+" + victim);
            } else if (mode.equals("-v")) {
                cp.getChat().appendLine("*" + name + " removes voice from " + victim);
                cp.getUserlist().getList().removeElement("+" + victim);
                cp.getUserlist().getList().add(victim);
            } else if (mode.equals("+o")) {
                cp.getChat().appendLine("*" + name + " gives operator to " + victim);
                cp.getUserlist().getList().removeElement(victim);
                cp.getUserlist().getList().add("@" + victim);
            } else if (mode.equals("-o")) {
                cp.getChat().appendLine("*" + name + " removes operator from " + victim);
                cp.getUserlist().getList().removeElement("@" + victim);
                cp.getUserlist().getList().add(victim);
            } else if (mode.equals("+q")) {
                cp.getChat().appendLine("*" + name + " gives owner to " + victim);
                cp.getUserlist().getList().removeElement(victim);
                cp.getUserlist().getList().add("~" + victim);
            } else if (mode.equals("-q")) {
                cp.getChat().appendLine("*" + name + " removes owner from " + victim);
                cp.getUserlist().getList().removeElement("~" + victim);
                cp.getUserlist().getList().add(victim);
            } else if (mode.equals("+a")) {
                cp.getChat().appendLine("*" + name + " gives operator to " + victim);
                cp.getUserlist().getList().removeElement(victim);
                cp.getUserlist().getList().add("&" + victim);
            } else if (mode.equals("-a")) {
                cp.getChat().appendLine("*" + name + " removes operator from " + victim);
                cp.getUserlist().getList().removeElement("&" + victim);
                cp.getUserlist().getList().add(victim);
            } else if (mode.equals("+h")) {
                cp.getChat().appendLine("*" + name + " gives half-operator to " + victim);
                cp.getUserlist().getList().removeElement(victim);
                cp.getUserlist().getList().add("%" + victim);
            } else if (mode.equals("-h")) {
                cp.getChat().appendLine("*" + name + " removes half-operator from " + victim);
                cp.getUserlist().getList().removeElement("%" + victim);
                cp.getUserlist().getList().add(victim);
            } else if (mode.equals("+b")) {
                cp.getChat().appendLine("*" + name + " sets ban on " + victim);
            } else if (mode.equals("-b")) {
                cp.getChat().appendLine("*" + name + " removes ban from " + victim);
            } else {
                serv.getServerPanel().getChannelPane(channel).getChat().appendLine("*" + name + " sets mode " + mode + " " + channel + " on " + split[4]);
            }
        } else {
            serv.getServerPanel().getChannelPane(channel).getChat().appendLine("*" + name + " sets mode " + mode + " " + channel + "\n");
        }

        return true;
    }

    private boolean parseCTCP(String in, String[] split) throws IOException {
        String name = in.split("!")[0].substring(1);


        if (in.toLowerCase().contains("\u0001version")) {
            String os = System.getProperty("os.name");
            String arch = System.getProperty("os.arch");
            String version = System.getProperty("os.version");
            int index = serv.getServerPanel().getTabbedPane().getSelectedIndex();

            serv.getServerPanel().getChannelPane(index).getChat().appendLine("*Recieved CTCP VERSION from " + name);
            serv.getOutput().sendNotice(name, "VERSION "
                    + serv.getServerPanel().getWindow().getTitle()
                    + " [" + os + " " + version + " " + arch + "]");

        } else if (in.toLowerCase().contains("\u0001action")) {
            String[] splitt = in.toLowerCase().split("\u0001action");
            serv.getServerPanel().getChannelPane(split[2]).getChat().appendCTCP(name, splitt[1]);
        } else {
            serv.getOutput().sendNotice(name, "Unkown command");
        }
        return true;
    }

    public String getUserHost() {
        return currUserHost;
    }

    public void resetUserHost() {
        currUserHost = null;
    }

    public boolean isUserHost() {
        return isUserHost;
    }

    public void setChanList(ChannelListWindow win) {
        chanlist = win;
    }

    public ChannelListWindow getChanList() {
        return chanlist;
    }
}
