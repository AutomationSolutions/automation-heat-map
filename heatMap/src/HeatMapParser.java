import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class HeatMapParser {

    public static void main(String[] args) {

        int exitCode = 0;
        List<String> allCSSRules = new ArrayList<>();
        List<String> jobShortNames = new ArrayList<>();
        try {
            parsingToCSS parse = new parsingToCSS();
            for (int i = 1; i < args.length - 1; i += 2) {
                String simpleConsoleOutput = getRemoteConsoleOutput(args[0], args[i + 1]);

                //Phase 2 - Remove redundant text - Just locators
                List<String> locatorsFullStrings = parse.createLocatorsStructure(simpleConsoleOutput);

                //Phase 3 - Remove xpath elements
                List<String> locatorsWithoutXpathElements = parse.removeXpathElements(locatorsFullStrings);

                //Phase 4 - Remove Strings "By.xxxxx"
                HashMap<String, Integer> locatorsHierarchyStructure = parse.createLocatorsHierarchy(locatorsWithoutXpathElements);

                //Phase 5 - Counting and removing duplications
                Map sortedMap = sortMapByValue(locatorsHierarchyStructure);

                allCSSRules.addAll(getCSSRulesPerJob(sortedMap, args[i]));

                jobShortNames.add(args[i]);
            }
            writeToFile(allCSSRules, jobShortNames, args[args.length - 1]);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            exitCode = -1;
        }
        System.exit(exitCode);
    }

    private static List<String> getCSSRulesPerJob(Map sortedMapElements, String shortName) {
        List<String> cssRules = new ArrayList<>();
        int maxValue = getMaxInstancesValue(sortedMapElements);
        Iterator iterator = sortedMapElements.entrySet().iterator();
        Map.Entry pair;
        while (iterator.hasNext()) {
            pair = (Map.Entry) iterator.next();
            int value = (int) pair.getValue();

            float normalizeValues = (float) value / maxValue;
            String hexValue = Integer.toHexString((int) ((1 - normalizeValues) * 255));
            if (hexValue.length() == 1) {
                hexValue = "0" + hexValue;
            }
            String cssRule = "." + shortName + " " + pair.getKey() + " { background-color: #ff" + hexValue + "00 !important; outline: 4px solid #ff" + hexValue + "00 !important; }" + System.getProperty("line.separator");
            cssRules.add(cssRule);
        }
        return cssRules;
    }

    private static void writeToFile(List<String> cssRules, List<String> jobShortNames, String cssFilePath) throws IOException {
        FileWriter writerToCss = new FileWriter(cssFilePath + "ahm-style.css");
        FileWriter writerToShortNames = new FileWriter(cssFilePath + "jobs-short-names.js");
        try {
            writerToShortNames.write("var ahmShortNames = [");

            Iterator iteratorForCss = cssRules.iterator();
            while (iteratorForCss.hasNext()) {
                writerToCss.write(iteratorForCss.next().toString());
            }
            for (int i = 0; i < jobShortNames.size(); i++) {
                writerToShortNames.write((i == 0 ? "" : ",") + "'" + jobShortNames.get(i) + "'");
            }
            writerToShortNames.write("];");
        } finally {
            writerToShortNames.close();
            writerToCss.close();
        }
    }

    private static int getMaxInstancesValue(Map sortMap) {
        int maxValue = 0;
        Map.Entry pair;
        Iterator iterator = sortMap.entrySet().iterator();
        while (iterator.hasNext()) {
            pair = (Map.Entry) iterator.next();
            maxValue = Math.max(maxValue, (int) pair.getValue());
        }
        return maxValue;
    }

    private static TreeMap<String, Integer> sortMapByValue(HashMap<String, Integer> map) {
        Comparator<String> comparator = new ValueComparator(map);
        //TreeMap is a map sorted by its keys.
        //The comparator is used to sort the TreeMap by keys.
        TreeMap<String, Integer> result = new TreeMap<>(comparator);
        result.putAll(map);
        return result;
    }

    private static String getRemoteConsoleOutput(String jenkinsURL, String jobName) throws Exception {
        String url = String.format("%s/jenkins/job/%s/lastStableBuild/consoleText", jenkinsURL, jobName);

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();

        if (responseCode != 200) {
            System.out.println(String.format("Failed to access to URL: %s %nresponse code equal to %d", url, responseCode));
        }
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine + '\n');
        }
        in.close();
        return response.toString();
    }
}

class ValueComparator implements Comparator<String> {

    HashMap<String, Integer> map = new HashMap<>();

    ValueComparator(HashMap<String, Integer> map) {
        this.map.putAll(map);
    }

    @Override
    public int compare(String s1, String s2) {
        if (map.get(s1) >= map.get(s2)) {
            return -1;
        } else {
            return 1;
        }
    }
}

