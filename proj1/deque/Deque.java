package deque;

import java.util.Iterator;

public interface Deque<T> extends Iterable<T> {
    void addFirst(T item);

    void addLast(T item);

    default boolean isEmpty() {
        return size() == 0;
    }

    int size();

    default void printDeque() {
        Iterator<T> itr = iterator();
        if (itr.hasNext()) {
            System.out.print(itr.next());
        }
        while (itr.hasNext()) {
            System.out.print(" " + itr.next());
        }
        System.out.println();
    }

    T removeFirst();

    T removeLast();

    T get(int index);

}
