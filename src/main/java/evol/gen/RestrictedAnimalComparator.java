package evol.gen;

import java.util.Comparator;

public class RestrictedAnimalComparator implements Comparator<Animal> {

final int minEnergyToReproduce;

    public RestrictedAnimalComparator(int minEnergyToReproduce) {
        this.minEnergyToReproduce = minEnergyToReproduce;
    }

    @Override
    public int compare(Animal a1, Animal a2) {
        // Compare the energies of the animals
        if (a1.energy > a2.energy && a1.energy > this.minEnergyToReproduce) {
            return -1;
        } else if (a1.energy < a2.energy && a2.energy > this.minEnergyToReproduce) {
            return 1;
        }
        // If the energies are equal, compare the ages of the animals
        else if (a1.age < a2.age && a2.energy > this.minEnergyToReproduce) {
            return 1;
        } else if (a1.age > a2.age && a1.energy > this.minEnergyToReproduce) {
            return -1;
        }
        // If the energies and ages are equal, compare the number of children of the animals
        else if (a1.children > a2.children && a1.energy > this.minEnergyToReproduce) {
            return -1;
        } else if (a1.children < a2.children && a2.energy > this.minEnergyToReproduce) {
            return 1;
        }
        // If the energies, ages, and number of children are all equal, return 0
        else {
            return 0;
        }
    }
}