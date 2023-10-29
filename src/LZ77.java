import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class LZ77 {
    private int bufferSize;
    private int windowSize;

    public LZ77(int bufferSize, int windowSize) {
        this.bufferSize = bufferSize;
        this.windowSize = windowSize;
    }

    public void compress(String inputFile, String outputFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            StringBuilder result = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                result.append(line);
                if (reader.ready()) {
                    result.append("\n");
                }
            }

            Vector<LZ77Tag> data = compressor(result.toString());

            FileWriter writer = new FileWriter(outputFile);
            for (int i = 0; i < data.size(); i++) {
                LZ77Tag tag = data.elementAt(i);
                String nextSymbol = String.valueOf(tag.nextChar);
                if (nextSymbol.equals("\n")) {
                    writer.write("<" + tag.position + "," + tag.length + "," + "\\n" + ">  ");
                } else {
                    writer.write("<" + tag.position + "," + tag.length + "," + nextSymbol + ">  ");
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Vector<LZ77Tag> compressor(String data) {
        Vector<LZ77Tag> tagList = new Vector<LZ77Tag>();
        int i = 0;
        while (i < data.length()) {
            int maxLength = 0;
            int pos = 0;
            for (int j = i - 1; j >= 0 && windowSize - i + j > 0; j--) {
                if (data.charAt(i) == data.charAt(j)) {
                    int length = 0;
                    for (int k = j; k < i; k++) {
                        if (i + k - j < data.length() && length < bufferSize
                                && data.charAt(k) == data.charAt(i + k - j)) {
                            length++;
                        } else {
                            break;
                        }
                    }
                    if (length > maxLength) {
                        maxLength = length;
                        pos = j;
                    }
                }
            }
            LZ77Tag tag = new LZ77Tag();
            tag.position = maxLength == 0 ? 0 : i - pos;
            tag.length = maxLength;
            if (i + maxLength >= data.length()) {
                tag.nextChar = '0';
            } else {
                tag.nextChar = data.charAt(i + maxLength);
            }
            tagList.add(tag);

            i += maxLength + 1;
        }
        // String result = "";
        // for (LZ77Tag tag : tagList) {
        // result += String.format("%d,%d,%c ", tag.position, tag.length, tag.nextChar);
        // }
        return tagList;
    }

    public String decompress(String data) {
        return null;
    }
}
