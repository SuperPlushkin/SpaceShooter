package edu.subclasses.classes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

public class StylesHelper {
    private static final String baseButtonStyle =
        "-fx-background-color: #FFFFFF;" +  // Белый фон
        "-fx-text-fill: #333333;" +
        "-fx-font-family: 'Arial';" +
        "-fx-font-size: 14px;" +
        "-fx-padding: 10 25 10 25;" +
        "-fx-border-color: #AAAAAA;" +
        "-fx-border-width: 1px;" +
        "-fx-border-radius: 5;" +
        "-fx-background-radius: 5;";
    private static final String hoverButtonStyle =
        "-fx-background-color: #EEEEEE;" +  // Светло-серый фон
        "-fx-text-fill: #111111;" +
        "-fx-font-family: 'Arial';" +
        "-fx-font-size: 14px;" +
        "-fx-padding: 10 25 10 25;" +
        "-fx-border-color: #777777;" +
        "-fx-border-width: 1px;" +
        "-fx-border-radius: 5;" +
        "-fx-background-radius: 5;";
    private static final String pressedButtonStyle =
        "-fx-background-color: #777777;" +  // Средне-серый фон
        "-fx-text-fill: white;" +
        "-fx-font-family: 'Arial';" +
        "-fx-font-size: 14px;" +
        "-fx-padding: 11 25 9 25;" +
        "-fx-border-color: #FFFFFF;" +
        "-fx-border-width: 1px;" +
        "-fx-border-radius: 5;" +
        "-fx-background-radius: 5;";
    private static final String menuStyle =
        "-fx-background-color: rgba(30, 30, 30, 0.7);" +
        "-fx-border-color: #AAAAAA;" +
        "-fx-border-width: 2;" +
        "-fx-border-radius: 10;" +
        "-fx-background-radius: 10;" +
        "-fx-padding: 30;";

    private static final String labelStyle =
        "-fx-font-weight: bold;" +
        "-fx-font-size: 28px;" +
        "-fx-background-color: transparent;" +
        "-fx-padding: 0 0 30 0;" +
        "-fx-text-alignment: center;" +
        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.7), 5, 0.0, 1, 1);";

    private static final String subLabelStyle =
        "-fx-font-size: 16px;" +
        "-fx-font-weight: bold;" +
        "-fx-padding: 0 0 10 0;" +
        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.7), 5, 0.0, 1, 1);" +
        "-fx-text-alignment: center;" +
        "-fx-text-fill: #FFFFFF;";

    public static void addBaseHoverPressEffects(Button button) {
        button.setStyle(baseButtonStyle);
        button.setOnMouseEntered(e -> button.setStyle(hoverButtonStyle));
        button.setOnMouseExited(e -> button.setStyle(baseButtonStyle));
        button.setOnMousePressed(e -> button.setStyle(pressedButtonStyle));
        button.setOnMouseReleased(e -> {
            if (button.isHover())
            {
                button.setStyle(hoverButtonStyle);
            }
            else button.setStyle(baseButtonStyle);
        });
    }
    public static void setMenuStyle(VBox menu) {
        menu.setStyle(menuStyle);
        menu.setAlignment(Pos.CENTER);
        menu.setPadding(new Insets(10));
    }
    public static void setLabelStyle(Label label, String colorProperty) {
        label.setStyle(labelStyle + colorProperty);
    }
    public static void setSubLabelStyle(Label label) {
        label.setStyle(subLabelStyle);
    }
}
