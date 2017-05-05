/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class QueryServlet extends HttpServlet {

    private ArrayList<String> getUrlsFromRS(ResultSet selectDocsHasWord) {
        ArrayList<String> temp = new ArrayList<>();
        try {
            while(selectDocsHasWord.next()){
                temp.add(selectDocsHasWord.getString("Url"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(QueryServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return temp;
    }
    
    public String getTitle(String url) {
        /*to be replaced by retrieving title from the database*/
        InputStream response;
        try {
            response = new URL(url).openStream();
            Scanner scanner = new Scanner(response);
            String responseBody = scanner.useDelimiter("\\A").next();
            return (responseBody.substring(responseBody.indexOf("<title>") + 7, responseBody.indexOf("</title>")));
        } catch (MalformedURLException ex) {
            Logger.getLogger(QueryServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(QueryServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "No title is available";
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Map<String, Double> resultPages = new TreeMap<>(Collections.reverseOrder());
        String Query = request.getParameter("QUERY").trim().toLowerCase();
        PorterStemmer PS = new PorterStemmer();
        queryManager qm = new queryManager();
        ArrayList<String> words;
        try {
            if (Query.length() > 2 && Query.charAt(0) == '\"' && Query.charAt(Query.length() - 1) == '\"') {
                /*Phrase Search*/
                Query = Query.substring(1, Query.length() - 1);
                ResultSet RS = qm.selectSimilarPhrases(Query);
                while (RS.next()) {
                    //    resultPages.put(RS.getString("Url"), 0.0);
                }
            } else {
                /*Word Search*/
                words = PS.StemText(Query);
                ArrayList<String> arr;
                Double numberOfDocsHasWord;
                Double IDF;
                String U;
                Double numberOfOcurrence;
                Double numberOfWords;
                Double TF;

                for (String word : words) {
                    //For Every word
                    numberOfDocsHasWord = qm.numberOfDocsContainingWord(word);
                    IDF = Math.log10(2 / numberOfDocsHasWord);
                    arr = getUrlsFromRS(qm.selectDocsHasWord(word));
                    for (int i = 0; i < arr.size(); i++) {
                        //For every document has this word
                        U = arr.get(i);
                        numberOfOcurrence = qm.getNumOfOccurence(U, word);
                        numberOfWords = qm.getNumOfWords(U);
                        TF = numberOfOcurrence / numberOfWords;
                        if(resultPages.get(U) == null)
                            resultPages.put(U,(IDF * TF * qm.getPageRank(U)));
                        else
                            resultPages.put(U, resultPages.get(U) +(IDF * TF * qm.getPageRank(U)));
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(QueryServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

        setResult(response, resultPages, Query);
    }

    private void setResult(HttpServletResponse response, Map<String, Double> res, String Query) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Searching result</title>");
            out.println("<meta charset=\"UTF-8\">");
            out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1 align=\"center\" >Search Results</h1>\n"
                    + "<div style=\"position: absolute; left:50px;padding: 10px;\">\n"
                    + "<form action=\"QueryServlet\" method=\"GET\" id=\"q\">\n"
                    + "<input autofocus autocomplete=\"off\" value = \"" + Query + "\" type=\"text\" name=\"QUERY\" size=\"35\"/>\n"
                    + "<input type=\"submit\" value=\"Search!\" style=\"font-size : 15px; font-weight: bold;\"/>\n"
                    + "</form>");
            
            for (Map.Entry<String, Double> entry : res.entrySet()) {
                out.println("<h3 style=\"margin:0;\"><a href =" + entry.getKey() + " >" + getTitle(entry.getKey()) + "</a> Rank = " + entry.getValue() + "</h3>\n"
                        + "<cite style=\" font-size:14px; color:green; font-style: normal; \">" + entry.getKey() + "</cite>");
            }
            
            
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>


}
