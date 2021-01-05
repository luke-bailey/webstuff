package mypkg;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
 
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.NoSuchProviderException;

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
 
public class ConfirmServlet extends HttpServlet {
    String DBURL = "database url connection";
    String PASSWORD_UPDATE = "UPDATE users SET Password = ?, Salt = ? WHERE UserID = ?"; 
 
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // reads input file from an absolute path
        PasswordConvert pass = new PasswordConvert();
        CreateSalt salt = new CreateSalt();
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String password = request.getParameter("password");
        String passagain = request.getParameter("passagain");
        String userid = request.getParameter("userid");
        boolean has_upper_case = !password.equals(password.toLowerCase());
    	boolean has_lower_case = !password.equals(password.toUpperCase());
    	String[] spec_characters = {"!", "?", "$", "#", "@", "%"};
    	String[] numbers = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
    	SearchString search = new SearchString();
    	boolean number_find = search.contains_char(password, numbers);
    	boolean find = search.contains_char(password, spec_characters);
      		if (!password.isEmpty() && !passagain.isEmpty()) 
       		{
       			Connection dbc = null;
        		Integer p_length = password.length();
   
      			if (p_length < 8 || p_length > 16)
      				{
        				out.println("<script language='javascript'>window.alert('Passwords must be between 8 and 16 characters');window.location='resetpage';</script>");
      				}
      			else if (find == false)
      				{
        				out.println("<script language='javascript'>window.alert('Passwords must contain a special character');window.location='resetpage';</script>");
        				System.out.println(find);
      				}
      			else if (number_find == false)
      				{
        				out.println("<script language='javascript'>window.alert('Passwords must contain a number');window.location='resetpage';</script>");
      				}
      			else if (!has_upper_case == true)
     				{
        				out.println("<script language='javascript'>window.alert('Passwords must contain an uppercase letter');window.location='resetpage';</script>");
      				}
      			else if (!has_lower_case == true)
      				{
        				out.println("<script language='javascript'>window.alert('Passwords must contain a lowercase letter');window.location='resetpage';</script>");
        			}
      			else if (password.equals(passagain) == false) 
      				{
        				out.println("<script language='javascript'>window.alert('Passwords do not match');window.location='resetpage';</script>");
      				} 
  	  			else {

            			try{

                			Class.forName("com.mysql.jdbc.Driver");
                			dbc = DriverManager.getConnection(DBURL, "username", "password");
                			byte[] my_salt = salt.getSalt();
                			String hash = pass.conversion(password, my_salt);
			                PreparedStatement pu = dbc.prepareStatement(PASSWORD_UPDATE);
			                pu.setString(1, hash);
			                pu.setBytes(2, my_salt);
			                pu.setString(3, userid);
			                int up = pu.executeUpdate();
			                pu.close();
			                if (up == 1)
				                {
				                    out.println("<script language='javascript'>window.alert('You have successfully updated your password');window.location='loginpage';</script>");
				                }

			                else
				                {
				                    out.println("<script language='javascript'>window.alert('We were not able to change your password at this time');window.location='resetpage';</script>");
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
					    }
        else 
          {
            out.println("<script language='javascript'>window.alert('You must fill out all fields');window.location='resetpage';</script>");
          }
    }
}