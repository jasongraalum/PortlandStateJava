package edu.pdx.cs410J.examples;

import java.io.*;

/**
 * This program reads <code>double</code>s from the command line and
 * writes them to a file.  It demonstrates the
 * <code>FileOutputStream</code> and <code>DataOutputStream</code>
 * classes.
 *
 * <P align="center"><EM><A href =
 * "{@docRoot}/../src/edu/pdx/cs410J/examples/WriteDate.java">
 * View Source</A></EM></P>
 */
public class WriteDoubles {

  static PrintStream err = System.err;

  /**
   * The first command line argument is the name of the file, the
   * remaining are the doubles
   */
  public static void main(String[] args) {
    // Create a FileOutputStream
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(args[0]);

    } catch(FileNotFoundException ex) {
      err.println("** No such file: " + args[0]);
      System.exit(1);
    }

    // Wrap a DataOutputStream around the FileOutputStream
    DataOutputStream dos = new DataOutputStream(fos);

    // Write the doubles to the DataOutputStream
    for(int i = 1; i < args.length; i++) {
      try {
	double d = Double.parseDouble(args[i]);
	dos.writeDouble(d);

      } catch(NumberFormatException ex) {
	err.println("** Not a double: " + args[i]);

      } catch(IOException ex) {
	err.println("** " + ex);
	System.exit(1);
      }
    }
  }
}