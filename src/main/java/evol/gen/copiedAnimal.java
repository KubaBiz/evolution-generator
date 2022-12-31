package evol.gen;

public class copiedAnimal {
    public int energy = 0;
    public String gen  = "";
    public int age = 0;
    public int children = 0;

    public Vector2d position = new Vector2d(0,0);
    public copiedAnimal(int energy,String gen,int age,int children,Vector2d vectorek){
        this.energy = energy;
        this.gen = gen;
        this.age = age;
        this.children = children;
        this.position = vectorek;

    }

}
