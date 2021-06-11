package org.example;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class for generating binned versions of chart data
 */
public class BucketHashMap {
    private final Map<Integer,List<Double>> map = new HashMap<>();

    public Map<Integer, List<Double>> getMap() {
        return map;
    }

    /**
     * Creates the bins keyset.
     */
    public BucketHashMap() {
        Set<Integer> keySet = new HashSet<>(Arrays.asList(1, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 200, 300, 400,
                500, 600, 700, 800, 900, 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000, 20000, 30000,
                40000, 50000, 60000, 70000, 80000, 90000, 100000, 20000, 30000, 40000, 50000, 60000, 70000, 80000,
                90000, 100000, 200000, 300000, 400000, 500000, 600000, 700000, 800000, 900000, 1000000, 2000000,
                3000000, 4000000, 5000000, 6000000, 7000000, 8000000, 9000000, 10000000));
        for(Integer key: keySet){
            map.put(key, new ArrayList<>());
        }
    }

    /**
     * OLD method for updating the bins
     * @param key original key from the data
     * @param value value to add to the key of the bin
     * @return new values for the key of the bin
     */
    public List<Double> update(Integer key, List<Double> value) {
        List<Double> priorValues = new ArrayList<>();
        if (findKey(key) > 0) {
            priorValues = map.get(key);
            priorValues.addAll(value);
        }
        map.put(key, priorValues);
        return priorValues;
    }

    /**
     * Method to add new value to the bin by key
     * @param key original key from the data
     * @param value value to add to the key of the bin
     * @return the updated values for the found bin key
     */
    public List<Double> update(Integer key, Double value) {
        List<Double> priorValues = new ArrayList<>();
        Integer correctKey = findKey(key);
        if (correctKey > 0) {
            priorValues = map.get(correctKey);
            priorValues.add(value);
            map.put(correctKey, priorValues);
        } else {
            System.out.println("Key out of the bin: " + key);
        }

        return priorValues;
    }

    /**
     * The method finds bin keys based on original key from the data
     * @param originalkey original key from the data
     * @return bin key or -1 if not found
     */
    public Integer findKey(Integer originalkey){
        List<Integer> keys = map.keySet().stream().collect(Collectors.toList());
        Collections.sort(keys);
        for (int i = 0; i < keys.size() - 1; i++){
            if (originalkey >= keys.get(i) && originalkey < keys.get(i+1)){
                return keys.get(i);
            }
        }
        return -1;
    }

    public List<Double> get(Integer key) {
        return map.get(key);
    }

    /**
     * Generates the chart values - X:bins and Y:avg
     * @return Map of binned chart values
     */
    public Map<Integer,Double> generate() {
        Map<Integer,Double> result = new HashMap<>();
        for (Integer key: map.keySet()){
            if(!map.get(key).isEmpty()) {
                int count = map.get(key).size();
                if(count > 10) {
                    result.put(key, map.get(key).stream().mapToDouble(v -> v).average().getAsDouble());
                }
            }
        }
        return result;
    }

    /**
     * Generates the counts of values for testing
     * @return Map of binned keys and count of values per key
     */
    public Map<Integer,Integer> generateCounts() {
        Map<Integer,Integer> result = new HashMap<>();
        for (Integer key: map.keySet()){
            if(!map.get(key).isEmpty()) {
                int count = map.get(key).size();
                if(count > 10) {
                    result.put(key, count);
                }
            }
        }
        return result;
    }
}