module edu.marmara.stockmarketdatacollector {
    requires javafx.controls;
    requires javafx.fxml;


    opens edu.marmara.stockmarketdatacollector to javafx.fxml;
    exports edu.marmara.stockmarketdatacollector;
}