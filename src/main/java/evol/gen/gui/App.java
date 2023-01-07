package evol.gen.gui;

import evol.gen.*;

import evol.gen.GrassField;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

public class App extends Application{

    private GridPane grid = new GridPane();
    private GrassField myMap;
    private  int width = 45;
    private int height = 45;
    public Stage primaryStage;


    public final int gridFullWidth = 500;
    public final int gridFullHeight = 500;
    private final Object lock = new Object();

    public Thread threadEngine;
    public SimulationEngine engine;

    public Stage animalStage;

    public boolean isTracked = false;
    public Animal trackedAnimal;

    public boolean isStopped = false;
    private VBox drawObject(Vector2d position) {
        VBox result = null;
        if (this.myMap.isOccupied(position)) {
            Object object = this.myMap.objectAt(position);
            if (object != null) {
                GuiElementBox newElem = new GuiElementBox((IMapElement) object,width,height,this.myMap.averageEnergy());
                result = newElem.getBox();

            } else {
                result = new VBox(new Label(""));
            }
        } else {
            result = new VBox(new Label(""));
        }
        return result;
    }

    public HBox getAnimalInfo(){
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        int quantity = 0;
        for (Vector2d key : this.myMap.animals.keySet()) {
            VBox vbox = new VBox();
            Label positionLabel = new Label(key.toString()+":");
            positionLabel.setStyle("-fx-font: 11 arial");
            vbox.getChildren().add(positionLabel);
            vbox.setSpacing(5);

            PriorityQueue<Animal> queue = this.myMap.animals.get(key);
            boolean flag = false;
            for (Animal animal : queue) {
                VBox infoBox = new VBox();

                    Label energyLabel = new Label(String.valueOf(animal.energy));
                    energyLabel.setStyle("-fx-font: 11 arial");
                    String gen = animal.gen;
                    if(gen.length() > 6) gen = gen.substring(0,6) + "...";
                    Label genLabel = new Label(gen);
                    genLabel.setStyle("-fx-font: 8 arial;-fx-font-weight:bold;");

                    infoBox.getChildren().addAll((Node) energyLabel,genLabel);
                infoBox.setSpacing(2);
                infoBox.setAlignment(Pos.CENTER);
                vbox.getChildren().add(infoBox);
                flag = true;
            }
            for (Animal animal : this.myMap.temporaryAnimalsArray) {
                VBox infoBox = new VBox();

                Label energyLabel = new Label(String.valueOf(animal.energy));
                energyLabel.setStyle("-fx-font: 11 arial");

                String gen = animal.gen;
                if(gen.length() > 6) gen = gen.substring(0,6) + "...";
                Label genLabel = new Label(gen);
                genLabel.setStyle("-fx-font: 7 arial;-fx-font-weight:bold;");

                infoBox.getChildren().addAll((Node) energyLabel,genLabel);
                infoBox.setSpacing(2);
                infoBox.setAlignment(Pos.CENTER);
                vbox.getChildren().add(infoBox);
                flag = true;
            }

            if(flag) {
                hbox.getChildren().add(vbox);
                hbox.setSpacing(2);
                quantity+=1;
                if(quantity >= 15){
                    Label kropki = new Label(". . .");
                    hbox.getChildren().add(kropki);
                    return hbox;
                }
            }

        }
        return hbox;
    }


