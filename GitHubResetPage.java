package mypkg;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
 
public class ResetPageServlet extends HttpServlet {  // JDK 6 and above only

String username;
String cookie_user = null;
   // The doGet() runs once per HTTP GET request to this servlet.
   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
                     throws ServletException, IOException {
      // Set the MIME type for the response message
      response.setContentType("text/html");
      PrintWriter out = response.getWriter();
      //DownloadFileFromInternet my_test = new DownloadFileFromInternet();
      // Get a output writer to write the response message into the network socket
      Connection conn = null;
      Statement stmt = null;
      try {
            // Step 1: Create a database "Connection" object
            // For MySQL
            Class.forName("com.mysql.jdbc.Driver");  // Needed for JDK9/Tomcat9
            conn = DriverManager.getConnection(
               "database url", "username", "password");  // <<== Check
    
            // Step 2: Create a "Statement" object inside the "Connection"
            stmt = conn.createStatement();
            Cookie cookie = null;
            Cookie[] cookies = null;
            cookies = request.getCookies();
               if (cookies != null)
               {  
                  System.out.println("We found cookies");
                  for (int i = 0; i < cookies.length; i++)
                  {
                     cookie = cookies[i];

                     if ((cookie.getName()).compareTo("user_reset") == 0)
                     {  
                        cookie_user = cookie.getValue();
                        System.out.println(cookie_user);
                        System.out.println(cookie.getName());
                        System.out.println(cookie.getValue());
                     }
                  }
                  if (cookie_user != null)
                  {
                     out.println("<!DOCTYPE html><html><head><title>Mace Felton - Reset</title><link rel='stylesheet' type='text/css' href='css/macefelton.css' /><script type='text/javascript' src='js/popup.js'></script>");
                     out.println("</head><header></header><body class ='grey'><nav><ul><li><a href = 'home'>Home</a></li><li><a href = 'about'>About Us</a></li><li><a href = 'products'>Music</a></li><li><a href ='events'>Events</a></li><li class ='leftlink'><a href = 'registerpage'>Register</a></li><li class = 'rightlink'><a href = 'loginpage'>Login</a></li></ul></nav>");
                     out.println("<div class = 'parentlogin'><div class = 'login'><h1>Password Reset</h1><br><form action='securityquestion' method='post'><fieldset><label for='u'>What is your Username?</label><input type='text' id='u' name='username' class ='pass-reset' value ='" + cookie_user + "'><br>");
                     out.println("<input type='submit' id='login' value='Submit'/><input type='reset' id='reset' value='Cancel'/></div></div></form>");
                     out.println("</body><footer><a href = 'https://www.facebook.com/TheBusiness.Musics/' target = 'blank'><img src = 'img/facebook3.png'></a>&nbsp&nbsp<a href = 'https://soundcloud.com/luke-bailey-40' target = 'blank'><img src = 'img/soundcloud8.png'></a>&nbsp&nbsp<a href = 'https://www.instagram.com/lukayluke/' target = 'blank'><img src = 'img/instagram3.png'></a>&nbsp&nbsp<a href = 'https://twitter.com/pubstarraves' target = 'blank'><img src = 'img/twitter3.png'></a></footer></html>");
                  }
                  else 
                  {
                     out.println("<!DOCTYPE html><html><head><title>Mace Felton - Reset</title><link rel='stylesheet' type='text/css' href='css/macefelton.css' /><script type='text/javascript' src='js/popup.js'></script>");
                     out.println("</head><header></header><body class ='grey'><nav><ul><li><a href = 'home'>Home</a></li><li><a href = 'about'>About Us</a></li><li><a href = 'products'>Music</a></li><li><a href ='events'>Events</a></li><li class ='leftlink'><a href = 'registerpage'>Register</a></li><li class = 'rightlink'><a href = 'loginpage'>Login</a></li></ul></nav>");
                     out.println("<div class = 'parentlogin'><div class = 'login'><h1>Password Reset</h1><br><form action='securityquestion' method='post'><fieldset><label for='u'>What is your Username?</label><input type='text' id='u' name='username' class ='pass-reset'><br>");
                     out.println("<input type='submit' id='login' value='Submit'/><input type='reset' id='reset' value='Cancel'/></div></div></form>");
                     out.println("</body><footer><a href = 'https://www.facebook.com/TheBusiness.Musics/' target = 'blank'><img src = 'img/facebook3.png'></a>&nbsp&nbsp<a href = 'https://soundcloud.com/luke-bailey-40' target = 'blank'><img src = 'img/soundcloud8.png'></a>&nbsp&nbsp<a href = 'https://www.instagram.com/lukayluke/' target = 'blank'><img src = 'img/instagram3.png'></a>&nbsp&nbsp<a href = 'https://twitter.com/pubstarraves' target = 'blank'><img src = 'img/twitter3.png'></a></footer></html>");
                  }
               }
               
               else 
               {
                  out.println("<!DOCTYPE html><html><head><title>Mace Felton - Reset</title><link rel='stylesheet' type='text/css' href='css/macefelton.css' /><script type='text/javascript' src='js/popup.js'></script>");
                  out.println("</head><header></header><body class ='grey'><nav><ul><li><a href = 'home'>Home</a></li><li><a href = 'about'>About Us</a></li><li><a href = 'products'>Music</a></li><li><a href ='events'>Events</a></li><li class ='leftlink'><a href = 'registerpage'>Register</a></li><li class = 'rightlink'><a href = 'loginpage'>Login</a></li></ul></nav>");
                  out.println("<div class = 'parentlogin'><div class = 'login'><h1>Password Reset</h1><br><form action='securityquestion' method='post'><fieldset><label for='u'>What is your Username?</label><input type='text' id='u' name='username' class ='pass-reset'><br>");
                  out.println("<input type='submit' id='login' value='Submit'/><input type='reset' id='reset' value='Cancel'/></div></div></form>");
                  out.println("</body><footer><a href = 'https://www.facebook.com/TheBusiness.Musics/' target = 'blank'><img src = 'img/facebook3.png'></a>&nbsp&nbsp<a href = 'https://soundcloud.com/luke-bailey-40' target = 'blank'><img src = 'img/soundcloud8.png'></a>&nbsp&nbsp<a href = 'https://www.instagram.com/lukayluke/' target = 'blank'><img src = 'img/instagram3.png'></a>&nbsp&nbsp<a href = 'https://twitter.com/pubstarraves' target = 'blank'><img src = 'img/twitter3.png'></a></footer></html>");
               }
            
               



         } catch (SQLException ex) {
                  ex.printStackTrace();
              } catch (ClassNotFoundException ex) {
                 ex.printStackTrace();
              } finally {
                  out.close();
                  try {
                     // Step 5: Close the Statement and Connection
                     if (stmt != null) stmt.close();
                     if (conn != null) conn.close();
                  } catch (SQLException ex) {
                     ex.printStackTrace();
                  }
               }
            }
         }
