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
import sgbd.DBConnection;

public class GestionLieux {

    private VBox root;
    private TableView<Lieu> table;
    private ObservableList<Lieu> lieuxData;

    public GestionLieux() {
        root = new VBox(10);
        root.setPadding(new Insets(10));

        HBox filterBox = new HBox(10);
        TextField nomField = new TextField();
        nomField.setPromptText("Nom du lieu");
        nomField.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #ccc; -fx-border-radius: 5px; -fx-padding: 5px;");


        TextField villeField = new TextField();
        villeField.setPromptText("Ville");
        villeField.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #ccc; -fx-border-radius: 5px; -fx-padding: 5px;");


        TextField capaciteField = new TextField();
        capaciteField.setPromptText("Capacité");
        capaciteField.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #ccc; -fx-border-radius: 5px; -fx-padding: 5px;");


        Button searchButton = new Button("Chercher");
        searchButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-border-radius: 5px;");

        Button addButton = new Button("Ajouter un lieu");
        addButton.setStyle("-fx-background-color: #1abc9c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-border-radius: 5px;");


        filterBox.getChildren().addAll(nomField, villeField, capaciteField, searchButton, addButton);


        table = new TableView<>();
        setupTable();

        // Action des boutons
        addButton.setOnAction(e -> afficherFormulaireAjout());
        searchButton.setOnAction(e -> chercherLieux(nomField.getText(), villeField.getText(), capaciteField.getText()));

        // Ajouter les composants à la racine
        root.getChildren().addAll(filterBox, table);

        // Charger les données
        loadData();
    }

