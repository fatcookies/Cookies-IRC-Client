package ircclient.gui.windows;

import ircclient.gui.ServerPanel;
import ircclient.irc.Server;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author fc
 */
public class NetworksWindow extends JFrame {

    private JPanel buttonPanel;
    private JPanel connectPanel;
    private JList networkList;
    private DefaultListModel networks;
    private IRCWindow win;
    private ArrayList<Server> servers = new ArrayList<Server>();

    public NetworksWindow(IRCWindow win) {
        super("Networks...");
        this.win = win;
        this.setSize(480, 320);
        init();
        this.setVisible(true);
    }

    private void init() {
        networks = new DefaultListModel();
        networkList = new JList(networks);
        networkList.setFixedCellWidth(360);
        loadNetworks();

        connectPanel = new JPanel();
        JButton close = new JButton("Close");
        JButton connect = new JButton("Connect");
        close.addActionListener(closeListener);
        connect.addActionListener(connectListener);
        connectPanel.add(close, BorderLayout.WEST);
        connectPanel.add(connect, BorderLayout.EAST);


        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        JButton add = new JButton("Add");
        JButton remove = new JButton("Remove");
        JButton edit = new JButton("Edit");
        add.addActionListener(addListener);
        remove.addActionListener(removeListener);
        edit.addActionListener(editListener);
        buttonPanel.add(add);
        buttonPanel.add(remove);
        buttonPanel.add(edit);


        this.add(networkList, BorderLayout.WEST);
        this.add(buttonPanel, BorderLayout.EAST);
        this.add(connectPanel, BorderLayout.SOUTH);
    }

    public boolean isServer(String name) {
        boolean result = false;
        for (Server s : servers) {
            if (s.getName() != null) {
                if (s.getName().equals(name)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    private void loadNetworks() {
        File[] f = new File("networks/").listFiles();
        for (File file : f) {
            Server s = new Server();
            s.read(file.getName());
            networks.addElement(s.getName());
            servers.add(s);
        }
    }

    private void removeNetwork() {
        int index = networkList.getSelectedIndex();

        if (!isConnected(servers.get(index))) {
            servers.get(index).delete();
            servers.remove(index);
            networks.remove(index);
        } else {
            JOptionPane.showMessageDialog(this, "You must close the network before deleting it!",
                    "Error deleting network", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isConnected(Server s) {
        boolean is = false;
        for (ServerPanel sp : win.getServerList()) {
            if (s.getName().equals(sp.getServerName())) {
                is = true;
                break;
            }
        }
        return is;
    }

    public DefaultListModel getListModel() {
        return networks;
    }

    public ArrayList<Server> getList() {
        return servers;
    }

    public JList getJList() {
        return networkList;
    }

    public NetworksWindow getThis() {
        return this;
    }
    public ActionListener addListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            new AddServerWindow(getThis());
        }
    };
    public ActionListener removeListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            removeNetwork();
        }
    };
    public ActionListener editListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            Server server = servers.get(networkList.getSelectedIndex());
            new AddServerWindow(server, getThis());
        }
    };
    public ActionListener closeListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            getThis().setVisible(false);
        }
    };
    public ActionListener connectListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            String network = networks.get(networkList.getSelectedIndex()).toString();
            win.joinServer(network);
            setVisible(false);
        }
    };
}

class AddServerWindow extends JFrame {

    Server s;
    NetworksWindow nw;
    JTextField name;
    JTextField server;
    JTextField port;
    JTextField nick;
    JTextField channels;
    JPasswordField nickPass;
    JCheckBox autoConnect;
    JCheckBox log;
    JCheckBox ssl;
    int index;

    public AddServerWindow(Server s, NetworksWindow nw) {
        this(nw);
        this.s = s;
        setTitle("Edit Server");
        setInfo();
    }

