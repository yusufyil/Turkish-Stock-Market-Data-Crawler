package edu.marmara.stockmarketdatacollector;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        SeleniumAgent seleniumAgent = new SeleniumAgent();

        AnchorPane anchorPane = new AnchorPane();
        Scene scene = new Scene(anchorPane);
        anchorPane.setStyle("-fx-background-color: #2ada72");

        Label label = new Label();
        label.setText("Istanbul Stock Exchange Market");
        Font headerFont = Font.font("Courier New", FontWeight.BOLD, 36);
        label.setFont(headerFont);
        label.setLayoutX(30);
        label.setLayoutY(60);
        anchorPane.getChildren().add(label);

        TextField stockNameTextField = new TextField();
        stockNameTextField.setPrefWidth(200);
        stockNameTextField.setPrefHeight(40);
        stockNameTextField.setLayoutX(260);
        stockNameTextField.setLayoutY(120);
        stockNameTextField.setPromptText("Stock Code");
        anchorPane.getChildren().add(stockNameTextField);

        Button button = new Button();
        button.setPrefWidth(200);
        button.setPrefHeight(40);
        button.setLayoutX(260);
        button.setLayoutY(180);
        button.setText("Get Prices");
        button.setOnAction(actionEvent -> {
            System.out.println(stockNameTextField.getText());
            //seleniumAgent.createCsvFileForGivenStock(stockNameTextField.getText());
            seleniumAgent.stockCode = stockNameTextField.getText();
            Thread thread = new Thread(seleniumAgent);
            thread.start();
        });
        anchorPane.getChildren().add(button);

        stage.setScene(scene);
        stage.setHeight(480);
        stage.setWidth(720);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}