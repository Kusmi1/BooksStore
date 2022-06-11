import com.sun.org.apache.xalan.internal.xsltc.compiler.Pattern;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import javax.naming.*;
import jakarta.servlet.ServletException;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.*;

@WebServlet("/Shop")
//@WebServlet("/Servletdemo_war_exploded")
public class BooksService extends HttpServlet {
    DataSource dataSource;
    static StringBuilder str = new StringBuilder();




    public void init() throws ServletException {
        try {
//            connect = DriverManager.getConnection(dbAdress, username, password);

            Context init = new InitialContext();
            Context contx = (Context) init.lookup("java:comp/env");

            dataSource = (DataSource) contx.lookup("jdbc/kusmi");
        
        } catch (NamingException e) {
            throw new ServletException("nie ustanowiono połączenia z bazą ", e);
        }
    }



    private void serviceRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=utf-8");

        PrintWriter responseOut = response.getWriter();
//Top sign
        responseOut.println("<table style=\"border-collapse: collapse; width: 100%; height: 46px;\" border-style: hidden; border=\"0\">"  +
                "    <tbody> " +
                "    <tr style=\"height: 102px;\">" +
                "        <td style=\"width: 100%; height: 46px;background-color: #321414;border-color: #321414;"+
                  " text-align: center;\" >"+
                "                <h2 style=\"font-size:xxx-large;text-allign: left;\"><span style=\"color: #fadfad;\"> BooksStore </span></h2>" +
                "        </td>" +
                "    </tr>" +
                "    </tbody>" +
                "</table>");

        // left side
        responseOut.println(
                        "<table cellpadding=\"15\" style=\"height: 115%; width: 12%; border-collapse: collapse; background-color: #321414;  border-color: #321414;\" border=\"0\""+"align=\"left\""+">\n" +

                "    <tbody>\n" +
              //  "    <tr style=\"height: 237px;\">\n" +
                                "<tr style=\"height: 100%;vertical-align: top;\">"+
                                "<td style=\"width: 100%; height: 10px;vertical-align: top;\">"+
                //"        <td style=\"width: 8%; height: 10px;vertical-align: top;\">\n" +
                "\n" +
                        "<form>"+
                "<p><span style=\"vertical-align: text-top;font-size:x-large;color: #fadfad;\">" +
                                "<a href=\"http://localhost:8080/Servletdemo_war_exploded\" style=\"vertical-align: top;font-size:x-large; color: #fadfad;\"> Home  </a></span> <br />"+
                        //"<a href=\"http://localhost:8080/Servletdemo_war_exploded\"> Home</a></span><br />" +


                "<span style=\"vertical-align: top;font-size:x-large; color: #fadfad;\">Title</span>\n" +
                " <br> <input type=\"text\" name=\"title\" /></br> "+

                 "<span style=\"vertical-align: top;font-size:x-large; color: #fadfad;\">Author</span>\n" +
                " <br> <input type=\"text\" name=\"author\" /></br> "+
                "<span style=\"vertical-align: top;font-size:x-large; color: #fadfad;\">Publisher</span>\n" +
                "<br>  <input type=\"text\" name=\"publisher\" /></br>  "+



                "<span style=\"vertical-align: top;font-size:x-large; color: #fadfad;\">Price Higher Than</span>\n" +
                " <br> <input type=\"text\" name=\"priceHigher\" /></br> "+
                "<span style=\"vertical-align: top;font-size:x-large; color: #fadfad;\">Price Lower Than</span>\n" +
                " <br> <input type=\"text\" name=\"priceLower\" /></br> "+

                "\n" +
                "<input type=\"submit\" name=\"bu1\" value=\"Search\" />"+"\n"+
                                "</form>"+
                                "<form>"+
                                "<a href=\"http://localhost:8080/Servletdemo_war_exploded/Shop\">\n" +
                                "      <input type=\"submit\" name=\"bu2\" value=\"Clear\"/>\n" +

                "        </td>\n" +
                        "</form>"+


                "\n"
                        +"    </tr>\n" +
                        "    </tbody>\n"+
                                " </td> " +
                                "</tr>\n" +
                                "    </tbody>\n" +
                                "</table>"

                );
        //prawa tabela
        responseOut.println("<table cellpadding=\"15\" style=\"height: 115%; width: 88%; border-collapse: collapse; background-color: #321414;  border-color: #321414;\" border=\"0\""+"align=\"right\""+">\n" +

                        "    <tbody>\n" +"<tr style=\"height: 100%;vertical-align: top; align=\"right\"\">"+
                "<td style=\"width: 8%; height: 10px;vertical-align: top; align=\"right\"\">"+

                "<td style=\"width: 80%; height: 100%;vertical-align: top;font-size:xx-large;color: #fadfad; background-color: #321414; align=\"left\"\">"+
                "Available books in our shop   <center> </center>");

        String publisher = request.getParameter("publisher");
        String author = request.getParameter("author");
        String title = request.getParameter("title");
        String priceHigher = request.getParameter("priceHigher");
        String priceLower = request.getParameter("priceLower");
        int priceLowNum = 0;
        int priceHighNum = 0;


