package net.aniby.gray.driver;

import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import net.aniby.gray.Main;
import net.aniby.gray.storage.Passport;
import net.aniby.gray.web.CodeFinder;
import net.aniby.gray.web.FiveSIM;
import net.aniby.utils.Colors;
import net.aniby.utils.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.util.Objects;
import java.util.Random;

public class AndroidExecutor implements ExecutorInstance {
    private AndroidDriver android;
    private final String appPackage = "com.ozan.android";
    private File savedCards;

    @Before
    public void setUp() throws IOException, URISyntaxException {
        savedCards = Main.getPath().resolve("saved_cards.txt").toFile();
        FileUtils.createFile(savedCards);

        URL url = new URL(Main.getConfig().getUrl());
//        URL url = new URL("http://127.0.0.1:5723/wd/hub");
        // Android
        DesiredCapabilities capabilities = new DesiredCapabilities();

        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("deviceName", Main.getConfig().getDeviceName());
        capabilities.setCapability("platformVersion", Main.getConfig().getPlatformVersion());
//        capabilities.setCapability("deviceName", "emulator-5554");
//        capabilities.setCapability("platformVersion", "10");
        capabilities.setCapability("automationName", "UIAutomator2");

        android = new AndroidDriver(url, capabilities);
    }

    public String activeAppActivity() {
        return android.getCurrentPackage() + android.currentActivity();
    }

    @Test
    public void text() {
//        Main.debugWarning("Code disappeared! Block...");
        forceClick(
                By.xpath("//android.widget.EditText[@resource-id=\"com.ozan.android:id/editTextSpinner\" and @text=\"City\"]"),
                10000L
        );
        forceClick(
                By.xpath("//androidx.recyclerview.widget.RecyclerView[@resource-id=\"com.ozan.android:id/recyclerViewItems\"]/android.view.ViewGroup[1]"),
                10000L
        );
        forceClick(
                By.xpath("//android.widget.EditText[@resource-id=\"com.ozan.android:id/editTextSpinner\" and @text=\"District\"]"),
                10000L
        );
        forceClick(
                By.xpath("//androidx.recyclerview.widget.RecyclerView[@resource-id=\"com.ozan.android:id/recyclerViewItems\"]/android.view.ViewGroup[1]"),
                10000L
        );
        forceClick(
                By.xpath("//android.widget.EditText[@resource-id=\"com.ozan.android:id/editTextSpinner\" and @text=\"Neighbourhood\"]")
        );
        forceClick(
                By.xpath("//androidx.recyclerview.widget.RecyclerView[@resource-id=\"com.ozan.android:id/recyclerViewItems\"]/android.view.ViewGroup[1]"),
                10000L
        );

        int buildingNumber = random.nextInt(1, 100);
        forceClick(By.id("com.ozan.android:id/editTextAddress")).sendKeys(
                "AAAAA" + " " + buildingNumber + ", " + random.nextInt(390000, 490000)
        );

        forceClick(By.id("com.ozan.android:id/editTextBuilding"), 10000L).sendKeys(String.valueOf(buildingNumber));
        forceClick(By.id("com.ozan.android:id/editTextApartment"), 10000L).sendKeys(String.valueOf(random.nextInt(1, 10)));

        forceClick(By.id("com.ozan.android:id/buttonContinue"), 10000L);
    }

    public String registerWithPhone() throws Exception {
        JSONObject bought = null;
        while (bought == null) {
            try {
                bought = FiveSIM.buy();
            } catch (Exception ignored) {}
        }
        long id = (long) bought.get("id");
        String phoneNumber = (String) bought.get("phone");
        phoneNumber = phoneNumber.substring(3);

        WebElement phoneNumberInput = forceClick(By.id("com.ozan.android:id/editTextPhoneNumber"), -1);
        phoneNumberInput.clear();

        Main.debugInfo("Offered phone number: " + phoneNumber);

        phoneNumberInput.sendKeys(phoneNumber);
        forceClick(By.id("com.ozan.android:id/authBottomActionView"));

        Main.debugInfo("Waiting for code...");
        String code = FiveSIM.getCode(id);
        if (code == null) {
            Main.debugWarning("Code disappeared! Block...");
            FiveSIM.ban(id);

            forceClick(By.id("com.ozan.android:id/imageViewBackButton"), -1); // Return back
            return registerWithPhone();
        }
        FiveSIM.end(id);
        return code;
    }

    private Passport passport;
    public void setPassport(Passport passport) {
        this.passport = passport;
        Main.debugInfo("Passport was set: " + passport.toString());
    }

