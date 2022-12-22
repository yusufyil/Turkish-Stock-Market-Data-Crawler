module edu.marmara.stockmarketdatacollector {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.seleniumhq.selenium.api;
    requires org.seleniumhq.selenium.java;
    requires org.seleniumhq.selenium.chrome_driver;
    requires io.github.bonigarcia.webdrivermanager;
    requires org.seleniumhq.selenium.support;


    opens edu.marmara.stockmarketdatacollector to javafx.fxml;
    exports edu.marmara.stockmarketdatacollector;
}