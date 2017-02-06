import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by malkahan on 2/5/2017.
 */
public class parsingToCSS {
    public String parsedFromText;

    public parsingToCSS() {
        parsedFromText = "";
    }

    public String getParsedStringFromFile(String path) throws IOException {
        String contents = Files.lines(Paths.get(path)).collect(Collectors.joining("\n"));
        return contents;
    }

    public String removeTimePrefixFromLine(String fileContent) {
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

    public List<String> createLocatorsStructure(String fileContent) {
        String stringPattern = "INFO: Executing Clicking on By.chained({";
        List<String> returnedString = new ArrayList<>();
        String[] lines = fileContent.split("\r\n|\r|\n");

        for (int i = 0; i < lines.length; i++) {
            if (lines[i].startsWith(stringPattern)) {
                StringBuilder builder = new StringBuilder();
                returnedString.add(builder.append(lines[i].substring(stringPattern.length(), lines[i].length() - 2)).toString());
            }
        }
        return returnedString;
    }

    public List<String> createLocatorsHierarchy(List<String> locatorsList) {
        List<String> returnedString;
        returnedString = removeBySelector(locatorsList, "By.cssSelector: ", "");
        returnedString = removeBySelector(returnedString, "By.className: ", ".");
        return returnedString;
    }

    public List<String> removeXpathElements(List<String> list) {
        List<String> returnedString = new ArrayList<>();
        for (String listItem : list) {
            if (!listItem.contains("xpath")) {
//                list.remove(list.indexOf(listItem));
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
