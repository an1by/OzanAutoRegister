package net.aniby.gray.driver;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public interface ExecutorInstance {
    long layout = 50L;
    long maximum_layout = 4000L;

    <T extends AppiumDriver> T driver();

    default WebElement searchUntilFind(By locator) {
        return searchUntilFind(locator, maximum_layout);
    }

    default WebElement searchUntilFind(By locator, long timeout) {
        long passed = 0;
        WebElement element = null;
        while (element == null) {
            try {
                if (timeout > 0 && passed >= timeout)
                    return null;
                element = driver().findElement(locator);
            } catch (Exception ignored) {
                try {
                    passed += layout;
                    Thread.sleep(layout);
                } catch (Exception ignored1) {}
            }
        }
        return element;
    }

    default WebElement forceClick(By locator) {
        return forceClick(locator, maximum_layout);
    }

    default WebElement forceClick(By locator, long timeout) {
        WebElement element = searchUntilFind(locator, timeout);
        if (element != null) {
            element.isDisplayed();
            element.click();
        }
        return element;
    }
}
