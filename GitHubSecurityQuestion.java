package mypkg;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

class SearchString
  {
    public static boolean contains_char(String inputString, String[] items){
      boolean found = false;
      for (String item : items) {
        if (inputString.contains(item)) {
          found = true;
          System.out.println(item);
          System.out.println(found);
          break;
        }
      }
      return found;
    }
  }
 
public class SecurityQuestionServlet extends HttpServlet {
    String DBURL = "database connection url";
    String USERNAME_QUERY = "SELECT UserID FROM users WHERE Username = ?";
    String QUESTION_QUERY = "SELECT * FROM security_questions WHERE UserID = ?";
    Integer userid;
    String cookie_user = null;

 
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // reads input file from an absolute path
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String username = request.getParameter("username");
        String url = request.getHeader("referer");
        SearchString search = new SearchString();
        String[] space = {" ", "[", "]", "(", ")", "=", ",", "/", "?", "@", ":", ";", "\"", "\\", "{", "}", ">", "<"};
        boolean find_space = search.contains_char(username, space);
        
        Connection dbc = null;
        if (username != null)
        {
          if (find_space == false)
                {
                   Cookie userCookie = new Cookie("user_reset", username);
                   userCookie.setMaxAge(60);
                   response.addCookie(userCookie);
                   System.out.println(userCookie.getValue());
                }
          else
          {
            out.println("<script language='javascript'>window.alert('You included a space or a special character in your username');window.location='resetpage';</script>");
          }
        }

        if (!username.isEmpty())
        {
            try{

                Class.forName("com.mysql.jdbc.Driver");
                dbc = DriverManager.getConnection(DBURL, "username", "password");
                PreparedStatement uq = dbc.prepareStatement(USERNAME_QUERY);
                uq.setString(1, username);
                ResultSet qu = uq.executeQuery();

                if (qu.next())
                {
                    do 
                    {
                        userid = qu.getInt("UserID");
                        PreparedStatement qq = dbc.prepareStatement(QUESTION_QUERY);
                        qq.setInt(1, userid);
                        ResultSet qs = qq.executeQuery();
                        if (qs.next())
                        {
                            String question1 = qs.getString("Question1");
                            String question2 = qs.getString("Question2");
                            out.println("<!DOCTYPE html><html><head><title>Mace Felton - Security Questions</title><link rel='stylesheet' type='text/css' href='css/macefelton.css' /><script type='text/javascript' src='js/popup.js'></script></head><header>");
                            out.println("</header><body class ='grey'><nav><ul><li><a href = 'home'>Home</a></li><li><a href = 'about'>About Us</a></li><li><a href = 'products'>Music</a></li><li><a href ='events'>Events</a></li><li class ='leftlink'><a href = 'registerpage'>Register</a></li><li class = 'rightlink'><a href = 'loginpage'>Login</a></li></ul></nav>");
                            out.println("<div class = 'secure'><div class = 'secure_quest'><h3>Answer your security questions for password reset</h3><br><form action='reset' method='post'><fieldset><label>" + question1 + "</label><input type='text' name='a1'/><input type ='hidden' name ='userid' value = '" + userid + "'><br><label>" + question2 + "</label><input type='text' name='a2' class ='pass-reset'><br>");
                            out.println("</fieldset><br/><input type='submit' id='password_reset' value='Submit'/><input type='reset' id='reset' value='Cancel'/></div></div></form></body>");
                        }
                        else
                        {
                            out.println("<script language='javascript'>window.alert('You did not set up your security questions.  Email macefeltonhelp@gmail.com to get help resetting your password.');window.location='" + url + "';</script>");
                        }
                    } 
                    while (qu.next());
                }

                else
                {
                    out.println("<script language='javascript'>window.alert('There is no account with that username');window.location='" + url + "';</script>");
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
            out.println("<script language='javascript'>window.alert('You must fill out all fields');window.location='" + url + "';</script>");
          }
    }
    }