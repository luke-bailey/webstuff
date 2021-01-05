package mypkg;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

 
public class ResetServlet extends HttpServlet {
    String DBURL = "database connection url";
    String ANSWER_QUERY = "SELECT * FROM security_questions WHERE Answer1 = ? and Answer2 = ?";
 
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // reads input file from an absolute path
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String answer1 = request.getParameter("a1");
        String answer2 = request.getParameter("a2");
        String userid = request.getParameter("userid");
        String url = request.getHeader("referer");
        Connection dbc = null;
        if (!answer1.isEmpty() && !answer2.isEmpty())
        {

            try{

                Class.forName("com.mysql.jdbc.Driver");
                dbc = DriverManager.getConnection(DBURL, "username", "password");
                PreparedStatement aq = dbc.prepareStatement(ANSWER_QUERY);
                aq.setString(1, answer1);
                aq.setString(2, answer2);
                ResultSet qa = aq.executeQuery();

                if (qa.next())
                {
                    do 
                    {   
                        out.println("<!DOCTYPE html><html><head><title>Mace Felton - Password Reset</title><link rel='stylesheet' type='text/css' href='css/macefelton.css' /><script type='text/javascript' src='js/popup.js'></script></head><header>");
                        out.println("</header><body class ='grey'><nav><ul><li><a href = 'home'>Home</a></li><li><a href = 'about'>About Us</a></li><li><a href = 'products'>Music</a></li><li><a href ='events'>Events</a></li><li class ='leftlink'><a href = 'register.html'>Register</a></li><li class = 'rightlink'><a href = 'login.html'>Login</a></li></ul></nav>");
                        out.println("<div class = 'parentlogin'><div class = 'login'><h3>What would you like to change your password to?</h3><div class = 'popup' onclick = 'myFunction()'><img src = 'img/questionmark2.png'>");
                        out.println("<span class = 'popuptext' id = 'myPopup'>Passwords must be between 8-16 characters, include one uppercase and lowercase letter, a number, and a special character, ie !, ?, %, #, $ or @</span></div>");
                        out.println("</fieldset><br/><form action ='confirm' method ='post'><label>Password</label><input type ='password' name ='password'><input type ='hidden' name ='userid' value ='" + userid + "'><label>Password Again</label><input type ='password' name ='passagain' class ='pass-reset'><input type='submit' id='password_reset' value='Submit'/><input type='reset' id='reset' value='Cancel'/></div></div></form></body>");
                    } 
                    while (qa.next());
                }

                else
                {
                    out.println("<script language='javascript'>window.alert('Those answers do not match our records');window.location='resetpage';</script>");
                    //response.sendRedirect("localhost:9999/hello/securityquestion");
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
            out.println("<script language='javascript'>window.alert('You must fill out all fields');window.location='resetpage';</script>");
          }
    }
}