package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {

    private final Node<T> sentinel;
    private int size;

    public LinkedListDeque() {
        sentinel = new Node<>(null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }

    private static class Node<T> {
        private final T item;
        private Node<T> prev;
        private Node<T> next;

        private Node(T item, Node<T> prev, Node<T> next) {
            this.item = item;
            this.prev = prev;
            this.next = next;
        }

        private Node(T item) {
            this(item, null, null);
        }
    }

    @Override
    public void addFirst(T item) {
        Node<T> node = new Node<>(item, sentinel, sentinel.next);
        sentinel.next.prev = node;
        sentinel.next = node;
        size++;
    }

    @Override
    public void addLast(T item) {
        Node<T> node = new Node<>(item, sentinel.prev, sentinel);
        sentinel.prev.next = node;
        sentinel.prev = node;
        size++;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        Iterator<T> itr = iterator();
        if (itr.hasNext()) {
            System.out.print(itr.next());
        }
        while (itr.hasNext()) {
            System.out.print(" " + itr.next());
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        return deleteNode(sentinel.next).item;
    }

    private Node<T> deleteNode(Node<T> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
        size--;
        return node;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        return deleteNode(sentinel.prev).item;
    }

    @Override
    public T get(int index) {
        assert index >= 0;
        Node<T> node = sentinel.next;
        while (node != sentinel) {
            if (index == 0) {
                return node.item;
            }
            node = node.next;
            index--;
        }
        return null;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            Node<T> current = sentinel.next;

            @Override
            public boolean hasNext() {
                return current != sentinel;
            }

            @Override
            public T next() {
                T item = current.item;
                current = current.next;
                return item;
            }
        };
    }

    private T getRecursiveHelper(int index, Node<T> node) {
        if (node == sentinel) {
            return null;
        }
        if (index == 0) {
            return node.item;
        }
        return getRecursiveHelper(index - 1, node.next);
    }

    public T getRecursive(int index) {
        assert index >= 0;
        return getRecursiveHelper(index, sentinel.next);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Deque)) {
            return false;
        }
        Deque<?> odq = (Deque<?>) o;
        if (size() != odq.size()) {
            return false;
        }
        for (int i = 0; i < size(); i++) {
            if (!get(i).equals(odq.get(i))) {
                return false;
            }
        }
        return true;
    }
}
