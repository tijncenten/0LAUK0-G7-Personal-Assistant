/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pma.utils;

import java.util.regex.Matcher;

public interface StringReplacerCallback {
    public String replace(Matcher match);
}