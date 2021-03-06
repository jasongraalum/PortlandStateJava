package edu.pdx.cs410J.phonebillgwt.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import org.junit.Test;

/**
 * An integration test for the airline GWT UI.  Remember that GWTTestCase is JUnit 3 style.
 * So, test methods names must begin with "test".
 * And since this test code is compiled into JavaScript, you can't use hamcrest matchers.  :(
 */
public class PhoneBillGwtIT extends GWTTestCase {
  @Override
  public String getModuleName() {
    return "edu.pdx.cs410J.phonebillgwt.PhoneBillIntegrationTests";
  }

  @Test
  public void testClickingButtonAlertsWithAirlineInformation() {
    final CapturingAlerter alerter = new CapturingAlerter();

    PhoneBillGwt ui = new PhoneBillGwt(alerter);
    click(ui.button);

    Timer verify = new Timer() {
      @Override
      public void run() {
        String message = alerter.getMessage();
        assertNotNull(message);
        assertTrue(message, message.contains("CS410J's phone bill with 1 phone calls"));
        finishTest();
      }
    };

    // Wait for the RPC call to return
    verify.schedule(500);

    delayTestFinish(1000);
  }

  /**
   * Clicks a <code>Button</code>
   *
   * One would think that you could testing clicking a button with Button.click(), but it looks
   * like you need to fire the native event instead.  Lame.
   *
   * @param button
   *        The button to click
   */
  private void click(Button button) {
    NativeEvent event = Document.get().createClickEvent(0, 0, 0, 0, 0, false, false, false, false);
    DomEvent.fireNativeEvent(event, button);
  }

  private class CapturingAlerter implements PhoneBillGwt.Alerter {
    private String message;

    @Override
    public void alert(String message) {
      this.message = message;
    }

    public String getMessage() {
      return message;
    }
  }
}
