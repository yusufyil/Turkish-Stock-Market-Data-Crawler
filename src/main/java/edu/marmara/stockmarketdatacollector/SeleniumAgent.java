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

import java.io.FileWriter;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

public class SeleniumAgent {

    WebDriver driver;

    WebDriverWait wait;

    public SeleniumAgent() {
        ChromeOptions chromeOptions = new ChromeOptions();
        //chromeOptions.addArguments("--no-sandbox");
        //chromeOptions.addArguments("--headless");
        //chromeOptions.addArguments("disable-gpu");
        //chromeOptions.addArguments("window-size=1400,2100");
        //WebDriver driver = new ChromeDriver(chromeOptions);
        //chromeOptions.addArguments("USER AGENT");
        this.driver = new ChromeDriver(chromeOptions);
        WebDriverManager.chromedriver().setup();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
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


        int numberOfYears = 20;
        for (int index = 0; index < numberOfYears * 12; index++) {
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

        String[][] stockNamesAndHeader = this.getStockNamesAndHeader();
        this.writeCsvFile(stockNamesAndHeader, stockCodeName);
    }

    public String[][] getStockNamesAndHeader() {
        String[][] stockNamesAndHeader;
        By stockRecordDate = By.xpath("//td[@class='sorting_1']");
        wait.until(ExpectedConditions.elementToBeClickable(stockRecordDate));
        List<WebElement> stockRecordDateElements = driver.findElements(stockRecordDate);
        int numberOfResults = stockRecordDateElements.size();
        int numberOfColumns = 13;
        stockNamesAndHeader = new String[numberOfResults][numberOfColumns];
        for (int row = 0; row < numberOfResults; row++) {
            stockNamesAndHeader[row][0] = stockRecordDateElements.get(row).getText();
            for (int column = 1; column < 13; column++) {
                By dataField = By.xpath("(//td[@class='text-right'])[" + (row * 12 + column) + "]");
                wait.until(ExpectedConditions.elementToBeClickable(dataField));
                WebElement dataFieldElement = driver.findElement(dataField);
                stockNamesAndHeader[row][column] = dataFieldElement.getText().replace(",", ".");
                if (stockNamesAndHeader[row][column].equals("")) {
                    stockNamesAndHeader[row][column] = "0";
                }
            }
            System.out.println("row: " + row + "total row: " + numberOfResults + " %: " + (row * 100 / numberOfResults));
        }
        return stockNamesAndHeader;
    }

    public void writeCsvFile(String[][] stockNamesAndHeader, String stockName) {
        System.out.println("number of rows: " + stockNamesAndHeader.length + " number of columns: " + stockNamesAndHeader[0].length);
        try {
            FileWriter csvWriter = new FileWriter(stockName + LocalDate.now() + "stockNamesAndHeader.csv");
            //adding columns
            String[] headers = new String[]{"Tarih", "Kapanis", "Min", "Max", "Aof", "Hacim",
                    "Sermaye", "USDTRY", "Bist100", "Piyasa degeri (mn Tl)", "Piyasa degeri (mn USD)",
                    "Halka acik PD (mn Tl)", "Halka acik PD (mn USD)", "sonuc"};
            for (String column : headers) {
                csvWriter.append(column);
                csvWriter.append(",");
            }
            csvWriter.append("\n");

            for (int row = 0; row < stockNamesAndHeader.length; row++) {
                for (int column = 0; column < stockNamesAndHeader[0].length; column++) {
                    csvWriter.append(stockNamesAndHeader[row][column]);
                    csvWriter.append(",");
                }
                //adding result
                if (row < 5) {
                    csvWriter.append("0");
                } else {
                    double lastPrice = Double.parseDouble(stockNamesAndHeader[row][1].replace(",", "."));
                    double firstPrice = Double.parseDouble(stockNamesAndHeader[row - 5][1].replace(",", "."));
                    double result = (lastPrice - firstPrice) / firstPrice * 100;
                    if (result > 5) {
                        csvWriter.append("1");
                    } else {
                        csvWriter.append("0");
                    }
                }
                csvWriter.append("\n");
            }
            csvWriter.flush();
            csvWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
