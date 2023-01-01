package evol.gen;

import java.util.*;
import java.util.HashMap;

import java.util.concurrent.ThreadLocalRandom;
public class Animal extends AbstractWorldMapElement{

    public IWorldMap myMap;
    protected Random randomizer = ThreadLocalRandom.current();
    public int energy = 0; //ilosc energii jaką zwierzę posiada na mapie
    public int age = 0;
    public int children = 0;

    public String gen = ""; //gen danego zwierzaka

    public boolean isDead = false;
    public int activatedGen = 0; // mowi o tym, ktory gen jest aktywny danego dnia
    public boolean moved = false;
    public int fullRandomness = 0; //WARIANT DO ZAZNACZENIA!

    public int eatenGrass = 0;

public final int id;
    protected final List<IPositionChangeObserver> observers = new ArrayList<>();
    private MapDirection orientation =  MapDirection.NORTH;
    //w konstruktorach przypisujemy zwierze do danej mapy, ale nie umieszczamy zwierzecia na tej mapie!!

    public Animal(GrassField map, Vector2d initialPosition){
            super(initialPosition);
            this.myMap = map;
            this.initialGen();
            this.initDirection();
            this.initActivatedGen();
            this.energy = ((GrassField) this.myMap).initEnergy;
            this.id = ((GrassField) this.myMap).addId();
            this.fullRandomness = this.myMap.setFullRandomness();

    }
    public String createNewGen(Animal Animal1,Animal Animal2){
        int totalEnergy = Animal1.energy + Animal2.energy;
        // nowy zwierzak ma otrzymac probabilityOfFirst*100 % genow Animal1 oraz probabilityOfSec*100% genow Animal2
        //  losowa liczba (wybranych również losowo) genów zmienia swoj stan na inny!-  mutacje
        String leftPart = this.parentRandomGen((double)Animal1.energy/(double)totalEnergy,Animal1.gen);
        String rightPart = this.parentRandomGen((double)Animal2.energy/(double)totalEnergy,Animal2.gen);
        StringBuilder sb = new StringBuilder();
        sb.append(leftPart);
        sb.append(rightPart);
        while(sb.toString().length() < Animal1.gen.length()) sb.append("0");
        String newGen = sb.toString();
        Animal1.addEnergy(-(int) ((GrassField) this.myMap).initEnergy/2); //ich energia musi sie zmniejszyc
        Animal2.addEnergy(-(int) ((GrassField) this.myMap).initEnergy/2); //ich energia musi sie zmniejszyc
        return this.mutations(newGen);
    }
    public Animal(IWorldMap map, Vector2d initialPosition,Animal Animal1, Animal Animal2){
        super(initialPosition);
        this.myMap = map;
        this.createNewGen(Animal1,Animal2);
        this.initDirection();
        this.initActivatedGen();
        this.energy = ((GrassField) this.myMap).initEnergy;
        this.id = ((GrassField) this.myMap).addId();
        this.fullRandomness = this.myMap.setFullRandomness();
    }

    public String parentRandomGen(double probability,String sequence){ // O(n)
        int numChars = (int) Math.floor(sequence.length() * probability);
        StringBuilder newGen = new StringBuilder();
        char[] array = sequence.toCharArray();
        ArrayList<String> list = new ArrayList<>();
        for (char c : array) {
            list.add(String.valueOf(c));
        }
        for (int i = 0; i < numChars; i++) {
            int index = randomizer.nextInt(list.size());
            newGen.append(list.get(index));
            String temp = list.get(index);
            list.set(index, list.get(list.size()-1));
            list.set(list.size()-1, temp);
            list.remove(list.size()-1);
        }
        return newGen.toString(); // Print the result
    }

