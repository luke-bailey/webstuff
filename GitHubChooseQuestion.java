package mypkg;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

 
public class ProfileServlet extends HttpServlet {  // JDK 6 and above only
 
String USERNAME_QUERY = "SELECT Username FROM users WHERE UserID = ?";
String DOWNLOAD_QUERY = "SELECT * FROM downloads WHERE UserID = ?";
String QUESTIONS_QUERY = "SELECT * FROM security_questions WHERE UserID = ?";
Integer userid;
String username;
String user_downloads;
String download_date;
String artist;
String album_art;
   // The doGet() runs once per HTTP GET request to this servlet.
   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
                     throws ServletException, IOException {
      // Set the MIME type for the response message
      response.setContentType("text/html");
      PrintWriter out = response.getWriter();
      HttpSession session = request.getSession(false);
      if (session == null)
      {
        response.sendRedirect(request.getContextPath() + "/home");
      }
      else
      {
      // Get a output writer to write the response message into the network socket
      Connection conn = null;
      Statement stmt = null;
      try {
         // Step 1: Create a database "Connection" object
         // For MySQL
         Class.forName("com.mysql.jdbc.Driver");  // Needed for JDK9/Tomcat9
         conn = DriverManager.getConnection(
            "databaseconnection, username, password");  // <<== Check
 
         // Step 2: Create a "Statement" object inside the "Connection"
         userid = (Integer) session.getAttribute("uid");
         PreparedStatement st = conn.prepareStatement(USERNAME_QUERY);
         st.setInt(1, userid);
         ResultSet rs = st.executeQuery();
         while(rs.next()) 
         {
            username = rs.getString("Username");
         }
         out.println("<!DOCTYPE html><html><head><title>Mace Felton - Profile</title><link rel='stylesheet' type='text/css' href='css/macefelton.css' /></head>");
         out.println("<header></header><body class='grey'>");
         out.println("<nav><ul><li><a href = 'home'>Home</a></li><li><a href = 'about'>About Us</a></li><li><a href = 'products'>Music</a></li><li><a href ='events'>Events</a></li><li class ='dropdown'><a class ='active' href ='javascript:void(0)' class = 'dropbtn'>Hello, " + username + " </a><div class ='dropdown-content'><a href ='profile'>My Profile</a><form method ='post' action ='logout'><a href = 'logout'>Logout</a></form></div></li></ul></nav>");
         PreparedStatement sq = conn.prepareStatement(QUESTIONS_QUERY);
         sq.setInt(1, userid);
         ResultSet qs = sq.executeQuery();
         if (!qs.next())
         {
            out.println("<div class='data-list-input'><h2 class = 'security'>Please create your security questions for password reset</h2>");
            out.println("<div class = 'questions_body'><form method = 'post' action = 'question'><h2 class = 'security_questions'>Choose your first Security Question</h2><br><h3>Question 1:</h3><select class='data-list-input' name = 'question1'><option value='What is your favorite book?'>What is your favorite book?</option><option value='What is the name of the road you grew up on?'>What is the name of the road you grew up on?</option>");
            out.println("<option value='What is your mother's maiden name?'>What is your mother's maiden name?</option><option value='What was the name of your first pet?'>What was the name of your first pet?</option><option value='What was the first company you worked for?'>What was the first company you worked for?</option>");
            out.println("</select><h3>Answer:</h3><input type = 'text' name = 'answer1' class = 'secure_question'><br>");
            out.println("<h2 class = 'security_questions'>Choose your second Security Question</h2><br><h3>Question 2:</h3><select class='data-list-input' name = 'question2'><option value='Where did you meet your spouse?'>Where did you meet your spouse?</option><option value='Where did you go to high school/college?'>Where did you go to high school/college?</option><option value='What is your favorite food?'>What is your favorite food?</option>");
            out.println("<option value='What city were you born in?'>What city were you born in?</option><option value='Where is your favorite place to vacation?'>Where is your favorite place to vacation?</option></select><h3>Answer:</h3><input type = 'text' name = 'answer2' class = 'secure_question'></div><input type = 'submit' value='Submit'/></div></form>");
         }

         else
         {
            System.out.println("Hello!");
         }

         out.println("<h1 class='top'>Download History</h1>");
         out.println("<div class ='downloads'><div class ='table_container'><table><tr class='rowcolor'><th class ='cellleft'>Song</div></td><th class ='cellcenter'>Artist</td><th class = 'cellright'>Download Date</td></tr></table></div>");
         PreparedStatement downloads = conn.prepareStatement(DOWNLOAD_QUERY);
         downloads.setInt(1, userid);
         ResultSet downloads_rs = downloads.executeQuery();
         if (downloads_rs.next())
         {  
            do
            {
               artist = downloads_rs.getString("Artist");
               user_downloads = downloads_rs.getString("SongName");
               download_date = downloads_rs.getString("DownloadDate");
               album_art = downloads_rs.getString("AlbumArt");
               out.println("<div class ='table_container'><table><tr class ='usersong'><td class ='cellleft'><img class = 'art' src='" + album_art + "'>&nbsp" + user_downloads + "</td><td class ='cellcenter'>" + artist + "</td><td class = 'cellright'>" + download_date + "</td></tr></table></div>");
            }         
         while (downloads_rs.next());
         }
         else
         {
            out.println("Your downloads will appear here, so go and download some music!");
         }
            

         out.println("</div>");
         
// Retrieve the album' id. Can order more than one album.
         
      
         out.println("</body><footer><a href = 'https://www.facebook.com/TheBusiness.Musics/' target = 'blank'><img src = 'img/facebook3.png'></a>&nbsp&nbsp<a href = 'https://soundcloud.com/luke-bailey-40' target = 'blank'><img src = 'img/soundcloud8.png'></a>&nbsp&nbsp<a href = 'https://www.instagram.com/lukayluke/' target = 'blank'><img src = 'img/instagram3.png'></a>&nbsp&nbsp<a href = 'https://twitter.com/pubstarraves' target = 'blank'><img src = 'img/twitter3.png'></a></footer></html>");
      
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
}