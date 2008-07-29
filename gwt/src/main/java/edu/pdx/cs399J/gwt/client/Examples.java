package edu.pdx.cs399J.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.*;

import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * The main entry point to GWT examples
 */
public class Examples implements EntryPoint {

  public void onModuleLoad() {
    TabPanel allExamples = new TabPanel();
    Map<String, List<? extends Example>> examples = getExamples();
    for (String title : examples.keySet()) {
      ExamplePanel panel = new ExamplePanel();
      for (Example example : examples.get(title)) {
        panel.add(example, example.getName());
        panel.selectFirst();
      }
      allExamples.add(panel, title);
    }
    allExamples.selectTab(0);


    RootPanel.get().add(allExamples);
  }

  private Map<String, List<? extends Example>> getExamples() {
    Map<String, List<? extends Example>> examples = new LinkedHashMap<String, List<? extends Example>>();
    examples.put("Widgets",
      Arrays.asList(
        new LabelExample(),
        new ListBoxExample(),
        new TextBoxExample(),
        new TextAreaExample(),
        new ButtonsExample()
    ));
    examples.put("Panels",
      Arrays.asList(
        new AxisPanelExample(),
        new DockPanelExample(),
        new DeckPanelExample(),
        new StackPanelExample(),
        new TabPanelExample()
      ));
    examples.put("Events",
      Arrays.asList(
        new ClickListenerExample(),
        new FocusPanelExample()
      ));
    return examples;
  }

  private class ExamplePanel extends DockPanel {
    private VerticalPanel buttons = new VerticalPanel();
    private DeckPanel deck = new DeckPanel();

    public ExamplePanel() {
      this.setSpacing(3);
      add(buttons, WEST);
      add(deck, CENTER);
    }

    public void add(final Example example, String name) {
      deck.add(example);
      buttons.add(new ToggleButton(name, new ClickListener() {
        public void onClick(Widget widget) {
          for (Widget w : buttons) {
            if (!w.equals(widget)) {
              ((ToggleButton) w).setDown(false);
            }
          }

          deck.showWidget(deck.getWidgetIndex(example));
        }
      }));
    }

    public void selectFirst() {
      ((ToggleButton) buttons.getWidget(0)).setDown(true);
      deck.showWidget(0);
    }
  }
}
