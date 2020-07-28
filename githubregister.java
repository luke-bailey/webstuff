package mypkg;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.NoSuchProviderException;
import java.util.Scanner;

//this class creates the salted and hashed password
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
//this class creates the salt used to add on to the hashed password
class CreateSalt {
      private static final SecureRandom r = new SecureRandom();
      public static byte[] getSalt()
    { 
        //Always use a SecureRandom generator
        //Create array for salt
        byte[] salt = new byte[16];
        //Get a random salt
        r.nextBytes(salt);
        //return salt
        return salt;
    }
  } 
//this class searches the input string for ojects that must be in the password and username as well as characters that are not allowed to be used
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

//This is where I define my queries for my database
public class RegisterServlet extends HttpServlet {
  String DBURL = //this is where you would put your database url;
  String REGISTER_QUERY =
    "SELECT UserID FROM users WHERE Username = ?";
  //this query is a prepared statement to prevent SQL injection attacks
  String UPDATE_QUERY =
    "INSERT INTO users (UserID, Username, Password, Firstname, Lastname, Salt)" +
    "VALUES (?,?,?,?,?,?)";
  String COUNT_ROWS = 
    "SELECT UserID FROM users";
 
  public void doPost(HttpServletRequest request, HttpServletResponse response)
  throws IOException, ServletException {
    //initialize my classes right here
    PasswordConvert pass = new PasswordConvert();
    CreateSalt my_salt = new CreateSalt();
    SearchString search = new SearchString();
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    Integer count = 0;
    //these variables are grabbing the text input from the register page
    String firstname = request.getParameter("firstname");
    String lastname = request.getParameter("lastname");
    String username = request.getParameter("username");
    String password = request.getParameter("password");
    String passagain = request.getParameter("passagain");
    //this array defines all of the characters that aren't allowed to be used in the username, first, and last name
    String[] space = {" ", "[", "]", "(", ")", "=", ",", "/", "?", "@", ":", ";", "\"", "\\", "{", "}", ">", "<"};
    boolean find_space = search.contains_char(username, space);
    boolean first_space = search.contains_char(firstname, space);
    boolean last_space = search.contains_char(lastname, space);

    //this section creates cookies so users don't have to retype out their information if their input doesn't meet requirements
    if (username != null)
      { 
        if (find_space == false)
        {
         Cookie userCookie = new Cookie("register_username", username);
         userCookie.setMaxAge(60);
         response.addCookie(userCookie);
        }
        else
        {
          out.println("<script language='javascript'>window.alert('You can not include a space or special character in your name');window.location='registerpage';</script>");
        }  
      }
    if (firstname != null)
    { 
      if (first_space == false)
      { 
         Cookie firstCookie = new Cookie("first_cookie", firstname);
         firstCookie.setMaxAge(60);
         response.addCookie(firstCookie);
      }
      else
        {
          out.println("<script language='javascript'>window.alert('You can not include a space or special character in your name');window.location='registerpage';</script>");
        }      
    }
    if (lastname != null)
    { 
      if (last_space == false)
      {
         Cookie lastCookie = new Cookie("last_cookie", lastname);
         lastCookie.setMaxAge(60);
         response.addCookie(lastCookie);
      }
      else
        {
          out.println("<script language='javascript'>window.alert('You can not include a space or special character in your name');window.location='registerpage';</script>");
        }     
    }
    //these next two booleans check to see if the text converts any of the characters to lower case or upper case, and if it doesn't convert anything then it returns true 
    boolean has_upper_case = !password.equals(password.toLowerCase());
    boolean has_lower_case = !password.equals(password.toUpperCase());
    //these next two arrays are all of the characters we're looking for in a secure password
    String[] spec_characters = {"!", "?", "$", "#", "@", "%"};
    String[] numbers = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
    boolean number_find = search.contains_char(password, numbers);
    boolean find = search.contains_char(password, spec_characters);
    //we must ensure that all fields are filled in before we move on to the other checks
    if (!firstname.isEmpty() && !lastname.isEmpty() && !username.isEmpty() && !password.isEmpty() && !passagain.isEmpty()) {
      int u_length = username.length();
      int p_length = password.length();
      int pa_length = passagain.length();
      int fn_length = firstname.length();
      int ln_length = lastname.length();
      Connection dbc = null;
      //the rest of these checks should be pretty self explanatory if you read through the text after the if statements
      if (u_length > 15) {
        out.println("<script language='javascript'>window.alert('Username is too long, choose a username with a max of 15 characters');window.location='registerpage';</script>");
      }
      else if (find_space == true)
      {
        out.println("<script language='javascript'>window.alert('Usernames cannot contain a space or special characters');window.location='registerpage';</script>");
      }
      else if (p_length < 8 || p_length > 16)
      {
        out.println("<script language='javascript'>window.alert('Passwords must be between 8 and 16 characters');window.location='registerpage';</script>");
      }
      else if (fn_length > 15 || ln_length >15)
      {
        out.println("<script language='javascript'>window.alert('First and Last name must be less than 15 characters');window.location='registerpage';</script>");
      }
      else if (find == false)
      {
        out.println("<script language='javascript'>window.alert('Passwords must contain a special character');window.location='registerpage';</script>");
        System.out.println(find);
      }
      else if (number_find == false)
      {
        out.println("<script language='javascript'>window.alert('Passwords must contain a number');window.location='registerpage';</script>");
      }
      else if (!has_upper_case == true)
      {
        out.println("<script language='javascript'>window.alert('Passwords must contain an uppercase letter');window.location='registerpage';</script>");
      }
      else if (!has_lower_case == true)
      {
        out.println("<script language='javascript'>window.alert('Passwords must contain a lowercase letter');window.location='registerpage';</script>");
      }
      else if (password.equals(passagain) == false) {
        out.println("<script language='javascript'>window.alert('Passwords do not match');window.location='registerpage';</script>");
      } else {
      //if all of the checks are passed then we can finally move on to inserting the information into the database
          try {
        //create the database connection here
        Class.forName("com.mysql.jdbc.Driver");
        dbc = DriverManager.getConnection(DBURL, "username", "password");//This is where you would put your own username and password
        //we check to make sure the username isn't already taken here
        PreparedStatement st = dbc.prepareStatement(REGISTER_QUERY);
        st.setString(1, username);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
          out.println("<script language='javascript'>window.alert('This username has already been taken, please choose another');window.location='registerpage';</script>");
        } else {
          //if everything else checks out we create a prepared statement to insert the information for the new account
          Statement counter = dbc.createStatement();
          ResultSet rset = counter.executeQuery(COUNT_ROWS);
          PreparedStatement update = dbc.prepareStatement(UPDATE_QUERY);
          if (!rset.next()) {
            System.out.println("There are no results.");
          } else {
              do {
                count++;
              } while (rset.next());
            }
          Integer id_count = count + 1;
          //create the salt and hashed password here
          //we have to save the salt value in the database as well because a random one is created everytime we try to create a password
          byte[] salty = my_salt.getSalt();
          String hash = pass.conversion(password, salty);
          update.setInt(1, id_count);
          update.setString(2, username);
          update.setString(3, hash);
          update.setString(4, firstname);
          update.setString(5, lastname);
          update.setBytes(6, salty);
          int result = update.executeUpdate();
          update.close();
          if (result == 1) {
            out.println("<script language='javascript'>window.alert('You have been registered! Go to login page to log in.');window.location='home';</script>");
          } 
          else
          {
            out.println("<script language='javascript'>window.alert('We were not able to register you at this time.');window.location='home';</script>");
          }  
        } 
      } catch (ClassNotFoundException nfe) {
      } catch (SQLException se) {
      } finally {
        //always close the database connection when you're done making changes
        if (dbc != null)
          try { dbc.close(); }
          catch (SQLException ignored) { }
        } 
      }
    } else {
      out.println("<script language='javascript'>window.alert('All fields required');window.location='registerpage';</script>"); 
    }
  }
}