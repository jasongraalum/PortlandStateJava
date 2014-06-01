package edu.pdx.cs410J.grader;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;

import java.io.*;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

class JarMaker {
  private Map<File, String> sourceFilesAndNames;
  private File jarFile;
  private String createdBy;
  private String manifestVersion;

  public JarMaker(Map<File, String> sourceFilesAndNames, File jarFile, String createdBy, String manifestVersion) {
    this.sourceFilesAndNames = sourceFilesAndNames;
    this.jarFile = jarFile;
    this.createdBy = createdBy;
    this.manifestVersion = manifestVersion;
  }

  public File makeJar() throws IOException {
    // Create a Manifest for the Jar file containing the name of the
    // author (userName) and a version that is based on the current
    // date/time.
    Manifest manifest = new Manifest();
    Attributes attrs = manifest.getMainAttributes();
    attrs.put(new Attributes.Name("Created-By"), createdBy);

    attrs.put(Attributes.Name.MANIFEST_VERSION, manifestVersion);

    // Create a JarOutputStream around the jar file
    JarOutputStream jos = new JarOutputStream(new FileOutputStream(jarFile), manifest);
    jos.setMethod(JarOutputStream.DEFLATED);

    // Add the source files to the Jar
    for (Map.Entry<File, String> fileEntry : sourceFilesAndNames.entrySet()) {
      {
        File file = fileEntry.getKey();
        String fileName = fileEntry.getValue();
        System.out.println("Adding " + fileName + " to jar");
        JarEntry entry = new JarEntry(fileName);
        entry.setTime(file.lastModified());
        entry.setSize(file.length());

        entry.setMethod(JarEntry.DEFLATED);

        // Add the entry to the JAR file
        jos.putNextEntry(entry);

        ByteStreams.copy(new FileInputStream(file), jos);

        jos.closeEntry();
      }
    }

    jos.close();

    return jarFile;
  }

  public static void main(String[] args) throws IOException {
    String jarFileName = null;
    Set<File> files = Sets.newHashSet();

    for (String arg : args) {
      if (jarFileName == null) {
        jarFileName = arg;

      } else {
        File file = new File(arg);
        if (file.exists()) {
          files.add(file);
        }
      }
    }

    if (jarFileName == null) {
      usage("Missing jar file name");
    }

    if (files.isEmpty()) {
      usage("Missing files");
    }

    Map<File, String> sourceFilesAndNames = Maps.toMap(files, new Function<File, String>() {
      @Override
      public String apply(File file) {
        return file.getPath();
      }
    });

    assert jarFileName != null;
    File jarFile = new File(jarFileName);

    new JarMaker(sourceFilesAndNames, jarFile, System.getProperty("user.name"), new Date().toString()).makeJar();
  }

  private static void usage(String message) {
    PrintStream err = System.err;
    err.println("** " + message);
    err.println("java JarMaker jarFileName files+");
    err.println("  jarFileName    The name of the jar file to create");
    err.println("  files+         One or more files to include in the jar");
    err.println();

    System.exit(1);
  }
}
