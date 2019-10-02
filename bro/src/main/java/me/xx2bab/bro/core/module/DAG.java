package me.xx2bab.bro.core.module;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

class DAG<T> {

    private Map<T, AtomicInteger> inDegree = new HashMap<>();
    private Map<T, List<T>> outDegree = new HashMap<>();

    /**
     * Build Dependent Relationship.
     *
     * @param obj          An object that is subject to T
     * @param prerequisite The dependency or prerequisite of obj
     */
    public void addPrerequisite(@NonNull T obj, @Nullable T prerequisite) {
        if (!inDegree.containsKey(obj) || inDegree.get(obj) == null) {
            inDegree.put(obj, new AtomicInteger(0));
        }
        if (prerequisite != null) {
            inDegree.get(obj).incrementAndGet();
        }

        if (prerequisite == null) {
            return;
        }
        if (!outDegree.containsKey(prerequisite) || outDegree.get(prerequisite) == null) {
            outDegree.put(prerequisite, new ArrayList<T>());
        }
        outDegree.get(prerequisite).add(obj);
    }

    public List<T> topologicalSort() {
        int num = inDegree.size();
        Deque<T> zeroDegreeStack = new LinkedList<>();
        for (T key : inDegree.keySet()) {
            if (inDegree.get(key).intValue() == 0) {
                zeroDegreeStack.addLast(key);
            }
        }

        List<T> res = new ArrayList<>();
        while (!zeroDegreeStack.isEmpty()) {
            T obj = zeroDegreeStack.removeLast();
            res.add(obj);
            if (outDegree.get(obj) == null) {
                continue;
            }
            for (T key : outDegree.get(obj)) {
                inDegree.get(key).decrementAndGet();
                if (inDegree.get(key).intValue() == 0) {
                    zeroDegreeStack.addLast(key);
                }
            }
        }
        if (res.size() != num) {
            return null; // Failed case
        }
        return res; // Successful case
    }
}