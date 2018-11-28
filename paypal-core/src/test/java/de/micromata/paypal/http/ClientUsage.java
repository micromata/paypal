package de.micromata.paypal.http;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ClientUsage
{
  private String url = "https://www.google.de";
  int loops = 100;
  HttpsClient client = new HttpsClient(url, false);

  static {
    MySSLSocketTestFactory.init();
  }

  private HttpsClient getClient()
  {
    // either create client every call, or just reuse it.
    // seem to be irrelevant.
    return client;
    //    Client c = new Client(url, false);
    //    return c;
  }

  @Test
  public void singleThreaded() throws IOException
  {
    MySSLSocketTestFactory.resetSocketCreated();
    long start = System.currentTimeMillis();
    for (int i = 0; i < loops; ++i) {
      String res = getClient().send("PING");
    }
    System.out.println("Non-Threaded: " + (System.currentTimeMillis() - start));
    System.out.println("Sockets: " + MySSLSocketTestFactory.getSocketCreated());
  }

  @Test
  public void multiThreaded() throws IOException
  {

    long start = System.currentTimeMillis();

    List<Thread> tl = new ArrayList<>();
    for (int i = 0; i < loops; ++i) {
      Thread t = new Thread()
      {
        @Override
        public void run()
        {
          try {
            String res = getClient().send("PING");
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      };
      tl.add(t);
      t.start();
    }
    try {
      for (Thread t : tl) {
        t.join();
      }
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }
    System.out.println("Threaded: " + (System.currentTimeMillis() - start));
    System.out.println("Sockets: " + MySSLSocketTestFactory.getSocketCreated());
  }

  @Test
  public void multiThreadedSequenced() throws IOException
  {

    long start = System.currentTimeMillis();

    try {
      for (int i = 0; i < loops; ++i) {
        Thread t = new Thread()
        {
          @Override
          public void run()
          {
            try {
              String res = getClient().send("PING");
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        };
        t.start();
        t.join();
      }
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }
    System.out.println("Sequenced Threaded: " + (System.currentTimeMillis() - start));
    System.out.println("Sockets: " + MySSLSocketTestFactory.getSocketCreated());
  }

  public void pooled() throws IOException
  {
    ExecutorService executor = Executors.newFixedThreadPool(10);
    long start = System.currentTimeMillis();
    List<Future<String>> taskList = new ArrayList<>();
    for (int i = 0; i < loops; ++i) {
      taskList.add(executor.submit(() -> getClient().send("PING")));
    }
    try {
      for (Future<String> f : taskList) {
        f.get();
      }
    } catch (ExecutionException | InterruptedException ex) {
      ex.fillInStackTrace();
    }
    executor.shutdown();
    System.out.println("Pooled: " + (System.currentTimeMillis() - start));
    System.out.println("Sockets: " + MySSLSocketTestFactory.getSocketCreated());
  }
}
