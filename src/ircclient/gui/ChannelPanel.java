package ircclient.gui;

import ircclient.irc.Command;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 *
 * @author fc
 */
public class ChannelPanel extends JPanel {

    private String channel;
    private JTextField topic;
    private JTextField text;
    private JButton nickButton;
    private JLabel online;
    private ChatArea chat;
    private UserList userList;
    private ServerPanel servpane;

    public ChannelPanel(String channel, ServerPanel servpane) {
        this.servpane = servpane;
        this.channel = channel;
        this.setLayout(new BorderLayout());

        topic = new JTextField();
        online = new JLabel("0 users");
        userList = new UserList(this);
        chat = new ChatArea();
        text = new JTextField();
        nickButton = new JButton(servpane.getServer().getNick());

        addListeners();

        JScrollPane chatScroll = new JScrollPane(chat);

        JPanel userbar = new JPanel();
        userbar.setLayout(new BorderLayout());
        userbar.add(nickButton, BorderLayout.WEST);
        userbar.add(text, BorderLayout.CENTER);

        this.add(chatScroll, BorderLayout.CENTER);
        this.add(userbar, BorderLayout.SOUTH);
    }

    public ChannelPanel(String chan, ServerPanel servpane, boolean show) {
        this(chan, servpane);

        JScrollPane userListScroll = new JScrollPane(userList);

        JPanel userListBar = new JPanel();
        userListBar.setLayout(new BorderLayout());
        userListBar.add(userListScroll);
        userListBar.add(online, BorderLayout.NORTH);

        this.add(userListBar, BorderLayout.EAST);
        this.add(topic, BorderLayout.NORTH);
        setVisible(true);
    }

    private void addListeners() {
        text.setFocusTraversalKeysEnabled(false);
        text.addKeyListener(textListener);
        topic.addActionListener(topicListener);
        nickButton.addActionListener(nickListener);
    }

    public JTextField getTopic() {
        return topic;
    }

    public JTextField getTextField() {
        return text;
    }

    public UserList getUserlist() {
        return userList;
    }

    public ChatArea getChat() {
        return chat;
    }

    public String getChannel() {
        return channel;
    }

    public ServerPanel getServPane() {
        return servpane;
    }

    public JButton getNickButton() {
        return nickButton;
    }

    public JLabel getOnlineCount() {
        return online;
    }

    public ChannelPanel getThis() {
        return this;
    }
    public ActionListener nickListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            String newnick = JOptionPane.showInputDialog(getThis(), "Choose a new nick", "Change nick", JOptionPane.QUESTION_MESSAGE);
            try {
                getServPane().getServer().getOutput().nick(newnick);
            } catch (IOException ioe) {
            }
        }
    };
    public ActionListener topicListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            String newTopic = topic.getText();

            try {
                getServPane().getServer().getOutput().topic(getChannel(), newTopic);
            } catch (IOException ioe) {
            }
        }
    };
    public KeyListener textListener = new KeyListener() {

        public void keyTyped(KeyEvent e) {
        }

        public void keyPressed(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {

            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                String chan = getChannel();
                String getText = text.getText();
                text.setText("");
                try {
                    if (!getText.startsWith("/")) {
                        getServPane().getServer().getOutput().say(chan, getText);
                        getChat().appendLine(getServPane().getServer().getNick(), getText);
                    } else {
                        new Command(getText.substring(1), getThis());
                    }
                } catch (IOException ioe) {
                    getChat().appendLine(chan, "Error Sending line to " + chan);
                }

            } else if (e.getKeyCode() == KeyEvent.VK_TAB) {
                try {
                    String newLine = new Tab(getThis()).getNewLine();
                    text.setText(newLine);
                } catch (Exception e1) {
                }

            } else {
            }

        }
    };
}