    public AddServerWindow(NetworksWindow nw) {
        super("Add Server...");
        this.nw = nw;

        index = nw.getJList().getSelectedIndex();
        this.setSize(480, 320);
        init();

        this.setVisible(true);
    }

    private boolean isValidInformation() {
        boolean valid = false;

        StringBuilder sb = new StringBuilder();
        for (char c : nickPass.getPassword()) {
            sb.append(c);
        }
        String pass = sb.toString();

        if (!(name.getText().equals("") || name.getText() == null
                || server.getText().equals("") || server.getText() == null
                || port.getText().equals("") || port.getText() == null || isInteger(port.getText())
                || nick.getText().equals("") || nick.getText() == null
                || pass.equals("") || pass == null)) {
        } else {
            if (nw.isServer(name.getText()) && s != null) {
                valid = true;
            } else if (nw.isServer(name.getText())) {
                valid = false;
            } else {
                valid = true;
            }
        }
        return valid;
    }

    public boolean isInteger(String i) {
        try {
            Integer.parseInt(i);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    private void setInfo() {
        name.setText(s.getName());
        server.setText(s.getServer());
        port.setText(Integer.toString(s.getPort()));
        nick.setText(s.getNick());
        channels.setText(Server.channelsToString(s.getChannels()));
        nickPass.setText(s.getNickPass());
        autoConnect.setSelected(s.isAutoConnect());
        log.setSelected(s.isLogged());
        ssl.setSelected(s.isSSL());
    }

    private void init() {
        this.setLayout(new GridLayout(10, 2));
        JLabel nameLabel = new JLabel("Name:");
        JLabel serverLabel = new JLabel("Server:");
        JLabel portLabel = new JLabel("Port:");
        JLabel nickLabel = new JLabel("Nick:");
        JLabel channelsLabel = new JLabel("Autojoin:eg, #foo,#bar,#channel");
        JLabel nickPassLabel = new JLabel("NickServ password:");
        JLabel autoConnectLabel = new JLabel("Auto-connect on startup?");
        JLabel logLabel = new JLabel("Log channels?");
        JLabel sslLabel = new JLabel("Use SSL?");

        name = new JTextField();
        server = new JTextField();
        port = new JTextField();
        nick = new JTextField();
        channels = new JTextField();
        nickPass = new JPasswordField();
        autoConnect = new JCheckBox();
        log = new JCheckBox();
        ssl = new JCheckBox();

        this.add(nameLabel);
        this.add(name);
        this.add(serverLabel);
        this.add(server);
        this.add(portLabel);
        this.add(port);
        this.add(sslLabel);
        this.add(ssl);
        this.add(nickLabel);
        this.add(nick);
        this.add(channelsLabel);
        this.add(channels);
        this.add(nickPassLabel);
        this.add(nickPass);
        this.add(autoConnectLabel);
        this.add(autoConnect);
        this.add(logLabel);
        this.add(log);

        JButton close = new JButton("Close");
        JButton save = new JButton("Save");
        close.addActionListener(closeListener);
        save.addActionListener(saveListener);

        this.add(close);
        this.add(save);

    }
    public ActionListener closeListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            setVisible(false);
        }
    };
    public ActionListener saveListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            if (!isValidInformation()) {
                System.out.println("Errornous information");
            } else {
                StringBuilder sb = new StringBuilder();
                for (char c : nickPass.getPassword()) {
                    sb.append(c);
                }
                String pass = sb.toString();
                String channelList[] = Server.channelsToArray(channels.getText());

                Server serv = new Server(name.getText(), server.getText(), Integer.parseInt(port.getText()),
                        nick.getText(), channelList, pass, autoConnect.isSelected(), log.isSelected(),
                        ssl.isSelected());
                serv.write();

                if (s == null) {
                    nw.getList().add(serv);
                    nw.getListModel().addElement(serv.getName());
                } else {
                    nw.getList().set(index, serv);
                    nw.getListModel().set(index, name.getText());
                }
                setVisible(false);
            }

        }
    };
}
