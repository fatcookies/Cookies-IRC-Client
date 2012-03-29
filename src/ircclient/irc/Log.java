package ircclient.irc;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.util.Date;

/**
 *
 * @author fc
 */
public class Log {

    public PrintStream bw;

    public Log(String name, String chan) {
        try {
            File f = new File("networks/" + name + "/log/" + chan + ".txt");
            if (!f.exists()) {
                f.createNewFile();
            }

            bw = new PrintStream(new AppendFileStream("networks/" + name + "/log/" + chan + ".txt"));
        } catch (IOException ioe) {
            System.out.println("Error opening log.");
        }
    }

    public void writeLine(String in) throws IOException {
        bw.println(in);
        bw.flush();
        bw.close();
    }

    public void writeLineTime(String in) throws IOException {
        Date d = new Date();
        String time = "[" + d.getHours() + ":" + d.getMinutes() + ":" + d.getSeconds() + "] ";
        writeLine(time + in);
    }
}

class AppendFileStream extends OutputStream {

    RandomAccessFile fd;

    public AppendFileStream(String file) throws IOException {
        fd = new RandomAccessFile(file, "rw");
        fd.seek(fd.length());
    }

    @Override
    public void close() throws IOException {
        fd.close();
    }

    @Override
    public void write(byte[] b) throws IOException {
        fd.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        fd.write(b, off, len);
    }

    public void write(int b) throws IOException {
        fd.write(b);
    }
}


