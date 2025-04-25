import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

public class LoginPageTest {
    private WebDriver driver;

    @BeforeEach
    public void setup() {
        Path driverPath = Paths.get("drivers", "chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", driverPath.toAbsolutePath().toString());

        driver = new ChromeDriver();
        driver.get("http://localhost:3000");
    }

    // 1
    @Test
    public void testLoginSuccess() {
        WebElement loginInput = driver.findElement(By.id("login"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement submitBtn = driver.findElement(By.id("submit"));

        loginInput.sendKeys("a");
        passwordInput.sendKeys("1");
        submitBtn.click();

        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.urlContains("/main"));

        String currentUrl = driver.getCurrentUrl();
        Assertions.assertEquals("http://localhost:3000/main", currentUrl);
    }

    // 2
    @Test
    public void testRegisterSuccess() {
        WebElement toggleRegister = driver.findElement(By.id("toggleRegister"));
        toggleRegister.click();

        WebElement loginInput = driver.findElement(By.id("login"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement passwordConfirmInput = driver.findElement(By.id("confirmPassword"));
        WebElement submitBtn = driver.findElement(By.id("submit"));

        String randomUsername = "user" + System.currentTimeMillis();

        loginInput.sendKeys(randomUsername);
        passwordInput.sendKeys("test");
        passwordConfirmInput.sendKeys("test");
        submitBtn.click();

        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.urlContains("/main"));

        String currentUrl = driver.getCurrentUrl();
        Assertions.assertEquals("http://localhost:3000/main", currentUrl);
    }

    // 3
    @Test
    public void testRegisterConfirmFail() {
        WebElement toggleRegister = driver.findElement(By.id("toggleRegister"));
        toggleRegister.click();

        WebElement loginInput = driver.findElement(By.id("login"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement passwordConfirmInput = driver.findElement(By.id("confirmPassword"));
        WebElement submitBtn = driver.findElement(By.id("submit"));

        loginInput.sendKeys("a");
        passwordInput.sendKeys("1");
        passwordConfirmInput.sendKeys("2");
        submitBtn.click();

        String currentUrl = driver.getCurrentUrl();
        Assertions.assertEquals("http://localhost:3000/", currentUrl);
    }

    // 4
    @Test
    public void testLoginFail() {
        WebElement loginInput = driver.findElement(By.id("login"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement submitBtn = driver.findElement(By.id("submit"));

        loginInput.sendKeys("a");
        passwordInput.sendKeys("2");
        submitBtn.click();

        String currentUrl = driver.getCurrentUrl();
        Assertions.assertEquals("http://localhost:3000/", currentUrl);
    }

    // 5
    @Test
    public void toggleTheme() {
        WebElement switchThemeBtn = driver.findElement(By.id("switch"));
        WebElement html = driver.findElement(By.tagName("html"));
        String initialTheme = html.getDomAttribute("data-theme");

        switchThemeBtn.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(d -> {
            WebElement htmlElement = d.findElement(By.tagName("html"));
            String newTheme = htmlElement.getDomAttribute("data-theme");
            return !newTheme.equals(initialTheme);
        });

        String updatedTheme = html.getDomAttribute("data-theme");
        Assertions.assertNotEquals(initialTheme, updatedTheme);
    }

    @AfterEach
    public void teardown() {
        try {
            WebElement logoutButton = driver.findElement(By.id("logout"));
            logoutButton.click();

            WebElement confirmLogoutButton = driver.findElement(By.id("confirmLogout"));
            confirmLogoutButton.click();
        } catch (Exception ignored) {

        }

        if (driver != null) {
            driver.quit();
        }
    }
}
