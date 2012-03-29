package ircclient.irc;

import ircclient.gui.ChannelPanel;
import java.io.IOException;

/**
 *
 * @author fc
 */
public class Command {

    public Command(String cmdd, ChannelPanel cp) throws IOException {
        String cmd = cmdd.toLowerCase();
        String chan = cp.getChannel();

        if (cmd.startsWith("join")) {
            String split[] = cmd.split(" ");
            cp.getServPane().joinChan(split[1]);

        } else if (cmd.startsWith("part")) {
            String[] split;
            try {
                split = cmd.split(" ");
                cp.getServPane().partChan(split[1]);
            } catch (Exception e) {
                cp.getServPane().partChan(chan);
            }

        } else if (cmd.startsWith("me")) {
            cp.getServPane().getServer().getOutput().ctcpsay(chan, cmdd.substring(3));
            cp.getChat().appendCTCP(cp.getServPane().getServer().getNick(), cmdd.substring(3));

        } else if (cmd.startsWith("msg")) {
            String reciep = cmdd.split(" ")[1];
            String msg = cmdd.substring(5 + reciep.length());
            cp.getServPane().getServer().getOutput().say(reciep, msg);
            cp.getChat().appendLine(">" + reciep + "< " + msg);

        } else if (cmd.startsWith("notice")) {
            String reciep = cmdd.split(" ")[1];
            String msg = cmdd.substring(8 + reciep.length());
            cp.getServPane().getServer().getOutput().sendNotice(reciep, msg);
            cp.getChat().appendLine(">" + reciep + "< " + msg);

        } else if (cmd.startsWith("op")) {
            String op = cmd.split(" ")[1];
            cp.getServPane().getServer().getOutput().setMode("+o", chan, op);

        } else if (cmd.startsWith("deop")) {
            String op = cmd.split(" ")[1];
            cp.getServPane().getServer().getOutput().setMode("-o", chan, op);

        } else if (cmd.startsWith("voice")) {
            String user = cmd.split(" ")[1];
            cp.getServPane().getServer().getOutput().setMode("+v", chan, user);

        } else if (cmd.startsWith("devoice")) {
            String user = cmd.split(" ")[1];
            cp.getServPane().getServer().getOutput().setMode("-v", chan, user);


        } else if (cmd.startsWith("unban")) {
            String user = cmd.split(" ")[1];
            cp.getServPane().getServer().getOutput().unban(user, cp.getChannel());

        } else if (cmd.startsWith("topic")) {
            String topic = cmdd.substring(6);
            cp.getServPane().getServer().getOutput().topic(cp.getChannel(), topic);

        } else if (cmd.startsWith("mode")) {
            String[] split = cmd.split(" ");
            Output o = cp.getServPane().getServer().getOutput();
            if (split.length == 4) {
                o.setMode(split[2], split[1], split[3]);
            } else if (split.length == 3) {
                o.setMode(split[2], split[1]);
            } else if (split.length == 2) {
                o.setMode(split[1], cp.getChannel());
            } else {
            }

        } else if (cmd.startsWith("away")) {
            String[] split = cmdd.split(" ");
            if (split.length > 1) {
                cp.getServPane().getServer().getOutput().away(cmdd.substring(5));
            } else {
                cp.getServPane().getServer().getOutput().away();
            }

        } else if (cmd.startsWith("kick")) {
            String user = cmd.split(" ")[1];
            cp.getServPane().getServer().getOutput().kick(user, cp.getChannel());

        } else if (cmd.startsWith("ban")) {
            String user = cmd.split(" ")[1];
            cp.getServPane().getServer().getOutput().ban(user, cp.getChannel());

        } else if (cmd.startsWith("nick")) {
            String nick = cmd.split(" ")[1];
            cp.getServPane().getServer().getOutput().nick(nick);

        } else if (cmd.startsWith("ctcp")) {
            String[] split = cmd.split(" ");
            if (split.length == 3) {
                cp.getServPane().getServer().getOutput().ctcp(split[1], split[2]);
            }

        }

    }
}
