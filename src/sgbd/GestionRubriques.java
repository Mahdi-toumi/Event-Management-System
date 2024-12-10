package sgbd;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.Optional;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class GestionRubriques {

    private VBox root;
    private TableView<Rubrique> table;
    private ObservableList<Rubrique> rubriquesData;

    public GestionRubriques() {
        root = new VBox(10);
        root.setPadding(new Insets(10));

        HBox filterBox = new HBox(10);
        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher une rubrique...");
        Button searchButton = new Button("Chercher");
        Button addButton = new Button("Ajouter une rubrique");
        
        
        
        filterBox.getChildren().addAll(searchField, searchButton, addButton);

        table = new TableView<>();
        setupTable();

        // Actions des boutons
        addButton.setOnAction(e -> afficherFormulaireAjout());
        searchButton.setOnAction(e -> chercherRubriques(searchField.getText()));

        // Ajouter les composants à la racine
        root.getChildren().addAll(filterBox, table);

        // Charger les données
        loadData();
    }

    private void setupTable() {
    // Colonne pour l'ID de la Rubrique
    TableColumn<Rubrique, Integer> idCol = new TableColumn<>("ID");
    idCol.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getId()).asObject());

    // Colonne pour l'ID du Spectacle
    TableColumn<Rubrique, Integer> idSpecCol = new TableColumn<>("ID Spectacle");
    idSpecCol.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getIdSpectacle()).asObject());

    // Colonne pour l'ID de l'Artiste
    TableColumn<Rubrique, Integer> idArtCol = new TableColumn<>("ID Artiste");
    idArtCol.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getIdArtiste()).asObject());

    // Colonne pour l'Heure de Début
    TableColumn<Rubrique, String> heureDebutCol = new TableColumn<>("Heure Début");
    heureDebutCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getHeureDebut()));

    // Colonne pour le Type
    TableColumn<Rubrique, String> typeCol = new TableColumn<>("Type");
    typeCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getType()));

    // Colonne pour les Actions (Modifier et Supprimer)
    TableColumn<Rubrique, Void> actionsCol = new TableColumn<>("Actions");
    actionsCol.setCellFactory(col -> new TableCell<>() {
        private final Button editButton = new Button("Modifier");
        private final Button deleteButton = new Button("Supprimer");

        {
               editButton.setStyle("-fx-background-color: #1abc9c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-border-radius: 5px;");
               deleteButton.setStyle("-fx-background-color: #d11a2a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-border-radius: 5px;");

            
            // Actions des boutons
            editButton.setOnAction(e -> afficherFormulaireModification(getTableRow().getItem()));
            deleteButton.setOnAction(e -> supprimerRubrique(getTableRow().getItem()));
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                HBox buttons = new HBox(5, editButton, deleteButton);
                setGraphic(buttons);
            }
        }
    });

    // Ajouter toutes les colonnes à la TableView
    table.getColumns().addAll(idCol, idSpecCol, idArtCol, heureDebutCol, typeCol, actionsCol);
}

       

    private void loadData() {
        rubriquesData = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT r.idRub, r.idSpec, r.idArt, r.h_debutr, r.type " +
                             "FROM Rubrique r")) {

            while (rs.next()) {
                rubriquesData.add(new Rubrique(
                        rs.getInt("idRub"),
                        rs.getInt("idSpec"),
                        rs.getInt("idArt"),
                        rs.getString("h_debutr"),
                        rs.getString("type")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        table.setItems(rubriquesData);
    }

    private void ajouterRubrique(int idSpec, int idArt, String heureDebut, String type) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "{CALL Gestion_Spectacles.AjouterRubrique(?, ?, ?, ?)}";
            CallableStatement stmt = conn.prepareCall(sql);
            stmt.setInt(1, idSpec);
            stmt.setInt(2, idArt);
            stmt.setTime(3, java.sql.Time.valueOf(heureDebut));
            stmt.setString(4, type);
            stmt.execute();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Rubrique ajoutée avec succès !");
            alert.show();
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ajout de la rubrique.");
            alert.show();
        }
    }

    private void supprimerRubrique(Rubrique rubrique) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "{CALL Gestion_Spectacles.SupprimerRubrique(?)}";
            CallableStatement stmt = conn.prepareCall(sql);
            stmt.setInt(1, rubrique.getId());
            stmt.execute();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Rubrique supprimée avec succès !");
            alert.show();
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la suppression de la rubrique.");
            alert.show();
        }
    }

    private void chercherRubriques(String nomArtiste) {
        rubriquesData = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "{CALL Gestion_Spectacles.ChercherRubrique(?, NULL)}";
            CallableStatement stmt = conn.prepareCall(sql);
            stmt.setString(1, "%" + nomArtiste + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                rubriquesData.add(new Rubrique(
                        rs.getInt("idRub"),
                        rs.getInt("idSpec"),
                        rs.getInt("idArt"),
                        rs.getString("h_debutr"),
                        rs.getString("type")
                ));
            }
            table.setItems(rubriquesData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void afficherFormulaireAjout() {
        // Création du formulaire d'ajout
        Dialog<Rubrique> dialog = new Dialog<>();
        dialog.setTitle("Ajouter une Rubrique");
        dialog.setHeaderText("Entrez les informations de la rubrique");

        // Création des champs de texte pour le formulaire
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField idSpecField = new TextField();
        idSpecField.setPromptText("ID Spectacle");

        TextField idArtField = new TextField();
        idArtField.setPromptText("ID Artiste");

        TextField heureDebutField = new TextField();
        heureDebutField.setPromptText("Heure de début (HH:MM)");

        TextField typeField = new TextField();
        typeField.setPromptText("Type");

        grid.add(new Label("ID Spectacle:"), 0, 0);
        grid.add(idSpecField, 1, 0);
        grid.add(new Label("ID Artiste:"), 0, 1);
        grid.add(idArtField, 1, 1);
        grid.add(new Label("Heure Début:"), 0, 2);
        grid.add(heureDebutField, 1, 2);
        grid.add(new Label("Type:"), 0, 3);
        grid.add(typeField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Ajouter des boutons OK et Annuler
        ButtonType okButton = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, cancelButton);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                return new Rubrique(0, Integer.parseInt(idSpecField.getText()), Integer.parseInt(idArtField.getText()),
                        heureDebutField.getText(), typeField.getText());
            }
            return null;
        });

        Optional<Rubrique> result = dialog.showAndWait();
        result.ifPresent(rubrique -> ajouterRubrique(rubrique.getIdSpectacle(), rubrique.getIdArtiste(),
                rubrique.getHeureDebut(), rubrique.getType()));
    }

    private void afficherFormulaireModification(Rubrique rubrique) {
        // Création du formulaire de modification
        Dialog<Rubrique> dialog = new Dialog<>();
        dialog.setTitle("Modifier une Rubrique");
        dialog.setHeaderText("Modifiez les informations de la rubrique");

        // Création des champs de texte pour le formulaire
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField idSpecField = new TextField(String.valueOf(rubrique.getIdSpectacle()));
        TextField idArtField = new TextField(String.valueOf(rubrique.getIdArtiste()));
        TextField heureDebutField = new TextField(rubrique.getHeureDebut());
        TextField typeField = new TextField(rubrique.getType());

        grid.add(new Label("ID Spectacle:"), 0, 0);
        grid.add(idSpecField, 1, 0);
        grid.add(new Label("ID Artiste:"), 0, 1);
        grid.add(idArtField, 1, 1);
        grid.add(new Label("Heure Début:"), 0, 2);
        grid.add(heureDebutField, 1, 2);
        grid.add(new Label("Type:"), 0, 3);
        grid.add(typeField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        ButtonType okButton = new ButtonType("Modifier", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, cancelButton);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                return new Rubrique(rubrique.getId(), Integer.parseInt(idSpecField.getText()), Integer.parseInt(idArtField.getText()),
                        heureDebutField.getText(), typeField.getText());
            }
            return null;
        });

        Optional<Rubrique> result = dialog.showAndWait();
        result.ifPresent(rubriqueModifie -> modifierRubrique(rubriqueModifie));
    }

    public VBox getUI() {
        return root;
    }

    private void modifierRubrique(Rubrique rubrique) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "{CALL Gestion_Spectacles.ModifierRubrique(?, ?, ?, ?, ?)}";
            CallableStatement stmt = conn.prepareCall(sql);
            stmt.setInt(1, rubrique.getId());
            stmt.setInt(2, rubrique.getIdSpectacle());
            stmt.setInt(3, rubrique.getIdArtiste());
            stmt.setString(4, rubrique.getHeureDebut());
            stmt.setString(5, rubrique.getType());
            stmt.execute();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Rubrique modifiée avec succès !");
            alert.show();
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la modification de la rubrique.");
            alert.show();
        }
    }
}
class Rubrique {
    private int id;
    private int idSpectacle;
    private int idArtiste;
    private String heureDebut;
    private String type;

    // Constructeur
    public Rubrique(int id, int idSpectacle, int idArtiste, String heureDebut, String type) {
        this.id = id;
        this.idSpectacle = idSpectacle;
        this.idArtiste = idArtiste;
        this.heureDebut = heureDebut;
        this.type = type;
    }

    // Getters
    public int getId() { return id; }
    public int getIdSpectacle() { return idSpectacle; }
    public int getIdArtiste() { return idArtiste; }
    public String getHeureDebut() { return heureDebut; }
    public String getType() { return type; }

    


}
