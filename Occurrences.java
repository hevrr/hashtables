
public class Occurrences {
    /* includes body paragraph/sonnet number and displayed information */
    /* note that the paragraph/sonnet number is used to retrieve the relevant indices through LinkedList<Pair> in Text.java */
    int bodyNumber;
    String information;

    /* constructor */
    Occurrences(int bodyNumber, String information) {
        this.bodyNumber = bodyNumber;
        this.information = information;
    }
}
