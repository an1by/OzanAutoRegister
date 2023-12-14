package net.aniby.gray.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ClientConfiguration {
    private File file;

    private String url;

    public String getUrl() {
        return url;
    }

    private String deviceName;

    public String getDeviceName() {
        return deviceName;
    }

    private String platformVersion;

    public String getPlatformVersion() {
        return platformVersion;
    }

    private String fiveSimLink;

    public String getFiveSimLink() {
        return fiveSimLink;
    }
    private String fiveSimApiKey;
    public String getFiveSimApiKey() {return fiveSimApiKey;}

    private String dropmailLink;

    public String getDropmailLink() {
        return dropmailLink;
    }

    private List<String> blacklistedDomains;

    public List<String> getBlacklistedDomains() {
        return blacklistedDomains;
    }
    private String ozanCode;

    public String getOzanCode() {
        return ozanCode;
    }

    private boolean debug;
    public boolean isDebug() {
        return debug;
    }

    private ClientConfiguration() {}

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
        mapper.findAndRegisterModules();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    public static ClientConfiguration get(File file) throws IOException {
        ClientConfiguration configuration = createObjectMapper().readValue(file, ClientConfiguration.class);
        configuration.file = file;
        return configuration;
    }

    public void save() throws IOException {
        createObjectMapper().writeValue(this.file, this);
    }
}
