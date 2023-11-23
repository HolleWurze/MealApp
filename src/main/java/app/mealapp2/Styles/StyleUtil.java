package app.mealapp2.Styles;

import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class StyleUtil {

    public static void stylePlaceOrderButton(Button button, String buttonText) {
        button.setStyle(
                "-fx-background-color: #229342; " + // Темно-синий цвет для состояния "по умолчанию"
                        "-fx-border: none; " +
                        "-fx-text-fill: #FFFFFF; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 7px; " +
                        "-fx-min-width: 150px; " +
                        "-fx-border-radius: 2px; " +
                        "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #33df64; " + // Цвет фона при наведении
                        "-fx-border: none; " +
                        "-fx-text-fill: #FFFFFF; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 7px; " +
                        "-fx-min-width: 150px; " +
                        "-fx-border-radius: 2px; " +
                        "-fx-cursor: hand;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: #229342; " + // Темно-синий цвет для состояния "по умолчанию"
                        "-fx-border: none; " +
                        "-fx-text-fill: #FFFFFF; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 7px; " +
                        "-fx-min-width: 150px; " +
                        "-fx-border-radius: 2px; " +
                        "-fx-cursor: hand;"
        ));

        Label textLabel = new Label(buttonText);
        textLabel.setTextFill(Color.WHITE);
        Label arrowLabel = new Label("\u00BB");
        arrowLabel.setTextFill(Color.WHITE);
        arrowLabel.setOpacity(0);
        arrowLabel.setTranslateX(-5);
        arrowLabel.setFont(new Font(20));

        HBox buttonContent = new HBox(textLabel, arrowLabel);

        buttonContent.setSpacing(5);
        buttonContent.setAlignment(Pos.CENTER);

        TranslateTransition tt = new TranslateTransition(Duration.millis(500), arrowLabel);

        button.setOnMouseEntered(e -> {
            arrowLabel.setOpacity(1);
            tt.setToX(12);
            tt.playFromStart();
        });

        button.setOnMouseExited(e -> {
            tt.setToX(1);
            tt.playFromStart();
        });

        button.setGraphic(buttonContent);
    }

    public static void styleAllButton(Button button, String buttonText, String symbolCode, int symbolSize) {
        button.setStyle(
                "-fx-background-color: #0077bb; " + // Темно-синий цвет для состояния "по умолчанию"
                        "-fx-border: none; " +
                        "-fx-text-fill: #FFFFFF; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 7px; " +
                        "-fx-min-width: 150px; " +
                        "-fx-border-radius: 2px; " +
                        "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #b9dff1; " + // Цвет фона при наведении
                        "-fx-border: none; " +
                        "-fx-text-fill: #FFFFFF; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 7px; " +
                        "-fx-min-width: 150px; " +
                        "-fx-border-radius: 2px; " +
                        "-fx-cursor: hand;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: #0077bb; " + // Темно-синий цвет для состояния "по умолчанию"
                        "-fx-border: none; " +
                        "-fx-text-fill: #FFFFFF; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 7px; " +
                        "-fx-min-width: 150px; " +
                        "-fx-border-radius: 2px; " +
                        "-fx-cursor: hand;"
        ));

        Label textLabel = new Label(buttonText);
        textLabel.setTextFill(Color.WHITE);

        Label symbolLabel = new Label(symbolCode);
        symbolLabel.setTextFill(Color.WHITE);
        symbolLabel.setFont(new Font(symbolSize));

        HBox buttonContent = new HBox(textLabel, symbolLabel);
        buttonContent.setSpacing(5);
        buttonContent.setAlignment(Pos.CENTER);

        button.setGraphic(buttonContent);
    }

    public static void styleStartButton(Button button, String buttonText, int textSize, String symbolCode, int symbolSize) {
        button.setStyle(
                "-fx-background-color: #0077bb; " + // Темно-синий цвет для состояния "по умолчанию"
                        "-fx-border: none; " +
                        "-fx-text-fill: #FFFFFF; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 7px; " +
                        "-fx-min-width: 150px; " +
                        "-fx-border-radius: 2px; " +
                        "-fx-cursor: hand;" +
                        "-fx-font-weight: bold;"
        );

        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #b9dff1; " + // Цвет фона при наведении
                        "-fx-border: none; " +
                        "-fx-text-fill: #FFFFFF; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 7px; " +
                        "-fx-min-width: 150px; " +
                        "-fx-border-radius: 2px; " +
                        "-fx-cursor: hand;" +
                        "-fx-font-weight: bold;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: #0077bb; " + // Темно-синий цвет для состояния "по умолчанию"
                        "-fx-border: none; " +
                        "-fx-text-fill: #FFFFFF; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 7px; " +
                        "-fx-min-width: 150px; " +
                        "-fx-border-radius: 2px; " +
                        "-fx-cursor: hand;" +
                        "-fx-font-weight: bold;"
        ));

        Label textLabel = new Label(buttonText);
        textLabel.setTextFill(Color.WHITE);
        textLabel.setFont(new Font(textSize));

        Label symbolLabel = new Label(symbolCode);
        symbolLabel.setTextFill(Color.WHITE);
        symbolLabel.setFont(new Font(symbolSize));

        HBox buttonContent = new HBox(textLabel, symbolLabel);
        buttonContent.setSpacing(5);
        buttonContent.setAlignment(Pos.CENTER);

        button.setGraphic(buttonContent);
    }
}