    public void scrollToApprove() {
        WebElement element = null;
        while (element == null)
            element = searchUntilFind(By.id("com.ozan.android:id/buttonApprove"));
        while (!element.isEnabled()) {
            new TouchAction(android).longPress(PointOption.point(256, 1156))
                    .waitAction(WaitOptions.waitOptions(Duration.ofMillis(5)))
                    .moveTo(PointOption.point(0, -100000)).perform().release();
        }
        element.isDisplayed();
        element.click();
    }

    private static final Random random = new Random();

    public CodeFinder getValidFinder() throws URISyntaxException, IOException, ParseException, InterruptedException {
        CodeFinder finder = new CodeFinder();
        forceClick(By.id("com.ozan.android:id/editTextEmail"), -1).sendKeys(finder.email());
        forceClick(By.id("com.ozan.android:id/buttonContinue"));

        WebElement error = searchUntilFind(By.id("com.ozan.android:id/textinput_error"));
        if (error != null) {
            forceClick(By.id("com.ozan.android:id/editTextEmail"), -1).clear();
            return getValidFinder();
        }
        return finder;
    }

    public void processingStep() throws Exception {
        while (!Objects.equals(android.getCurrentPackage(), appPackage))
            Thread.sleep(1);

        switch (android.currentActivity()) {
            case ".appintro.language.ui.LanguageSelectActivity":
                Main.debugInfo("Selecting language and region...");
                forceClick(By.id("com.ozan.android:id/languageEn"));
                forceClick(By.id("com.ozan.android:id/buttonContinue"));
                forceClick(By.id("com.ozan.android:id/buttonSkip"));
                forceClick(By.id("com.ozan.android:id/regionDefault"));
                break;
            case ".authentication.ui.AuthenticationActivity":
                // 5sim get code
                String phoneCode = registerWithPhone();
                Main.debugInfo("Inserting code from phone: " + phoneCode);

                // Write phone code
                searchUntilFind(By.id("com.ozan.android:id/otpTextView"));
                new Actions(android).sendKeys(phoneCode).perform();

                // Email

                String emailCode = null;
                while (emailCode == null) {
                    CodeFinder finder = getValidFinder();

                    Main.debugInfo("Email registered: " + finder.email());

                    // Write E-Mail code
                    searchUntilFind(By.id("com.ozan.android:id/otpTextView"));
                    emailCode = finder.loopFetching(20000L);
                    if (emailCode == null)
                        forceClick(By.id("com.ozan.android:id/imageViewBackButton"));
                }
                Main.debugInfo("Inserting code from email: " + emailCode);
                new Actions(android).sendKeys(emailCode).perform();


                // Write name / surname
                Main.debugInfo("Inserting name and surname...");
                WebElement nameElement = forceClick(By.id("com.ozan.android:id/editTextName"));
                nameElement.sendKeys(passport.name);
                WebElement surnameElement = forceClick(By.id("com.ozan.android:id/editTextSurname"));
                surnameElement.sendKeys(passport.surname);
                forceClick(By.id("com.ozan.android:id/buttonContinue"));

                Thread.sleep(2000);
                forceClick(By.id("com.ozan.android:id/buttonContinue"));
                break;
            case ".profile.ui.questions.SecurityQuestionsActivity":
                Main.debugInfo("Security questions answering...");
                String answersXpath = "(//android.widget.EditText[@resource-id=\"com.ozan.android:id/editTextAnswer\"])[%s]";
                for (int i = 1; i < 4; i++) {
                    WebElement answerElement = forceClick(By.xpath(
                            String.format(answersXpath, i)
                    ));
                    answerElement.sendKeys("1");
                    if (i == 2) {
                        android.pressKey(new KeyEvent(AndroidKey.BACK));
                    }
                }

                forceClick(By.id("com.ozan.android:id/buttonContinue"));
                Thread.sleep(2000);
                break;
            case ".authentication.ui.gdpr.GdprAgreementActivity":
                Main.debugInfo("Agreememts...");
                forceClick(By.id("com.ozan.android:id/checkBoxGdprOverlay"));
                scrollToApprove();
                Thread.sleep(500);

                forceClick(By.id("com.ozan.android:id/checkBoxUserAgreementOverlay"));
                scrollToApprove();
                Thread.sleep(1000);

                forceClick(By.id("com.ozan.android:id/buttonApprove"));

                // .authentication.ui.AuthenticationActivity
                Main.debugInfo("Inserting auth code...");
                String ozanCode = Main.getConfig().getOzanCode();
                Thread.sleep(1500);
                new Actions(android).sendKeys(ozanCode).perform();
                Thread.sleep(1500);
                new Actions(android).sendKeys(ozanCode).perform();
                return;
        }
        Thread.sleep(100);
        processingStep();
    }

