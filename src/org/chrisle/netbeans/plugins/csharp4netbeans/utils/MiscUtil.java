package org.chrisle.netbeans.plugins.csharp4netbeans.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

public class MiscUtil {
    
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    
    public static List<String> getLinesFromFileContent(String fileContent) {
        List<String> result = new ArrayList<>();
        try (Scanner scanner = new Scanner(fileContent)) {
            while (scanner.hasNextLine()) {
                result.add(scanner.nextLine());
            }
        }

        return result;
    }
    
    public static int getOpenSymbolsAmount(String line) {
        int result = 0;
        char[] chars = line.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char ch = chars[i];
            if (ch == '{' || ch == '(') {
                result++;
            } else if (ch == '}' || ch == ')') {
                result--;
            }
        }
        return result;
    }
    
    public static String getAmountSeparatorChars(int amount) {
        if (amount <= 0) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        IntStream.rangeClosed(1, amount).forEach((int i) -> {
            stringBuilder.append(" ");
        });
        return stringBuilder.toString();
    }

}