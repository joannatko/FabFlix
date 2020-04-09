import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

//Declare WebServlet
@WebServlet(name = "MoviesServlet", urlPatterns = "/movies")
public class MoviesServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;

  // Create a dataSource which registered in web.xml
  @Resource(name = "jdbc/moviedb")
  private DataSource dataSource;

  protected void query(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setContentType("application/json"); // Response mime type

    // Output stream to STDOUT
    PrintWriter out = response.getWriter();

    try {
      // Get a connection from dataSource
      Connection connection = dataSource.getConnection();

      // Declare our statement
      Statement statement = connection.createStatement();

      String query = "SELECT  title, year, director FROM movies";

      // Perform the query
      ResultSet resultSet = statement.executeQuery(query);

      JsonArray jsonArray = new JsonArray();

      // Iterate through each row of resultSet
      while (resultSet.next()) {
        String movie_title = resultSet.getString("title");
        String movie_year = resultSet.getString("year");
        String movie_director = resultSet.getString("director");
        //TODO: add first 3 genres, first 3 stars, rating

        // Create a JsonObject based on the data we retrieve from resultSet
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("movie_title", movie_title);
        jsonObject.addProperty("movie_year", movie_year);
        jsonObject.addProperty("movie_director", movie_director);

        jsonArray.add(jsonObject);
      }

      // write JSON string to output
      out.write(jsonArray.toString());
      // set response status to 200 (OK)
      response.setStatus(200);

      resultSet.close();
      statement.close();
      connection.close();
    } catch (Exception e) {

      // write error message JSON object to output
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("errorMessage", e.getMessage());
      out.write(jsonObject.toString());

      // set reponse status to 500 (Internal Server Error)
      response.setStatus(500);

    }
    out.close();

  }
}