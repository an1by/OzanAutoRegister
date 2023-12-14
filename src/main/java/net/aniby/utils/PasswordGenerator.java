package net.aniby.utils;

import java.util.Random;
import java.util.stream.Collectors;

public class PasswordGenerator {
    public static String alphabet = "0123456789ABCDEFGHIKLMNOPQRSTUVWXYZabcdefghiklmnopqrstuvwxyz";
    public static String generate(int length) {
        return new Random().ints(length, 0, alphabet.length()).mapToObj(i -> String.valueOf(alphabet.charAt(i))).collect(Collectors.joining());
    }
}
