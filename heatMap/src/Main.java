import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        parsingToCSS parse = new parsingToCSS();
        String returnString = parse.getParsedStringFromFile("C:\\Users\\malkahan\\Desktop\\ConsoleOutput-Automation.txt");
        //Phase 1 - Remove Time Prefix
        String consoleOutputWithoutTime = parse.removeTimePrefixFromLine(returnString);

        //Phase 2 - Remove redundant text - Just locators
        List<String> locatorsStrings = parse.createLocatorsStructure(consoleOutputWithoutTime);

        //Phase 3 - Remove xpath elements
        List<String> listWithoutXpathElements = parse.removeXpathElements(locatorsStrings);

        //Phase 4 - Remove Strings "By.xxxxx"
        List<String> listInLocatorsStructure = parse.createLocatorsHierarchy(listWithoutXpathElements);

        FileWriter writer = new FileWriter("C:\\Users\\malkahan\\Desktop\\withoutXpathElements_.txt");
        for (String str : listInLocatorsStructure) {
            writer.write(str + " Count = " + Collections.frequency(listInLocatorsStructure, str) + System.getProperty("line.separator"));
        }
        writer.close();
    }
}
