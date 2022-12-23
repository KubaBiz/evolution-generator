package evol.gen.gui;

import evol.gen.*;

import evol.gen.GrassField;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.concurrent.CountDownLatch;

public class App extends Application{

    private GridPane grid = new GridPane();
    private GrassField myMap;
    private final int width = 45;
    private final int height = 45;
    public Stage primaryStage;


    private final Object lock = new Object();




    private VBox drawObject(Vector2d position) {
        VBox result = null;
        if (this.myMap.isOccupied(position)) {
            Object object = this.myMap.objectAt(position);
            if (object != null) {
                GuiElementBox newElem = new GuiElementBox((IMapElement) object);
                result = newElem.getBox();

            } else {
                result = new VBox(new Label(""));
            }
        } else {
            result = new VBox(new Label(""));
        }
        return result;
    }
    private void drawMap(int statusOfMap){
        grid.setGridLinesVisible(true);
        //grid.setStyle("-fx-margin: auto;");
        grid.setStyle("-fx-padding: 100 100 100 100;");
        GrassField myMap = this.myMap;
        int rangeY = myMap.getTopRight().y;
        int rangeX = myMap.getTopRight().x;
        Label label;
        for (int i = 0; i <= rangeY; i++) {
            Integer value = myMap.getTopRight().y-i;

            //tworzenie labela dla pierwszej wspolrzednej z lewej w poszczegolnych wierszach
            label = new Label(value.toString());

            grid.getRowConstraints().add(new RowConstraints(height));
            grid.add(label, 0, i+1);

            GridPane.setHalignment(label, HPos.CENTER);
            for (int j = 0; j < rangeX+1; j++) {
                if (i == 0) {
                    //tworzenie labela dla pierwszej wspolrzednej z gory w poszczegolnych kolumnach
                    value = j;
                    label = new Label(value.toString());
                    grid.add(label, j+1, 0);
                    grid.getColumnConstraints().add(new ColumnConstraints(width));
                    GridPane.setHalignment(label, HPos.CENTER);
                }
                //rysowanie i stylizowanie kwadratów na mapie
                VBox result = drawObject(new Vector2d(j, i));
                grid.add(result, j+1, rangeY-i+1);
                GridPane.setHalignment(label, HPos.CENTER);
            }
        }
        //tworzenie dodatkowego labela w lewym gornym rogu
        label = new Label("x/y");
        grid.getColumnConstraints().add(new ColumnConstraints(width));
        grid.getRowConstraints().add(new RowConstraints(height));
        grid.add(label, 0, 0);
        GridPane.setHalignment(label, HPos.CENTER);

        //tworzenie widku w okienku
        HBox hbox = new HBox();

        //hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(20);
        if(statusOfMap == 0){
            label = new Label("usuwanie zwlok");
            label.setStyle("-fx-padding: 100 100 100 100;-fx-font: 24 arial;-fx-text-fill: rgba(140, 0, 0, 1);");
        }else if(statusOfMap == 1){
            label = new Label("przemieszczanie sie");
            label.setStyle("-fx-padding: 100 100 100 100;-fx-font: 24 arial;-fx-text-fill: rgba(242, 133, 0, 1);");
        }else if(statusOfMap == 2){
            label = new Label("zjadanie traw");
            label.setStyle("-fx-padding: 100 100 100 100;-fx-font: 24 arial;-fx-text-fill: rgba(137, 242, 0, 1);");
        }else if(statusOfMap == 3){
            label = new Label("rozmnazanie sie!");
            label.setStyle("-fx-padding: 100 100 100 100;-fx-font: 24 arial;-fx-text-fill: rgba(234, 0, 242, 1);");
        }else if(statusOfMap == 4){
            label = new Label("koniec dnia");
            label.setStyle("-fx-padding: 100 100 100 100;-fx-font: 24 arial;-fx-text-fill: rgba(40, 0, 242, 1);");
        }else if(statusOfMap == 5){
            label = new Label("nowe roslinki");
            label.setStyle("-fx-padding: 100 100 100 100;-fx-font: 24 arial;-fx-text-fill: rgba(34, 255, 0, 1);");
        }

        VBox vbox = new VBox();
        Label labelgrass = new Label("Ilosc roslinek:"+this.myMap.getGrasses().size());
        labelgrass.setStyle("-fx-padding: 100 100 100 100;-fx-font: 24 arial;-fx-text-fill: rgba(34, 255, 0, 1);");
        vbox.getChildren().addAll((Node) label,labelgrass);
        hbox.getChildren().addAll((Node) grid, vbox);
        Scene scene = new Scene(hbox, (rangeX+2)*width*45.5, (rangeY+2)*height*45.5);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        //primaryStage.show();

        System.out.println(this.myMap.toString());
        System.out.println();
        //System.out.println("System zakończył działanie");
    }

    public void threadExceptionHandler(){
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println("Nieprawidlowo wpisane dane: " + e);
                Platform.exit();
                System.exit(0);
            }
        });

    }

    public void updateMap(int statusOfMap){
        Platform.runLater(()->{
            grid.getChildren().clear();
            this.grid = new GridPane();
            drawMap(statusOfMap);
        });
    }

    private void init(SimulationEngine engine, String text){
        String[] array = text.split(" ");
        Thread threadEngine = new Thread(engine);
        threadEngine.start();

    }

    public void start(Stage primaryStage) {
        try {
            threadExceptionHandler();
            GrassField map = new GrassField(10,10,10);
            SimulationEngine engine = new SimulationEngine(map,20,this);
            this.myMap = map;
            this.primaryStage = primaryStage;
            Button button = new Button("Start");
            button.setPadding(new Insets(20, 100, 20 ,100));
            button.setStyle("-fx-font: 24 arial;");
            TextField text = new TextField("Enter directions");
            text.setPadding(new Insets(20,30,20,30));
            text.setStyle("-fx-font: 24 arial;");
            HBox hbox = new HBox(button);

            hbox.setAlignment(Pos.CENTER);
            hbox.setSpacing(20);

            button.setOnAction(actionEvent -> init( engine, text.getText()));
            Scene scene = new Scene(hbox, 400, 400);

            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();


        } catch (IllegalArgumentException exception) {
            // kod obsługi wyjątku
            System.out.println(exception.getMessage());

        }
        catch (RuntimeException e){
            System.out.println(e.getMessage());
        }
    }
}