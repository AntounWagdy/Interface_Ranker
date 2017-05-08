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
    int targetDB;
    
    public queryManager() {
        this.db = databaseManager.getInstance();
    }
    private void setTarget(){
        sql = "SELECT num FROM search_engine.working_db where DB = \"DB\";";
        try {
            myRes = db.select(sql);
            myRes.next();
            targetDB = myRes.getInt("num");
        } catch (SQLException ex) {
            Logger.getLogger(queryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    ResultSet selectSimilarPhrases(String Query) {
        setTarget();
        sql = "SELECT * FROM phrases"+(((targetDB == 2) ? "2" : "") )+" where phrase like %"+Query+"%;";

        try {
            myRes = db.select(sql);
        } catch (SQLException ex) {
            Logger.getLogger(queryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return myRes;
    }

    ResultSet selectDocsHasWord(String word) {
        /*TODO : Determining which database to use*/
        setTarget();

        sql = "SELECT distinct Url from search_engine.doc_words"+(((targetDB == 2) ? "2" : "") )+",search_engine.document"+(((targetDB == 2) ? "2" : "") )+" where docId"+(((targetDB == 2) ? "2" : "") )+" = ID_doc and word = \""+word+"\";";
        try {
            myRes = db.select(sql);
        } catch (SQLException ex) {
            Logger.getLogger(queryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return myRes;
    }

    Double numberOfDocsContainingWord(String word) {
        /*TODO : Determining which database to use*/
        setTarget();

        sql = "SELECT count(distinct Url) as num from search_engine.doc_words"+(((targetDB == 2) ? "2" : "") )+",search_engine.document"+(((targetDB == 2) ? "2" : "") )+" where docId"+(((targetDB == 2) ? "2" : "") )+" = ID_doc and word = \""+word+"\";";
        try {
            myRes = db.select(sql);
            myRes.next();
            return myRes.getDouble("num");
        } catch (SQLException ex) {
            Logger.getLogger(queryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Double.NaN;
    }
    Integer getDocId(String U){
        /*TODO : Determining which database to use*/
        setTarget();

        sql = "SELECT docId"+(((targetDB == 2) ? "2" : "") )+" from search_engine.document"+(((targetDB == 2) ? "2" : "") )+" where Url =\""+U+"\";";
        try {
            myRes = db.select(sql);
            myRes.next();
            return myRes.getInt("docId2");
        } catch (SQLException ex) {
            Logger.getLogger(queryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }
    Double getNumOfOccurence(String U, String word) {
        /*TODO : Determining which database to use*/
        setTarget();

        sql = "SELECT count(*) as num from search_engine.doc_words"+(((targetDB == 2) ? "2" : "") )+" where ID_doc = "+getDocId(U)+" and word = \""+word+"\";";
        try {
            myRes = db.select(sql);
            myRes.next();
            return myRes.getDouble("num");
        } catch (SQLException ex) {
            Logger.getLogger(queryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Double.NaN;
    }

    Double getNumOfWords(String U) {
        /*TODO : Determining which database to use*/
        setTarget();

        sql = "SELECT count(*) as num from search_engine.doc_words"+(((targetDB == 2) ? "2" : "") )+" where ID_doc = "+getDocId(U)+";";
        try {
            myRes = db.select(sql);
            myRes.next();
            return myRes.getDouble("num");
        } catch (SQLException ex) {
            Logger.getLogger(queryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Double.NaN;
    }

    Double getPageRank(String U) {
        setTarget();
        //"+(((targetDB == 2) ? "2" : "") )+"
         sql = "SELECT page_rank from rank"+(((targetDB == 2) ? "2" : "") )+" where docId = "+getDocId(U)+";";
        try {
            myRes = db.select(sql);
            myRes.next();
            return myRes.getDouble("page_rank");
        } catch (SQLException ex) {
            Logger.getLogger(queryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Double.NaN;
    }

    Double getNumOfPages() {
        setTarget();
         sql = "SELECT count(*) as num from document"+(((targetDB == 2) ? "2" : "") )+";";
        try {
            myRes = db.select(sql);
            myRes.next();
            return myRes.getDouble("num");
        } catch (SQLException ex) {
            Logger.getLogger(queryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Double.NaN;
    
    }
}