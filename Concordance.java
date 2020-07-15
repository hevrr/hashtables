
public class Concordance {

    /* holds hashtable and text information */
    private HashTable currentTable;
    private Text text;

    /* a concordance has a text */
    public Concordance(Text text) {
        this.text = text;
        currentTable = new HashTable<String, LinkedList<Occurrences>>();
    }

    /* sets the hashtable */
    public void setTable(HashTable ht){
        this.currentTable = ht;
    }

    /* checks whether contains a search */
    public boolean containsSearch(String search){
        return currentTable.contains(search);
    }

    /* returns occurrences of given search */
    public LinkedList<Occurrences> getOccurrences(String search) {
        return (LinkedList<Occurrences>) currentTable.get(search);
    }

    /* returns the text */
    public Text getText() {
        return text;
    }

}
