package edu.fgcu.dataengineering;

public class AuthorParser {
  private String author_name;
  private String author_email;
  private String author_url;

  public AuthorParser(String author_name, String author_email, String author_url) {
    this.author_name = author_name;
    this.author_email = author_email;
    this.author_url = author_url;
  }

  protected String getName() {
    return author_name;
  }

  protected String getEmail() {
    return author_email;
  }

  protected String getUrl() {
    return author_url;
  }
}
