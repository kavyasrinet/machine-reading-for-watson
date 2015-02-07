package edu.cmu.lti.oaqa.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;

public class StdOuLogProxy {
  private static final Logger logger = LoggerFactory.getLogger(StdOuLogProxy.class);

  static final PrintStream STD_OUT = System.out;

  public static void redirectSystemOutToLog() {
    System.setOut(createLoggingProxy(System.out));
  }

  public static void restoreSystemOutRedirect() {
    System.setOut(STD_OUT);
  }

  public static PrintStream createLoggingProxy(final PrintStream realPrintStream) {
    return new PrintStream(realPrintStream) {
      public void print(final String string) {
        logger.trace(string);
      }
    };
  }
}
