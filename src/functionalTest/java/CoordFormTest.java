import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    public void logoutSuccess() {
        WebElement logoutButton = driver.findElement(By.id("logout"));
        logoutButton.click();

        WebElement confirmLogoutButton = driver.findElement(By.id("confirmLogout"));
        confirmLogoutButton.click();

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(webDriver -> webDriver.getCurrentUrl().equals("http://localhost:3000/"));

        String currentUrl = driver.getCurrentUrl();
        assertEquals("http://localhost:3000/", currentUrl);
    }

    // 7
    @Test
    public void coordinatesFormHit() {
        setSliderValue(By.id("x-slider"), 1.0, -3.0, 5.0);
        setSliderValue(By.id("radius-slider"), 2.0, 0.1, 5.0);

        WebElement yInput = driver.findElement(By.id("y-input"));
        yInput.clear();
        yInput.sendKeys("1");

        WebElement checkButton = driver.findElement(By.id("check"));
        checkButton.click();

        WebElement table = driver.findElement(By.id("table"));
        WebElement firstRow = table.findElements(By.cssSelector("tbody tr")).get(0);

        WebElement xCell = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> {
                    WebElement cell = firstRow.findElements(By.tagName("td")).get(0);
                    return cell.getText().equals("1") ? cell : null;
                });

        WebElement yCell = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> {
                    WebElement cell = firstRow.findElements(By.tagName("td")).get(1);
                    return cell.getText().equals("1") ? cell : null;
                });

        WebElement resultCell = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> {
                    WebElement cell = firstRow.findElements(By.tagName("td")).get(3);
                    return cell.getText().equals("попадание") ? cell : null;
                });

        assertEquals("1", xCell.getText());
        assertEquals("1", yCell.getText());
        assertEquals("попадание", resultCell.getText());
    }

    // 8
    @Test
    public void coordinatesFormNotHit() {
        setSliderValue(By.id("x-slider"), 2.0, -3.0, 5.0);
        setSliderValue(By.id("radius-slider"), 1.5, 0.1, 5.0);

        WebElement yInput = driver.findElement(By.id("y-input"));
        yInput.clear();
        yInput.sendKeys("0.5");

        WebElement checkButton = driver.findElement(By.id("check"));
        checkButton.click();

        WebElement table = driver.findElement(By.id("table"));
        WebElement firstRow = table.findElements(By.cssSelector("tbody tr")).get(0);
        WebElement xCell = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> {
                    WebElement cell = firstRow.findElements(By.tagName("td")).get(0);
                    return cell.getText().equals("2") ? cell : null;
                });

        WebElement yCell = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> {
                    WebElement cell = firstRow.findElements(By.tagName("td")).get(1);
                    return cell.getText().equals("0.5") ? cell : null;
                });

        WebElement resultCell = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> {
                    WebElement cell = firstRow.findElements(By.tagName("td")).get(3);
                    return cell.getText().equals("мимо") ? cell : null;
                });

        assertEquals("2", xCell.getText());
        assertEquals("0.5", yCell.getText());
        assertEquals("мимо", resultCell.getText());
    }

    @AfterEach
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void setSliderValue(By sliderLocator, double targetValue, double min, double max) {
        WebElement slider = driver.findElement(sliderLocator);
        WebElement handle = slider.findElement(By.className("p-slider-handle"));

        int sliderWidth = slider.getSize().getWidth();

        double proportion = (targetValue - min) / (max - min);
        int xOffset = (int) (sliderWidth * (proportion + 0.01));

        String leftStyle = handle.getDomAttribute("style");
        int currentOffset = extractPercentageLeft(leftStyle, sliderWidth);

        int moveBy = xOffset - currentOffset;

        Actions actions = new Actions(driver);
        actions.clickAndHold(handle)
                .moveByOffset(moveBy, 0)
                .release()
                .perform();
    }

    private int extractPercentageLeft(String style, int sliderWidth) {
        try {
            int start = style.indexOf("left:") + 5;
            int end = style.indexOf("%", start);
            String percentStr = style.substring(start, end).trim();
            double percent = Double.parseDouble(percentStr);
            return (int) ((percent / 100.0) * sliderWidth);
        } catch (Exception e) {
            return 0;
        }
    }

}
