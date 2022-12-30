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

    public Thread threadEngine;
    public SimulationEngine engine;

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
        //grid.setAlignment(Pos.CENTER);
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
        Label labelanimals = new Label("Ilosc zwierzatek: "+this.myMap.animalQuantity());
        labelanimals.setStyle("-fx-padding: 100 100 100 100;-fx-font: 24 arial;-fx-text-fill: rgba(34, 255, 0, 1);");

        //dziala ale jak sam widzisz, są warningi, zeby suspendu i resuma nie uzywac raczej w javie bo niebezpieczne.
        // jak znajdziesz lepsze wyjscie czy cos to dawaj znac, tez bede szukał.
        Button newBtn = new Button("stop");
        newBtn.setPadding(new Insets(5, 20, 5 ,20));
        newBtn.setStyle("-fx-font: 18 arial;");
        newBtn.setOnAction(ac->{
            this.threadEngine.suspend();
            newBtn.setText("start");
            newBtn.setOnAction(ac2->{
                this.threadEngine.resume();
            });
        });


        vbox.getChildren().addAll((Node) label,labelgrass,labelanimals,newBtn);
        hbox.getChildren().addAll((Node) grid, vbox);
        //hbox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(hbox, (rangeX+2)*width*45.5, (rangeY+2)*height*45.5);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();

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

    private void init(String nrOfAnimals, String genLimit, String eatingEnergy,
                      String minEnergyToReproduce, String initEnergy,
                      String takenEnergyEachDay, String globe, String newGrasses, String isItDeathField,
                      String width, String height, String n,
                      String someMadness, String fullRandomness) {
        int widthInt = Integer.parseInt(width);
        int heightInt = Integer.parseInt(height);
        int nInt = Integer.parseInt(n);
        GrassField map = new GrassField(widthInt, heightInt, nInt);
        this.myMap = map;
        this.myMap.genLimit = Integer.parseInt(genLimit);
        this.myMap.eatingEnergy = Integer.parseInt(eatingEnergy);
        this.myMap.minEnergyToReproduce = Integer.parseInt(minEnergyToReproduce);
        this.myMap.initEnergy = Integer.parseInt(initEnergy);
        this.myMap.takenEnergyEachDay = Integer.parseInt(takenEnergyEachDay);
        this.myMap.globe = Boolean.parseBoolean(globe);
        this.myMap.newGrasses = Integer.parseInt(newGrasses);
        this.myMap.isItDeathField = Boolean.parseBoolean(isItDeathField);
        this.myMap.fullRandomness = Boolean.parseBoolean(fullRandomness);

        this.engine = new SimulationEngine(myMap, Integer.parseInt(nrOfAnimals), this);
        this.engine.someMadness = Boolean.parseBoolean(someMadness);
        this.threadEngine = new Thread(engine);

        threadEngine.start();
        //abstractWorldMap
//        public final int genLimit = 10;
//        public final int eatingEnergy = 100;
//        public final int minEnergyToReproduce = 50;
//        public final int initEnergy = 50;
//        public final int takenEnergyEachDay = 25;
//        public final boolean globe = true;
//        public final int newGrasses = 40;
//        public final boolean isItDeathField = false;
//     GrassField
          //public final int width;
          //public final int height;
          //int n;
      // Simulation Engine
         //public final boolean someMadness = false;
      //animal:
        //public int fullRandomness = 0;
    }

    public HBox createHboxParameters(Label label,TextField text){

        //TextField text = new TextField("20");
        text.setPadding(new Insets(10,20,10,20));
        text.setStyle("-fx-font: 15 arial;");
        text.setMaxWidth(250);
        //Label label = new Label("Starting number of animals");
        label.setStyle("-fx-font: 15 arial;-fx-text-fill;");
        HBox hbox = new HBox(label,text);
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(20);
        return hbox;
    }

    public void start(Stage primaryStage) {
        try {
            threadExceptionHandler();


            this.primaryStage = primaryStage;
            Button button = new Button("Start");
            button.setPadding(new Insets(15, 50, 15 ,50));
            button.setStyle("-fx-font: 24 arial;");

            TextField nrOfAnimals = new TextField("20");
            Label label_nrOfAnimals = new Label("Starting number of animals");
            HBox nrOfAnimalsHBox = createHboxParameters(label_nrOfAnimals, nrOfAnimals);

            TextField genLimitField = new TextField("10");
            Label genLimitLabel = new Label("Generation limit");
            HBox genLimitHBox = createHboxParameters(genLimitLabel, genLimitField);

            TextField eatingEnergyField = new TextField("100");
            Label eatingEnergyLabel = new Label("Eating energy");
            HBox eatingEnergyHBox = createHboxParameters(eatingEnergyLabel, eatingEnergyField);

            TextField minEnergyToReproduceField = new TextField("50");
            Label minEnergyToReproduceLabel = new Label("Minimum energy to reproduce");
            HBox minEnergyToReproduceHBox = createHboxParameters(minEnergyToReproduceLabel, minEnergyToReproduceField);

            TextField initEnergyField = new TextField("50");
            Label initEnergyLabel = new Label("Initial energy");
            HBox initEnergyHBox = createHboxParameters(initEnergyLabel, initEnergyField);


            TextField takenEnergyEachDayField = new TextField("25");
            Label takenEnergyEachDayLabel = new Label("Taken energy each day");
            HBox takenEnergyEachDayHBox = createHboxParameters(takenEnergyEachDayLabel, takenEnergyEachDayField);

            TextField globeField = new TextField("true");
            Label globeLabel = new Label("Globe");
            HBox globeHBox = createHboxParameters(globeLabel, globeField);

            TextField newGrassesField = new TextField("40");
            Label newGrassesLabel = new Label("New grasses");
            HBox newGrassesHBox = createHboxParameters(newGrassesLabel, newGrassesField);

            TextField isItDeathFieldField = new TextField("false");
            Label isItDeathFieldLabel = new Label("Is it death field?");
            HBox isItDeathFieldHBox = createHboxParameters(isItDeathFieldLabel, isItDeathFieldField);


            TextField widthField = new TextField("10");
            Label widthLabel = new Label("Width");
            HBox widthHBox = createHboxParameters(widthLabel, widthField);

            TextField heightField = new TextField("10");
            Label heightLabel = new Label("Height");
            HBox heightHBox = createHboxParameters(heightLabel, heightField);

            TextField nField = new TextField("10");
            Label nLabel = new Label("Initial grasses");
            HBox nHBox = createHboxParameters(nLabel, nField);

            TextField someMadnessField = new TextField("false");
            Label someMadnessLabel = new Label("Some madness");
            HBox someMadnessHBox = createHboxParameters(someMadnessLabel, someMadnessField);

            TextField fullRandomnessField = new TextField("false");
            Label fullRandomnessLabel = new Label("Full randomness");
            HBox fullRandomnessHBox = createHboxParameters(fullRandomnessLabel, fullRandomnessField);


            VBox vbox0 = new VBox(button, eatingEnergyHBox, minEnergyToReproduceHBox, initEnergyHBox, takenEnergyEachDayHBox);
            vbox0.setAlignment(Pos.CENTER);
            vbox0.setSpacing(20);

            VBox vbox1 = new VBox(nrOfAnimalsHBox, genLimitHBox,globeHBox, newGrassesHBox, isItDeathFieldHBox );
            vbox1.setAlignment(Pos.CENTER);
            vbox1.setSpacing(20);

            VBox vbox2 = new VBox(widthHBox, heightHBox, nHBox, someMadnessHBox, fullRandomnessHBox);
            vbox2.setAlignment(Pos.CENTER);
            vbox2.setSpacing(20);


            HBox mainHbox = new HBox(vbox1,vbox0,vbox2);
            mainHbox.setAlignment(Pos.CENTER);
            mainHbox.setSpacing(20);


            button.setOnAction(actionEvent -> init(nrOfAnimals.getText(), genLimitField.getText(), eatingEnergyField.getText(), minEnergyToReproduceField.getText(), initEnergyField.getText(), takenEnergyEachDayField.getText(), globeField.getText(), newGrassesField.getText(), isItDeathFieldField.getText(),widthField.getText(), heightField.getText(), nField.getText(), someMadnessField.getText(), fullRandomnessField.getText()));
            Scene scene = new Scene(mainHbox, 400, 400);

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