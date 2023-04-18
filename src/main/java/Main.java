import org.apache.commons.io.IOUtils;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static final Pattern ENTRY_PATTERN = Pattern.compile("([a-zA-Z]+):([^;]+);");
    private static final Pattern ITEM_PATTERN = Pattern.compile("(.*?)##");

    public String readRawDataToString() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        String result = IOUtils.toString(classLoader.getResourceAsStream("RawData.txt"));

        return result;
    }

    public static void main(String[] args) throws Exception {
        String rawData = (new Main()).readRawDataToString();
        List<Map<String, String>> items = parseRawData(rawData);
        printFormattedData(items);
    }

    private static List<Map<String, String>> parseRawData(String rawData) {
        List<Map<String, String>> items = new ArrayList<>();
        Matcher itemMatcher = ITEM_PATTERN.matcher(rawData);

        while (itemMatcher.find()) {
            String itemData = itemMatcher.group(1);
            Matcher entryMatcher = ENTRY_PATTERN.matcher(itemData);
            Map<String, String> item = new LinkedHashMap<>();

            while (entryMatcher.find()) {
                String key = entryMatcher.group(1).toLowerCase();
                String value = entryMatcher.group(2);
                item.put(key, value);
            }

            items.add(item);
        }

        return items;
    }

    private static void printFormattedData(List<Map<String, String>> items) {
        Map<String, Map<String, Integer>> summary = new LinkedHashMap<>();
        int errors = 0;

        for (Map<String, String> item : items) {
            String name = item.get("name");
            String price = item.get("price");

            if (name == null || price == null) {
                errors++;
                continue;
            }

            name = name.toLowerCase();
            summary.putIfAbsent(name, new LinkedHashMap<>());
            summary.get(name).put(price, summary.get(name).getOrDefault(price, 0) + 1);
        }

        for (String name : summary.keySet()) {
            System.out.printf("name:    %-12s seen: %d times\n", capitalize(name), summary.get(name).values().stream().mapToInt(Integer::intValue).sum());
            System.out.println("=============         =============");

            for (String price : summary.get(name).keySet()) {
                System.out.printf("Price:   %-12s seen: %d %s\n", price, summary.get(name).get(price), summary.get(name).get(price) > 1 ? "times" : "time");
                System.out.println("-------------         -------------");
            }

            System.out.println();
        }

        System.out.printf("Errors          seen: %d times\n", errors);
    }

    private static String capitalize(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}
