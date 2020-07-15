import java.util.Iterator;

public class Queue<Item> implements Iterable<Item> {

    /* variables */
    private Node top;
    private Node bottom;
    private int size;

    /* constructor */
    public Queue() {
        top = null;
        bottom = null;
        size = 0;
    }

    /* enqueues new node */
    public void enqueue(Item item) {
        Node temp = bottom;
        bottom = new Node();
        bottom.item = item;
        bottom.next = null;
        if (isEmpty()) top = bottom;
        else temp.next = bottom;
        size++;
    }

    /* dequeues first null */
    public Item dequeue() {
        Item item = top.item;
        top = top.next;
        size--;
        if (isEmpty()) bottom = null;
        return item;
    }

    /* peeks first */
    public Item peek() {
        if (top != null)
            return top.item;
        return null;
    }

    /* returns size */
    public int size() {
        return size;
    }

    /* checks if queue is empty */
    public boolean isEmpty() {
        return top == null;
    }

    /* iterator */
    @Override
    public Iterator<Item> iterator() {
        return new QueueIterator();
    }

    private class QueueIterator implements Iterator<Item> {

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
        StringBuilder returned = new StringBuilder();
        Node temp = top;
        while (temp != null) {
            returned.append(temp.item).append(" ");
            temp = temp.next;
        }
        return returned.toString();
    }

    /* node */
    private class Node {
        Item item;
        Node next;
    }
}
