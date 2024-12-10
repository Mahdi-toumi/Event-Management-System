/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sgbd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:xe"; // Modifiez l'URL si nécessaire
    private static final String USER = "GestionSpectacles"; // Remplacez par votre utilisateur Oracle
    private static final String PASSWORD = "1922"; // Remplacez par votre mot de passe Oracle

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

