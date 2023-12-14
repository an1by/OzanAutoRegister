package net.aniby.utils.dropmail;

public class IncomingMail {
    private final String text;

    public IncomingMail(String text, String subject, String sender, String receiver, String downloadUrl, long rawSize) {
        this.text = text;
        this.subject = subject;
        this.sender = sender;
        this.receiver = receiver;
        this.downloadUrl = downloadUrl;
        this.rawSize = rawSize;
    }

    public String text() {
        return text;
    }

    private final String subject;

    public String subject() {
        return subject;
    }

    private final String sender;

    public String sender() {
        return sender;
    }

    private final String receiver;

    public String receiver() {
        return receiver;
    }

    private final String downloadUrl;

    public String downloadUrl() {
        return downloadUrl;
    }

    private final long rawSize;

    public long rawSize() {
        return rawSize;
    }
}