    public HBox fiveMostPopularGenes(){
        ArrayList<copiedAnimal> sortedAnimals = new ArrayList<>();

        for (Vector2d key : this.myMap.animals.keySet()) {
            PriorityQueue<Animal> queue = this.myMap.animals.get(key);
            for (Animal animal : queue) {
                sortedAnimals.add(new copiedAnimal(animal.energy, animal.gen,animal.age,animal.children,animal.position));
            }
            for (Animal animal : this.myMap.temporaryAnimalsArray) {
                sortedAnimals.add(new copiedAnimal(animal.energy, animal.gen,animal.age,animal.children,animal.position));
            }

        }
        Collections.sort(sortedAnimals,new CopiedAnimalComparator());

        HBox hbox = new HBox();
        Label label = new Label("5 najpopularniejszych genomow: ");
        label.setStyle("-fx-font: 18 arial;-fx-font-weight:bold;");
        hbox.getChildren().add(label);
        hbox.setSpacing(3);
        for(int i =0; i < Math.min(sortedAnimals.size(),5);i++){
            copiedAnimal animal = sortedAnimals.get(i);
            VBox vbox = new VBox();
            vbox.setSpacing(4);

            label = new Label(animal.gen);
            label.setStyle("-fx-font: 14 arial;-fx-font-weight:bold;");
            label.setPadding(new Insets(0,5,0,5));
            label.setAlignment(Pos.CENTER);
            vbox.getChildren().add(label);

            Label label0 = new Label("("+animal.energy+")");
            label0.setStyle("-fx-font: 14 arial;-fx-font-weight:bold;");
            label0.setPadding(new Insets(0,5,0,5));
            label0.setAlignment(Pos.CENTER);
            vbox.getChildren().add(label0);

            hbox.getChildren().add(vbox);
        }
        return hbox;
    }
    Vector2d getBestAnimal(){
        ArrayList<copiedAnimal> sortedAnimals = new ArrayList<>();

        for (Vector2d key : this.myMap.animals.keySet()) {
            PriorityQueue<Animal> queue = this.myMap.animals.get(key);
            for (Animal animal : queue) {
                sortedAnimals.add(new copiedAnimal(animal.energy, animal.gen,animal.age,animal.children,animal.position));
            }
            for (Animal animal : this.myMap.temporaryAnimalsArray) {
                sortedAnimals.add(new copiedAnimal(animal.energy, animal.gen,animal.age,animal.children,animal.position));
            }

        }
        Collections.sort(sortedAnimals,new CopiedAnimalComparator());
        if(sortedAnimals.size() == 0) return null;
        return sortedAnimals.get(0).position;
    }

    public boolean checkingPositionOnMap(int j, int i,int rangeY){
        int posX = j-1;
        int posY = rangeY -i + 1;
        if(this.myMap.animals.containsKey(new Vector2d(j,i))){
            return true;
        }
        return false;
    }


    public void addEventHandler(VBox result,Vector2d position){
        result.setOnMouseClicked(event -> {
            if(this.isStopped){
                Animal animal = this.myMap.animals.get(position).peek();
                this.isTracked = true;
                this.trackedAnimal = animal;
                this.animalStage = new Stage();

                this.updateAnimalDetailsMap();

            }
        });;
    }

