package net.aniby.gray.storage;

public class Passport {
    public final String id;
    public final String serial;
    public final String name;
    public final String surname;
    public final String born;

    public Passport(String data) {
        String[] split = data.split(" \\| ");
        serial = split[0];
        id = split[2];
        name = split[3];
        surname = split[4];
        born = split[5];
    }

    public String getFormattedDate() {
        String[] split = born.split("\\.");
        return String.format("%s/%s/%s", split[1], split[0], split[2]);
    }

    @Override
    public String toString() {
        return serial + " | " + id + " | " + name + " | " + surname + " | " + born;
    }
}
