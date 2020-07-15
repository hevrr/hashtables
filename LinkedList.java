import java.util.Iterator;

public class LinkedList<Item> implements Iterable<Item> {

    /* variables */
    private Node top;
    private int size;

    /* constructor */
    public LinkedList() {
        top = null;
        size = 0;
    }

    /* adds item */
    public void add(Item item) {
        if (top == null) {
            top = new Node();
            top.item = item;
        } else {
            Node temp = top;
            while (temp.next != null) {
                temp = temp.next;
            }
            temp.next = new Node();
            temp.next.item = item;
        }
        size++;
    }

    /* remove */
    public void remove(int index) {
        Node temp = top;
        if (index == 0) {
            top = top.next;
        } else {
            for (int i = 0; i < index - 1; i++)
                temp = temp.next;
            Node temp2 = temp.next.next;
            temp.next = temp2;
        }
    }

    /* gets item at index */
    public Item get(int index) {
        if (size() - 1 < index) {
            System.out.println("Out of bounds.");
        } else {
            Node temp = top;
            for (int i = 0; i < index; i++)
                temp = temp.next;
            return temp.item;
        }
        return null;
    }

    /* sets item at index */
    public void set(int index, Item item) {
        if (size() - 1 < index) {
            System.out.println("Out of bounds.");
        } else {
            Node temp = top;
            for (int i = 0; i < index; i++)
                temp = temp.next;
            temp.item = item;
        }
    }

    /* clears list */
    public void clear() {
        top = null;
    }

    /* returns size */
    public int size() {
        return size;
    }

    /* if is empty */
    public Boolean isEmpty() {
        return top == null;
    }

    /* iterator */
    @Override
    public Iterator<Item> iterator() {
        return new ListIterator();
    }

    private class ListIterator implements Iterator<Item> {

        private Node temp = top;

        public boolean hasNext() {
            return temp != null;
        }

        public void remove() {
        }

        public Item next() {
            Item item = temp.item;
            temp = temp.next;
            return item;
        }
    }

    /* prints in string */
    public String toString() {
        String returned = "";
        Node temp = top;
        while (temp != null) {
            returned += String.valueOf(temp.item) + " ";
            temp = temp.next;
        }
        return returned;
    }

    /* node */
    class Node {
        Item item;
        Node next;
    }
}
