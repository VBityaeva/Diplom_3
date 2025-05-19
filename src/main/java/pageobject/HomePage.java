package pageobject;

import io.qameta.allure.Step;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pageobject.locators.HomePageLocators;

import java.time.Duration;

public class HomePage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @Step("Нажать кнопку 'Войти в аккаунт' на главной странице")
    public void clickLoginButton() {
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(HomePageLocators.LOGIN_BUTTON));
        loginButton.click();
    }

    @Step("Нажать кнопку 'Личный кабинет' на главной странице")
    public void clickPersonalAccountButton() {
        WebElement accountButton = wait.until(ExpectedConditions.elementToBeClickable(HomePageLocators.PERSONAL_ACCOUNT_BUTTON));
        accountButton.click();
    }

    @Step("Проверить, что пользователь вошел (кнопка 'Оформить заказ' видна)")
    public boolean isUserLoggedIn() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(HomePageLocators.ORDER_BUTTON));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Убедиться, что кнопка 'Оформить заказ' отображается")
    public void assertOrderButtonVisible() {
        if (!isUserLoggedIn()) {
            throw new AssertionError("Кнопка 'Оформить заказ' не найдена. Пользователь не авторизован.");
        }
    }

    @Step("Клик по вкладке 'Булки'")
    public void clickBunTab() {
        WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(HomePageLocators.BUN_TAB));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", tab);
        wait.until(ExpectedConditions.attributeContains(HomePageLocators.BUN_TAB, "class", HomePageLocators.ACTIVE_TAB_CLASS));
    }

    @Step("Клик по вкладке 'Соусы'")
    public void clickSauceTab() {
        WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(HomePageLocators.SAUCE_TAB));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", tab);
        wait.until(ExpectedConditions.attributeContains(HomePageLocators.SAUCE_TAB, "class", HomePageLocators.ACTIVE_TAB_CLASS));
    }

    @Step("Клик по вкладке 'Начинки'")
    public void clickFillingTab() {
        WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(HomePageLocators.FILLING_TAB));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", tab);
        wait.until(ExpectedConditions.attributeContains(HomePageLocators.FILLING_TAB, "class", HomePageLocators.ACTIVE_TAB_CLASS));
    }

    @Step("Проверка, что активна вкладка '{tabName}'")
    private void assertTabIsActive(WebElement tabElement, String tabName) {
        String classAttr = tabElement.getAttribute("class");
        if (!classAttr.contains(HomePageLocators.ACTIVE_TAB_CLASS)) {
            throw new AssertionError("Вкладка '" + tabName + "' не активна");
        }
    }

    @Step("Проверка, что активна вкладка 'Булки'")
    public void assertBunTabIsActive() {
        WebElement tab = wait.until(ExpectedConditions.visibilityOfElementLocated(HomePageLocators.BUN_TAB));
        assertTabIsActive(tab, "Булки");
    }

    @Step("Проверка, что активна вкладка 'Соусы'")
    public void assertSauceTabIsActive() {
        WebElement tab = wait.until(ExpectedConditions.visibilityOfElementLocated(HomePageLocators.SAUCE_TAB));
        assertTabIsActive(tab, "Соусы");
    }

    @Step("Проверка, что активна вкладка 'Начинки'")
    public void assertFillingTabIsActive() {
        WebElement tab = wait.until(ExpectedConditions.visibilityOfElementLocated(HomePageLocators.FILLING_TAB));
        assertTabIsActive(tab, "Начинки");
    }

}
