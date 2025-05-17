import config.AppUrls;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pageobject.HomePage;
import pageobject.ProfilePage;
import util.DriverFactory;
import util.NavigationUtils;
import util.api.UserApi;
import util.model.User;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class NavigationProfileTest {

    private WebDriver driver;
    private HomePage homePage;
    private ProfilePage profilePage;
    private final UserApi userApi = new UserApi();
    private User testUser;
    private String accessToken;
    private final String browser;

    public NavigationProfileTest(String browser) {
        this.browser = browser;
    }

    @Parameterized.Parameters(name = "Browser: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {"chrome"},
                {"yandex"}
        });
    }

    @Before
    public void setUp() {
        driver = DriverFactory.getDriver(browser);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

        homePage = new HomePage(driver);
        profilePage = new ProfilePage(driver);

        testUser = User.random();
        accessToken = userApi.createAndLoginUserWithRetry(testUser);
        if (accessToken == null || accessToken.isEmpty()) {
            throw new RuntimeException("Не удалось получить accessToken");
        }

        NavigationUtils.openUrlWithRetry(driver, AppUrls.BASE_URL);
        profilePage.setAccessTokenToLocalStorage(accessToken);
        driver.navigate().refresh();
    }

    @After
    public void tearDown() {
        if (testUser != null && accessToken != null) {
            try {
                userApi.deleteUserByAccessToken(accessToken);
            } catch (Exception e) {
                System.err.println("Не удалось удалить пользователя: " + e.getMessage());
            }
        }
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testNavigateToPersonalAccount() {
        homePage.clickPersonalAccountButton();
        profilePage.waitForProfilePageToLoad();
        String currentUrl = driver.getCurrentUrl();
        assertEquals("Ожидался переход на страницу профиля", AppUrls.PROFILE_PAGE, currentUrl);
    }

    @Test
    public void navigateFromProfileToConstructor() {
        System.out.println("Открытие страницы профиля...");
        NavigationUtils.openUrlWithRetry(driver, AppUrls.PROFILE_PAGE);

        String currentUrl = driver.getCurrentUrl();
        System.out.println("Текущий URL после перехода: " + currentUrl);

        // Проверка, не попали ли мы на страницу логина
        if (currentUrl.contains("/login")) {
            System.err.println("❌ Пользователь не авторизован. Попали на страницу логина вместо профиля.");
            System.err.println("accessToken: " + accessToken);
            System.err.println("HTML страницы:\n" + driver.getPageSource());
            throw new AssertionError("Пользователь не авторизован. Тест остановлен.");
        }

        System.out.println("Нажатие на кнопку 'Конструктор'...");
        profilePage.clickConstructorButton();

        System.out.println("Ожидание перехода на главную страницу (URL: " + AppUrls.BASE_URL + ")...");
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.urlToBe(AppUrls.BASE_URL));

        System.out.println("Проверка отображения кнопки 'Оформить заказ'...");
        homePage.assertOrderButtonVisible();

        System.out.println("✅ Успешный переход из профиля в конструктор");
    }

    @Test
    public void testNavigateFromProfileToConstructorViaLogo() {
        profilePage.clickLogo();
        NavigationUtils.openUrlWithRetry(driver, AppUrls.BASE_URL);

        homePage.assertOrderButtonVisible();
    }
}
