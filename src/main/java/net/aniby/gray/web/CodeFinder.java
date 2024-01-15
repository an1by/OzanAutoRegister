package net.aniby.gray.web;

import net.aniby.gray.Main;
import net.aniby.utils.dropmail.DropMailAPI;
import net.aniby.utils.dropmail.IncomingMail;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeFinder {
    private final DropMailAPI dropMail;

    public CodeFinder() throws URISyntaxException, IOException, ParseException, InterruptedException {
        this.dropMail = new DropMailAPI(Main.getConfig().getDropmailLink());
        while (email() == null || Main.getConfig().getBlacklistedDomains().contains(email().split("@")[1]))
            dropMail.create();
    }

    public String email() {
        return dropMail.emailAddress();
    }

    public String loopFetching(long timeout) throws ParseException, URISyntaxException, IOException, InterruptedException {
        long start = System.currentTimeMillis();
        while (true) {
            for (IncomingMail founded : dropMail.fetchMails()) {
                System.out.println(founded.text());
                String code = findCode(founded.text());
                if (code != null)
                    return code;
            }
            if (System.currentTimeMillis() - start >= timeout)
                return null;
            Thread.sleep(2000);
        }
    }

    private static final Pattern pattern = Pattern.compile("\\d{6}");
    private String findCode(String text) {
        final Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group() : null;
    }
}
