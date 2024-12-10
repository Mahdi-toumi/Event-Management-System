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

public class GestionSpectacles {

    private VBox root;
    private TableView<Spectacle> table;
    private ObservableList<Spectacle> spectaclesData;

    public GestionSpectacles() {
        root = new VBox(15);  // Augmenter l'espacement entre les éléments
        root.setPadding(new Insets(20));  // Ajouter du padding autour du contenu

        // Création de la boîte de filtre avec un espacement de 10
        HBox filterBox = new HBox(10);
        filterBox.setPadding(new Insets(10));  // Padding autour de la box de filtre

        TextField searchField = new TextField();
        searchField.setPromptText("Titre du spectacle....");
        searchField.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #ccc; -fx-border-radius: 5px; -fx-padding: 5px;");
        
        TextField IdField = new TextField();
        IdField.setPromptText("Id du spectacle....");
        IdField.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #ccc; -fx-border-radius: 5px; -fx-padding: 5px;");

        
        Button searchButton = new Button("Chercher");
        searchButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-border-radius: 5px;");
        Button addButton = new Button("Ajouter un spectacle");
        addButton.setStyle("-fx-background-color: #1abc9c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-border-radius: 5px;");

        // Ajouter les boutons et le champ de recherche dans la boîte de filtre
        filterBox.getChildren().addAll(searchField,IdField, searchButton, addButton);

        // Création de la TableView
        table = new TableView<>();
        setupTable();

       

        // Actions des boutons
        addButton.setOnAction(e -> afficherFormulaireAjout());
        searchButton.setOnAction(e -> chercherSpectacle(searchField.getText(),IdField.getText()));

        // Ajouter la boîte de filtre et la table dans le VBox principal
        root.getChildren().addAll(filterBox, table);

        // Charger les données dans la table
        loadData();

    }

