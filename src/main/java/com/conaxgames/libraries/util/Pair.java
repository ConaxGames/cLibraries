package com.conaxgames.libraries.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Pair<K, V> {

    private K key;
    private V value;

    public static <K, V> Pair<K, V> of(K key, V value) {
        return new Pair<K, V>(key, value);
    }

    public static Pair<Integer, Integer> fromString(String s) {
        String[] nums = s.split(",");
        return new Pair<Integer, Integer>(Integer.parseInt(nums[0]), Integer.parseInt(nums[1]));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pair<?, ?> objPair))
            return false;

        return this.key.equals(objPair.key) && this.value.equals(objPair.value);
    }

    @Override
    public int hashCode() {
        return 31 * this.key.hashCode() + 31 * this.value.hashCode();
    }

    @Override
    public String toString() {
        return this.key + ", " + this.value;
    }

}