    public void passportInput() throws InterruptedException {
        Main.debugInfo("Inserting passport data...");
        Thread.sleep(500);
        WebElement nameInput = forceClick(By.id("com.ozan.android:id/editTextName"));
        nameInput.clear();
        nameInput.sendKeys(passport.name);

        Thread.sleep(500);
        WebElement surname = forceClick(By.id("com.ozan.android:id/editTextSurname"));
        surname.clear();
        surname.sendKeys(passport.surname);

        // Date
        Thread.sleep(500);
        forceClick(By.id("com.ozan.android:id/editTextDateOfBirth"));
        Thread.sleep(500);
        forceClick(By.id("com.ozan.android:id/mtrl_picker_header_toggle"));

        Thread.sleep(500);
        WebElement dateInput = forceClick(By.className("android.widget.EditText"));
        dateInput.clear();
        dateInput.sendKeys(passport.getFormattedDate());
        forceClick(By.id("com.ozan.android:id/confirm_button"));

        // Continue
        forceClick(By.id("com.ozan.android:id/buttonContinue"));

        // Documents
        forceClick(By.id("com.ozan.android:id/editTextIdNumber")).sendKeys(passport.id);
        forceClick(By.id("com.ozan.android:id/editTextDocumentNumber")).sendKeys(passport.serial);

        forceClick(By.id("com.ozan.android:id/buttonContinue"));
    }

    public boolean notPassedVerify = false;

    public void verifyStep() throws Exception {
        while (!Objects.equals(android.getCurrentPackage(), appPackage))
            Thread.sleep(1);

        switch (android.currentActivity()) {
            case ".dashboard.ui.DashboardActivity":
                Main.debugInfo("Going to SuperPlan verify menu...");
                forceClick(By.id("com.ozan.android:id/bottomNavProfile"));
                break;
            case ".profile.ui.ProfileScreenActivity":
                forceClick(By.xpath("(//androidx.recyclerview.widget.RecyclerView[@resource-id=\"com.ozan.android:id/recyclerViewSubItems\"])[2]/android.view.ViewGroup[1]"));
                break;
            case ".kyc.second.ui.KYCActivity":
                Main.debugInfo("Region selecting...");
                forceClick(By.id("com.ozan.android:id/editTextSearch")).sendKeys("Turkey");
                forceClick(By.xpath("//androidx.recyclerview.widget.RecyclerView[@resource-id=\"com.ozan.android:id/countriesRecyclerView\"]/android.view.ViewGroup"));

                passportInput();

                boolean passed = false;
                while (!passed) {
                    WebElement mistake
                            = searchUntilFind(By.xpath("//android.widget.TextView[@resource-id=\"com.ozan.android:id/textContentHeader\"]"));
                    if (mistake != null && mistake.getText().equals("There seems to be a mistake")) {
                        forceClick(By.id("com.ozan.android:id/buttonAction"));
                        Thread.sleep(300);
                        forceClick(By.id("com.ozan.android:id/imageViewBackButton"));
                        Thread.sleep(300);
                        Main.getPassportList().remove(0);
                        if (Main.getPassportList().isEmpty()) {
                            notPassedVerify = true;
                            return;
                        }
                        setPassport(Main.getPassportList().get(0));
                        passportInput();
                    } else passed = true;
                }

                // Address
                Main.debugInfo("Fake Address inserting...");
                forceClick(
                        By.xpath("//android.widget.EditText[@resource-id=\"com.ozan.android:id/editTextSpinner\" and @text=\"City\"]"),
                        10000L
                );
                forceClick(
                        By.xpath("//androidx.recyclerview.widget.RecyclerView[@resource-id=\"com.ozan.android:id/recyclerViewItems\"]/android.view.ViewGroup[1]"),
                        10000L
                );
                forceClick(
                        By.xpath("//android.widget.EditText[@resource-id=\"com.ozan.android:id/editTextSpinner\" and @text=\"District\"]"),
                        10000L
                );
                forceClick(
                        By.xpath("//androidx.recyclerview.widget.RecyclerView[@resource-id=\"com.ozan.android:id/recyclerViewItems\"]/android.view.ViewGroup[1]"),
                        10000L
                );
                forceClick(
                        By.xpath("//android.widget.EditText[@resource-id=\"com.ozan.android:id/editTextSpinner\" and @text=\"Neighbourhood\"]")
                );
                forceClick(
                        By.xpath("//androidx.recyclerview.widget.RecyclerView[@resource-id=\"com.ozan.android:id/recyclerViewItems\"]/android.view.ViewGroup[1]"),
                        10000L
                );
                int buildingNumber = random.nextInt(1, 100);
                forceClick(By.id("com.ozan.android:id/editTextAddress")).sendKeys(
                        "AAAAA" + " " + buildingNumber + ", " + random.nextInt(390000, 490000)
                );

                forceClick(By.id("com.ozan.android:id/editTextBuilding"), 10000L).sendKeys(String.valueOf(buildingNumber));
                forceClick(By.id("com.ozan.android:id/editTextApartment"), 10000L).sendKeys(String.valueOf(random.nextInt(1, 10)));

                forceClick(By.id("com.ozan.android:id/buttonContinue"), 10000L);

                // Profession
                Main.debugInfo("Profession selecting...");
                forceClick(
                        By.xpath("//androidx.recyclerview.widget.RecyclerView[@resource-id=\"com.ozan.android:id/recyclerViewProfessions\"]/android.view.ViewGroup[1]"),
                        1_000L
                );

                forceClick(By.id("com.ozan.android:id/buttonContinue"));
                forceClick(By.id("com.ozan.android:id/buttonAction"));
                return;
        }

        Thread.sleep(100);
        verifyStep();
    }

