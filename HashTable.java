
public class HashTable<Key, Value> {

    /* hashTable information */
    private Key[] keys;
    private Value[] vals;
    private int numItems;
    private int tableSize;

    /* initial array size */
    private static final int INITIAL = 2;

    /* resize threshold ratio */
    private static final double RESIZE_THRESHOLD = 0.5;

    /* resize ratio */
    private static final int RESIZE_RATIO = 2;

    /* for hashtable testing */
    public int probeCounter = 0;
    private boolean isResizable = true;

    /* prime number multiplier */
    private static int multiplier = 23;

    /* creates new table with initial size */
    HashTable() {
        this(INITIAL);
    }

    /* creates keys and values */
    HashTable(int size) {
        keys = (Key[]) new Object[size];
        vals = (Value[]) new Object[size];
        numItems = 0;
        tableSize = size;
    }

    /* returns number of items inside table */
    int size() {
        return numItems;
    }

    /* returns if empty */
    boolean isEmpty() {
        return size() == 0;
    }

    /* returns whether table contains key */
    boolean contains(Key key) {
        return get(key) != null;
    }

    /* returns value of given key */
    Value get(Key key) {
        for (int i = hash(key); keys[i] != null; i = (i + 1) % tableSize) {
            probeCounter++;
            if (keys[i].equals(key))
                return vals[i];
        }
        return null;
    }

    /* puts a key and value inside table */
    void put(Key key, Value val) {
        /* if more than half filled, dynamically size */
        if (isResizable && numItems >= tableSize * RESIZE_THRESHOLD) resize(RESIZE_RATIO * tableSize);
        int i;

        /* starts at hashed index, checks whether given space is empty, increments and loops around */
        for (i = hash(key); keys[i] != null; i = (i + 1) % tableSize) {
            probeCounter++;
            /* if key is same, replace */
            if (keys[i].equals(key)) {
                vals[i] = val;
                return;
            }
        }

        probeCounter++;
        /* add in key and value and update number of items */
        keys[i] = key;
        vals[i] = val;
        numItems++;
    }

    /* returns he array size */
    int trueSize() {
        return tableSize;
    }

    /* sets resizable for HTClient testing */
    void setResizable(boolean v) {
        isResizable = v;
    }

    /* for HTClient2 testing */
    /* note: I would not have this function if it weren't for testing because the multiplier should be completely secure and private */
    void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }

    /* resize the hashtable */
    private void resize(int newSize) {
        /* creates a new table */
        HashTable<Key, Value> temporaryTable = new HashTable<>(newSize);

        /* put in the new keys and values inside newly sized hashtable */
        /* note: will rehash all the keys to compensate for new size */
        for (int i = 0; i < tableSize; i++)
            if (keys[i] != null)
                temporaryTable.put(keys[i], vals[i]);

        /* update current hashtable with temporaryTable's properties */
        keys = temporaryTable.keys;
        vals = temporaryTable.vals;
        tableSize = newSize;
    }

    /* hashes function for key */
    private int hash(Key s) {
        int hash = 0;
        /* adds up all ASCII characters */
        for (char x : s.toString().toCharArray())
            /* for HTClient: want to modify string to get search miss but produce the same hash as original string */
            if (x != ' ')
                hash = (hash * multiplier + x) % tableSize;
        return hash;
    }
}
