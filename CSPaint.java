//I worked on the homework assignment alone, using only course materials and the Java API.

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class contains an overridden start method which creates the main components of the CSPaint program.
 *
 * @author iquadri3
 * @version 1.0
 */

public class CSPaint extends Application {


    /**
     * The start method which creates the three main components to the CSPaint program: a center canvas where the
     * users will draw, a side pane that will contain the toolbox, and a bottom pane that will contain labels
     * displaying the user's mouse coordinates on the canvas as well as the number of shapes.
     *
     * @param primaryStage new Stage that will hold CSPaint program
     */
    @Override
    public void start(Stage primaryStage) {

        //setting up blank canvas
        StackPane holder = new StackPane();
        final Canvas canvas = new Canvas(650, 450);
        holder.getChildren().add(canvas);
        holder.setStyle("-fx-background-color: white");

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(1);

        Line line = new Line();
        Circle circ = new Circle();

        //creating bottom pane for coordinates and number of shapes
        HBox hBox = new HBox(30);
        hBox.setPadding(new Insets(15, 15, 15, 15));
        hBox.setStyle("-fx-background-color: azure");

        //creating side pane with different editing options
        VBox sidePane = new VBox(10);
        sidePane.setPadding(new Insets(7, 7, 7, 7));
        sidePane.setStyle("-fx-border-width: 2px; -fx-background-color: lightgrey");
        RadioButton drawButton = new RadioButton("Draw");
        RadioButton eraseButton = new RadioButton("Erase");
        RadioButton circleButton = new RadioButton("Circle");
        RadioButton lineButton = new RadioButton("Line (drag to create)");

        Label colorMessage = new Label("Enter a color below and\npress ENTER:");
        colorMessage.setContentDisplay(ContentDisplay.LEFT);

        //color picker and fill color
        ColorPicker color = new ColorPicker(Color.BLACK);

        //label names
        Label lineColor = new Label("OR choose a color:");

        //creating line width selector
        Label sliderLabel = new Label("Adjust line width:");
        Slider slider = new Slider(1, 25, 2);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);

        //clear canvas button
        TextField colorField = new TextField();
        Button clearButton = new Button("Clear Canvas");
        clearButton.setLayoutX(200);
        clearButton.setLayoutY(200);

        //building side pane
        sidePane.getChildren().addAll(drawButton, eraseButton, circleButton, lineButton,
                colorMessage, colorField, lineColor, color, sliderLabel, slider, clearButton);
        ToggleGroup group = new ToggleGroup();
        drawButton.setToggleGroup(group);
        eraseButton.setToggleGroup(group);
        circleButton.setToggleGroup(group);
        lineButton.setToggleGroup(group);

        //taking in color name in color text field
        colorField.setOnAction(e -> {
                try {
                    String colorInput = new String(colorField.getText());
                    color.setValue(Color.valueOf(colorInput));
                } catch (IllegalArgumentException x) {
                    Alert a = new Alert(AlertType.ERROR, "Invalid color entered! Please try again.");
                    a.show();
                }
            });

        //updating coordinates
        Label coordinates = new Label("-------");
        hBox.getChildren().add(coordinates);
        canvas.setOnMouseMoved(e -> {
                String s = new String("(" + (e.getX()) + "," + (e.getY()) + ")");
                coordinates.setText(s);
            });

        //number of shapes label
        final AtomicInteger numberOfShapes = new AtomicInteger(0);
        Label numShapes = new Label("Number of shapes: " + numberOfShapes);
        hBox.getChildren().add(numShapes);

        //canvas click events
        canvas.setOnMouseClicked(e -> {
                if (drawButton.isSelected()) {
                    gc.setStroke(color.getValue());
                } else if (eraseButton.isSelected()) {
                    gc.setFill(Color.WHITE);
                    gc.fillOval(e.getX(), e.getY(), 20, 20);
                } else if (circleButton.isSelected()) {
                    gc.setFill(color.getValue());
                    gc.fillOval(e.getX(), e.getY(), 30, 30);
                    numShapes.setText("Number of shapes: " + numberOfShapes.incrementAndGet());
                }
            });

        //canvas mouse pressed events
        canvas.setOnMousePressed(e -> {
                if (drawButton.isSelected()) {
                    gc.setStroke(color.getValue());
                    gc.beginPath();
                    gc.lineTo(e.getX(), e.getY());
                } else if (lineButton.isSelected()) {
                    gc.setStroke(color.getValue());
                    line.setStartX(e.getX());       //Start line at this x value
                    line.setStartY(e.getY());       //Start line at this y value
                }
            });

        //canvas mouse dragged events
        canvas.setOnMouseDragged(e -> {
                String s = new String("(" + (e.getX()) + "," + (e.getY()) + ")");
                coordinates.setText(s);
                if (drawButton.isSelected()) {
                    double lineWidth = gc.getLineWidth();
                    gc.lineTo(e.getX(), e.getY());
                    gc.stroke();
                } else if (eraseButton.isSelected()) {
                    gc.setFill(Color.WHITE);
                    gc.fillOval(e.getX(), e.getY(), 20, 20);
                }
            });

        //if mouse button is released
        canvas.setOnMouseReleased(e -> {
                if (lineButton.isSelected()) {
                    line.setEndX(e.getX());
                    line.setEndY(e.getY());
                    gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
                    numShapes.setText("Number of shapes: " + numberOfShapes.incrementAndGet());
                }
            });

        clearButton.setOnAction(e -> {
                gc.clearRect(0, 0, 650, 450);
                numberOfShapes.set(0);
                numShapes.setText("Number of shapes: 0");
            });

        //setting up color picker
        color.setOnAction(e -> {
                gc.setStroke(color.getValue());
            });

        //setting up the width slider
        slider.valueProperty().addListener(e -> {
                double width = slider.getValue();
                gc.setLineWidth(width);
            });

        BorderPane pane = new BorderPane();
        pane.setCenter(holder);
        pane.setBottom(hBox);
        pane.setLeft(sidePane);

        Scene scene = new Scene(pane, 835, 500);
        primaryStage.setTitle("CSPaint");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Main method serves as fallback in case the application cannot be launched through IDE with
     * limited FX support
     *
     * @param args the command line's argument
     */
    public static void main(String[] args) {
        launch(args);
    }

}
