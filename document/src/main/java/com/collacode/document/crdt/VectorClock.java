package com.collacode.document.crdt;

import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
public class VectorClock {
    private Map<String, Long> clock = new HashMap<>();

    public void increment(String clientId) {
        clock.merge(clientId, 1L, Long::sum);
    }

    public VectorClock() {
    }

    public VectorClock(Map<String, Long> clock) {
        this.clock = clock;
    }

    public boolean isBefore(VectorClock other) {
        Map<String, Long> otherClock = other.clock;
        boolean atLeastOneLess = false;

        // Проверяем все ключи из текущих часов и otherClock
        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(clock.keySet());
        allKeys.addAll(otherClock.keySet());

        for (String key : allKeys) {
            long thisValue = clock.getOrDefault(key, 0L);
            long otherValue = otherClock.getOrDefault(key, 0L);

            if (thisValue > otherValue) {
                return false; // Не может быть "before", если хотя бы одно значение больше
            }
            if (thisValue < otherValue) {
                atLeastOneLess = true; // Есть хотя бы одно строго меньшее значение
            }
        }

        return atLeastOneLess; // true, если все ≤ и хотя бы одно <
    }

    /**
     * Проверяет, являются ли векторные часы конкурентными (не сравнимыми)
     * @param other другие векторные часы для сравнения
     * @return true если часы конкурентны (ни один не включает другой)
     */
    public boolean isConcurrent(VectorClock other) {
        boolean thisGreater = false;
        boolean otherGreater = false;

        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(clock.keySet());
        allKeys.addAll(other.clock.keySet());

        for (String key : allKeys) {
            long thisValue = clock.getOrDefault(key, 0L);
            long otherValue = other.clock.getOrDefault(key, 0L);

            if (thisValue > otherValue) thisGreater = true;
            else if (thisValue < otherValue) otherGreater = true;

            if (thisGreater && otherGreater) return true;
        }

        return false;
    }

    public static boolean clockComparator(VectorClock earlier, VectorClock later) {
        return later.isBefore(earlier);
    }

    public void merge(VectorClock anotherVector){
        Map<String, Long> anotherClock = anotherVector.getClock();
        for (String anotherKey : anotherClock.keySet()){
            clock.merge(anotherKey, anotherClock.get(anotherKey), Long::max);
        }
    }
}
