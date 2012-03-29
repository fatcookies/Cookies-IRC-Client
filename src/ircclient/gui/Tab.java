package ircclient.gui;

import java.util.ArrayList;

/**
 *
 * @author fc
 */
public class Tab {

    ChannelPanel cp;
    int wordNumber;

    public Tab(ChannelPanel cp) {
        this.cp = cp;
    }

    private String getWord(String line, int caretPos, String[] split) {
        String result = null;
        int pos = 0;

        for (int i = 0; i < split.length; i++) {
            int oldPos = pos + 1;
            pos += split[i].length() + 1;
            if (caretPos >= oldPos && caretPos <= pos) {
                result = split[i];
                wordNumber = i;
                break;
            }
        }
        return result;
    }

    public String getNewLine() throws Exception {
        String line = cp.getTextField().getText();
        String[] split = line.split(" ");
        int caretPos = cp.getTextField().getCaretPosition();
        String currWord = getWord(line, caretPos, split);

        ArrayList<String> names = cp.getUserlist().getList().toArrayList();
        String[] ops = {"@", "&", "%", "+", "~"};

        for (int i = 0; i < names.size(); i++) {
            String s = names.get(i);
            if (s.startsWith("+") || s.startsWith("%") || s.startsWith("~")
                    || s.startsWith("@")
                    || s.startsWith("&")) {
                names.set(i, s.substring(1));
            }
        }

        ArrayList<String> matches = new ArrayList<String>();
        for (String s : names) {
            if (s.toLowerCase().startsWith(currWord.toLowerCase())) {
                matches.add(s);
            }
        }

        split[wordNumber] = matches.get(0);
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            sb.append(s + " ");
        }

        return sb.toString();

    }
}
