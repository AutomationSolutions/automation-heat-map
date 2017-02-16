import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static javafx.scene.input.KeyCode.K;
import static javafx.scene.input.KeyCode.V;

/**
 * Created by malkahan on 2/5/2017.
 */
class parsingToCSS {
    String parsedFromText;

    parsingToCSS() {
        parsedFromText = "";
    }

    String getParsedStringFromFile(String path) throws IOException {
        String contents = Files.lines(Paths.get(path)).collect(Collectors.joining("\n"));
        return contents;
    }

    String removeTimePrefixFromLine(String fileContent) {
        StringBuilder builder = new StringBuilder();
        String[] lines = fileContent.split("\r\n|\r|\n");
        Pattern timePattern = Pattern.compile("(?m)^(\\d\\d:\\d\\d:\\d\\d)");

        for (String line : lines) {
            Matcher matcher = timePattern.matcher(line);
            if (matcher.find()) {
                line = line.substring(matcher.end() + 1);
                builder.append(line).append("\r\n");
            }
        }
        return builder.toString();
    }

    List<String> createLocatorsStructure(String fileContent) {
        String stringPattern = "INFO: Executing Clicking on By.chained({";
        List<String> returnedString = new ArrayList<>();
        String[] lines = fileContent.split("\\r?\\n");

        for (int i = 0; i < lines.length; i++) {
            if (lines[i].startsWith(stringPattern)) {
                StringBuilder builder = new StringBuilder();
                returnedString.add(builder.append(lines[i].substring(stringPattern.length(), lines[i].length() - 2)).toString());
            }
        }
        return returnedString;
    }

    HashMap<String, Integer> createLocatorsHierarchy(List<String> locatorsList) {
        List<String> returnedString;
        HashMap<String, Integer> hashMap = new HashMap<>();
        returnedString = removeBySelector(locatorsList, "By.cssSelector: ", "");
        returnedString = removeBySelector(returnedString, "By.className: ", ".");
        for (String str : returnedString) {
            int count = hashMap.get(str) != null ? hashMap.get(str) : 0;
            hashMap.put(str, count + 1);
        }
        return hashMap;
    }

    List<String> removeXpathElements(List<String> list) {
        List<String> returnedString = new ArrayList<>();
        for (String listItem : list) {
            if (!listItem.contains("xpath")) {
                returnedString.add(listItem);
            }
        }
        return returnedString;
    }

    private List<String> removeBySelector(List<String> list, String patternToRemove, String patternToReplace) {
        List<String> returnedString = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            returnedString.add(i, list.get(i).replaceAll(patternToRemove, patternToReplace).replaceAll(",", " "));
        }
        return returnedString;
    }
}
