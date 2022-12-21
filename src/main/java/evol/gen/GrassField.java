package evol.gen;
import java.util.*;

public class GrassField extends AbstractWorldMap{
    int n;

    private HashMap<Vector2d,Grass> grasses = new HashMap<>();
    public GrassField(int width,int height,int n){
        super(new Vector2d(width,height));//nie wplynie na wielkosc mapy w klasie nadrzednej!
        this.n = n;
        this.createGrasses(this.n);
    }

    public void createGrasses(int n){ //definiuje ile mozna stworzyc nowych trawek i randomowo je umeiszcza tam gdzie nie ma obiektow
        for(int i = 0; i < n; i++){
            Vector2d newVec = uniqPosVector(new Vector2d(topRight.x,topRight.y));
            grasses.put(newVec,new Grass(newVec));

        }
    }


    public boolean isOccupied(Vector2d position) {
        if(super.isOccupied(position))return true;//jesli tam jest zwierzę to zwroc true
        return grasses.containsKey(position); // jesli tam jest roslina zwroc true
        //zwroc false w przeciwnym wypadku
    }


    public Object objectAt(Vector2d position){                        //zwroci obiekt lub null (jesli nie znalezione)
        return (super.objectAt(position) != null) ?//najpierw sprawdzi czy tam jest zwierze
                super.objectAt(position) : // po pierwsze jesli jest tam zwierzę to zwroci to zwierze, jesli nie to trawe!
                grasses.get(position);
    }

    @Override
    public HashMap<Vector2d, Animal> getAnimals() {
        return null;
    }


    public HashMap<Vector2d, Grass> getGrasses() {
        return grasses;
    }

    public void positionChangedGrass(Vector2d oldPosition, Vector2d newPosition){
        Grass grass = this.grasses.get(oldPosition);
        this.grasses.remove(oldPosition);
        this.grasses.put(newPosition, grass);
    }

    protected void eatingGrass(Vector2d vector){
        if(super.getNumberOfAnimalsAtPosition(vector) > 0){
            this.grasses.remove(vector);
            super.eatingGrass(vector);
        }
    }

    protected void eatingTime(){
        ArrayList<Vector2d> originalVectorki= new ArrayList<>();
        Set<Vector2d> keys = this.grasses.keySet();
        originalVectorki.addAll(keys);
        originalVectorki.forEach(vector->{
           this.eatingGrass(vector);
        });
    }


    @Override
    public void positionChanged(Vector2d oldPosition, Vector2d newPosition) {
        return;
    }
}