    public void updateAnimalDetailsMap(){
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(1);
        Label pos = new Label("Pozycja na mapie:"+this.trackedAnimal.position);
        pos.setStyle("-fx-padding: 20 20 20 20;-fx-font: 16 arial;-fx-font-weight:bold;-fx-text-fill: rgba(31, 31, 31, 1);");
        Label gen = new Label("Genom:"+this.trackedAnimal.gen);
        gen.setStyle("-fx-padding: 20 20 20 20;-fx-font: 16 arial;-fx-font-weight:bold;-fx-text-fill: rgba(64, 64, 64, 1);");
        Label activatedGen = new Label("Aktywny gen: "+this.trackedAnimal.gen.charAt(this.trackedAnimal.activatedGen));
        activatedGen.setStyle("-fx-padding: 20 20 20 20;-fx-font: 16 arial;-fx-font-weight:bold;-fx-text-fill: rgba(96, 96, 96, 1);");
        Label energy= new Label("Ilosc energii: "+this.trackedAnimal.energy);
        energy.setStyle("-fx-padding: 20 20 20 20;-fx-font: 16 arial;-fx-font-weight:bold;-fx-text-fill: rgba(128, 128, 128, 1);");

        Label eatenGrass= new Label("Zjedzone roslinki: "+this.trackedAnimal.eatenGrass);
        eatenGrass.setStyle("-fx-padding: 20 20 20 20;-fx-font: 16 arial;-fx-font-weight:bold;-fx-text-fill: rgba(34,139,34, 1);");


        Label children= new Label("Ilosc dzieci: "+this.trackedAnimal.children);
        children.setStyle("-fx-padding: 20 20 20 20;-fx-font: 16 arial;-fx-font-weight:bold;-fx-text-fill: rgba(160, 160, 160, 1);");
        Label days= new Label("ilosc przezytych dni: "+this.trackedAnimal.age);
        days.setStyle("-fx-padding: 20 20 20 20;-fx-font: 16 arial;-fx-font-weight:bold;-fx-text-fill: rgba(192, 192, 192, 1);");
        String isDeadMessage = this.trackedAnimal.energy == 0 ?  "zmarło w wieku "+this.trackedAnimal.age : "nie zmarlo";
        Label isDead= new Label(isDeadMessage);
        isDead.setStyle("-fx-padding: 20 20 20 20;-fx-font: 16 arial;-fx-font-weight:bold;-fx-text-fill: rgba(255, 71, 71, 1);");
        vbox.getChildren().addAll((Node)pos, gen,activatedGen,energy,eatenGrass,children,days,isDead);
        Scene scene = new Scene(vbox, 300, 300);
        this.animalStage.setHeight(800);
        this.animalStage.setWidth(400);
        this.animalStage.setScene(scene);
        //this.animalStage.setMaximized(true);
        this.animalStage.show();
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
                if(this.checkingPositionOnMap(j,i,rangeY)){
                    this.addEventHandler(result,new Vector2d(j,i));
                }
//grid.add(result, pos.x+1, rangeY-pos.y+1);
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
            label.setStyle("-fx-padding: 80 20 20 20;-fx-font: 16 arial;-fx-font-weight:bold;-fx-text-fill: rgba(140, 0, 0, 1);");
        }else if(statusOfMap == 1){
            label = new Label("przemieszczanie sie");
            label.setStyle("-fx-padding: 80 20 20 20;-fx-font: 16 arial;-fx-font-weight:bold;-fx-text-fill: rgba(242, 133, 0, 1);");
        }else if(statusOfMap == 2){
            label = new Label("zjadanie traw");
            label.setStyle("-fx-padding: 80 20 20 20;-fx-font: 16 arial;-fx-font-weight:bold;-fx-text-fill: rgba(137, 242, 0, 1);");
        }else if(statusOfMap == 3){
            label = new Label("rozmnazanie sie!");
            label.setStyle("-fx-padding: 80 20 20 20;-fx-font: 16 arial;-fx-font-weight:bold;-fx-text-fill: rgba(234, 0, 242, 1);");
        }else if(statusOfMap == 4){
            label = new Label("koniec dnia");
            label.setStyle("-fx-padding: 80 20 20 20;-fx-font: 16 arial;-fx-font-weight:bold;-fx-text-fill: rgba(40, 0, 242, 1);");
        }else if(statusOfMap == 5){
            label = new Label("nowe roslinki");
            label.setStyle("-fx-padding: 80 20 20 20;-fx-font: 16 arial;-fx-font-weight:bold;-fx-text-fill: rgba(34, 255, 0, 1);");
        }

        VBox vbox = new VBox();
        Label labelgrass = new Label("Ilosc roslinek:"+this.myMap.getGrasses().size());
        labelgrass.setStyle("-fx-padding: 20 20 20 20;-fx-font: 16 arial;-fx-font-weight:bold;-fx-text-fill: rgba(34, 255, 0, 1);");
        Label labelanimals = new Label("Ilosc zwierzatek: "+this.myMap.animalQuantity());
        labelanimals.setStyle("-fx-padding: 20 20 20 20;-fx-font: 16 arial;-fx-font-weight:bold;-fx-text-fill: rgba(250, 189, 47, 1);");

