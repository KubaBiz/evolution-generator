package evol.gen.gui;

import evol.gen.*;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class App extends Application {
    private GridPane grid = new GridPane();
    private IWorldMap myMap;
    private final int width = 45;
    private final int height = 45;
    public Stage stage;

    private VBox drawObject(Vector2d position) {
        VBox result;
        if (myMap.isOccupied(position)) {
            Object object = myMap.objectAt(position);
            GuiElementBox newElem = new GuiElementBox((IMapElement) object);
            result = newElem.getBox();
        } else { result = new VBox(new Label("")); }
        return result;
    }

    private void drawMap(){
        grid.setGridLinesVisible(true);
        GrassField myMap = (GrassField) this.myMap;
        int rangeY = myMap.getUpperRight().y - myMap.getLowerLeft().y;
        int rangeX = myMap.getUpperRight().x - myMap.getLowerLeft().x;
        Label label;

        label = new Label("y/x");
        grid.getColumnConstraints().add(new ColumnConstraints(width));
        grid.getRowConstraints().add(new RowConstraints(height));
        grid.add(label, 0, 0);
        GridPane.setHalignment(label, HPos.CENTER);

        for (int i = 0; i <= rangeY; i++) {
            Integer value = myMap.getUpperRight().y-i;

            label = new Label(value.toString());
            grid.getRowConstraints().add(new RowConstraints(height));
            grid.add(label, 0, i+1);
            GridPane.setHalignment(label, HPos.CENTER);

            for (int j = 0; j < rangeX+1; j++) {
                if (i == 0) {
                    value = myMap.getLowerLeft().x + j;
                    label = new Label(value.toString());
                    grid.add(label, j+1, 0);
                    grid.getColumnConstraints().add(new ColumnConstraints(width));
                    GridPane.setHalignment(label, HPos.CENTER);
                }

                VBox result = drawObject(new Vector2d(j+myMap.getLowerLeft().x , i+myMap.getLowerLeft().y));
                grid.add(result, j+1, rangeY-i+1);
                GridPane.setHalignment(label, HPos.CENTER);
            }
        }

        Scene scene = new Scene(grid, (rangeX+2)*width*1.2, (rangeY+2)*height*1.2);
        stage.setScene(scene);

        System.out.println(this.myMap.toString());
    }

    public void updateMap(){
        grid.getChildren().clear();
        grid = new GridPane();
        drawMap();
    }

    public void threadExceptionHandler(){
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println("Nieprawidlowo wpisane dane: " + e);
                Platform.exit();
            }
        });
    }

    private void startGame(SimulationEngine engine, String text){
        String[] array = text.split(" ");
        MoveDirection[] directions = new OptionsParser().parse(array);
        engine.setDirections(directions);
        Thread threadEngine = new Thread(engine);
        threadEngine.start();
    }

    public void start(Stage primaryStage) {
        try {
            threadExceptionHandler();

            AbstractWorldMap map = new GrassField(10);
            myMap = map;
            stage = primaryStage;
            Vector2d[] positions2 = {new Vector2d(2,2), new Vector2d(3,4)};
            SimulationEngine engine = new SimulationEngine(map, positions2, this, 600);
            Button button = new Button("Start");
            button.setPadding(new Insets(10, 50, 10 ,50));
            TextField text = new TextField("Enter directions");
            text.setPadding(new Insets(10,10,10,10));

                HBox hbox = new HBox(text, button);

                hbox.setAlignment(Pos.CENTER);
                hbox.setSpacing(10);

                button.setOnAction(actionEvent -> startGame(engine, text.getText()));
                Scene scene = new Scene(hbox, 600, 600);

                stage.setScene(scene);
                stage.show();

        } catch (IllegalArgumentException ex) {
            System.out.println(ex);

        }
        catch (RuntimeException e){
            System.out.println(e.getMessage());
        }
    }
}
