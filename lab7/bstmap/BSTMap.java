package bstmap;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private BSTNode<K, V> root;

    private static class BSTNode<K extends Comparable<K>, V> {
        private final K key;
        private V value;
        private int size;
        private BSTNode<K, V> left;
        private BSTNode<K, V> right;

        public BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
            this.size = 1;
        }

    }

    @Override
    public void clear() {
        root = null;
    }

    @Override
    public boolean containsKey(K key) {
        return get(root, key) != null;
    }

    @Override
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("calls get() with a null key");
        }
        BSTNode<K, V> node = get(root, key);
        if (node == null) {
            return null;
        }
        return node.value;
    }

    private static <K extends Comparable<K>, V> BSTNode<K, V> get(BSTNode<K, V> root, K key) {
        if (root == null) {
            return null;
        }
        int cmp = key.compareTo(root.key);
        if (cmp < 0) {
            return get(root.left, key);
        } else if (cmp > 0) {
            return get(root.right, key);
        } else {
            return root;
        }
    }


    @Override
    public int size() {
        return size(root);
    }

    private static <V, K extends Comparable<K>> int size(BSTNode<K, V> node) {
        if (node == null) {
            return 0;
        }
        return node.size;
    }

    @Override
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("calls put() with a null key");
        }
        root = put(root, key, value);
    }

    private static <K extends Comparable<K>, V> BSTNode<K, V> put(BSTNode<K, V> root, K key, V value) {
        if (root == null) {
            return new BSTNode<>(key, value);
        }
        int cmp = key.compareTo(root.key);
        if (cmp < 0) {
            root.left = put(root.left, key, value);
        } else if (cmp > 0) {
            root.right = put(root.right, key, value);
        } else {
            root.value = value;
        }
        root.size = 1 + size(root.left) + size(root.right);
        return root;
    }

    private class KeySet implements Set<K> {
        @Override
        public int size() {
            return BSTMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return size() == 0;
        }

        @Override
        public boolean contains(Object o) {
            //noinspection unchecked
            return BSTMap.this.containsKey((K) o);
        }

        @Override
        public Iterator<K> iterator() {
            return BSTMap.this.iterator();
        }

        @Override
        public Object[] toArray() {
            return stream().toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return (T[]) toArray();
        }

        @Override
        public boolean add(K k) {
            throw new UnsupportedOperationException("add is not supported");
        }

        @Override
        public boolean remove(Object o) {
            //noinspection unchecked
            return BSTMap.this.remove((K) o) != null;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            for (Object o : c) {
                //noinspection unchecked
                if (!contains(o)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends K> c) {
            throw new UnsupportedOperationException("addAll is not supported");
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException("retainAll is not supported");
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            boolean result = false;
            for (Object o : c) {
                //noinspection unchecked
                if (remove(o)) {
                    result = true;
                }
            }
            return result;
        }

        @Override
        public void clear() {
            BSTMap.this.clear();
        }
    }


    @Override
    public Set<K> keySet() {
        return new KeySet();
    }

    @Override
    public V remove(K key) {
        if (key == null) {
            throw new IllegalArgumentException("calls remove() with a null key");
        }
        V v = get(key);
        if (v != null) {
            root = remove(root, key);
        }
        return v;
    }

    private static <K extends Comparable<K>, V> BSTNode<K, V> min(BSTNode<K, V> node) {
        if (node.left == null) {
            return node;
        }
        return min(node.left);
    }

    private static <K extends Comparable<K>, V> BSTNode<K, V> deleteMin(BSTNode<K, V> node) {
        if (node.left == null) {
            return node.right;
        }
        node.left = deleteMin(node.left);
        node.size = 1 + size(node.left) + size(node.right);
        return node;
    }

    private static <K extends Comparable<K>, V> BSTNode<K, V> remove(BSTNode<K, V> node, K key) {
        if (node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = remove(node.left, key);
        } else if (cmp > 0) {
            node.right = remove(node.right, key);
        } else {
            if (node.right == null) {
                return node.left;
            }
            if (node.left == null) {
                return node.right;
            }
            BSTNode<K, V> t = node;
            node = min(t.right);
            node.right = deleteMin(t.right);
            node.left = t.left;
        }
        node.size = 1 + size(node.left) + size(node.right);
        return node;
    }

    @Override
    public V remove(K key, V value) {
        if (Objects.equals(get(key), value)) {
            return remove(key);
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException("iterator is not supported");
    }

    public void printInOrder() {
        print(root);
        System.out.println();
    }

    private static <K extends Comparable<K>, V> void print(BSTNode<K, V> root) {
        if (root == null) {
            return;
        }
        print(root.left);
        System.out.print(root.key + " ");
        print(root.right);
    }
}
