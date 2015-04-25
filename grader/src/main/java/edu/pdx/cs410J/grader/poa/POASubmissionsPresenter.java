package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class POASubmissionsPresenter {
  private final EventBus bus;
  private final POASubmissionsView view;
  private final List<POASubmission> submissions = new ArrayList<>();

  @Inject
  public POASubmissionsPresenter(EventBus bus, POASubmissionsView view) {
    this.bus = bus;
    this.view = view;

    this.view.addSubmissionSelectedListener(this::selectPOASubmission);
    this.view.addDownloadSubmissionsListener(this::fireDownloadSubmissionsEvent);

    this.bus.register(this);
  }

  private void fireDownloadSubmissionsEvent() {
    this.bus.post(new DownloadPOASubmissionsRequest());
  }

  private void selectPOASubmission(int index) {
    this.bus.post(new POASubmissionSelected(this.submissions.get(index)));
  }

  @Subscribe
  public void displaySubmission(POASubmission submission) {
    this.submissions.add(submission);
    view.setPOASubmissionsDescriptions(getSubmissionsDescriptions());
  }

  private List<String> getSubmissionsDescriptions() {
    return submissions.stream().map(POASubmission::getSubject).collect(Collectors.toList());
  }
}