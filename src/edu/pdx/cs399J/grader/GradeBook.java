package edu.pdx.cs410J.grader;

import java.io.*;
import java.util.*;

import edu.pdx.cs410J.ParserException;

/**
 * This class represents a grade book that contains information about
 * a CS410J class: the assignments, the students and their grades.
 */
public class GradeBook {

  private String className;
  private Map assignments;  // name -> Assignment
  private Map students;     // id -> Student
  
  /**
   * Creates a new <code>GradeBook</code> for a given class
   */
  public GradeBook(String className) {
    this.className = className;
    this.assignments = new TreeMap();
    this.students = new TreeMap();
  }

  /**
   * Returns the name of the class represented by this
   * <code>GradeBook</code>
   */
  public String getClassName() {
    return(this.className);
  }

  /**
   * Returns the names of the assignments for this class
   */
  public Set getAssignmentNames() {
    return(this.assignments.keySet());
  }

  /**
   * Returns the <code>Assignment</code> of a given name
   */
  public Assignment getAssignment(String name) {
    return((Assignment) this.assignments.get(name));
  }

  /**
   * Adds an <code>Assignment</code> to this class
   */
  public void addAssignment(Assignment assign) {
    this.assignments.put(assign.getName(), assign);
  }

  /**
   * Returns the ids of all of the students in this class
   */
  public Set getStudentIds() {
    return(this.students.keySet());
  }

  /**
   * Returns the <code>Student</code> with the given id.  If a student
   * with that name does not exist, a new <code>Student</code> is
   * created.
   */
  public Student getStudent(String id) {
    Student student = (Student) this.students.get(id);
    if(student == null) {
      student = new Student(id);
      this.students.put(id, student);
    }
    return(student);
  }

  /**
   * Adds a <code>Student</code> to this <code>GradeBook</code>
   */
  public void addStudent(Student student) {
    this.students.put(student.getId(), student);
  }

  /**
   * Returns a brief textual description of this
   * <code>GradeBook</code>.
   */
  public String toString() {
    return("Grade book for " + this.getClassName() + " with " +
           this.getStudentIds().size() + " students");
  }

  private static PrintWriter out = new PrintWriter(System.out, true);
  private static PrintWriter err = new PrintWriter(System.err, true);

  /**
   * Prints usage information about the main program.
   */
  private static void usage() {
    err.println("\nusage: java GradeBook -file xmlFile" +
                " -name className");
    err.println("\n");
    System.exit(1);
  }

  /**
   * Main program that is used to create a <code>GradeBook</code>
   */
  public static void main(String[] args) {
    String xmlFile = null;
    String name = null;

    // Parse the command line
    for(int i = 0; i < args.length; i++) {
      if(args[i].equals("-file")) {
        if(++i >= args.length) {
          err.println("** Missing file name");
          usage();
        }

        xmlFile = args[i];

      } else if(args[i].equals("-name")) {
        if(++i >= args.length) {
          err.println("** Missing class name");
          usage();
        }

        name = args[i];

      } else {
        err.println("** Spurious command line option: " + args[i]);
        usage();
      }
    }

    if(xmlFile == null) {
      err.println("** No XML file specified");
      usage();
    }

    GradeBook book = null;

    File file = new File(xmlFile);
    if(file.exists()) {
      // Parse a grade book from the XML file
      try {
        XmlParser parser = new XmlParser(file);
        book = parser.parse();

      } catch(FileNotFoundException ex) {
        err.println("** Could not find file: " + ex.getMessage());
        System.exit(1);

      } catch(IOException ex) {
        err.println("** IOException during parsing: " + ex.getMessage());
        System.exit(1);

      } catch(ParserException ex) {
        err.println("** Error during parsing: " + ex.getMessage());
        System.exit(1);
      }

    } else if(name == null) {
      err.println("** Must specify a class name when creating a " +
                  "grade book");
      System.exit(1);
      
    } else {
      // Create an empty GradeBook
      book = new GradeBook(name);
    }

    // Write the grade book to the XML file
    try {
      XmlDumper dumper = new XmlDumper(file);
      dumper.dump(book);

    } catch(IOException ex) {
      err.println("** Error while writing XML file: " + ex);
      System.exit(1);
    }
  }

}