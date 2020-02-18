package edu.fgcu.dataengineering;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class CsvParser {

  private ArrayList<String[]> fileRows = new ArrayList<>();

  public CsvParser(String csvFile) throws IOException, CsvValidationException {

    if (checkFile(csvFile)) {
      readCsv(csvFile);
    }
  }

  protected void readCsv(String csvFile) throws IOException, CsvValidationException {

    FileInputStream fileInputStream = new FileInputStream(csvFile);
    InputStreamReader inputStreamReader =
        new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
    CSVReader csvReader = new CSVReader(inputStreamReader);

    String[] nextLine;
    while ((nextLine = csvReader.readNext()) != null) {
      fileRows.add(nextLine);
    }

    csvReader.close();
  }

  protected void writeCsv(String csvFile) {}

  protected void printCsv() {

    for (Object row : fileRows) {

      for (String fields : (String[]) row) {
        System.out.print(fields + ", ");
      }
      System.out.println();
    }
  }

  private boolean checkFile(String csvFile) {

    if (!Files.exists(Paths.get(csvFile))) {
      System.out.println("File does not exist");
      return false;
    }
    return true;
  }
}
