import java.util.*;
import java.util.stream.Collectors;

public class Compression implements CompressionInterface {
    List<String> inputList = new ArrayList<>();
    List<String> outputList = new ArrayList<>();
    Map<String, Integer> words = new HashMap<>();
    Map<String, String> header = new HashMap<>();

    private int stringListLength(List<String> list) {
        int length = 0;

        for (String i : list) {
            length += i.length();
        }

        return length;
    }

    private int stringMapLength(Map<String, String> map) {
        int length = 0;

        for (String i : map.keySet()) {
            length = length + i.length() + map.get(i).length();
        }

        return length;
    }

    private Map<String, Integer> sortAndRemoveOnes(Map<String, Integer> map) {
        Map<String, Integer> sortedWords = new LinkedHashMap<>();

        map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> sortedWords.put(x.getKey(), x.getValue()));

        sortedWords.values().removeAll(Collections.singleton(1));

        return sortedWords;
    }

    private Map<String, String> swapKeysAndValues(Map<String, String> map) {
        Map<String, String> swapped;

        swapped = map.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

        return swapped;
    }

    private void removeLast(Map<String, Integer> map) {
        int counter = 1;

        for (String item : map.keySet()) {
            if (counter == map.size()) {
                map.remove(item);
            }
            counter++;
        }
    }

    private void addOnes(List<String> possibleCompressionList, Map<String, String> possibleHeader) {
        for (String word : inputList) {
            if (possibleHeader.containsKey(word)) {
                possibleCompressionList.add(possibleHeader.get(word));
            } else {
                possibleCompressionList.add("1" + word);
            }
        }
    }

    private List<String> possibleCompression(Map<String, Integer> sortedInput, Map<String, String> possibleHeader) {
        List<String> possibleCompressionList = new ArrayList<>();
        int size = sortedInput.size();
        int attempts = 0;
        String binarySize = Integer.toBinaryString(size - 1);

        for (String word : sortedInput.keySet()) {
            if (size == 1) {
                possibleHeader.put(word, "0");
            } else {
                String binaryAttempts = Integer.toBinaryString(attempts);
                attempts++;
                int diff = binarySize.length() - binaryAttempts.length();
                String compressedWord;
                if (diff == 0) {
                    compressedWord = "0" + binaryAttempts;
                } else {
                    compressedWord = "0".repeat(diff + 1) + binaryAttempts;
                }
                possibleHeader.put(word, compressedWord);
            }
        }

        addOnes(possibleCompressionList, possibleHeader);

        return possibleCompressionList;
    }

    private boolean possibleCompressionCheck(List<String> inputList, List<String> outputList, Map<String, String> header, int inputSize) {
        boolean possible = false;

        if (stringListLength(outputList) + stringMapLength(header) >= stringListLength(inputList)) {
            if (inputSize > stringListLength(outputList) + stringMapLength(header)) {
                possible = false;
            }
        } else if (inputSize > stringListLength(outputList) + stringMapLength(header)) {
            possible = true;
        }

        return possible;
    }

    @Override
    public void addWord(String word) {
        inputList.add(word);
    }

    @Override
    public void compress() {
        for (String input : inputList) {
            words.merge(input, 1, Integer::sum);
        }

        int inputSize = stringListLength(inputList);
        Map<String, Integer> sortedWords = sortAndRemoveOnes(words);

        while (sortedWords.size() != 0) {

            Map<String, String> possibleHeader = new HashMap<>();
            List<String> possibleOutput = possibleCompression(sortedWords, possibleHeader);
            boolean possible = possibleCompressionCheck(inputList, possibleOutput, possibleHeader, inputSize);

            if (possible) {
                inputSize = stringListLength(possibleOutput) + stringMapLength(possibleHeader);
                outputList = new ArrayList<>(possibleOutput);
                header = new HashMap<>(possibleHeader);
            }

            if (!possible) {
                inputSize = stringListLength(possibleOutput) + stringMapLength(possibleHeader);
            }

            removeLast(sortedWords);

            possibleHeader.clear();
            possibleOutput.clear();
        }

        if (outputList.size() == 0) {
            outputList = new ArrayList<>(inputList);
        }

    }

    @Override
    public Map<String, String> getHeader() {
        return swapKeysAndValues(header);
    }

    @Override
    public String getWord() {
        return outputList.remove(0);
    }
}
