import config.AppUrls;
import io.qameta.allure.Description;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.WebDriver;
import pageobject.HomePage;
import pageobject.LoginPage;
import pageobject.ProfilePage;
import util.DriverFactory;
import util.NavigationUtils;
import util.api.UserApi;
import util.model.User;
import util.api.RetryUtils;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class ProfileNavigationTest {

    private WebDriver driver;
    private HomePage homePage;
    private ProfilePage profilePage;
    private final UserApi userApi = new UserApi();
    private User testUser;
    private String accessToken;
    private final String browser;

    public ProfileNavigationTest(String browser) {
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
        userApi.createAndLoginUserWithRetry(testUser);

        if (testUser.getAccessToken() == null || testUser.getRefreshToken() == null) {
            throw new RuntimeException("Не удалось получить токены");
        }

        NavigationUtils.openUrlWithRetry(driver, AppUrls.BASE_URL);
        profilePage.setAccessTokenToLocalStorage(testUser.getAccessToken());
        profilePage.setRefreshTokenToLocalStorage(testUser.getRefreshToken());
        driver.navigate().refresh();

        homePage.clickPersonalAccountButton();
        profilePage.waitForProfilePageToLoad();
        NavigationUtils.assertCurrentUrlEquals(driver, AppUrls.PROFILE_PAGE);
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
    @Description("Переход из профиля в конструктор через кнопку 'Конструктор'")
    public void navigateFromProfileToConstructor() {
        profilePage.clickConstructorButton();

        RetryUtils.withRetry(() -> {
            NavigationUtils.assertCurrentUrlEquals(driver, AppUrls.BASE_URL);
            return null;
        });

        homePage.assertOrderButtonVisible();
    }

    @Test
    @Description("Переход из профиля в конструктор через клик по логотипу")
    public void navigateFromProfileToConstructorViaLogo() {
        profilePage.clickLogo();

        RetryUtils.withRetry(() -> {
            NavigationUtils.assertCurrentUrlEquals(driver, AppUrls.BASE_URL);
            return null;
        });

        homePage.assertOrderButtonVisible();
    }

    @Test
    @Description("Выход из аккаунта через кнопку 'Выйти' в личном кабинете")
    public void logoutFromProfile() {
        profilePage.clickLogoutButton();

        LoginPage loginPage = new LoginPage(driver);
        loginPage.assertOnLoginPage();
        loginPage.assertLoginButtonVisible();
    }
}