        Label labelFreePlaces= new Label("Ilosc wolnych miejsc (bez zwierzat): "+this.myMap.freePlaces());
        labelFreePlaces.setStyle("-fx-padding: 20 20 20 20;-fx-font: 16 arial;-fx-font-weight:bold;-fx-text-fill: rgba(121, 85, 72, 1);");

        Label emptySpace= new Label("Ilosc wolnych miejsc (puste pola): "+this.myMap.emptyFields());
        emptySpace.setStyle("-fx-padding: 20 20 20 20;-fx-font: 16 arial;-fx-font-weight:bold;-fx-text-fill: rgba(33, 150, 243, 1);");



        Label averageLengOfLife= new Label("srednia dlugosc zycia (dla martwych): "+this.myMap.averageDeathAge.averageAge()+" dni");
        averageLengOfLife.setStyle("-fx-padding: 20 20 20 20;-fx-font: 16 arial;-fx-font-weight:bold;-fx-text-fill: rgba(89, 54, 0, 1);");

        Label averageEnergy= new Label("srednia energia (dla zywych): "+this.myMap.averageEnergy());
        averageEnergy.setStyle("-fx-padding: 20 20 20 20;-fx-font: 16 arial;-fx-font-weight:bold;-fx-text-fill: rgba(255, 71, 71, 1);");




        //dziala ale jak sam widzisz, są warningi, zeby suspendu i resuma nie uzywac raczej w javie bo niebezpieczne.
        // jak znajdziesz lepsze wyjscie czy cos to dawaj znac, tez bede szukał.
        Button newBtn = new Button("stop");
        newBtn.setPadding(new Insets(5, 20, 5 ,20));

