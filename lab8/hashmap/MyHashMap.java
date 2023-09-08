package hashmap;

import java.util.*;

/**
 * A hash table-backed Map implementation. Provides amortized constant time
 * access to elements via get(), remove(), and put() in the best case.
 * <p>
 * Assumes null keys will never be inserted, and does not resize down upon remove().
 *
 * @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    private static final int INT = 0x7fffffff;

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof MyHashMap.Node)) {
                return false;
            }
            //noinspection unchecked
            Node other = (Node) obj;
            return key.equals(other.key);
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private static final int DEFAULT_INITIAL_SIZE = 16;
    private int size;
    private double loadFactor;
    private static final double DEFAULT_LOAD_FACTOR = 0.75;
    // You should probably define some more!

    /**
     * Constructors
     */
    public MyHashMap() {
        this(DEFAULT_INITIAL_SIZE, DEFAULT_LOAD_FACTOR);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, DEFAULT_LOAD_FACTOR);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad     maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        buckets = createTable(initialSize);
        loadFactor = maxLoad;
        size = 0;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     * <p>
     * The only requirements of a hash table bucket are that we can:
     * 1. Insert items (`add` method)
     * 2. Remove items (`remove` method)
     * 3. Iterate through items (`iterator` method)
     * <p>
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     * <p>
     * Override this method to use different data structures as
     * the underlying bucket type
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        //noinspection unchecked
        return (Collection<Node>[]) new Collection[tableSize];
    }

    // Your code won't compile until you do so!

    @Override
    public void clear() {
        buckets = createTable(DEFAULT_INITIAL_SIZE);
        size = 0;
    }

    private int index(K key, int bucketLength) {
        return (key.hashCode() & INT) % bucketLength;
    }

    private Collection<Node> getBucket(K key, Collection<Node>[] table) {
        return table[index(key, table.length)];
    }


    @Override
    public boolean containsKey(K key) {
        Collection<Node> bucket = getBucket(key, buckets);
        if (bucket == null) {
            return false;
        }
        return bucket.contains(createNode(key, null));
    }

    @Override
    public V get(K key) {
        Collection<Node> bucket = getBucket(key, buckets);
        if (bucket == null) {
            return null;
        }
        return getV(key, bucket);
    }

    protected V getV(K key, Collection<Node> bucket) {
        for (Node node : bucket) {
            if (node.key.equals(key)) {
                return node.value;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    private void tryRehash() {
        if (size > loadFactor * buckets.length) {
            Collection<Node>[] newBuckets = createTable(buckets.length * 2);
            for (Collection<Node> bucket : buckets) {
                if (bucket == null) {
                    continue;
                }
                for (Node node : bucket) {
                    Collection<Node> newBucket = getBucket(node.key, newBuckets);
                    if (newBucket == null) {
                        newBucket = createBucket();
                        newBuckets[index(node.key, newBuckets.length)] = newBucket;
                    }
                    newBucket.add(node);
                }
            }
            buckets = newBuckets;
        }
    }

    @Override
    public void put(K key, V value) {
        tryRehash();
        Collection<Node> bucket = getBucket(key, buckets);
        if (bucket == null) {
            bucket = createBucket();
            buckets[index(key, buckets.length)] = bucket;
        }
        if (put(key, value, bucket)) {
            size++;
        }
    }

    protected boolean put(K key, V value, Collection<Node> bucket) {
        for (Node n : bucket) {
            if (n.key.equals(key)) {
                n.value = value;
                return false;
            }
        }
        Node node = createNode(key, value);
        bucket.add(node);
        return true;
    }

    @Override
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        for (Collection<Node> bucket : buckets) {
            if (bucket == null) {
                continue;
            }
            for (Node node : bucket) {
                keys.add(node.key);
            }
        }
        return keys;
    }

    @Override
    public V remove(K key) {
        Collection<Node> bucket = getBucket(key, buckets);
        if (bucket != null) {
            V v = getV(key, bucket);
            if (bucket.remove(createNode(key, null))) {
                size--;
            }
            return v;
        }
        return null;
    }

    @Override
    public V remove(K key, V value) {
        Collection<Node> bucket = getBucket(key, buckets);
        if (bucket != null) {
            V v = getV(key, bucket);
            if (Objects.equals(v, value)) {
                bucket.remove(createNode(key, null));
                size--;
                return v;
            }
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }
}
