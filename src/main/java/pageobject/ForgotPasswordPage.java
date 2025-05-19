package pageobject;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pageobject.locators.ForgotPasswordLocators;

import java.time.Duration;

public class ForgotPasswordPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public ForgotPasswordPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    @Step("Нажимаем ссылку 'Войти'")
    public void clickLoginLink() {
        WebElement loginLink = wait.until(ExpectedConditions.elementToBeClickable(ForgotPasswordLocators.LOGIN_LINK));
        loginLink.click();
    }
}
