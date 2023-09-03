package deque;

import java.util.Iterator;

public abstract class AbstractDeque<T> implements Deque<T> {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Deque)) {
            return false;
        }
        Iterator<?> oit = ((Deque<?>) o).iterator();
        Iterator<T> it = iterator();
        while (oit.hasNext() && it.hasNext()) {
            if (!oit.next().equals(it.next())) {
                return false;
            }
        }
        return !oit.hasNext() && !it.hasNext();
    }
}
