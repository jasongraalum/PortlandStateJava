package edu.pdx.cs410J.grader.poa;

public interface POAGradeView {
  void setIsEnabled(boolean isEnabled);

  void setIsLate(boolean isLate);

  void addIsLateHandler(IsLateHandler handler);

  public interface IsLateHandler {
    void setIsLate(boolean isLate);
  }
}
