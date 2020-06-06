package ru.nnl.types.list;

public class MyList {

    private Node head;
    private Node tail;
    private int size;

    public MyList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public void add(Object value) {
        Node node = new Node(value, null, tail);

        if (tail != null) {
            tail.setPrev(node);
        }
        tail = node;

        if (head == null) {
            head = tail;
        }

        ++size;
    }

    public void insert(int index, Object value) {
        Node node = find(index);
        Node newNode = new Node(value, node, node.getNext());

        if (node.getNext() != null) {
            node.getNext().setPrev(newNode);
        }
        if (node.getPrev() != null) {
            node.setNext(newNode);
        }
    }

    public Object get(int index) {
        return find(index).getValue();
    }

    public void remove(int index) {
        Node node = find(index);

        if (node.getNext() != null) {
            node.getNext().setPrev(node.getPrev());
        }
        if (node.getPrev() != null) {
            node.getPrev().setNext(node.getNext());
        }

        --size;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        head = tail = null;
        size = 0;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("{");

        boolean first = true;
        for (int i = 0; i < size; ++i) {
            if (!first) {
                builder.append(", ");
            }

            builder.append(get(i));
            first = false;
        }
        builder.append("}");

        return builder.toString();
    }

    private Node find(int index) {
        if (index < 0 || index > size - 1) {
            throw new IndexOutOfBoundsException();
        }

        Node node;

        if (index < size / 2) {
            node = head;
            for (int i = 0; i < index; ++i) {
                node = node.getPrev();
            }
        } else {
            node = tail;
            for (int i = 0; i < size - index - 1; ++i) {
                node = node.getNext();
            }
        }

        return node;
    }
}