package net.aniby.gray;

import net.aniby.gray.configuration.ClientConfiguration;
import net.aniby.gray.driver.AndroidExecutor;
import net.aniby.gray.storage.Passport;
import net.aniby.utils.Colors;
import net.aniby.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    private static ClientConfiguration config;
    private static List<Passport> passportList;

    public static ClientConfiguration getConfig() {
        return config;
    }

    public static List<Passport> getPassportList() {
        return passportList;
    }

    public static Path getPath() throws URISyntaxException {
        return new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toPath().getParent();
    }

    public static void debugInfo(String text) {
        debug(Colors.ANSI_CYAN.code() + text);
    }
    public static void debugWarning(String text) {
        debug(Colors.ANSI_YELLOW.code() + text);
    }

    private static PrintWriter printer;
    public static void debug(String text) {
        if (config == null || config.isDebug())
            printer.println(text);
    }

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        printer = new PrintWriter(System.out, true, StandardCharsets.UTF_8);

        debugInfo("[!] Ozan Autoreg [!]");
        debug(Colors.ANSI_YELLOW.code() + "[>] Creator/coder: " + Colors.ANSI_GREEN.code() + "aniby.net");

        config = ClientConfiguration.get(getPath().resolve("config.yml").toFile());
        debugInfo("Config loaded!");

        passportList = Arrays.stream(FileUtils.readFile(
                getPath().resolve("passports.txt").toFile()
        ).split("\n")).map(Passport::new).collect(Collectors.toList());
        debugInfo("Passports (" + passportList.size() + ") are loaded!");

        try {
            AndroidExecutor androidExecutor = new AndroidExecutor();
            androidExecutor.setUp();
            debug("Initialized! Starting...");

            while (!passportList.isEmpty()) {
                androidExecutor.setPassport(passportList.get(0));
                androidExecutor.executeAutoRegister();
                passportList.remove(0);
            }
            androidExecutor.tearDown();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}