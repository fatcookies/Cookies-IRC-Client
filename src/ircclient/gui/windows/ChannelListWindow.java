package ircclient.gui.windows;

import ircclient.irc.ServerConnection;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author fc
 */
public class ChannelListWindow extends JFrame {

    private int count = 0;
    private JTable channelTable;
    private DefaultTableModel channels;
    private ServerConnection serv;
    private JLabel countLabel;
    private JButton join, close;

    public ChannelListWindow(ServerConnection serv) {
        super("Channel List");
        this.serv = serv;
        this.setLayout(new BorderLayout());
        this.addWindowListener(new ChannelWindowHandler(this));
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        channels = new DefaultTableModel();
        channelTable = new JTable(channels);
        channels.addColumn("Channel");
        channels.addColumn("Users");
        channels.addColumn("Topic");

        countLabel = new JLabel("Channels: " + count);
        join = new JButton("Join");
        join.addActionListener(joinListener);
        close = new JButton("Close");
        close.addActionListener(closeListener);


        JScrollPane scroll = new JScrollPane(channelTable);
        JPanel buttons = new JPanel();
        buttons.add(join);
        buttons.add(close);

        this.add(scroll, BorderLayout.CENTER);
        this.add(countLabel, BorderLayout.NORTH);
        this.add(buttons, BorderLayout.SOUTH);
        this.setSize(600, 400);

        this.setVisible(true);
    }

    public void addChannel(String chan, String users, String topic) {
        channels.addRow(new String[]{chan, users, topic});
        count++;
        countLabel.setText("Channels: " + count);
    }

    public ServerConnection getServer() {
        return serv;
    }

    public DefaultTableModel getTableModel() {
        return channels;
    }

    public JTable getTable() {
        return channelTable;
    }

    public ChannelListWindow getThis() {
        return this;
    }
    private ActionListener joinListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            int row = getTable().getSelectedRow();
            String channel = (String) getTableModel().getValueAt(row, 0);

            try {
                serv.getServerPanel().joinChan(channel);
            } catch (IOException ioe) {
            }
        }
    };
    private ActionListener closeListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            serv.getInput().setChanList(null);
            getThis().dispose();
        }
    };
}

class ChannelWindowHandler extends WindowAdapter {

    ServerConnection serv;

    public ChannelWindowHandler(ChannelListWindow win) {
        this.serv = win.getServer();
    }

    @Override
    public void windowClosing(WindowEvent evt) {
        serv.getInput().setChanList(null);
    }
}


