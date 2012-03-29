package ircclient.gui;

import java.awt.Color;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 *
 * @author fc
 */
public class ChatArea extends JTextPane {

    public ChatArea() {
        setEditable(false);
    }

    public void append(Color c, String s, Object style) {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, style, c);

        try {
            getDocument().insertString(getDocument().getLength(), s, aset);
        } catch (BadLocationException ble) {
        }
        this.setCaretPosition(getDocument().getLength() - 1);
    }

    public void appendLine(String nick, String line) {
        append(Color.blue, "<" + nick + "> ", StyleConstants.Foreground);
        append(Color.black, line + "\n", StyleConstants.Foreground);
    }

    public void appendCTCP(String nick, String line) {
        append(Color.blue, "*" + nick + " ", StyleConstants.Foreground);
        append(Color.black, line + "\n", StyleConstants.Foreground);
    }

    public void appendNotice(String nick, String line) {
        append(Color.magenta, "-" + nick + "- ", StyleConstants.Foreground);
        append(Color.black, line + "\n", StyleConstants.Foreground);
    }

    public void appendTopic(String line) {
        append(Color.magenta, line + "\n", StyleConstants.Foreground);
    }

    public void appendLine(String line) {
        append(Color.black, line + "\n", StyleConstants.Foreground);
    }

    public void appendHighlight(String nick, String line) {
        append(Color.red, "<" + nick + "> " + line + "\n", StyleConstants.Foreground);
    }

    public void appendJoin(String nick, String channel) {
        append(Color.green, "* " + nick + " has joined " + channel + "\n", StyleConstants.Foreground);
    }

    public void appendPart(String nick, String channel) {
        append(Color.red, "* " + nick + " has part " + channel + "\n", StyleConstants.Foreground);
    }

    public void appendKick(String kicker, String victim, String channel, String reason) {
        append(Color.red, kicker + " has kicked " + victim + " from " + channel + "(" + reason + ")\n", StyleConstants.Foreground);
    }

    public void appendQuit(String nick, String channel) {
        append(Color.red, "* " + nick + " has quit\n", StyleConstants.Foreground);
    }
}
