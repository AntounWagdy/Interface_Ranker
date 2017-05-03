package Main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
public class queryManager {

    ResultSet myRes = null;       //for select query
    int res;    //for insert query
    boolean flag; //for delete query
    String sql;
    databaseManager db;

    public queryManager() {
        this.db = databaseManager.getInstance();
    }

    ResultSet selectSimilarPhrases(String Query) {
        /*TODO: change to the name of the database*/
        sql = "SELECT PhrasesColumn FROM Phrases where PhraseColumn like %"+Query+"%;";

        try {
            myRes = db.select(sql);
        } catch (SQLException ex) {
            Logger.getLogger(queryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return myRes;
    }

    ResultSet selectDocsHasWord(String word) {
        /*TODO : Determining which database to use*/
        sql = "SELECT Url from search_engine.doc_words2,search_engine.document2 where docId = ID_doc and word = "+word+";";
        try {
            myRes = db.select(sql);
        } catch (SQLException ex) {
            Logger.getLogger(queryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return myRes;
    }

    Double numberOfDocsContainingWord(String word) {
        /*TODO : Determining which database to use*/
        sql = "SELECT count(Url) as num from search_engine.doc_words2,search_engine.document2 where docId = ID_doc and word = "+word+";";
        try {
            myRes = db.select(sql);
            return myRes.getDouble("num");
        } catch (SQLException ex) {
            Logger.getLogger(queryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Double.NaN;
    }
}
