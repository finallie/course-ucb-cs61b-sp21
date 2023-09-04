package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {

    private static final double FACTOR = 2;
    private int size;
    private int pOfAddFirst;
    private T[] items;

    public ArrayDeque() {
        size = 0;
        pOfAddFirst = 7;
        //noinspection unchecked
        items = ((T[]) new Object[8]);
    }

    private int prev(int index) {
        return (index - 1 + items.length) % items.length;
    }

    private int next(int index) {
        return (index + 1) % items.length;
    }

    private int capacity() {
        return items.length;
    }

    private int getPOfAddLast() {
        return (pOfAddFirst + size + 1) % items.length;
    }

    private void resize(int capacity) {
        //noinspection unchecked
        T[] newItems = ((T[]) new Object[capacity]);
        for (int i = 0, first = next(pOfAddFirst); i < size; i++) {
            newItems[i] = items[first];
            first = next(first);
        }
        items = newItems;
        pOfAddFirst = items.length - 1;
    }

    private boolean lowerUsage() {
        return size <= items.length / 4 && items.length >= 16;
    }

    private void tryResize() {
        if (size == capacity() || lowerUsage()) {
            resize(Math.max(size + 1, (int) (size * FACTOR)));
        }
    }

    @Override
    public void addFirst(T item) {
        tryResize();
        items[pOfAddFirst] = item;
        pOfAddFirst = prev(pOfAddFirst);
        size++;
    }

    @Override
    public void addLast(T item) {
        tryResize();
        items[getPOfAddLast()] = item;
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
        pOfAddFirst = next(pOfAddFirst);
        T item = items[pOfAddFirst];
        items[pOfAddFirst] = null;
        size--;
        tryResize();
        return item;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        int pOfAddLast = prev(getPOfAddLast());
        T item = items[pOfAddLast];
        items[pOfAddLast] = null;
        size--;
        tryResize();
        return item;
    }

    @Override
    public T get(int index) {
        assert index >= 0;
        if (index >= size) {
            return null;
        }
        return items[(pOfAddFirst + 1 + index) % items.length];
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {

            int i = 0;

            @Override
            public boolean hasNext() {
                return i < size;
            }

            @Override
            public T next() {
                T item = items[(pOfAddFirst + 1 + i) % items.length];
                i++;
                return item;
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ArrayDeque)) {
            return false;
        }
        Iterator<?> oit = ((ArrayDeque<?>) o).iterator();
        Iterator<T> it = iterator();
        while (oit.hasNext() && it.hasNext()) {
            if (!oit.next().equals(it.next())) {
                return false;
            }
        }
        return !oit.hasNext() && !it.hasNext();
    }
}
