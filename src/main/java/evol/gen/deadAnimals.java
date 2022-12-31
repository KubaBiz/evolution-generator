package evol.gen;

public class deadAnimals {

    private int sumOfAges;
    private int quantityOfDeadAnimals;

    public deadAnimals(){
        this.sumOfAges = 0;
        this.quantityOfDeadAnimals = 0;
    }

    public void addDeadAnimal(Animal animal){
        this.sumOfAges += animal.age;
        this.quantityOfDeadAnimals +=1;
    }

    public double averageAge(){
        if(this.quantityOfDeadAnimals == 0) return 0.0;
        return  Math.round(((double) this.sumOfAges/ ((double) this.quantityOfDeadAnimals))*100.0)/100.0;
    }



}
