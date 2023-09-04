package deque;

import java.util.Comparator;
import java.util.Iterator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {

    private final Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> c) {
        super();
        this.comparator = c;
    }

    public T max() {
        return max(comparator);
    }

    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        Iterator<T> iterator = iterator();
        T t = iterator.next();
        while (iterator.hasNext()) {
            T next = iterator.next();
            if (c.compare(t, next) < 0) {
                t = next;
            }
        }
        return t;
    }
}

