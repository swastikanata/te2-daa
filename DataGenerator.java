import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataGenerator {
    
    public static List<List<List<Integer>>> generateSubsets(int[] sizes) throws IOException {
        
        List<List<List<Integer>>> subsetsList = new ArrayList<>();
        for (int size : sizes) {
            int numberOfSubset = Math.min(size, 40);
            List<List<Integer>> subsets = new ArrayList<>();

            for (int i = 0; i < numberOfSubset; i++) {
                subsets.add(new ArrayList<>());
            }

            // ensure every element is contain in one of the subsets
            for (int i = 1; i <= size; i++) {
                int pos = randInt(0, numberOfSubset - 1);
                subsets.get(pos).add(i);
            }

            // add elements again
            for (int j = 0; j < size * 2; j++) {
                int number = randInt(1, size);
                int pos = randInt(0, numberOfSubset - 1);
                
                if (!subsets.get(pos).contains(number)) {
                    subsets.get(pos).add(number);
                }
            }
            
            List<List<Integer>> finalSubsets = new ArrayList<>();
            for (int i = 0; i < subsets.size(); i++) {
                if (!subsets.get(i).isEmpty()) {
                    finalSubsets.add(subsets.get(i));
                }
            }
            subsetsList.add(finalSubsets);
            saveSubsets(finalSubsets, size);
        }
        return subsetsList;
    }

    public static void saveSubsets(List<List<Integer>> subsets, int size) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter("input/subset" + size + ".txt"));
        for (List<Integer> subset : subsets) {
            for (Integer value : subset) {
                bw.write(value + " ");
            }
            bw.newLine();
        }
        bw.close();
    }

    public static List<List<Integer>> loadSubsets(int size) throws IOException {
        List<List<Integer>> subsets = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader("input/subset" + size + ".txt"));
    
        String line;
        while ((line = br.readLine()) != null) {
            List<Integer> subset = new ArrayList<>();
            String[] values = line.split(" ");
    
            for (String value : values) {
                subset.add(Integer.parseInt(value));
            }
    
            subsets.add(subset);
        }
    
        br.close();
        return subsets;
    }

    public static void generateCosts(int[] sizes, int[] ns) throws IOException {
        for (int j = 0; j < sizes.length; j++) {
            List<Integer> costs = new ArrayList<>();
            for (int i = 0; i < sizes[j]; i++) {
                costs.add(randInt(1, 100));
            }
            saveCosts(costs, ns[j]);
        }
    }

    public static void saveCosts(List<Integer> costs, int size) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter("input/cost" + size + ".txt"));
        for (Integer cost : costs) {
            bw.write(cost + " ");
            bw.newLine();
        }
        
        bw.close();
    }

    public static List<Integer> loadCosts(int size) throws IOException {
        List<Integer> costs = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader("input/cost" + size + ".txt"));
    
        String value;
        while ((value = br.readLine()) != null) {
            costs.add(Integer.parseInt(value.split(" ")[0]));
        }
    
        br.close();
        return costs;
    }
    
    public static int randInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }
}