package evol.gen;
import evol.gen.gui.App;
import javafx.application.Platform;
import java.util.*;


public class SimulationEngine implements IEngine,Runnable {

    private final GrassField myMap;
    //private App observer;
    private int moveDelay = 300;
    private final boolean isAppOpening;
    private final boolean someMadness = false;
    private App observer;


    public SimulationEngine(GrassField map, int initAnimals) {
        this.myMap = map; //nasza kochana mapa

        this.isAppOpening =false;
        for(int i = 0; i < initAnimals;i++){
            Vector2d newVec =((GrassField) this.myMap).getRandom(((GrassField)this.myMap).topRight);
            Animal animal = new Animal(this.myMap, newVec);
            this.myMap.place(animal);
        }
        System.out.println(myMap.toString());
    }

    public SimulationEngine(GrassField map, int initAnimals,App app) {
        this.myMap = map; //nasza kochana mapa
        this.observer = app;
        this.isAppOpening =true;
        for(int i = 0; i < initAnimals;i++){
            Vector2d newVec =((GrassField) this.myMap).getRandom(((GrassField)this.myMap).topRight);
            Animal animal = new Animal(this.myMap, newVec);
            this.myMap.place(animal);
        }
        System.out.println(myMap.toString());
    }


    @Override
    public void run() {
//
//        Symulacja każdego dnia składa się z poniższej sekwencji kroków:
//
//        usunięcie martwych zwierząt z mapy,
//        skręt i przemieszczenie każdego zwierzęcia,
//        konsumpcja roślin na których pola weszły zwierzęta,
//        rozmnażanie się najedzonych zwierząt znajdujących się na tym samym polu,
//        wzrastanie nowych roślin na wybranych polach mapy.



        //mamy listę zwierzątek; teraz musimy uzyskać informację o ich aktywowanym genie:
        //w petli:
        // tworzymy teraz listę aktywnych genów (animal.activatedGen)
        //podaną listę aktywnych genów dodajemy do parsera, przeksztalci nam je w MoveDirection)
        // zwierzątka się zaczynają poruszać (tutaj zajdzie duzo roznych akcji)
        //warunek pętli: (poniewaz wiemy, ze kazdy zwierzak ma taką samą długość genomu, to wybierzmy jeden ze zwierzakow i dla niego robmy warunek)



        this.observer.updateMap(0);
        int maxDay = this.myMap.genLimit;
        int counter = 0;
        boolean start = true;
        while(this.myMap.animals.size() > 0 && counter < maxDay){//jesli zwierzakow nie ma to nie ma sensu uruchamiac petli, dzialamy do ostatniego dnia! (tzn. do dlugosci genomu)
            if(!start) {
                System.out.println("przed usuwaniem zwlok: ");
                System.out.println(this.myMap);
                this.observer.updateMap(0);
                this.sleepNow(2000);
                this.myMap.removingCorpse();
                this.sleepNow(2000);
            }
            start = false;
            this.observer.updateMap(1);
            System.out.println("po usunieciu zwlok: ");
            System.out.println(this.myMap);
            //przemieszczanie sie!!! ---------------------------------------------
            ArrayList<Vector2d> originalVectorki= new ArrayList<>();
            Set<Vector2d> keys = this.myMap.animals.keySet();
            originalVectorki.addAll(keys);
            originalVectorki.stream().forEach(vector->{
                PriorityQueue<Animal> queue = ((GrassField)this.myMap).animals.get(vector);
                while(!queue.isEmpty()) this.myMap.temporaryAnimalsArray.add(queue.poll());
                while(this.myMap.temporaryAnimalsArray.size() > 0){
                    Animal animal = this.myMap.temporaryAnimalsArray.remove(this.myMap.temporaryAnimalsArray.size()-1);
                    if(!animal.moved){
                        int active = animal.activatedGen;
                        animal.nextActivatedGen(someMadness);
                        animal.move(new OptionsParser().parseOneOption(animal.gen.charAt(active)));//ta metoda z automatu dodaje do animals!
                        animal.moved = true;
                        System.out.println(this.myMap);
                        this.observer.updateMap(1);
                        this.sleepNow(700);

                    }else{
                        this.myMap.addAnimal(animal.position,animal);
                    }

                }

            });
            this.myMap.clearMovedValues();
            // koniec funkcjonalnosci zwiazanej z przemieszczaniem sie!!------------------------
            System.out.println("po przemieszczeniu sie");
            this.observer.updateMap(2);
            this.sleepNow(2000);
            this.myMap.eatingTime();
            this.observer.updateMap(2);
            this.sleepNow(2000);
            this.observer.updateMap(3);
            this.sleepNow(2000);
            int oldNumberOfAnimals = this.myMap.animals.size();
            this.myMap.reproducingTime();
            counter = this.clearCounter(oldNumberOfAnimals,counter); // jesli pojawi sie nowe zwierze na mapie to musi wykonac caly swoj genom, wiec resetujemy counter;
            // inne zwierzaki będą powtarzać swoje sekwencje.
            this.observer.updateMap(3);
            this.sleepNow(2000);
            this.observer.updateMap(5);
            this.sleepNow(2000);
            this.myMap.createProperNumberOfGrass();
            this.observer.updateMap(5);
            this.sleepNow(2000);
            this.observer.updateMap(4);
            this.sleepNow(2000);
            this.myMap.aging();
            this.sleepNow(2000);
            System.out.println("-------INFO-----");
            this.myMap.printInfo();
            System.out.println(this.myMap);
            System.out.println("-------INFO END-----");

            System.out.println("Po wykonanych pracach, przed starzeniem sie:");
            System.out.println(this.myMap);

            this.observer.updateMap(4);
            counter++;
        }
        System.out.println("Ostateczny stan:");
        System.out.println(this.myMap);
        this.sleepNow(2000);
        Platform.exit();
        System.exit(0);

    }

    public void sleepNow(int time){
        try{
            Thread.sleep(time);
        }catch (InterruptedException e) {
            throw new RuntimeException(e + "Przerwano symulację");
        }
    }

    public int clearCounter(int oldNumberOfAnimals,int counter){
        if(this.myMap.animals.size() > oldNumberOfAnimals){
            counter = 0;
        }
        return counter;
    }

}