        newBtn.setStyle("-fx-font: 18 arial;");
        newBtn.setOnAction(ac->{
            this.threadEngine.suspend();
            newBtn.setText("start");
            Vector2d pos = this.getBestAnimal();
            if(pos != null){
                VBox result = drawObject(pos);
                result.setStyle("-fx-background-color: rgb(255, 191, 0)");
                ColorAdjust colorAdjust = new ColorAdjust();
                colorAdjust.setHue(0.15);
                colorAdjust.setBrightness(-0.5);
                colorAdjust.setSaturation(-0.5);
                result.setEffect(colorAdjust);
                grid.add(result, pos.x+1, rangeY-pos.y+1);
                this.addEventHandler(result,new Vector2d(pos.x,pos.y));

                this.isStopped =  true;
                if(this.isTracked) {
                    this.animalStage.close();
                    this.isTracked = false;
                }
                this.animalStage = new Stage();
                this.closingStage();
            }
            newBtn.setOnAction(ac2->{
                this.isStopped = false;
                this.threadEngine.resume();
            });
        });
        HBox buttonContainer = new HBox(newBtn);
        buttonContainer.setMargin(newBtn,new Insets(10, 20, 20, 10));
        vbox.getChildren().addAll((Node) label,labelgrass,labelanimals,labelFreePlaces,emptySpace,this.fiveMostPopularGenes(),averageEnergy,averageLengOfLife,buttonContainer,this.getAnimalInfo());
        hbox.getChildren().addAll((Node) grid, vbox);
        grid.setPrefSize(300, 400);
        Scene scene = new Scene(hbox, (rangeX+2)*width*45.5, (rangeY+2)*height*45.5);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
        if(this.isTracked) this.updateAnimalDetailsMap();
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
                      String someMadness, String fullRandomness,String minimumGen,String maximumGen) {
        int widthInt = Integer.parseInt(width);
        int heightInt = Integer.parseInt(height);
        int nInt = Integer.parseInt(n);
        boolean isItDeathFieldBoolean = Boolean.parseBoolean(isItDeathField);
        GrassField map = new GrassField(widthInt, heightInt, nInt, isItDeathFieldBoolean);
        this.myMap = map;
        this.myMap.genLimit = Integer.parseInt(genLimit);
        this.myMap.eatingEnergy = Integer.parseInt(eatingEnergy);
        this.myMap.minEnergyToReproduce = Integer.parseInt(minEnergyToReproduce);
        this.myMap.initEnergy = Integer.parseInt(initEnergy);
        this.myMap.takenEnergyEachDay = Integer.parseInt(takenEnergyEachDay);
        this.myMap.globe = Boolean.parseBoolean(globe);
        this.myMap.newGrasses = Integer.parseInt(newGrasses);
        this.myMap.fullRandomness = Boolean.parseBoolean(fullRandomness);
        this.myMap.minNrOfMutations = Integer.parseInt(minimumGen);
        this.myMap.maxNrOfMutations = Integer.parseInt(maximumGen);

        this.width = (int) (this.gridFullWidth)/widthInt;
        this.height = (int) (this.gridFullHeight)/heightInt;
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });

        this.engine = new SimulationEngine(myMap, Integer.parseInt(nrOfAnimals), this);
        this.engine.someMadness = Boolean.parseBoolean(someMadness);
        this.threadEngine = new Thread(engine);

        threadEngine.start();
    }

    public void closingStage(){
        this.animalStage.setOnCloseRequest(event -> {
           this.isTracked = false;
        });
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
            button.setPadding(new Insets(5, 50, 5 ,50));
            button.setStyle("-fx-font: 24 arial;");

            TextField nrOfAnimals = new TextField("20");
            Label label_nrOfAnimals = new Label("Starting number of animals");
            HBox nrOfAnimalsHBox = createHboxParameters(label_nrOfAnimals, nrOfAnimals);

            TextField genLimitField = new TextField("10");
            Label genLimitLabel = new Label("Gen length");
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


            //z tych parametrów jeszcze nie ma pożytku
            TextField minimalGenField = new TextField("0");
            Label minimalGenLabel = new Label("Minimalna liczba mutacji");
            HBox minimalGenHBox = createHboxParameters(minimalGenLabel, minimalGenField);

            TextField maximumGenField = new TextField("10");
            Label maximumGenLabel = new Label("Maksymalna liczba mutacji");
            HBox maximumGenHBox = createHboxParameters(maximumGenLabel, maximumGenField);

            TextField excelNameField = new TextField("");
            Label excelNameLabel = new Label("Nazwa pliku csv");
            HBox excelNameHBox = createHboxParameters(excelNameLabel, excelNameField);


            VBox vbox0 = new VBox(button, globeHBox, isItDeathFieldHBox, someMadnessHBox, fullRandomnessHBox, maximumGenHBox);
            vbox0.setAlignment(Pos.CENTER);
            vbox0.setSpacing(20);

            VBox vbox1 = new VBox(nrOfAnimalsHBox, initEnergyHBox,minEnergyToReproduceHBox, genLimitHBox, takenEnergyEachDayHBox, minimalGenHBox );
            vbox1.setAlignment(Pos.CENTER);
            vbox1.setSpacing(20);

            VBox vbox2 = new VBox(widthHBox, heightHBox, nHBox, newGrassesHBox, eatingEnergyHBox, excelNameHBox);
            vbox2.setAlignment(Pos.CENTER);
            vbox2.setSpacing(20);


            HBox mainHbox = new HBox(vbox1,vbox0,vbox2);
            mainHbox.setAlignment(Pos.CENTER);
            mainHbox.setSpacing(20);


            button.setOnAction(
                    actionEvent -> init(nrOfAnimals.getText()  ,
                            genLimitField.getText(),
                            eatingEnergyField.getText(),
                            minEnergyToReproduceField.getText(),
                            initEnergyField.getText(),
                            takenEnergyEachDayField.getText(),
                            globeField.getText(),
                            newGrassesField.getText(),
                            isItDeathFieldField.getText(),
                            widthField.getText(),
                            heightField.getText(),
                            nField.getText(),
                            someMadnessField.getText(),
                            fullRandomnessField.getText(),
                            minimalGenField.getText(),
                            maximumGenField.getText())
            );
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