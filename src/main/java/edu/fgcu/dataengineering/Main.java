package edu.fgcu.dataengineering;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Main {

  public static void main(String[] args) throws IOException, CsvValidationException {

    // Reading the CSV file and printing to the console //
    CsvParser csvParser = new CsvParser("src/Data/bookstore_report2.csv");
    System.out.println("Reading the CSV file and printing to the console:");
    csvParser.printCsv();
    System.out.println();

    // Reading the JSON file and printing to the console //
    Gson gson = new Gson();
    JsonReader jsonReader = new JsonReader(new FileReader("src/Data/authors.json"));
    AuthorParser[] authors = gson.fromJson(jsonReader, AuthorParser[].class);
    System.out.println("Reading the JSON file and printing to the console:");

    for (var element : authors) {
      System.out.println(
          "Name: "
              + element.getName()
              + ", Email: "
              + element.getEmail()
              + ", URL: "
              + element.getUrl());
    }
    System.out.println();

    // JSoup extra credit //
    // JSoup is going to the SEOExample file on your Github and scraping the data //
    // Reading the HTML page and printing the data to the console //
    try {

      final Document document =
          Jsoup.connect(
                  "https://github.com/jsgreenwell/CsvToDatabase/blob/master/src/Data/SEOExample.csv")
              .get();

      System.out.println("Reading the HTML page and printing the data to the console with JSoup:");
      for (Element row : document.select("table.js-csv-data.csv-data.js-file-line-container tr")) {
        if (row.select("td:nth-of-type(2)").text().equals("")) {
          continue;
        } else {
          final String name = row.select("td:nth-of-type(2)").text();
          final String date = row.select("td:nth-of-type(3)").text();
          final String entryPoint = row.select("td:nth-of-type(4)").text();
          final String region = row.select("td:nth-of-type(5)").text();
          final String location = row.select("td:nth-of-type(6)").text();
          final String accepted = row.select("td:nth-of-type(7)").text();
          final String enrolled = row.select("td:nth-of-type(8)").text();
          final String searchTerms = row.select("td:nth-of-type(9)").text();
          final String engine = row.select("td:nth-of-type(10)").text();
          final String reason = row.select("td:nth-of-type(11)").text();
          System.out.println(
              name
                  + ", "
                  + date
                  + ", "
                  + entryPoint
                  + ", "
                  + region
                  + ", "
                  + location
                  + ", "
                  + accepted
                  + ", "
                  + enrolled
                  + ", "
                  + searchTerms
                  + ", "
                  + engine
                  + ", "
                  + reason);
        }
      }
      System.out.println();
    } catch (Exception e) {
      e.printStackTrace();
    }

    // Reading the CSV file and inserting the data into the database //
    // There are errors because the table columns do not match what is being passed //
    // The table requires a year and a price but the CSV file does not provide those //
    // I cannot pass Strings for integers and cannot parse Strings into Integers //
    try {
      System.out.println("Inserting CSV data:");
      Connection connection = DriverManager.getConnection("jdbc:sqlite:src/Data/BookStore.db");
      String sql =
          "INSERT INTO book (isbn, publisher_name, author_name, book_year, book_title, book_price) VALUES (?, ?, ?, ?, ?, ?)";
      PreparedStatement preparedStatement = connection.prepareStatement(sql);

      BufferedReader lineReader =
          new BufferedReader(new FileReader("src/Data/bookstore_report2.csv"));
      String lineText;

      lineReader.readLine();

      while ((lineText = lineReader.readLine()) != null) {
        String[] data = lineText.split(",");
        String isbn = data[0];
        String publisherName = data[1];
        String authorName = data[2];
        String bookYear = data[3];
        String bookTitle = data[4];
        String bookPrice = data[5];

        int price = Integer.parseInt(bookPrice);
        int year = Integer.parseInt(bookYear);

        preparedStatement.setString(1, isbn);
        preparedStatement.setString(2, publisherName);
        preparedStatement.setString(3, authorName);
        preparedStatement.setInt(4, year);
        preparedStatement.setString(5, bookTitle);
        preparedStatement.setInt(6, price);
      }

      lineReader.close();
      connection.close();

      System.out.println();
    } catch (Exception e) {
      e.printStackTrace();
    }

    // Reading the JSON file and inserting the data into the database //
    // Errors are being thrown for UNIQUE constraint failed for author URL //
    System.out.println("Inserting JSON data:");
    try {
      Connection connection = DriverManager.getConnection("jdbc:sqlite:src/Data/BookStore.db");
      PreparedStatement preparedStatement =
          connection.prepareStatement("INSERT INTO author VALUES (?, ?, ?)");
      for (var object : authors) {
        String name = object.getName();
        String email = object.getEmail();
        String url = object.getUrl();
        preparedStatement.setString(1, name);
        preparedStatement.setString(2, email);
        preparedStatement.setString(3, url);
        preparedStatement.executeUpdate();
      }
      for (var element : authors) {
        System.out.println(element.getName());
      }
      connection.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