    private void setupTable() {
         // ID column: Correspond à 'idSpec' dans la classe Spectacle
         TableColumn<Spectacle, Integer> idCol = new TableColumn<>("ID");
         idCol.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getId()).asObject());

         // Titre column: Correspond à 'titre' dans la classe Spectacle
         TableColumn<Spectacle, String> titreCol = new TableColumn<>("Titre");
         titreCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getTitre()));

         // Date column: Correspond à 'date' dans la classe Spectacle
         TableColumn<Spectacle, String> dateCol = new TableColumn<>("Date");
         dateCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getDate()));

         // Heure Début column: Correspond à 'heureDebut' dans la classe Spectacle
         TableColumn<Spectacle, String> heureDebutCol = new TableColumn<>("Heure Début");
         heureDebutCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getHeureDebut()));

         // Durée column: Correspond à 'dureeS' dans la classe Spectacle
         TableColumn<Spectacle, String> dureeCol = new TableColumn<>("Durée");
         dureeCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getDureeS()));

         // Nombre Spectateurs column: Correspond à 'Nbrspectateur' dans la classe Spectacle
         TableColumn<Spectacle, String> nbrSpectateurCol = new TableColumn<>("Nbr Spectateurs");
         nbrSpectateurCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getNbrspectateur()));

         // ID Lieu column: Correspond à 'idLieu' dans la classe Spectacle
         TableColumn<Spectacle, String> idLieuCol = new TableColumn<>("ID Lieu");
         idLieuCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getIdlieu()));

         // Actions column: Column with actions like Edit and Delete
         TableColumn<Spectacle, Void> actionsCol = new TableColumn<>("Actions");
         actionsCol.setCellFactory(col -> new TableCell<Spectacle, Void>() {
             private final Button editButton = new Button("Modifier");
             private final Button deleteButton = new Button("Annuler");

             {
                    editButton.setStyle("-fx-background-color: #1abc9c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-border-radius: 5px;");
                    deleteButton.setStyle("-fx-background-color: #d11a2a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-border-radius: 5px;");

                 
                 // Action pour le bouton "Modifier"
                 editButton.setOnAction(e -> afficherFormulaireModification(getTableRow().getItem()));
                 // Action pour le bouton "Supprimer"
                 deleteButton.setOnAction(e -> annulerSpectacle(getTableRow().getItem()));
             }

             @Override
             protected void updateItem(Void item, boolean empty) {
                 super.updateItem(item, empty);
                 if (empty) {
                     setGraphic(null); // Pas de boutons pour les lignes vides
                 } else {
                     HBox buttons = new HBox(5, editButton, deleteButton);
                     setGraphic(buttons); // Ajouter les boutons pour chaque ligne
                 }
             }
         });

         // Ajouter toutes les colonnes à la table
         table.getColumns().addAll(idCol, titreCol, dateCol, heureDebutCol, dureeCol, nbrSpectateurCol, idLieuCol, actionsCol);
     }

    private void loadData() {
        spectaclesData = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Spectacle WHERE dateS IS NOT NULL")) {

            while (rs.next()) {
                spectaclesData.add(new Spectacle(
                        rs.getInt("idSpec"),
                        rs.getString("titre"),
                        rs.getString("dateS"),
                        rs.getString("h_debut"),
                        rs.getString("dureeS"),
                        rs.getString("Nbrspectateur"),
                         rs.getString("Idlieu")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        table.setItems(spectaclesData);
    }

    private void ajouterSpectacle(String titre, String date, String heureDebut, int duree, int nbrSpectateurs, int idLieu) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "{CALL Gestion_Spectacles.AjouterSpectacle(?, ?, ?, ?, ?, ?)}";
            CallableStatement stmt = conn.prepareCall(sql);
            stmt.setString(1, titre);
            stmt.setDate(2, java.sql.Date.valueOf(date));
            stmt.setTime(3, java.sql.Time.valueOf(heureDebut));
            stmt.setInt(4, duree);
            stmt.setInt(5, nbrSpectateurs);
            stmt.setInt(6, idLieu);
            stmt.execute();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Spectacle ajouté avec succès !");
            alert.show();
            loadData();  // Reload data after addition
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ajout du spectacle.");
            alert.show();
        }
    }


    private void annulerSpectacle(Spectacle spectacle) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "{CALL Gestion_Spectacles.AnnulerSpectacle(?)}";
            CallableStatement stmt = conn.prepareCall(sql);
            stmt.setInt(1, spectacle.getId());
            stmt.execute();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Spectacle annulé avec succès !");
            alert.show();
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'annulation du spectacle.");
            alert.show();
        }
    }

    private void chercherSpectacle(String titre , String Id) {
        spectaclesData = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection()) {
            
            String sql = "{ ? = CALL Gestion_Spectacles.ChercherSpectacle(?, ?) }";
            CallableStatement stmt = conn.prepareCall(sql);

            // Définir le paramètre de retour (le curseur)
            stmt.registerOutParameter(1, java.sql.Types.REF_CURSOR);
            
            
            
            // Définir les paramètres IN
            stmt.setObject(2, Id.isEmpty() ? null : Integer.parseInt(Id));
            stmt.setString(3, titre.isEmpty() ? null : "%" + titre + "%");

            // Exécuter l'appel
            stmt.execute();

            // Récupérer le curseur (paramètre de retour)
            ResultSet rs = (ResultSet) stmt.getObject(1);
            
            while (rs.next()) {
                spectaclesData.add(new Spectacle(
                        rs.getInt("idSpec"),
                        rs.getString("titre"),
                        rs.getString("dateS"),
                        rs.getString("h_debut"),
                        rs.getString("dureeS"),
                        rs.getString("Nbrspectateur"),
                         rs.getString("Idlieu")
                ));
            }
            table.setItems(spectaclesData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void afficherFormulaireAjout() {
    // Création du formulaire d'ajout
    Dialog<Spectacle> dialog = new Dialog<>();
    dialog.setTitle("Ajouter un Spectacle");
    dialog.setHeaderText("Entrez les informations du spectacle");

    // Création des champs de texte pour le formulaire
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    TextField titreField = new TextField();
    titreField.setPromptText("Titre du spectacle");

    TextField dateField = new TextField();
    dateField.setPromptText("Date du spectacle (YYYY-MM-DD)");

    TextField heureDebutField = new TextField();
    heureDebutField.setPromptText("Heure de début (h)");

    Spinner<Double> dureeField = new Spinner<>(0, 240, 90, 1);  // Durée en minutes, valeur par défaut 90
    dureeField.setEditable(true);

    Spinner<Integer> nbrSpectateurField = new Spinner<>(0, 10000, 100, 10); // Nombre de spectateurs

    TextField idLieuField = new TextField();
    idLieuField.setPromptText("ID Lieu");

    grid.add(new Label("Titre:"), 0, 0);
    grid.add(titreField, 1, 0);
    grid.add(new Label("Date:"), 0, 1);
    grid.add(dateField, 1, 1);
    grid.add(new Label("Heure Début:"), 0, 2);
    grid.add(heureDebutField, 1, 2);
    grid.add(new Label("Durée (h):"), 0, 3);
    grid.add(dureeField, 1, 3);
    grid.add(new Label("Nbr Spectateurs:"), 0, 4);
    grid.add(nbrSpectateurField, 1, 4);
    grid.add(new Label("ID Lieu:"), 0, 5);
    grid.add(idLieuField, 1, 5);

    dialog.getDialogPane().setContent(grid);

    // Ajouter des boutons OK et Annuler
    ButtonType okButton = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
    ButtonType cancelButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
    dialog.getDialogPane().getButtonTypes().addAll(okButton, cancelButton);

    // Quand l'utilisateur clique sur OK, récupérer les informations saisies
    dialog.setResultConverter(dialogButton -> {
        if (dialogButton == okButton) {
            return new Spectacle(0, titreField.getText(), dateField.getText(), heureDebutField.getText(),
                    dureeField.getValue().toString(), nbrSpectateurField.getValue().toString(), idLieuField.getText());
        }
        return null;
    });

    Optional<Spectacle> result = dialog.showAndWait();
    result.ifPresent(spectacle -> ajouterSpectacle(spectacle.getTitre(), spectacle.getDate(), spectacle.getHeureDebut(),
            Integer.parseInt(spectacle.getDureeS()), Integer.parseInt(spectacle.getNbrspectateur()), Integer.parseInt(spectacle.getIdlieu())));
}


    private void afficherFormulaireModification(Spectacle spectacle) {
        // Création du formulaire de modification
        Dialog<Spectacle> dialog = new Dialog<>();
        dialog.setTitle("Modifier un Spectacle");
        dialog.setHeaderText("Modifiez les informations du spectacle");

        // Création des champs de texte pour le formulaire
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titreField = new TextField(spectacle.getTitre());
        TextField dateField = new TextField(spectacle.getDate());
        TextField heureDebutField = new TextField(spectacle.getHeureDebut());

        Spinner<Double> dureeField = new Spinner<>(0, 240, Double.parseDouble(spectacle.getDureeS()), 0.25) ;  // Durée en minutes
        dureeField.setEditable(true);

        Spinner<Integer> nbrSpectateurField = new Spinner<>(0, 10000, Integer.parseInt(spectacle.getNbrspectateur()), 10);
        TextField idLieuField = new TextField(spectacle.getIdlieu());

        grid.add(new Label("Titre:"), 0, 0);
        grid.add(titreField, 1, 0);
        grid.add(new Label("Date:"), 0, 1);
        grid.add(dateField, 1, 1);
        grid.add(new Label("Heure Début:"), 0, 2);
        grid.add(heureDebutField, 1, 2);
        grid.add(new Label("Durée (h):"), 0, 3);
        grid.add(dureeField, 1, 3);
        grid.add(new Label("Nbr Spectateurs:"), 0, 4);
        grid.add(nbrSpectateurField, 1, 4);
        grid.add(new Label("ID Lieu:"), 0, 5);
        grid.add(idLieuField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // Ajouter des boutons OK et Annuler
        ButtonType okButton = new ButtonType("Modifier", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, cancelButton);

        // Quand l'utilisateur clique sur OK, récupérer les informations saisies
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                return new Spectacle(spectacle.getId(), titreField.getText(), dateField.getText(), heureDebutField.getText(),
                        dureeField.getValue().toString(), nbrSpectateurField.getValue().toString(), idLieuField.getText());
            }
            return null;
        });

        Optional<Spectacle> result = dialog.showAndWait();
        result.ifPresent(spectacleModifie -> modifierSpectacle(spectacleModifie));
    }

    private void modifierSpectacle(Spectacle spectacle) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "{CALL Gestion_Spectacles.ModifierSpectacle(?, ?, ?, ?, ?, ?, ?)}";
            CallableStatement stmt = conn.prepareCall(sql);
            stmt.setInt(1, spectacle.getId());
            stmt.setString(2, spectacle.getTitre());
            stmt.setString(3, spectacle.getDate());
            stmt.setString(4, spectacle.getHeureDebut());
            stmt.setString(5, spectacle.getDureeS());
            stmt.setString(6, spectacle.getNbrspectateur());
            stmt.setString(7, spectacle.getIdlieu());
            stmt.execute();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Spectacle modifié avec succès !");
            alert.show();
            loadData();  // Reload data after modification
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la modification du spectacle : "+ e.getMessage());
            alert.show();
        }
    }




    public VBox getUI() {
        return root;
    }
}

class Spectacle {
    private int id;
    private String titre;
    private String date;
    private String heureDebut;
    private String dureeS;
    private String nbrSpectateur;
    private String idLieu;

    // Constructeur
    public Spectacle(int id, String titre, String date, String heureDebut, String dureeS, String nbrSpectateur, String idLieu) {
        this.id = id;
        this.titre = titre;
        this.date = date;
        this.heureDebut = heureDebut;
        this.dureeS = dureeS;
        this.nbrSpectateur = nbrSpectateur;
        this.idLieu = idLieu;
    }

    // Getters
    public int getId() { return id; }
    public String getTitre() { return titre; }
    public String getDate() { return date; }
    public String getHeureDebut() { return heureDebut; }
    public String getDureeS() { return dureeS; }
    public String getNbrspectateur() { return nbrSpectateur; }
    public String getIdlieu() { return idLieu; }
}

