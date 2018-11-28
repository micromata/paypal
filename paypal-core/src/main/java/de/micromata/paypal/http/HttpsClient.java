package de.micromata.paypal.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpsClient
{
  private static Logger log = LoggerFactory.getLogger(HttpsClient.class);

  private String url;
  private boolean post = false;

  public HttpsClient(String url, boolean post)
  {
    this.url = url;
    this.post = post;

  }

  public HttpURLConnection getConnection() throws IOException
  {
    URL turl = new URL(url);
    HttpsURLConnection conn = (HttpsURLConnection) turl.openConnection();
    conn.setRequestMethod(post ? "POST" : "GET");
    conn.setDefaultUseCaches(false);
    conn.setRequestProperty("keep-alive", "true");
    conn.setRequestProperty("Content-Type", "text/json");

    return conn;
  }

  public String send(String body) throws IOException
  {
    HttpURLConnection conn = getConnection();
    if (post == true) {
      byte[] data = body.getBytes("UTF-8");

      conn.setRequestProperty("Content-Length", String.valueOf(data.length));

      conn.setDoOutput(true);
      try (OutputStream os = conn.getOutputStream()) {
        os.write(data);
      }
    }
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
      StringBuilder builder = new StringBuilder();
      for (String line = null; (line = reader.readLine()) != null;) {
        builder.append(line).append("\n");
      }
      // not disconnect, leave it to HttpURLConnection
      //conn.disconnect();
      return builder.toString();

    }
  }
}
