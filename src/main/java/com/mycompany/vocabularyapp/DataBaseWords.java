package com.mycompany.vocabularyapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Optional;
import java.util.Random;
import java.util.Map;


/**
 *
 * @author walth
 */
public class DataBaseWords {

    private String url;

    public DataBaseWords() {
        this.url = "jdbc:mysql://localhost:3306/curso";
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    public static void main(String[] args){
        DataBaseWords dbw = new DataBaseWords();
        
        int randomId = dbw.randomId();
        dbw.accessWords("English", randomId).ifPresentOrElse(
            word -> System.out.println("Palabra en inglés: " + word), 
            () -> System.out.println("No se encontró el ID")
        );

       
    }
   
    
    public Optional<String> accessWords(String typeWord,int id) {
        try (Connection connection = DriverManager.getConnection(getUrl(),"root","1234")){
            System.out.println("Conexión establecida");
            return queryAccessWords(connection,typeWord,id);
        }catch(SQLException e){
            System.out.println("No se ha podido establecer conexion: " + e.getMessage());
        }
        return Optional.empty();
    }
    
    
    
    public Optional<String> queryAccessWords(Connection connection,String typeWord, int id){
        Map<String,String> typeQuerys = Map.of(
            "Spanish","select wordSpanish from TranslationLog where id = ?;",
            "English","select wordEnglish from TranslationLog where id = ?;"
        );
        
        Map<String,String> typeColumns = Map.of(
            "Spanish","wordSpanish",
            "English","wordEnglish"
        );
        
        String querySql = typeQuerys.get(typeWord);
        
        try(PreparedStatement pstmt = connection.prepareStatement(querySql)){
            pstmt.setInt(1,id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()){
                    String wordSpanish = rs.getString(typeColumns.get(typeWord));
                    return Optional.of(wordSpanish);
                }
            }
        }catch(SQLException e){
            System.out.println("Error al mostrar la palabra por el id: " + e.getMessage());
        }
        return Optional.empty();
    }
    
    
    /**
     * Este metodo sirve para acceder a un id aleatorio de la tablas.
     * @return me devuleve un int (dato primitivo) que es el valor de un id.
     */
    public int randomId(){
        
        int randomId = 0;
        try (Connection connection = DriverManager.getConnection(getUrl(), "root", "1234")) {
            String query = "SELECT count(id) AS countId FROM TranslationLog";

            try(Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)){

                if (rs.next()){
                    int count = rs.getInt("countId");
                    if (count > 0) {
                        Random random = new Random();
                        randomId = random.nextInt(count) + 1;
                    }
                }


            }catch(SQLException e){
                System.out.println(e.getMessage());
            }

            
        } catch (SQLException e) {
            System.out.println("Error al establecer la conexión: " + e.getMessage());
        }
        return randomId;
    }
    
    
}
