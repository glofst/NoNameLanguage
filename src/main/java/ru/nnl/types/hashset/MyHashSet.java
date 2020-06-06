package ru.nnl.types.hashset;

import ru.nnl.types.list.MyList;

import java.util.NoSuchElementException;

public class MyHashSet {

    private final int bucketsCount = 5;
    private final MyList[] buckets;
    private int size;

    private static class Item {
        public Object key;
        public Object value;

        public Item(Object key, Object value) {
            this.key = key;
            this.value = value;
        }
    }

    private static class Pair<T, U> {
        public T first;
        public U second;
    }

    public MyHashSet() {
        this.buckets = new MyList[bucketsCount];
        this.size = 0;
    }

    public void put(Object key, Object value) {
        Pair<MyList, Integer> pair = find(key);
        if (pair == null) {
            int index = getIndex(key);
            if (buckets[index] == null) {
                buckets[index] = new MyList();
            }

            buckets[index].add(new Item(key, value));
            ++size;
        } else {
            ((Item) pair.first.get(pair.second)).value = value;
        }
    }

    public Object get(Object key) throws NoSuchElementException {
        Pair<MyList, Integer> pair = find(key);
        if (pair == null) {
            throw new NoSuchElementException("There is no key " + key);
        }

        return ((Item) pair.first.get(pair.second)).value;

    }

    public void remove(Object key) {
        Pair<MyList, Integer> pair = find(key);
        if (pair != null) {
            pair.first.remove(pair.second);
            --size;
        }
    }

    public int size() {
        return size;
    }

    public void clear() {
        for (int i = 0; i < bucketsCount; ++i) {
            buckets[i].clear();
        }
        size = 0;
    }

    private int getIndex(Object key) {
        return Math.abs(key.hashCode()) % bucketsCount;
    }

    private Pair<MyList, Integer> find(Object key) {
        Pair<MyList, Integer> res = null;
        MyList bucket = buckets[getIndex(key)];

        if (bucket != null) {
            for (int i = 0; i < bucket.size(); ++i) {
                Item item = (Item) bucket.get(i);
                if (item.key.equals(key)) {
                    res = new Pair<MyList, Integer>();
                    res.first = bucket;
                    res.second = i;
                    break;
                }
            }
        }

        return res;
    }


}