    public void initialGen(){
        int length = ((GrassField)this.myMap).genLimit; // desired length of the string
        StringBuilder sb = new StringBuilder();


        for (int i = 0; i < length; i++) {
            int randomInt = randomizer.nextInt(8); // generate a random number between 0 and 7
            char randomChar = (char)('0' + randomInt); // convert the random number to a character
            sb.append(randomChar); // add the character to the string
        }
        this.gen = sb.toString();
    }
    public String mutations(String gen){
        HashMap<Integer, Character> selectedChars = new HashMap<>(); //(pozycja, nowy znak)

        StringBuilder newGen = new StringBuilder();

        int quantity = randomizer.nextInt(gen.length());
        for(int i =0; i < quantity; i++){
            int index = randomizer.nextInt(gen.length());//losowany index do zamiany
            while(selectedChars.containsKey(index)) index = randomizer.nextInt(gen.length()); //ma znalezc inny ten index!
            char c = gen.charAt(index);
            char newChar;
            if(this.fullRandomness == 1){
                newChar = (char)(randomizer.nextInt(8) + '0');
                while(c == newChar) newChar = (char)(randomizer.nextInt(8) + '0');
            }
            else{
                int r = randomizer.nextInt(2);
                if (r == 0) {
                    // Decrease the char value
                    if (c == '7') {
                        newChar = '6';
                    } else if (c == '0') {
                        newChar = '7';
                    } else {
                        c--;
                        newChar = c;
                    }
                } else {
                    // Increase the char value
                    if (c == '7') {
                        newChar = '0';
                    } else if (c == '0') {
                        newChar = '1';
                    } else {
                        c++;
                        newChar = c;
                    }
                }
            }
            selectedChars.put(index,newChar);
        }
        for(int i =0; i < gen.length();i++){
            if(selectedChars.containsKey(i)) newGen.append(selectedChars.get(i));
            else newGen.append(gen.charAt(i));
        }
        //System.out.println(selectedChars);
        this.gen = newGen.toString();
        return newGen.toString();
    }

    public void initDirection(){
         // Use the current time as the seed
        MapDirection[] arr = MapDirection.values();
        int index = randomizer.nextInt(arr.length);
        this.orientation = arr[index];

    }
    public void initActivatedGen(){
        this.activatedGen = randomizer.nextInt(this.gen.length());
    }


    public String toString(){
//        if(!((GrassField)this.myMap).animals.containsKey(this.position)) return "1";
//        return""+((GrassField)this.myMap).animals.get(this.position).size();
        //return ""+this.age;
        return this.orientation.toString();
    }

    public String extendedToString(){
        return position.toString() + " " + orientation.toString();
    }

    public void nextActivatedGen(boolean someMadness){
        if(!someMadness) this.activatedGen = (this.activatedGen + 1) %this.gen.length();
        else{

            int scope = randomizer.nextInt(10);
            if(scope < 2){
                int newActivated = randomizer.nextInt(this.gen.length()-1)+1;  //number from 1 to this.gen.length-1
                this.activatedGen = (this.activatedGen + newActivated) %this.gen.length();
            }
            else{
                this.activatedGen = (this.activatedGen + 1) %this.gen.length();
            }
        }
    }

    public void move(MoveDirection direction0){
        switch(direction0){
            case rotate0:
                this.GoForward();
                break;
            case rotate1:
                rotatingOrientation(1);
                this.GoForward();
                break;
            case rotate2:
                rotatingOrientation(2);
                this.GoForward();
                break;
            case rotate3:
                rotatingOrientation(3);
                this.GoForward();
                break;
            case rotate4:
                rotatingOrientation(4);
                this.GoForward();
                break;
            case rotate5:
                rotatingOrientation(5);
                this.GoForward();
                break;
            case rotate6:
                rotatingOrientation(6);
                this.GoForward();
                break;
            case rotate7:
                rotatingOrientation(7);
                this.GoForward();
                break;
            default:
                System.out.println("Nieznany kierunek");
                }
        }
    public void rotatingOrientation(int range){
        for(int i=0;i<range;i++){
            orientation = orientation.next();
        }
    }


    public void GoForward(){

        Vector2d newVectorek0 = this.position.add(orientation.toUnitVector());

        if(!myMap.canMoveTo(newVectorek0)){

            this.myMap.goSomewhereElse(this,newVectorek0);

        }else{
            this.position = newVectorek0;
            this.myMap.addAnimal(newVectorek0,this);
        }
    }


    public MapDirection getDirection() {
        return orientation;
    }


    public void addObserver(IPositionChangeObserver observer){
        this.observers.add(observer);
    }

    public void removeObserver(IPositionChangeObserver observer){
        this.observers.remove(observer);
    }

    protected void positionChanged(Vector2d oldPosition, Vector2d newPosition){
        for(IPositionChangeObserver observer : this.observers){ //dla wszystkich obserwowanych map trzeba zrobic zmiane!
            observer.positionChanged(oldPosition, newPosition);
        }
    }
    public void addEnergy(int energy){
        this.energy += energy;
    }
    public void addChlidren(int children){
        this.children += children;
    }
    public void increaseAge(){
        this.age+=1;
    }

    public void increaseNrOfEatenGrass(){
        this.eatenGrass+=1;
    }
}

