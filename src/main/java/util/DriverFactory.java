package util;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Allure;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class DriverFactory {

    private static final int MAX_RETRIES = 2;

    public static WebDriver getDriver(String browserName) {
        if (browserName == null) {
            return createDriverWithRetry(() -> createChromeDriver());
        }

        switch (browserName.toLowerCase()) {
            case "yandex":
                return createDriverWithRetry(() -> createYandexDriver());
            case "chrome":
            default:
                return createDriverWithRetry(() -> createChromeDriver());
        }
    }

    private static WebDriver createChromeDriver() {
        WebDriverManager.chromedriver().setup();
        return new ChromeDriver();
    }

    private static WebDriver createYandexDriver() {
        String driverDir = System.getenv("YANDEX_BROWSER_DRIVER_DIR");
        String driverFilename = System.getenv("YANDEX_BROWSER_DRIVER_FILENAME");
        String browserPath = System.getenv("YANDEX_BROWSER_PATH");

        if (driverDir == null || driverFilename == null || browserPath == null) {
            throw new IllegalStateException("Yandex browser environment variables are not set.");
        }

        String driverPath = String.format("%s/%s", driverDir, driverFilename);
        System.setProperty("webdriver.chrome.driver", driverPath);

        ChromeOptions options = new ChromeOptions();
        options.setBinary(browserPath);

        return new ChromeDriver(options);
    }

    private static WebDriver createDriverWithRetry(DriverCreator creator) {
        int attempt = 0;
        while (attempt <= MAX_RETRIES) {
            int currentAttempt = attempt + 1;
            try {
                Allure.step("Попытка запуска WebDriver №" + currentAttempt);
                WebDriver driver = creator.create();
                Allure.step("Успешный запуск WebDriver на попытке №" + currentAttempt);
                return driver;
            } catch (Exception e) {
                Allure.step("Ошибка запуска WebDriver на попытке №" + currentAttempt + ": " + e.getClass().getSimpleName() + " — " + e.getMessage());
                attempt++;
                if (attempt > MAX_RETRIES) {
                    throw new RuntimeException("Не удалось запустить WebDriver после " + MAX_RETRIES + " попыток", e);
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {}
            }
        }
        throw new IllegalStateException("Неожиданная ошибка в логике retry");
    }

    @FunctionalInterface
    private interface DriverCreator {
        WebDriver create();
    }
}
