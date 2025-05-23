import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MainPageTest {
    private WebDriver driver;

    @BeforeEach
    public void setup() {
        Path driverPath = Paths.get("drivers", "chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", driverPath.toAbsolutePath().toString());

        driver = new ChromeDriver();
        driver.get("http://localhost:3000/");
        driver.findElement(By.id("login")).sendKeys("a");
        driver.findElement(By.id("password")).sendKeys("1");
        driver.findElement(By.id("submit")).click();

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(webDriver -> webDriver.getCurrentUrl().equals("http://localhost:3000/main"));
        driver.manage().window().maximize();
    }

    // 6
    @Test
    public void testLogoutSuccess() {
        WebElement logoutButton = driver.findElement(By.id("logout"));
        logoutButton.click();

        WebElement confirmLogoutButton = driver.findElement(By.id("confirmLogout"));
        confirmLogoutButton.click();

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(webDriver -> webDriver.getCurrentUrl().equals("http://localhost:3000/"));

        String currentUrl = driver.getCurrentUrl();
        assertEquals("http://localhost:3000/", currentUrl);
    }

    // 9
    @Test
    public void testCheckStats() {
        WebElement statsButton = driver.findElement(By.id("user"));
        statsButton.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("stats")));
        wait.until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElementLocated(
                By.cssSelector("#stats p"),
                "loading"
        )));

        WebElement loginElement = dialog.findElement(By.cssSelector("#stats p"));

        WebElement closeButton = driver.findElement(By.id("closeDialog"));
        closeButton.click();
        Assertions.assertEquals("Login: a", loginElement.getText());
    }

    // 10
    @Test
    public void testDeleteAllResults() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement deleteButton = driver.findElement(By.id("deleteButton"));
        deleteButton.click();

        WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("deleteDialog")));

        WebElement confirmButton = dialog.findElement(By.id("confirmDelete"));
        confirmButton.click();

        wait.until(driver -> {
            List<WebElement> rows = driver.findElements(By.cssSelector("#table tbody tr"));
            return rows.isEmpty();
        });

        List<WebElement> finalRows = driver.findElements(By.cssSelector("#table tbody tr"));
        Assertions.assertEquals(0, finalRows.size());
    }

    // 11
    @Test
    public void testDeleteAllResultsCancel() {
        List<WebElement> initialRows = driver.findElements(By.cssSelector("#table tbody tr"));

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement deleteButton = driver.findElement(By.id("deleteButton"));
        deleteButton.click();

        WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("deleteDialog")));

        WebElement confirmButton = dialog.findElement(By.id("cancelDelete"));
        confirmButton.click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("deleteDialog")));

        List<WebElement> finalRows = driver.findElements(By.cssSelector("#table tbody tr"));
        Assertions.assertEquals(initialRows.size(), finalRows.size());
    }

    // 13
    @Test
    public void testClickOnGraph() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement table = driver.findElement(By.id("table"));
        List<WebElement> rowsBefore = table.findElements(By.cssSelector("tbody tr"));
        int initialRowCount = rowsBefore.size();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("canvas")));

        WebElement canvas = driver.findElement(By.tagName("canvas"));

        Actions actions = new Actions(driver);
        actions.moveToElement(canvas, 10, 20).click().perform();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#table tbody tr")));

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.numberOfElementsToBeMoreThan(
                By.cssSelector("#table tbody tr"), initialRowCount
        ));

        List<WebElement> updatedRows = table.findElements(By.cssSelector("tbody tr"));
        int newRowCount = updatedRows.size();

        assertEquals(newRowCount, initialRowCount + 1);
    }


    @AfterEach
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
