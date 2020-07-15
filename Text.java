import javafx.util.Pair;

public class Text {
    /* a text has a title, a text, a location, if it's a sonnet, and if the file is already stored inside project folder */
    String title;
    String text;
    String location;
    boolean isSonnet;
    boolean fileStored;

    /* a linkedlist of pairs (pair: <paragraph begin index, paragraph end index */
    /* the index number of the pair is the paragraph number - 1 */
    LinkedList<Pair<Integer, Integer>> indices;

    /* stores most common words from least to greatest */
    LinkedList[] commonWords;

    /* constructor */
    Text(String title, String text, String location, boolean isSonnet, boolean fileStored) {
        this.title = title;
        this.text = text;
        this.location = location;
        this.isSonnet = isSonnet;
        this.fileStored = fileStored;
        indices = new LinkedList<>();
    }
}
