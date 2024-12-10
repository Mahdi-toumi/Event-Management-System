package sgbd;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

public class App extends Application {

    private VBox sidebar;
    private BorderPane rootLayout;
    private GestionLieux gestionLieux;
    private GestionSpectacles gestionSpectacles;
    private GestionRubriques gestionRubriques;

    @Override
    public void start(Stage primaryStage) {
        // Initialisation des composants principaux
        rootLayout = new BorderPane();
        sidebar = createSidebar();

        gestionLieux = new GestionLieux();
        gestionSpectacles = new GestionSpectacles();
        gestionRubriques = new GestionRubriques();

        // Afficher la première interface (Gestion des lieux par défaut)
        rootLayout.setLeft(sidebar);
        rootLayout.setCenter(gestionLieux.getUI());

        // Configuration de la fenêtre
        Scene scene = new Scene(rootLayout, 1275, 600);
        scene.setFill(Color.WHITE); // Pour une couleur de fond générale plus claire

        primaryStage.setTitle("Gestion des Spectacles");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(25);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-image: url('file:src/images/background.jpg');" +
                "-fx-background-size: cover; -fx-background-position: center; -fx-border-radius: 10; -fx-background-radius: 10;");

        // Boutons avec style
        Button lieuxButton = createSidebarButton("Gestion des Lieux");
        Button spectaclesButton = createSidebarButton("Gestion des Spectacles");
        Button rubriquesButton = createSidebarButton("Gestion des Rubriques");

        // Actions des boutons
        lieuxButton.setOnAction(e -> rootLayout.setCenter(gestionLieux.getUI()));
        spectaclesButton.setOnAction(e -> rootLayout.setCenter(gestionSpectacles.getUI()));
        rubriquesButton.setOnAction(e -> rootLayout.setCenter(gestionRubriques.getUI()));

        sidebar.getChildren().addAll(lieuxButton, spectaclesButton, rubriquesButton);
        return sidebar;
    }

    private Button createSidebarButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #0094d2; -fx-text-fill: white; -fx-font-size: 14px; "
                + "-fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-font-weight: bold;");
        button.setMinWidth(200);

        // Effet au survol (hover)
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #0094d2; -fx-text-fill: white; "
                + "-fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-font-weight: bold;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #0094d2; -fx-text-fill: white; "
                + "-fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-font-weight: bold;"));
        return button;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
