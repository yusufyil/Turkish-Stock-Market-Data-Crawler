package edu.marmara.stockmarketdatacollector;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class SeleniumAgent {

    WebDriver driver;

    WebDriverWait wait;

    public SeleniumAgent() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        //WebDriver driver = new ChromeDriver(chromeOptions);
        this.driver = new ChromeDriver();
        WebDriverManager.chromedriver().setup();
        wait = new WebDriverWait(driver, Duration.ofSeconds(3000));
        driver.get("https://www.isyatirim.com.tr/tr-tr/analiz/hisse/Sayfalar/Tarihsel-Fiyat-Bilgileri.aspx");
    }

    public void createCsvFileForGivenStock(String stockCodeName) {
        Actions actions = new Actions(driver);
        By chosenStockBar = By.xpath("//span[@aria-labelledby='select2-ctl00_ctl58_g_0d19e9f2_2afd_4e5a_9a92_57c4ab45c57a_ctl00_ddlHisseSec-container']");
        wait.until(ExpectedConditions.elementToBeClickable(chosenStockBar));
        WebElement searchBoxElement = driver.findElement(chosenStockBar);
        searchBoxElement.click();

        By searchBar = By.xpath("//input[@class='select2-search__field']");
        wait.until(ExpectedConditions.elementToBeClickable(searchBar));
        WebElement searchBarElement = driver.findElement(searchBar);
        actions
                .click(searchBarElement)
                .sendKeys(stockCodeName)
                .pause(Duration.ofSeconds(1))
                .sendKeys(Keys.ENTER)
                .perform();

        By loadingBar = By.xpath("//span[@class='loader']");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(loadingBar));


        By priceType = By.xpath("//span[@aria-labelledby='select2-ddlFiyatSec-container']");
        wait.until(ExpectedConditions.elementToBeClickable(priceType));
        WebElement priceTypeElement = driver.findElement(priceType);
        priceTypeElement.click();

        By unchangedPriceType = By.xpath("(//li[@role='treeitem'])[2]");
        wait.until(ExpectedConditions.elementToBeClickable(unchangedPriceType));
        WebElement uncachedPriceTypeElement = driver.findElement(unchangedPriceType);
        uncachedPriceTypeElement.click();

        By startDate = By.xpath("//input[@id='dtStartDateHisse']");
        wait.until(ExpectedConditions.elementToBeClickable(startDate));
        WebElement startDateElement = driver.findElement(startDate);
        startDateElement.click();

        By yearButton = By.xpath("(//select[@title='Select a year'])[1]");//
        wait.until(ExpectedConditions.elementToBeClickable(yearButton));
        WebElement yearButtonElement = driver.findElement(yearButton);


        for (int index = 0; index < 20 * 12; index++) {
            By previousMonthButton = By.xpath("//div[@title='Previous month']");
            wait.until(ExpectedConditions.elementToBeClickable(previousMonthButton));
            WebElement previousMonthButtonElement = driver.findElement(previousMonthButton);
            previousMonthButtonElement.click();
        }

        By dayButton = By.xpath("(//td[@role='presentation'])[15]");
        wait.until(ExpectedConditions.elementToBeClickable(dayButton));
        WebElement dayButtonElement = driver.findElement(dayButton);
        dayButtonElement.click();


        By button = By.xpath("//a[@id='btnGetHisseTekil']");
        wait.until(ExpectedConditions.elementToBeClickable(button));
        WebElement buttonElement = driver.findElement(button);
        buttonElement.click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(loadingBar));

        this.getStockNamesAndHeader();
    }

    public String[][] getStockNamesAndHeader() {
        String[][] stockNamesAndHeader;
        By stockRecordDate = By.xpath("//td[@class='sorting_1']");
        wait.until(ExpectedConditions.elementToBeClickable(stockRecordDate));
        List<WebElement> stockRecordDateElements = driver.findElements(stockRecordDate);
        int numberOfResults = stockRecordDateElements.size();
        stockNamesAndHeader = new String[numberOfResults + 1][13];
        By dataField = By.xpath("//td[@class='text-right']");
        wait.until(ExpectedConditions.elementToBeClickable(dataField));
        List<WebElement> dataFieldElements = driver.findElements(dataField);
        for (int row = 0; row < numberOfResults; row++) {
            stockNamesAndHeader[row][0] = stockRecordDateElements.get(row).getText();
            for (int column = 1; column < 13; column++) {
                stockNamesAndHeader[row][column] = dataFieldElements.get(row * 12 + column - 1).getText();
            }
        }

        //temp
        for (int row = 0; row < numberOfResults; row++) {
            for (int column = 0; column < 13; column++) {
                System.out.print(stockNamesAndHeader[row][column] + " ");
            }
            System.out.println();
        }
        return stockNamesAndHeader;
    }

    public void writeCsvFile(String[][] stockNamesAndHeader) {

    }
}
