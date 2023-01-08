package evol.gen.gui;

import evol.gen.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.FontWeight;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class GuiElementBox {
    private Image image;
    private ImageView imageView;
    private Label label;
    private VBox box = new VBox(-5);

    public VBox getBox(){
        return this.box;
    }

    public double averageEnergy;



    public GuiElementBox(IMapElement element,int width,int height,double averageEnergy) {
        this.averageEnergy = averageEnergy;
        try {
            this.image = new Image(new FileInputStream(element.getLinkToImage()));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e + "Nie znaleziono pliku ze zdjęciem");
        }
        this.imageView = new ImageView(image);

        if(element.toString().equals("*")){
            this.imageView.setFitWidth(Math.max(width-10,6));
            this.imageView.setFitHeight(Math.max(height-10,6));
            label = new Label(element.getPosition().toString());


            label.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
            StackPane stack = new StackPane(this.imageView,label);
            box.getChildren().addAll((Node) stack);
            box.setAlignment(Pos.CENTER);


        }
        else{
            this.imageView.setFitWidth(Math.max(width-4,6));
            this.imageView.setFitHeight(Math.max(height-4,6));

            ColorAdjust monochrome = new ColorAdjust();
            monochrome.setSaturation(generateProperSaturation(element.getEnergy()));
            this.imageView.setEffect(monochrome);
            Animal animal = (Animal) element;
            //int nrOfAnimals = ((GrassField)animal.myMap).animals.get(animal.position).size();
            label = new Label(element.getPosition().toString());
            label.setStyle("-fx-font: 10 arial;");
            StackPane stack = new StackPane(this.imageView,label);
            label.setAlignment(Pos.BOTTOM_CENTER);
            label.setTranslateY(10);


            box.getChildren().addAll((Node) stack);
            box.setAlignment(Pos.CENTER);
        }

    }

    public double generateProperSaturation(int energy){
        double calculatedSaturation = 0.0;
        //it should be from 0.0 to 1.0
        if(this.averageEnergy < 0.1) return 1;

        if((double)((double)energy/this.averageEnergy) < 0.1) return 1.0;
        if((double)((double)energy/this.averageEnergy) < 0.2) return 0.9;
        if((double)((double)energy/this.averageEnergy) < 0.3) return 0.8;
        if((double)((double)energy/this.averageEnergy) < 0.4) return 0.7;
        if((double)((double)energy/this.averageEnergy) < 0.5) return 0.6;
        if((double)((double)energy/this.averageEnergy) < 0.6) return 0.5;
        if((double)((double)energy/this.averageEnergy) < 0.7) return 0.4;
        if((double)((double)energy/this.averageEnergy) < 0.8) return 0.3;
        if((double)((double)energy/this.averageEnergy) < 0.9) return 0.2;

        return 0;
    }

}