        Connection connect = null;
        try {
            synchronized (dataSource) {
                connect = dataSource.getConnection();
            }
            Statement statement = connect.createStatement();

           String query = "SELECT * From books";

            StringBuilder strTitle = new StringBuilder(" && title IS NOT NULL ");
            StringBuilder strPublisher = new StringBuilder(" && publisher IS NOT NULL ");
            StringBuilder strAuthor = new StringBuilder(" && author IS NOT NULL ");
            StringBuilder strHighPrice = new StringBuilder(" && price IS NOT NULL ");
            StringBuilder strLowPrice = new StringBuilder(" && price IS NOT NULL ");

                if (checkingNumbers(author) || checkingNumbers(publisher)) {
                    responseOut.println("<br> author or publisher contains digit in name </br> ");
                }
                if (checkingLetters(priceHigher) || checkingLetters(priceLower)) {
                responseOut.println("<br> price  contains letters</br>");
                }
                if ((!priceHigher.isEmpty() && !priceLower.isEmpty()) && Integer.parseInt(priceHigher) > Integer.parseInt(priceLower)) {
                    responseOut.println("<br> number in the pool \"price higher\" cannot be lower as number in the pool \"price lower\"</br>");
                }
                if (!title.isEmpty()) {
                    strTitle = strTitle.append(" && title LIKE '%").append(title).append("%' ");
                }

                if (!publisher.isEmpty()) {
                    strPublisher = strPublisher.append(" && publisher LIKE '%").append(publisher).append("%' ");
                }
                if (!author.isEmpty()) {
                    strAuthor = strAuthor.append(" && author LIKE '%").append(author).append("%' ");
                }
                if (!priceHigher.isEmpty()) {
                    priceHighNum = Integer.parseInt(priceHigher);
                    strHighPrice = strHighPrice.append(" && price >  ").append(priceHighNum);

                }
                if (!priceLower.isEmpty()) {
                    priceLowNum = Integer.parseInt(priceLower);
                    strLowPrice = strLowPrice.append(" && price <= ").append(priceLowNum);
                }
                query = " SELECT * From Books Where ID>0 "+strTitle+strAuthor+strPublisher+strHighPrice+strLowPrice;
                ResultSet results = statement.executeQuery(query);

            responseOut.println(" <table style=\"border-collapse: collapse; width: 52%;\" border=2px solid>\n" +
                    "<tbody>\n" +
                    "<tr>\n" +
                    "<td style=\"width: 15%;\"><span style=\"color: white;font-size:large\">"+"Title"+"</span></td>\n" +
                    "<td style=\"width: 15%;\"><span style=\"color: white;font-size:large\">"+"Author"+"</span></td>\n" +
                    "<td style=\"width: 15%;\"><span style=\"color: white;font-size:large\">"+"Publisher"+"</span></td>\n" +
                    "<td style=\"width: 15%;\"><span style=\"color: white;font-size:large\">"+"Price"+"</span></td>\n" +
                    "</tr>\n" +
                    "</tbody>\n" +
                    "</table> ");
            int i =0;
                while (results.next()) {
                    String color ="white";
                    String colorLetters ="#321414";


                    int id = results.getInt("ID");
                    String title2 = results.getString("title");
                    String author2 = results.getString("author");
                    String publisher2 = results.getString("publisher");
                    float price2 = results.getFloat("price");
//
//                    responseOut.println("<table style=\"height: 20px; width: 100%; border-collapse: collapse; border-style: hidden; background-color: white;\" border=\"2\"> ");
                   i++;
                   if (i%2==0){
                       color = "#321414";
                       colorLetters="white";
                    }
                    responseOut.println(" <table style=\"border-collapse: collapse; width: 52%;background-color:"+color+"\""+" border=2px solid>\n" +
                            "<tbody>\n" +
                            "<tr>\n" +
                            "<td style=\"width: 15%;\"><span style=\"color:"+ colorLetters+";font-size:large\">"+title2+"</span></td>\n" +
                            "<td style=\"width: 15%;\"><span style=\"color: "+ colorLetters+";font-size:large\">"+author2+"</span></td>\n" +
                            "<td style=\"width: 15%;\"><span style=\"color: "+ colorLetters+";font-size:large\">"+publisher2+"</span></td>\n" +
                            "<td style=\"width: 15%;\"><span style=\"color: "+ colorLetters+";font-size:large\">"+price2+"</span></td>\n" +
                            "</tr>\n" +
                            "</tbody>\n" +
                            "</table> ");

                }

            responseOut.println (" </td> " +
                    "</tr>\n" +
                    "    </tbody>\n" +
                    "</table>");

                results.close();
                statement.close();

            } catch(Exception e){
                responseOut.println(e.getMessage());
            } finally{
                try {
                    connect.close();

                } catch (Exception e) {
                }
            }
            responseOut.close();
        }



    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        serviceRequest(request, response);
    }
    public static boolean checkingLetters (String check){

        boolean result = check.matches("[a-zA-Z]+");
        return result;
    }
    public static boolean checkingNumbers (String check){
        //boolean result = check.matches("^(?:[+-]?(\\d+)([,.]\\d+)?)?$");
        boolean result = check.matches("[0-9]+");
        return result;
    }


}




