package demos.components;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.effects.JFXDepthManager;
import com.jfoenix.transitions.template.JFXAnimationTemplate;
import com.jfoenix.transitions.template.TemplateBuilder;
import javafx.animation.Interpolator;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class AnimationTemplateDemo extends Application {

  private static final TemplateBuilder<Node> HEART_BEAT =
      JFXAnimationTemplate.create()
          .percent(0)
          .action(b -> b.target(Node::scaleXProperty, Node::scaleYProperty).endValue(1))
          .percent(14)
          .action(b -> b.target(Node::scaleXProperty, Node::scaleYProperty).endValue(1.3))
          .percent(28)
          .action(b -> b.target(Node::scaleXProperty, Node::scaleYProperty).endValue(1))
          .percent(42)
          .action(b -> b.target(Node::scaleXProperty, Node::scaleYProperty).endValue(1.3))
          .percent(70)
          .action(b -> b.target(Node::scaleXProperty, Node::scaleYProperty).endValue(1))
          .config(b -> b.duration(Duration.seconds(1.3)).interpolator(Interpolator.EASE_BOTH));

  private static final TemplateBuilder<Node> FLASH =
      JFXAnimationTemplate.create()
          .from()
          .percent(50)
          .action(b -> b.target(Node::opacityProperty).endValue(0))
          .percent(25, 75)
          .action(b -> b.target(Node::opacityProperty).endValue(1))
          .config(b -> b.duration(Duration.seconds(1.5)).interpolator(Interpolator.EASE_BOTH));

  private static final TemplateBuilder<Node> TADA =
      JFXAnimationTemplate.create()
          .from()
          .action(b -> b.target(Node::scaleXProperty, Node::scaleYProperty).endValue(1))
          .percent(10, 20)
          .action(b -> b.target(Node::scaleXProperty, Node::scaleYProperty).endValue(0.9))
          .action(b -> b.target(Node::rotateProperty).endValue(-3))
          .percent(30, 50, 70, 90)
          .action(b -> b.target(Node::scaleXProperty, Node::scaleYProperty).endValue(1.1))
          .action(b -> b.target(Node::rotateProperty).endValue(3))
          .percent(40, 60, 80)
          .action(b -> b.target(Node::scaleXProperty, Node::scaleYProperty).endValue(1.1))
          .action(b -> b.target(Node::rotateProperty).endValue(-3))
          .to()
          .action(b -> b.target(Node::scaleXProperty, Node::scaleYProperty).endValue(1))
          .action(b -> b.target(Node::rotateProperty).endValue(0))
          .config(b -> b.duration(Duration.seconds(1.3)).interpolator(Interpolator.EASE_BOTH));

  private static final TemplateBuilder<Node> BOUNCE_IN_UP =
      JFXAnimationTemplate.create()
          .from()
          .action(b -> b.target(Node::opacityProperty).endValue(0))
          .action(b -> b.target(Node::translateYProperty).endValue(3000))
          .percent(60)
          .action(b -> b.target(Node::opacityProperty).endValue(1))
          .action(b -> b.target(Node::translateYProperty).endValue(-20))
          .percent( 75)
          .action(b -> b.target(Node::translateYProperty).endValue(10))
          .percent(90)
          .action(b -> b.target(Node::translateYProperty).endValue(-5))
          .to()
          .action(b -> b.target(Node::translateYProperty).endValue(0))
          .config(b -> b.duration(Duration.seconds(1.2)).interpolator(Interpolator.SPLINE(0.215, 0.61, 0.355, 1)));

  @SafeVarargs
  private final TemplateBuilder<Node> createColorTransition(
      ObjectProperty<Paint> paintObjectProperty, ObjectProperty<Paint>... paintObjectProperties) {
    return JFXAnimationTemplate.create()
        .from()
        .action(b -> b.target(paintObjectProperty, paintObjectProperties).endValue(Color.ORANGERED))
        .percent(16)
        .action(b -> b.target(paintObjectProperty, paintObjectProperties).endValue(Color.LAWNGREEN))
        .percent(32)
        .action(
            b -> b.target(paintObjectProperty, paintObjectProperties).endValue(Color.DODGERBLUE))
        .percent(48)
        .action(
            b -> b.target(paintObjectProperty, paintObjectProperties).endValue(Color.YELLOWGREEN))
        .percent(64)
        .action(
            b -> b.target(paintObjectProperty, paintObjectProperties).endValue(Color.SPRINGGREEN))
        .percent(80)
        .action(b -> b.target(paintObjectProperty, paintObjectProperties).endValue(Color.DEEPPINK))
        .to()
        .action(b -> b.target(paintObjectProperty, paintObjectProperties).endValue(Color.ORANGERED))
        .config(
            b ->
                b.duration(Duration.minutes(2.5))
                    .infiniteCycle()
                    .interpolator(Interpolator.EASE_BOTH));
  }

  private String toRGBCode(Color color) {
    return String.format(
        "#%02X%02X%02X",
        (int) (color.getRed() * 255),
        (int) (color.getGreen() * 255),
        (int) (color.getBlue() * 255));
  }

  @Override
  public void start(Stage primaryStage) {

    VBox vBox = new VBox();
    vBox.setStyle("-fx-background-color:WHITE");
    HBox hBox = new HBox();
    hBox.setStyle("-fx-background-color:WHITE");

    Label label = new Label("JFXAnimation.template");
    Font font = new Font(50);
    label.setFont(font);

    Map<String, Timeline> animations = new HashMap<>();
    animations.put("Flash", FLASH.build(label));
    animations.put("Heart Beat", HEART_BEAT.build(label));
    animations.put("Tada", TADA.build(label));
    animations.put("BounceIn Up", BOUNCE_IN_UP.build(label));

    JFXComboBox<String> comboBox = new JFXComboBox<>();
    comboBox.setPrefWidth(170);
    comboBox.getItems().addAll(animations.keySet());
    comboBox.getSelectionModel().selectFirst();
    comboBox
        .getSelectionModel()
        .selectedItemProperty()
        .addListener((observable, oldValue, newValue) -> animations.get(newValue).play());

    JFXButton button = new JFXButton("Animate it");
    button.setPrefWidth(120);
    button.setDisableVisualFocus(true);
    button.setOnMouseClicked(
        event -> animations.get(comboBox.getSelectionModel().getSelectedItem()).play());

    Line line = new Line(0, 0, 450, 0);
    line.setStyle(
        "-fx-stroke: linear-gradient(to right, transparent 1%, #dadada 50%, transparent 99%); -fx-stroke-width: 1.3");
    VBox.setMargin(line, new Insets(-9, 0, -15, 0));

    Label subLabel = new Label("Another thing from JFoenix.");

    Label subLabel2 = new Label("Inspired by animate.css");
    subLabel2.setStyle("-fx-font-size: 13; -fx-text-fill: #a5a5a5;");
    VBox.setMargin(subLabel2, new Insets(-30, 0, 0, 0));

    ObjectProperty<Paint> paintProperty = new SimpleObjectProperty<>(Color.TRANSPARENT);

    createColorTransition(
            button.ripplerFillProperty(),
            label.textFillProperty(),
            comboBox.focusColorProperty(),
            comboBox.unFocusColorProperty(),
            paintProperty)
        .build(null)
        .play();

    StringBinding colorStringBinding =
        Bindings.createStringBinding(() -> toRGBCode(((Color) paintProperty.get())), paintProperty);
    StringBinding darkerColorStringBinding =
        Bindings.createStringBinding(
            () -> toRGBCode(((Color) paintProperty.get()).darker()), paintProperty);

    label
        .styleProperty()
        .bind(
            Bindings.format(
                "-fx-text-fill: linear-gradient(to bottom, %s, %s);",
                colorStringBinding, darkerColorStringBinding));

    button
        .styleProperty()
        .bind(
            Bindings.format(
                "-fx-border-color: %s; \n"
                    + "-fx-background-color: transparent; \n"
                    + "-fx-text-fill: %s; \n"
                    + "-fx-border-width: 2; \n"
                    + "-fx-border-radius: 5;\n"
                    + "-fx-font-size: 16",
                darkerColorStringBinding, darkerColorStringBinding));

    hBox.setAlignment(Pos.BOTTOM_CENTER);
    hBox.setSpacing(23);
    hBox.getChildren().addAll(comboBox, button);
    vBox.setAlignment(Pos.CENTER);
    vBox.setSpacing(40);
    vBox.getChildren().addAll(label, hBox, line, subLabel, subLabel2);

    Map<String, Node> t = new HashMap<>();
    t.put("", line);
    BOUNCE_IN_UP.build(label, t).play();

    final Scene scene = new Scene(vBox, 1100, 600);
    scene
        .getStylesheets()
        .add(ButtonDemo.class.getResource("/css/jfoenix-components.css").toExternalForm());
    primaryStage.setTitle("JFX Animation Template Demo");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
