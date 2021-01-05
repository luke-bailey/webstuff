package mypkg;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
 
 
public class QuestionServlet extends HttpServlet {
    String DBURL = "database connection here";
    String QUESTION_INSERT = "INSERT INTO security_questions (UserID, Question1, Question2, Answer1, Answer2)" +
    "VALUES (?,?,?,?,?)";
 
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // reads input file from an absolute path
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String question1 = request.getParameter("question1");
        String question2 = request.getParameter("question2");
        String answer1 = request.getParameter("answer1");
        String answer2 = request.getParameter("answer2");
        Integer ans_length1 = answer1.length();
        Integer ans_length2 = answer2.length();
        HttpSession session = request.getSession(false);
        String url = request.getHeader("referer");
        Connection dbc = null;


        if (ans_length1 > 32)
        {
            out.println("<script language='javascript'>window.alert('Please limit your answers to 32 characters');window.location='" + url + "';</script>");
        }

        else if (ans_length2 > 32) 
        {
            out.println("<script language='javascript'>window.alert('Please limit your answers to 32 characters');window.location='" + url + "';</script>");
        }

        else if (!question1.isEmpty() && !question2.isEmpty() && !answer1.isEmpty() && !answer2.isEmpty())
        {

            try{

                Integer userid = (Integer) session.getAttribute("uid");
                Class.forName("com.mysql.jdbc.Driver");
                dbc = DriverManager.getConnection(DBURL, "username", "password");
                PreparedStatement sq = dbc.prepareStatement(QUESTION_INSERT);
                sq.setInt(1, userid);
                sq.setString(2, question1);
                sq.setString(3, question2);
                sq.setString(4, answer1);
                sq.setString(5, answer2);
                int qs = sq.executeUpdate();

                if (qs == 1)
                {
                    out.println("<script language='javascript'>window.alert('Your security questions and answers have been successfully added');window.location='" + url + "';</script>");
                }

                else
                {
                    out.println("<script language='javascript'>window.alert('We could not add your questions and answers at this time');window.location='" + url + "';</script>");
                }   
            }
            
            catch (ClassNotFoundException nfe) {
        
      } catch (SQLException se) {
      } finally {
        if (dbc != null)
          try { dbc.close(); }
          catch (SQLException ignored) { }
        } 
        }
        else
        {
            out.println("<script language='javascript'>window.alert('Please fill out all fields');window.location='" + url + "';</script>");
        }
    }
}