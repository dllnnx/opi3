import org.junit.jupiter.api.AfterEach;
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
import static org.junit.jupiter.api.Assertions.assertNull;

public class CoordFormTest {
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

    // 7
    @Test
    public void coordinatesFormHit() {
        setSliderValue(By.id("x-slider"), 1.0, -3.0, 5.0);
        setSliderValue(By.id("radius-slider"), 2.0, 0.1, 5.0);

        WebElement yInput = driver.findElement(By.id("y-input"));
        yInput.clear();
        yInput.sendKeys("1");

        WebElement table = driver.findElement(By.id("table"));
        List<WebElement> rowsBefore = table.findElements(By.cssSelector("tbody tr"));
        int initialRowCount = rowsBefore.size();

        WebElement checkButton = driver.findElement(By.id("check"));
        checkButton.click();

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.numberOfElementsToBeMoreThan(
                        By.cssSelector("#table tbody tr"), initialRowCount
                ));

        List<WebElement> updatedRows = table.findElements(By.cssSelector("tbody tr"));
        WebElement newRow = updatedRows.get(0);

        List<WebElement> cells = newRow.findElements(By.tagName("td"));

        assertEquals("1", cells.get(0).getText());
        assertEquals("1", cells.get(1).getText());
        assertEquals("2", cells.get(2).getText());
        assertEquals("true", cells.get(3).getText());
    }

    // 8
    @Test
    public void coordinatesFormNotHit() {
        setSliderValue(By.id("x-slider"), 2.0, -3.0, 5.0);
        setSliderValue(By.id("radius-slider"), 1.5, 0.1, 5.0);

        WebElement yInput = driver.findElement(By.id("y-input"));
        yInput.clear();
        yInput.sendKeys("0.5");

        WebElement table = driver.findElement(By.id("table"));
        List<WebElement> rowsBefore = table.findElements(By.cssSelector("tbody tr"));
        int initialRowCount = rowsBefore.size();

        WebElement checkButton = driver.findElement(By.id("check"));
        checkButton.click();

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.numberOfElementsToBeMoreThan(
                        By.cssSelector("#table tbody tr"), initialRowCount
                ));

        List<WebElement> updatedRows = table.findElements(By.cssSelector("tbody tr"));
        WebElement newRow = updatedRows.get(0);

        List<WebElement> cells = newRow.findElements(By.tagName("td"));

        assertEquals("2", cells.get(0).getText());
        assertEquals("0.5", cells.get(1).getText());
        assertEquals("1.5", cells.get(2).getText());
        assertEquals("false", cells.get(3).getText());
    }

    // 12
    @Test
    public void clearFieldsTest() {
        WebElement yInput = driver.findElement(By.id("y-input"));
        yInput.sendKeys("2");

        setSliderValue(By.id("x-slider"), 2.0, -3.0, 5.0);
        setSliderValue(By.id("radius-slider"), 1.5, 0.1, 5.0);

        WebElement xSlider = driver.findElement(By.id("x-slider"));
        WebElement rSlider = driver.findElement(By.id("radius-slider"));

        WebElement clearButton = driver.findElement(By.id("clean"));
        clearButton.click();

        String yValue = yInput.getDomAttribute("value");
        String xValue = xSlider.getDomAttribute("value");
        String rValue = rSlider.getDomAttribute("value");

        assertEquals("", yValue);
        assertNull(xValue);
        assertNull(rValue);
    }

    // 14
    @Test
    public void testDecimalInput() {
        String expectedYValue = "1.00000000000001";
        WebElement yInput = driver.findElement(By.id("y-input"));
        yInput.sendKeys(expectedYValue);

        WebElement table = driver.findElement(By.id("table"));
        List<WebElement> rowsBefore = table.findElements(By.cssSelector("tbody tr"));
        int initialRowCount = rowsBefore.size();

        WebElement checkButton = driver.findElement(By.id("check"));
        checkButton.click();

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.numberOfElementsToBeMoreThan(
                        By.cssSelector("#table tbody tr"), initialRowCount
                ));

        List<WebElement> updatedRows = table.findElements(By.cssSelector("tbody tr"));
        WebElement newRow = updatedRows.get(0);

        List<WebElement> cells = newRow.findElements(By.tagName("td"));

        assertEquals(expectedYValue, cells.get(1).getText());
    }

    // 15
    @Test
    public void testOutOfBoundariesY () {
        WebElement yInput = driver.findElement(By.id("y-input"));
        yInput.sendKeys("5");

        WebElement submitBtn = driver.findElement(By.id("check"));

        assertFalse(submitBtn.isEnabled());
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
