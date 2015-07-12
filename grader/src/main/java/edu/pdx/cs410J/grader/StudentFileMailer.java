package edu.pdx.cs410J.grader;

import com.google.common.io.Files;
import edu.pdx.cs410J.ParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentFileMailer extends EmailSender {
  private static Logger logger = LoggerFactory.getLogger(StudentFileMailer.class.getPackage().getName());

  private final String subject;
  private final Session session;

  public StudentFileMailer(String subject, Session session) {
    this.subject = subject;
    this.session = session;
  }

  public static void main(String[] args) {
    String gradeBookFileName = null;
    String subject = null;
    List<String> fileNames = new ArrayList<>();

    for (String arg : args) {
      if (gradeBookFileName == null) {
        gradeBookFileName = arg;

      } else if (subject == null) {
        subject = arg;

      } else {
        fileNames.add(arg);
      }
    }

    usageIfNull(gradeBookFileName, "Missing grade book file name");
    usageIfNull(subject, "Missing email subject");
    usageIfEmpty(fileNames, "Missing file names");
    List<File> files = getFiles(fileNames);
    GradeBook gradeBook = getGradeBook(gradeBookFileName);
    List<Student> students = getStudentsFromFiles(files, gradeBook);

    assert files.size() == students.size();

    StudentFileMailer mailer = new StudentFileMailer(subject, newEmailSession(false));
    for (int i = 0; i < files.size(); i++) {
      File file = files.get(i);
      Student student = students.get(i);
      try {
        mailer.mailFileToStudent(file, student);

      } catch (MessagingException | IOException ex) {
        System.err.println("While mailing \"" + file + "\" to " + student);
        ex.printStackTrace(System.err);
      }
    }
  }

  private void mailFileToStudent(File file, Student student) throws MessagingException, IOException {
    String studentEmail = getEmailAddress(student);

    System.out.println("Mailing \"" + file + "\" to \"" + studentEmail + "\"");

    MimeMessage message = newEmailTo(this.session, studentEmail, this.subject);
    message.setText(readTextFromFile(file));

    sendEmailMessage(message);
  }

  private String readTextFromFile(File file) throws IOException {
    StringBuilder sb = new StringBuilder();
    Files.copy(file, Charset.defaultCharset(), sb);
    return sb.toString();
  }

  private void sendEmailMessage(MimeMessage message) throws MessagingException {
    Transport.send(message);
  }

  private String getEmailAddress(Student student) {
    StringBuilder sb = new StringBuilder();
    sb.append(student.getFirstName());
    sb.append(" ");
    if (student.getNickName() != null) {
      sb.append("\"");
      sb.append(student.getNickName());
      sb.append("\" ");
    }
    sb.append(student.getLastName());
    sb.append(" <");
    sb.append(student.getEmail());
    sb.append(">");

    return sb.toString();
  }

  private static List<Student> getStudentsFromFiles(List<File> files, GradeBook gradeBook) {
    List<Student> students = new ArrayList<>(files.size());
    for (File file : files) {
      String studentId;
      studentId = getStudentIdFromFileName(file);
      Optional<Student> maybeStudent = gradeBook.getStudent(studentId);
      if (!maybeStudent.isPresent()) {
        cannotFindStudent(studentId);
      } else {
        students.add(maybeStudent.get());
      }
    }

    return students;
  }

  private static void cannotFindStudent(String studentId) {
    logger.warn("Cannot find student with id \"" + studentId + "\" in grade book");
  }

  private static String getStudentIdFromFileName(File file) {
    String studentId;
    String fileName = file.getName();
    int index = fileName.lastIndexOf('.');
    if (index < 0) {
      studentId = fileName;

    } else {
      studentId = fileName.substring(0, index);
    }
    return studentId;
  }

  private static List<File> getFiles(List<String> fileNames) {
    List<File> files = new ArrayList<>(fileNames.size());
    for (String fileName : fileNames) {
      File file = new File(fileName);
      if (!file.exists()) {
        throw new IllegalStateException("File \"" + fileName + "\" does not exist");

      } else if (!file.isFile()) {
        throw new IllegalStateException("File \"" + fileName + "\" is not a file");
      }

      files.add(file);
    }

    return files;
  }

  private static GradeBook getGradeBook(String gradeBookFileName) {
    try {
      XmlGradeBookParser parser = new XmlGradeBookParser(gradeBookFileName);
      return parser.parse();

    } catch (IOException ex) {
      return usage("Couldn't read grade book file \"" + gradeBookFileName + "\"");

    } catch (ParserException ex) {
      return usage("Couldn't parse grade book file \"" + gradeBookFileName + "\"");
    }
  }


  private static void usageIfEmpty(List<String> list, String message) {
    if (list.isEmpty()) {
      usage(message);
    }
  }

  private static void usageIfNull(String arg, String message) {
    if (arg == null) {
      usage(message);
    }
  }

  private static <T> T usage(String message) {
    PrintStream err = System.err;

    err.println("** " + message);
    err.println();
    err.println("usage: java StudentFileMailer gradeBookFileName subject fileName+");
    err.println();
    err.println("Emails a file to a student in a grade book.  The name of the file must");
    err.println("begin with the student's id.");
    err.println();
    err.println("  gradeBookFileName    Name of the grade book file");
    err.println("  subject              The subject of the email");
    err.println("  fileName             A file to send to the student");
    err.println();

    System.exit(1);

    throw new UnsupportedOperationException("Should never get here");
  }
}