    public String registerCard(boolean firstTime) throws InterruptedException, IOException {
        if (firstTime) {
            forceClick(By.id("com.ozan.android:id/bottomNavSuperCard"), 5000L);
        } else {
            forceClick(By.id("com.ozan.android:id/buttonAddCard"));
        }
//        adb uninstall io.appium.uiautomator2.server.test
        forceClick(By.xpath("//androidx.recyclerview.widget.RecyclerView[@resource-id=\"com.ozan.android:id/recyclerViewCreateCards\"]/android.view.ViewGroup[2]"));
        forceClick(By.id("com.ozan.android:id/buttonAction"));

        forceClick(By.xpath("//android.widget.Button[@text='Card Details']"), -1);

        String card = getCardData();

        FileUtils.appendToFile(savedCards, card);

        forceClick(By.id("com.ozan.android:id/imageViewBackButton"));
        return card;

    }
    public void cardsStep() throws InterruptedException, IOException {
        while (!Objects.equals(android.getCurrentPackage(), appPackage))
            Thread.sleep(1);

        switch (android.currentActivity()) {
            case ".dashboard.ui.DashboardActivity":
                for (int i = 0; i < 5; i++) {
                    Main.debugInfo("Creating new card...");
                    String card = registerCard(i == 0);
                    Main.debug(Colors.ANSI_GREEN.code() + "New card successfully created and saved: " + card);
                }
                return;
        }

        Thread.sleep(100);
        cardsStep();
    }

    public String getCardData() throws InterruptedException {
        String cardNumber = "";
        String expDate = "";
        String cvv = "";
        while (cardNumber.isEmpty() || expDate.isEmpty() || cvv.isEmpty()) {
            cardNumber = searchUntilFind(By.xpath("//android.widget.TextView[@resource-id=\"com.ozan.android:id/textViewCardNumber\"]"), -1).getText();
            expDate = searchUntilFind(By.xpath("//android.widget.TextView[@resource-id=\"com.ozan.android:id/textViewValidThru\"]"), -1).getText();
            cvv = searchUntilFind(By.xpath("//android.widget.TextView[@resource-id=\"com.ozan.android:id/textViewCvv\"]"), -1).getText();
            Thread.sleep(50);
        }
        String[] date = expDate.split("/");
        return String.format("%s:%s:%s:%s",
                cardNumber.replace(" ", ""),
                date[0], date[1], cvv
        );
    }

    @Test
    public void executeAutoRegister() throws Exception {
        android.terminateApp(appPackage);
        clearOzan();
        Main.debugInfo("Previous Ozan data was cleared");

        Main.debugInfo("Starting Ozan...");
        android.activateApp(appPackage);

        Main.debugInfo("Authorization...");
        processingStep();

        Main.debugInfo("Authorization ended! Verification...");
        verifyStep();

        if (notPassedVerify) {
            Main.debug("Stop because debil");
            return;
        }

        Main.debugInfo("Verification ended! Card creating...");
        cardsStep();
    }

    public void clearOzan() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("adb", "shell", "pm", "clear", "com.ozan.android");
        Process pc = pb.start();
        pc.waitFor();
    }

    @After
    public void tearDown()
    {
        android.quit();
    }

    @Override
    public AndroidDriver driver() {
        return android;
    }
}
