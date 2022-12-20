package evol.gen.gui;

import evol.gen.IMapElement;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class GuiElementBox {
    private Image image;
    private ImageView imageView;
    private Label label;
    private VBox box = new VBox();

    public VBox getBox(){
        return box;
    }

    public GuiElementBox(IMapElement element) {
        try {
            image = new Image(new FileInputStream(element.getImage()));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e + "nie znaleziono pliku");
        }
        imageView = new ImageView(image);
        imageView.setFitWidth(30);
        imageView.setFitHeight(30);

        if(element.toString().equals("*")){ label = new Label("Grass"); }
        else{ label = new Label("A " + element.getPosition().toString()); }

        box.getChildren().addAll((Node) imageView, label);
        box.setAlignment(Pos.CENTER);
    }
}
