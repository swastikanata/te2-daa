import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.io.IOException;

class SetCover {
    
    public static void main(String[] args) throws IOException {
        int[] sizes = {20, 200, 2000};

        // ONLY run WHEN generating data
        // List<List<List<Integer>>> subsetsList = DataGenerator.generateSubsets(sizes);
        // int[] subset_sizes = new int[sizes.length];
        // for (int i = 0; i < subsetsList.size(); i++) { 
        //     subset_sizes[i] = subsetsList.get(i).size(); 
        // }
        // DataGenerator.generateCosts(subset_sizes, sizes);

        for (int i = 0; i < sizes.length; i++) {
            int n = sizes[i];
            List<List<Integer>> subsets = DataGenerator.loadSubsets(n);
            List<Integer> costs = DataGenerator.loadCosts(n);
            test(n, subsets, costs);
        }
    }
    
    public static void test(int n, List<List<Integer>> subs, List<Integer> costs) {
        double startTime, endTime, beforeUsedMemory, afterUsedMemory, memoryUsed, timeTaken;
        Runtime runtime = Runtime.getRuntime();

        Set<Integer> universe = new HashSet<>();
        for (int i = 1; i <= n; i++) {
            universe.add(i);
        }

        System.out.println("Greedy for " + n + " elements");
        List<Set<Integer>> subsets = new ArrayList<>();
        for (List<Integer> subset : subs) {
            Set<Integer> set = new HashSet<>();
            for (int element : subset) {
                set.add(element);
            }
            subsets.add(set);
        }
        startTime = System.currentTimeMillis();
        System.gc();
        beforeUsedMemory = runtime.totalMemory() - runtime.freeMemory();
        int[] result = greedy(universe, subsets, costs);
        afterUsedMemory = runtime.totalMemory() - runtime.freeMemory();
        endTime = System.currentTimeMillis();
        memoryUsed = (double) (afterUsedMemory - beforeUsedMemory) / 1024.0;
        timeTaken = (double) (endTime - startTime);
        System.out.println("cost = " + result[subsets.size()]);
        System.out.println("time = " + timeTaken);
        System.out.println("memory = " + memoryUsed);

        System.out.println("Branch and Bound for " + n + " elements");
        startTime = System.currentTimeMillis();
        System.gc();
        beforeUsedMemory = runtime.totalMemory() - runtime.freeMemory();
        result = branchAndBound(universe, subsets, costs);
        afterUsedMemory = runtime.totalMemory() - runtime.freeMemory();
        endTime = System.currentTimeMillis();
        memoryUsed = (double) (afterUsedMemory - beforeUsedMemory) / 1024.0;
        timeTaken = (double) (endTime - startTime);
        List<List<Integer>> cover = new ArrayList<>();
        for (int i = 0; i < subsets.size(); i++) {
            if (result[i] == 1) {
                cover.add(subs.get(i));
            }
        }
        System.out.println("cost = " + result[subsets.size()]);
        System.out.println("time = " + timeTaken);
        System.out.println("memory = " + memoryUsed);

        System.out.println();
    }
    
    public static int[] greedy(Set<Integer> universe, List<Set<Integer>> subsets, List<Integer> costs) {
        Set<Integer> elements = new HashSet<>();
        for (Set<Integer> subset : subsets) {
            elements.addAll(subset);
        }
        
        if (!elements.equals(universe)) {
            return null;
        }
        
        int cost = 0;
        Set<Integer> covered = new HashSet<>();
        int[] cover = new int[subsets.size()];
        
        while (!covered.equals(elements)) {
            int maxIndex = -1;
            double maxRatio = 0;

            for (int i = 0; i < subsets.size(); i++) {
                Set<Integer> subset = subsets.get(i);
                int numUncovered = 0;
                
                for (int num : subset) {
                    if (!covered.contains(num)) {
                        numUncovered++;
                    }
                }
                
                double ratio = (double) numUncovered / costs.get(i);
                
                if (ratio > maxRatio) {
                    maxRatio = ratio;
                    maxIndex = i;
                }
            }
            
            if (maxIndex != -1) {
                cover[maxIndex] = 1;
                cost += costs.get(maxIndex);
                covered.addAll(subsets.get(maxIndex));
            }
        }
        
        int[] result = new int[subsets.size() + 1];
        for (int i = 0; i < subsets.size(); i++) {
            result[i] = cover[i];
        }
        result[subsets.size()] = cost;
        
        return result;
    }

    public static int[] branchAndBound(Set<Integer> universe, List<Set<Integer>> subsets, List<Integer> costs) {
        List<Integer> subset = new ArrayList<>();
        subset.add(0);
        for (int i = 1; i < subsets.size(); i++) {
            subset.add(1);
        }
        int bestCost = costs.stream().mapToInt(Integer::intValue).sum();
        int i = 1;

        while (i > 0) {
            if (i < subsets.size()) {
                int cost = 0;
                Set<Integer> tempSet = new HashSet<>();
                for (int k = 0; k < i; k++) {
                    cost += subset.get(k) * costs.get(k);
                    if (subset.get(k) == 1) {
                        tempSet.addAll(subsets.get(k));
                    }
                }

                if (cost > bestCost) {
                    i = bypassBranch(subset, i);
                    continue;
                }

                for (int k = i; k < subsets.size(); k++) {
                    tempSet.addAll(subsets.get(k));
                }

                if (!tempSet.equals(universe)) {
                    i = bypassBranch(subset, i);
                } else {
                    i = nextVertex(subset, i, subsets.size());
                }
            } else {
                int cost = 0;
                Set<Integer> finalSet = new HashSet<>();
                for (int k = 0; k < i; k++) {
                    cost += subset.get(k) * costs.get(k);
                    if (subset.get(k) == 1) {
                        finalSet.addAll(subsets.get(k));
                    }
                }

                if (cost < bestCost && finalSet.equals(universe)) {
                    bestCost = cost;
                }

                i = nextVertex(subset, i, subsets.size());
            }
        }

        int[] result = new int[subsets.size() + 1];
        for (int k = 0; k < subsets.size(); k++) {
            result[k] = subset.get(k);
        }
        result[subsets.size()] = bestCost;

        return result;
    }
 
    public static int bypassBranch(List<Integer> subset, int i) {
        for (int j = i-1; j >= 0; j--) {
            if (subset.get(j) == 0) {
                subset.set(j, 1);
                return j+1;
            }
        }
        
        return 0;
    }
    
    public static int nextVertex(List<Integer> subset, int i, int m) {
        if (i < m) {
            subset.set(i, 0);
            return i + 1;
        } else {
            for (int j = m-1; j >= 0; j--) {
                if (subset.get(j) == 0) {
                    subset.set(j, 1);
                    return j + 1;
                }
            }
        }
        
        return 0;
    }

}