    private void setupTable() {
        // ID column: explicit setup with cell value factory
        TableColumn<Lieu, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getId()).asObject());

        // Nom column: Binding with getter method for the 'nom' field
        TableColumn<Lieu, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getNom()));

        // Adresse column: Binding with getter method for 'adresse'
        TableColumn<Lieu, String> adresseCol = new TableColumn<>("Adresse");
        adresseCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getAdresse()));

        // Ville column: Binding with getter method for 'ville'
        TableColumn<Lieu, String> villeCol = new TableColumn<>("Ville");
        villeCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getVille()));

        // Capacité column: Binding with getter method for 'capacite'
        TableColumn<Lieu, Integer> capaciteCol = new TableColumn<>("Capacité");
        capaciteCol.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getCapacite()).asObject());

       
        // Actions column: Custom actions (buttons) for each row
        TableColumn<Lieu, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(col -> new TableCell<Lieu, Void>() {
            private  Button editButton = new Button("Modifier");

            private final Button deleteButton = new Button("Supprimer");


            {
                editButton.setStyle("-fx-background-color: #1abc9c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-border-radius: 5px;");
                deleteButton.setStyle("-fx-background-color: #d11a2a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-border-radius: 5px;");

                // Edit button action
                editButton.setOnAction(e -> afficherFormulaireModification(getTableRow().getItem()));
                // Delete button action
                deleteButton.setOnAction(e -> supprimerLieu(getTableRow().getItem()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null); // No buttons for empty rows
                } else {
                    HBox buttons = new HBox(10, editButton, deleteButton);
                    setGraphic(buttons); // Add buttons to row
                }
            }
        });

        // Add all columns to the table
        table.getColumns().addAll(idCol, nomCol, adresseCol, villeCol, capaciteCol,  actionsCol);
    }

    private void loadData() {
        lieuxData = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Lieu WHERE Active = 1")) {

            while (rs.next()) {
                lieuxData.add(new Lieu(
                        rs.getInt("idLieu"),
                        rs.getString("nomLieu"),
                        rs.getString("adresse"),
                        rs.getInt("capacite"),
                        rs.getString("ville"),
                        rs.getInt("active")
                ));
                System.out.println("Lieu récupéré: " + rs.getString("nomLieu"));  // Vérification
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        table.setItems(lieuxData); // Mise à jour de la table avec les nouvelles données
    }


    private void ajouterLieu(String nom, String adresse, String ville, int capacite) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "{CALL Gestion_lieux.AjouterLieu(?, ?, ?, ?)}";
            CallableStatement stmt = conn.prepareCall(sql);
            stmt.setString(1, nom);
            stmt.setString(2, adresse);
            stmt.setString(3, ville);
            stmt.setInt(4, capacite);
            stmt.execute();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Lieu ajouté avec succès !");
            alert.show();
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ajout du lieu.");
            alert.show();
        }
    }

    private void supprimerLieu(Lieu lieu) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "{CALL Gestion_lieux.SupprimerLieu(?)}";
            CallableStatement stmt = conn.prepareCall(sql);
            stmt.setInt(1, lieu.getId());
            stmt.execute();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Lieu supprimé avec succès !");
            alert.show();
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la suppression du lieu.");
            alert.show();
        }
    }
        
    
    private void chercherLieux(String nom, String ville, String capacite) {
        lieuxData = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection()) {
            // Appel de la fonction ChercherLieu
            String sql = "{ ? = CALL Gestion_lieux.ChercherLieu(?, ?, ?) }";
            CallableStatement stmt = conn.prepareCall(sql);

            // Définir le paramètre de retour (le curseur)
            stmt.registerOutParameter(1, java.sql.Types.REF_CURSOR);

            // Définir les paramètres IN
            stmt.setString(2, nom.isEmpty() ? null : "%" + nom + "%");
            stmt.setString(3, ville.isEmpty() ? null : "%" + ville + "%");
            stmt.setString(4, capacite.isEmpty() ? null : capacite);

            // Exécuter l'appel
            stmt.execute();

            // Récupérer le curseur (paramètre de retour)
            ResultSet rs = (ResultSet) stmt.getObject(1);

            // Parcourir les résultats
            while (rs.next()) {
                lieuxData.add(new Lieu(
                        rs.getInt("idLieu"),
                        rs.getString("nomLieu"),
                        rs.getString("adresse"),
                        rs.getInt("capacite"),
                        rs.getString("ville"),
                        rs.getInt("active")
                ));
            }

            // Mise à jour de la TableView avec les données
            table.setItems(lieuxData);

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la recherche des lieux.");
            alert.show();
        }
    }



   

    private void afficherFormulaireAjout() {
        // Création du formulaire d'ajout
        Dialog<Lieu> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un Lieu");
        dialog.setHeaderText("Entrez les informations du lieu");

        // Création des champs de texte pour le formulaire
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nomField = new TextField();
        nomField.setPromptText("Nom du lieu");
        TextField adresseField = new TextField();
        adresseField.setPromptText("Adresse du lieu");
        TextField villeField = new TextField();
        villeField.setPromptText("Ville du lieu");
        Spinner<Integer> capaciteField = new Spinner<>(0, 10000, 100, 10);  // Valeurs entre 0 et 10000, initialisée à 100

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Adresse:"), 0, 1);
        grid.add(adresseField, 1, 1);
        grid.add(new Label("Ville:"), 0, 2);
        grid.add(villeField, 1, 2);
        grid.add(new Label("Capacité:"), 0, 3);
        grid.add(capaciteField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Ajouter des boutons OK et Annuler
        ButtonType okButton = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, cancelButton);

        // Quand l'utilisateur clique sur OK, nous récupérons les informations saisies
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                // Retourne un nouveau lieu avec les informations saisies
                return new Lieu(0, nomField.getText(), adresseField.getText(), capaciteField.getValue(), villeField.getText(), 1);
            }
            return null;
        });

        Optional<Lieu> result = dialog.showAndWait();
        result.ifPresent(lieu -> ajouterLieu(lieu.getNom(), lieu.getAdresse(), lieu.getVille(), lieu.getCapacite()));
    }


    private void afficherFormulaireModification(Lieu lieu) {
        // Création du formulaire de modification
        Dialog<Lieu> dialog = new Dialog<>();
        dialog.setTitle("Modifier un Lieu");
        dialog.setHeaderText("Modifiez les informations du lieu");

        // Création des champs de texte pour le formulaire
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nomField = new TextField(lieu.getNom());
        TextField adresseField = new TextField(lieu.getAdresse());  // Pré-remplir avec l'adresse actuelle
        TextField villeField = new TextField(lieu.getVille());
        Spinner<Integer> capaciteField = new Spinner<>(0, 10000, lieu.getCapacite(), 10); // Valeur initiale avec la capacité actuelle

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Adresse:"), 0, 1);
        grid.add(adresseField, 1, 1);
        grid.add(new Label("Ville:"), 0, 2);
        grid.add(villeField, 1, 2);
        grid.add(new Label("Capacité:"), 0, 3);
        grid.add(capaciteField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Ajouter des boutons OK et Annuler
        ButtonType okButton = new ButtonType("Modifier", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, cancelButton);

        // Quand l'utilisateur clique sur OK, nous récupérons les informations saisies
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                // Retourne un lieu modifié avec les informations saisies
                return new Lieu(lieu.getId(), nomField.getText(), adresseField.getText(), capaciteField.getValue(), villeField.getText(), 1);
            }
            return null;
        });

        Optional<Lieu> result = dialog.showAndWait();
        result.ifPresent(lieuModifie -> modifierLieu(lieuModifie));
    }


    public VBox getUI() {
        return root;
    }
    
    private void modifierLieu(Lieu lieu) {
    try (Connection conn = DBConnection.getConnection()) {
        String sql = "{CALL Gestion_lieux.ModifierLieu(?, ?, ?)}";
        CallableStatement stmt = conn.prepareCall(sql);
        stmt.setInt(1, lieu.getId());
        stmt.setString(2, lieu.getNom());
        stmt.setInt(3, lieu.getCapacite());
        stmt.execute();
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Lieu modifié avec succès !");
        alert.show();
        loadData();
    } catch (SQLException e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la modification du lieu : "+e.getMessage());
        String triggerError = null   ;
        if (e.getMessage().contains("trigger")){
            for (String line : e.getMessage().split("\n")) {
               if (line.contains("ORA-20")) { // Mot-clé de l'erreur spécifique
                   triggerError = line.trim();
                   break;
               }
            }

                // Afficher l'erreur si elle est trouvée
             if (triggerError != null) {
                 alert = new Alert(Alert.AlertType.ERROR,"Erreur lors de la modification du lieu : "+ triggerError);
             } else {
                 System.out.println("Erreur non liée au trigger spécifié : " + e.getMessage());
             }
        }

        alert.show();
    }
}


}

class Lieu {
    private int id  ;
    private String nom;
    private String adresse ; 
    private int capacite;
    private String ville;
    private int active ;
    

    // Constructeur
    public Lieu(int id, String nom, String adress, int capacite, String ville, int active) {
        this.id = id;
        this.nom = nom;
        this.adresse = adress ;
        this.ville = ville;
        this.capacite = capacite;
        this.active = active ;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getVille() {
        return ville;
    }

    public int getCapacite() {
        return capacite;
    }
    
    public String getAdresse() {
        return adresse;
    }
    
    public int getActive() {
        return active;
    }


}

