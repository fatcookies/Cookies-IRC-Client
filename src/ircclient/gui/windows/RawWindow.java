package ircclient.gui.windows;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.Document;

/**
 *
 * @author fc
 */
public class RawWindow extends JFrame {

    private JTextArea log;

    public RawWindow() {
        super("Raw Log");
        log = new JTextArea();
        log.setEditable(false);
        JScrollPane scrollLog = new JScrollPane(log);

        System.setOut(new PrintStream(
                new ConsoleOutputStream(log.getDocument(), System.out), true));

        System.setErr(new PrintStream(
                new ConsoleOutputStream(log.getDocument(), null), true));

        this.add(scrollLog);
        this.setSize(600, 400);
        this.setVisible(true);
    }
}

class ConsoleOutputStream extends OutputStream {

    private Document document = null;
    private ByteArrayOutputStream outputStream = new ByteArrayOutputStream(256);
    private PrintStream ps = null;

    public ConsoleOutputStream(Document document, PrintStream ps) {
        this.document = document;
        this.ps = ps;
    }

    public void write(int b) {
        outputStream.write(b);
    }

    @Override
    public void flush() throws IOException {
        super.flush();

        try {
            if (document != null) {
                document.insertString(document.getLength(),
                        new String(outputStream.toByteArray()), null);
            }

            if (ps != null) {
                ps.write(outputStream.toByteArray());
            }

            outputStream.reset();
        } catch (Exception e) {
        }
    }
}
