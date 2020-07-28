package mypkg;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.security.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.NoSuchProviderException;
import java.util.Scanner;

//we need to have this class on the login servlet as well to recreate the salted and hashed password from the register servlet
class PasswordConvert{
    
      public String conversion(String password, byte[] salt) {
        String passwordToHash = password;
        String generatedPassword = null;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Add password bytes to digest
            md.update(salt);
            //Get the hash's bytes
            byte[] bytes = md.digest(passwordToHash.getBytes());
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return generatedPassword;
      }
    }

    //this grabs the unique salt value from the database to recreate the password
    class FindSalt {

      String DBURL = //this is where your database connection goes;
      Connection dbc = null;
      String usernameToCheck = null;
      String SALT_QUERY =
        "SELECT Salt FROM users WHERE Username = ?";
      public byte[] getSalt(String username) {
        byte[] salt_result = null;
      try {
        usernameToCheck = username;
        Class.forName("com.mysql.jdbc.Driver");
        dbc = DriverManager.getConnection(DBURL, "username", "password");//type in your own username + password here
        PreparedStatement salty = dbc.prepareStatement(SALT_QUERY);
        salty.setString(1, usernameToCheck);
        ResultSet salt_rs = salty.executeQuery();
        while (salt_rs.next()) {
          salt_result = salt_rs.getBytes("Salt");
        }
      }
        catch (ClassNotFoundException nfe) {
      } catch (SQLException se) {
      }finally {
        if (dbc != null)
          try { dbc.close(); }
          catch (SQLException ignored) { }
      } return salt_result;
    }
}

//same class as register servlet to prevent banned characters for cookies
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

public class LoginServlet extends HttpServlet {
  String DBURL = ; //your database url again
  String LOGIN_QUERY =
    "SELECT UserID FROM users WHERE Username = ? and Password = ?";
  String QUESTIONS_QUERY = "SELECT * FROM security_questions WHERE UserID = ?"; 
  String USERNAME_QUERY = "SELECT Username FROM users WHERE Username = ?";

  

  /*public void doGet(HttpServletRequest request, HttpServletResponse response)
  throws IOException, ServletException {
    request.getRequestDispatcher("/login.html").forward(request, response);
  }*/

  public void doPost(HttpServletRequest request, HttpServletResponse response)
  throws IOException, ServletException {
    byte[] test = null;
    FindSalt my_salt = new FindSalt();
    PasswordConvert pass = new PasswordConvert();
    SearchString search = new SearchString();
    //this is getting the input from the login page
    String username = request.getParameter("u");
    String password = request.getParameter("p");
    PrintWriter out = response.getWriter();
    String[] space = {" ", "[", "]", "(", ")", "=", ",", "/", "?", "@", ":", ";", "\"", "\\", "{", "}", ">", "<"};
    boolean find_space = search.contains_char(username, space);
    if (username != null)
    {
      if (find_space == false)
            {
               Cookie userCookie = new Cookie("username", username);
               userCookie.setMaxAge(60);
               response.addCookie(userCookie);
               System.out.println(userCookie.getValue());
            }
      else
      {
        out.println("<script language='javascript'>window.alert('You included a space or a special character in your username');window.location='loginpage';</script>");
      }
    }
    /* login checking from the database */
    //again ensure that all fields are filled out
    if (!username.isEmpty() && !password.isEmpty()) {
      Connection dbc = null;
      try {
            Class.forName("com.mysql.jdbc.Driver");
            dbc = DriverManager.getConnection(DBURL, "username", "password"); //type in your own password and username
            //this next section checks to see if the username exists in the database, and if it does grabs the salt value from the database
            PreparedStatement username_check = dbc.prepareStatement(USERNAME_QUERY);
            username_check.setString(1, username);
            ResultSet user_result = username_check.executeQuery();
            if (user_result.next()) 
            { 
              //if we get a result from the username, we move on to the password check
              byte[] my_salt_test = my_salt.getSalt(username);
              PreparedStatement st = dbc.prepareStatement(LOGIN_QUERY);
              String hash = pass.conversion(password, my_salt_test);
              st.setString(1, username);
              st.setString(2, hash);
              ResultSet rs = st.executeQuery();
              //if everything checks out with the password and username combo, we get the userid to create a session
              if (rs.next()) {
                int uid = rs.getInt(1);
                if (!rs.next()) {
                  HttpSession session = request.getSession();
                  session.setAttribute("u", username);
                  session.setAttribute("uid", uid);
                  //this next section is for the password reset system
                  PreparedStatement sq = dbc.prepareStatement(QUESTIONS_QUERY);
                  sq.setInt(1, uid);
                  ResultSet qs = sq.executeQuery();
                  if (!qs.next())
                  {
                    out.println("<script language='javascript'>window.alert('Login Successful, log into your profile page to set your security questions!');window.location='home';</script>");
                  }
                  else
                  {
                    out.println("<script language='javascript'>window.alert('Login Successful');window.location='home';</script>");
                  }
                  return;
                }
              }
              else
              {
                out.println("<script language='javascript'>window.alert('Username/Password combination does not match our records');window.location='loginpage';</script>");
              }
           }
        else
         {
          out.println("<script language='javascript'>window.alert('That Username does not match our records');window.location='loginpage';</script>");
         }
      } catch (ClassNotFoundException nfe) {
      } catch (SQLException se) {
      } finally {
        if (dbc != null)
          try { dbc.close(); }
          catch (SQLException ignored) { }
      }
    }
    else {
      out.println("<script language='javascript'>window.alert('All fields must be filled in');window.location='loginpage';</script>");
    }
  }
}
