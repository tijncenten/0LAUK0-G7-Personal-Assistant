/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pma.utils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringReplacer {
    public static String replace(String input, Pattern regex, StringReplacerCallback callback) {
        StringBuffer resultString = new StringBuffer();
        Matcher regexMatcher = regex.matcher(input);
        while (regexMatcher.find()) {
            regexMatcher.appendReplacement(resultString, callback.replace(regexMatcher));
        }
        regexMatcher.appendTail(resultString);

        return resultString.toString();
    }
}