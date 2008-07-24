package edu.pdx.cs399J.family.gwt.client;

import com.google.gwt.user.client.ui.*;
import com.google.gwt.i18n.client.DateTimeFormat;
import edu.pdx.cs399J.family.Person;
import edu.pdx.cs399J.family.Marriage;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

/**
 * Displays information about a {@link Person}
 */
public class PersonPanel extends DockPanel {
  private FamilyTreeGWT familyUI;

  private Person person;
  private List<Marriage> marriages = new ArrayList<Marriage>();


  private Label name;
  private Label dob;
  private Label dod;
  private Label fatherName;
  private Label motherName;
  private ListBox marriagesList;

  /**
   * Creates a <code>PersonPanel</code> for displaying
   * <code>Person</code>s
   * @param familyUI
   */
  public PersonPanel(FamilyTreeGWT familyUI) {
    fillInLabels();

    VerticalPanel panel = new VerticalPanel();
    panel.setSpacing(3);

    this.name.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
    panel.add(this.name);

    this.dob.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
    panel.add(this.dob);

    this.dod.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
    panel.add(this.dod);

    this.fatherName.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
    this.fatherName.addClickListener(new ClickListener() {
      public void onClick(Widget widget) {
        PersonPanel.this.familyUI.displayFather();
      }
    });
    panel.add(this.fatherName);

    this.motherName.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
    this.motherName.addClickListener(new ClickListener() {
      public void onClick(Widget widget) {
        PersonPanel.this.familyUI.displayMother();
      }
    });
    panel.add(this.motherName);

    panel.add(new Label("Marriages:"));
    this.marriagesList = new ListBox();
    this.marriagesList.setVisibleItemCount(10);
    this.marriagesList.setWidth("100%");
    ScrollPanel scroll = new ScrollPanel(this.marriagesList);
    panel.add(scroll);
    panel.setWidth("100%");
    panel.setCellWidth(panel, "100%");
 
    this.add(panel, DockPanel.NORTH);
  }

  private void fillInLabels() {
    this.name = new Label("Name:");
    this.dob = new Label("Born:");
    this.dod = new Label("Died:");
    this.fatherName = new Label("Father:");
    this.motherName = new Label("Mother:");
  }

  /**
   * Dispays information aout a <code>Person</code> in this
   * <code>PersonPanel</code>
   */
  public void showPerson(Person person) {
    this.person = person;

    fillInLabels();

    if (this.person == null) {
      // When no person is selected
      return;
    }

    DateTimeFormat df = DateTimeFormat.getLongDateFormat();

    this.name.setText(person.getFullName());

    Date dob = person.getDateOfBirth();
    if (dob != null) {
      this.dob.setText("Born: " + df.format(dob));
    }

    Date dod = person.getDateOfDeath();
    if (dod != null) {
      this.dod.setText("Died: " + df.format(dod));
    }

    Person father = person.getFather();
    if (father != null) {
      this.fatherName.setText("Father: " + father.getFullName());
    }

    Person mother = person.getMother();
    if (mother != null) {
      this.motherName.setText("Mother: " + mother.getFullName());
    }

    this.marriagesList.clear();

    this.marriages = new ArrayList<Marriage>(person.getMarriages());

    for (Marriage marriage : this.marriages) {
      StringBuffer sb = new StringBuffer();
      Person spouse = (marriage.getHusband().getId() == person.getId()
		       ? marriage.getWife()
		       : marriage.getHusband());
      sb.append(spouse.getFullName());

      Date date = marriage.getDate();
      if (date != null) {
	sb.append(" on " + df.format(date));
      }

      String location = marriage.getLocation();
      if (location != null || !location.equals("")) {
	sb.append(" in " + location);
      }

      this.marriagesList.addItem(sb.toString());
    }
  }

  /**
   * Displays and returns the current person's father
   */
  public Person showFather() {
    Person father = this.person.getFather();
    if (father != null) {
      this.showPerson(father);
    }
    return father;
  }

  /**
   * Displays the current person's mother
   */
  public Person showMother() {
    Person mother = this.person.getMother();
    if (mother != null) {
      this.showPerson(mother);
    }
    return mother;
  }
}
