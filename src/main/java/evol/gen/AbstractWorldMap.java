package evol.gen;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractWorldMap implements IWorldMap, IPositionChangeObserver  {

    public HashMap<Vector2d,PriorityQueue<Animal>> animals = new HashMap<>();
    public ArrayList<Animal> temporaryAnimalsArray = new ArrayList<>();
    //zwierzeta mogą poruszac sie po obszarze definiowanym przez bottomLeft i topRight!
    //protected final Vector2d bottomLeft;
    protected final Vector2d topRight;
    public int counter = 0;
    //mapa bedzie wyswietlana zgodnie z wszystkimi elementami zawierajacymi sie na mapie

    //mapa bedzie zawierac wszystkie parametry
    private MapVisualizer displayer = new MapVisualizer(this);

    protected Random randomizer = ThreadLocalRandom.current();

    protected HashMap<Vector2d, Integer> deathfield = new HashMap<>();
    //limited gen:
    public final int genLimit = 10;

    public final int eatingEnergy = 100;
    public final int minEnergyToReproduce = 50;

    public final int initEnergy = 50;
    public final int takenEnergyEachDay = 25;
    public final boolean globe = true;

    public final int newGrasses = 40;

    public final boolean isItDeathField = false;

    public AbstractWorldMap(Vector2d topRight){
        if(topRight.x > 0 && topRight.y > 0) this.topRight = topRight;
            //this.bottomLeft = bottomLeft;
        else{
            this.topRight = new Vector2d(10,10);
            //this.bottomLeft = new Vector2d(0,0);
        }
        //this.initializeMap();

            //this.updateTotalBoundary(topRight);
    }
    public void initializeMap(){
        for(int i = 0; i < this.topRight.x;i++){
            for(int j = 0; j < this.topRight.y;j++){
                this.animals.put(new Vector2d(i,j),new PriorityQueue<>(new AnimalComparator()));
            }
        }
    }

    public String toString(){
        return displayer.draw(new Vector2d(0,0), this.topRight);
    }


    public boolean place(Animal animal) {
            this.addAnimal(animal);
            return true;
        }

    public boolean canMoveTo(Vector2d newPos){
        //jesli na danej pozycji cos jest, zwroc false natychmiast, w przeciwnym razie zwroc true/false zaleznie czy wychodzimy poza mape
        //przypadek 1:
        //zwierze chce dostac sie na pozycję, na ktorej jest trawa: wtedy zwierze moze tam wejsc, ale dla trawy trzeba znalezc nową pozycję!
        return newPos.follows(new Vector2d(0,0)) && newPos.precedes(this.getTopRight());
    }


    //wykorzystanie metody nadpisanej equals w Vector2D

    public boolean isOccupied(Vector2d position) {
        if(!animals.containsKey(position)) return false;
        PriorityQueue<Animal> queue = this.animals.get(position);
        return queue.size() > 0;
    }


    public Object objectAt(Vector2d position){                        //zwroci obiekt lub null (jesli nie znalezione)
        if(!animals.containsKey(position))
            return this.temporaryAnimalsArray.stream()
                .filter(a -> a.position == position)
                .findFirst()
                .orElse(null);;
        PriorityQueue<Animal> queue = this.animals.get(position);
        if(queue.size() == 0) return null;
        Animal animal = queue.peek();
        return animal; //zwroci najlepsze zwierze!
    }

    //public Vector2d getBottomLeft() {
//        return bottomLeft;
//    }

    public Vector2d getTopRight() {
        return topRight;
    }



    protected Vector2d getRandom(Vector2d vector){
        return new Vector2d(
                randomizer.nextInt(vector.x+1),
                randomizer.nextInt(vector.y+1));
    }
    protected Vector2d uniqPosVector(Vector2d vector){ //vector to graniczny vector! topRight naszej granicy
        Vector2d newVec = this.getRandom(this.topRight);
        while(this.objectAt(newVec) instanceof Grass){newVec = this.getRandom(vector);}
        return newVec;
    }





    abstract public HashMap<Vector2d, Grass> getGrasses();



    public ArrayList<Animal> moveAllAnimalsToArrayList() {
        ArrayList<Animal> animalsArray = new ArrayList<>();
        for (Map.Entry<Vector2d, PriorityQueue<Animal>> entry : this.animals.entrySet()) {
            Vector2d key = entry.getKey();
            PriorityQueue<Animal> queue = entry.getValue();
            while (queue != null && !queue.isEmpty()) {
                animalsArray.add(queue.poll());
            }
            queue.clear();
            //this.animals.remove(key);

        }
        //this.animals = new HashMap<>();
        return animalsArray;
    }
    public void addAnimal(Vector2d vectorek, Animal animal) {
        PriorityQueue<Animal> queue =this.animals.get(vectorek);
        if (queue == null) {
            queue = new PriorityQueue<>(new AnimalComparator());
            queue.add(animal);
            this.animals.put(vectorek, queue);
        }else{
            queue.add(animal);
        }
    }

    public void addAnimal(Animal animal) {
        PriorityQueue<Animal> queue =this.animals.get(animal.position);
        if (queue == null) {
            queue = new PriorityQueue<>(new AnimalComparator());
            queue.add(animal);
            this.animals.put(animal.position, queue);
        }else{
            queue.add(animal);
        }

    }


    public void reproduceIfPossible(Vector2d vectorek) {
        PriorityQueue<Animal> queue = this.animals.get(vectorek);
        if (queue != null && queue.size() >= 2) {
            Animal animal1 = queue.poll();
            Animal animal2 = queue.poll();
            if(animal1.energy < this.minEnergyToReproduce || animal2.energy < this.minEnergyToReproduce){
                queue.add(animal1);
                queue.add(animal2);
                PriorityQueue<Animal> tmpQueue = new PriorityQueue<>(new RestrictedAnimalComparator(this.minEnergyToReproduce));
                this.drainFromTo(queue,tmpQueue);
                Animal animal3 = tmpQueue.poll();
                Animal animal4 = tmpQueue.poll();
                if(animal3.energy < this.minEnergyToReproduce || animal4.energy < this.minEnergyToReproduce){
                    this.drainFromTo(tmpQueue,queue);
                    queue.add(animal3);
                    queue.add(animal4);
                    return;
                }
                Animal animal5 = new Animal(this,vectorek,animal3, animal4);
                animal3.addChlidren(1);
                animal4.addChlidren(1);
                this.drainFromTo(tmpQueue,queue);
                queue.add(animal3);
                queue.add(animal4);
                queue.add(animal5);
                return;
            }
            Animal animal3 = new Animal(this,vectorek,animal1, animal2);  // Create a new Animal object using the two popped animals
            animal1.addChlidren(1);
            animal2.addChlidren(1);
            queue.add(animal1);
            queue.add(animal2);
            queue.add(animal3);

        }
    }


    protected int getNumberOfAnimalsAtPosition(Vector2d vectorek){
        PriorityQueue<Animal> queue = this.animals.get(vectorek);
        if(queue == null) return 0;
        return queue.size();
    }
    protected void eatingGrass(Vector2d vector){
        PriorityQueue<Animal> queue = this.animals.get(vector);
        Animal animal1 = queue.poll();
        animal1.addEnergy(this.eatingEnergy);
        queue.add(animal1);
    }

    public void drainFromTo(PriorityQueue<Animal> queue,PriorityQueue<Animal> tmpQueue){
            while(queue.size() > 0){
                tmpQueue.add(queue.poll());
            }
    }

    public void goSomewhereElse(Animal animal,Vector2d vector){ //tylko ustawia pozycje zwierzaka na jakąś nową!
        if(this.globe){
            if(vector.x > this.topRight.x)vector.x = 0;
            if(vector.y > this.topRight.y) vector.y = 0;
            if(vector.x < 0) vector.x = this.topRight.x;
            if(vector.y < 0) vector.y = this.topRight.y;
            animal.setPosition(vector);
            this.addAnimal(vector,animal);
        }
        else{
            Vector2d newVec = this.getRandom(this.topRight);
            animal.setPosition(newVec);
            animal.addEnergy(-(int)this.initEnergy/2);
            this.addAnimal(animal);
        }
    }
    public int addId(){
        this.counter++;
        return this.counter;
    }

    public void clearMovedValues(){
        for (Map.Entry<Vector2d, PriorityQueue<Animal>> entry : this.animals.entrySet()) {
            PriorityQueue<Animal> queue = entry.getValue();
            Iterator<Animal> iterator = queue.iterator();
            while (iterator.hasNext()) {
                Animal animal = iterator.next();
                animal.moved = false;
            }
        }
    }

    public void printInfo(){
        for (Map.Entry<Vector2d, PriorityQueue<Animal>> entry : this.animals.entrySet()) {
            PriorityQueue<Animal> queue = entry.getValue();
            Iterator<Animal> iterator = queue.iterator();
            while (iterator.hasNext()) {
                Animal animal = iterator.next();
                System.out.println("position: "+animal.getPosition()+", energy: "+animal.energy+","+" age: "+animal.age);
            }
        }
    }


    public void reproducingTime(){
        for (Map.Entry<Vector2d, PriorityQueue<Animal>> entry : this.animals.entrySet()) {
            Vector2d vector = entry.getKey();
            this.reproduceIfPossible(vector);
        }
    }
    public void aging(){
        for (Map.Entry<Vector2d, PriorityQueue<Animal>> entry : this.animals.entrySet()) {
            PriorityQueue<Animal> queue = entry.getValue();
            Iterator<Animal> iterator = queue.iterator();
            while (iterator.hasNext()) {
                Animal animal = iterator.next();
                animal.increaseAge();
                animal.addEnergy(-takenEnergyEachDay);
                if(animal.energy <= 0){
                    animal.isDead = true;
                    this.deathFieldIncrementer(animal);
                }
            }
        }
    }

    public void deathFieldIncrementer(Animal animal){
        if(this.deathfield.containsKey(animal.getPosition())){
            this.deathfield.put(animal.getPosition(),this.deathfield.get(animal.getPosition())+1);
        }
        else{
            this.deathfield.put(animal.getPosition(),1);
        }
    }

    public void removingCorpse() {
        for (Map.Entry<Vector2d, PriorityQueue<Animal>> entry : this.animals.entrySet()) {
            PriorityQueue<Animal> queue = entry.getValue();
            Vector2d vector = entry.getKey();
            ArrayList<Animal> newArr = new ArrayList<>();

            while (!queue.isEmpty()) newArr.add(queue.poll());
            while (newArr.size() > 0) {
                Animal animal = newArr.remove(newArr.size() - 1);
                if (!animal.isDead) {
                    queue.add(animal);
                }
            }
        }

    }

    public int animalQuantity(){
        int number = 0;
        for (Map.Entry<Vector2d, PriorityQueue<Animal>> entry : animals.entrySet()) {
            PriorityQueue<Animal> queue = entry.getValue();
            number += queue.size();
        }
        return number;
    }


}