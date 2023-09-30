# Evolution Generator

## Description

This project facilitates the simulation of an ecosystem. It allows observing interactions between animals and plants on a map. The simulation encompasses functionalities like:

- Removing dead animals from the map
- Turning and moving every animal
- Plants consumption by animals
- Reproduction of fed animals
- Growth of new plants

## Configuration Parameters

Upon application launch, users can configure a wide array of parameters including:

- Map height and width
- Map variant
- Initial number of plants and animals
- Energy parameters for animals and plants
- Growth variants for plants
- Mutation and behavior variants for animals

## Variants

Various map and animal behavior variants are available in the project, such as:

- Earth globe vs Hellish portal
- Forested equators vs Toxic carcasses
- Complete randomness vs Slight correction
- Full predestination vs A bit of madness

  ## Variant Models Description

    In the ecosystem simulation, several variant models have been incorporated to offer diverse map and animal behavior experiences:

    ### Earth Globe vs Hellish Portal

    - **Earth Globe**: 
     This is a traditional map representing the terrestrial ecosystem we're familiar with. It emphasizes natural environments and behavior patterns observed in our world.
    
    - **Hellish Portal**:
    A more fantastical representation of an ecosystem, perhaps representing a hellish or infernal realm. Environments and animal behaviors may be more aggressive, unpredictable, or influenced by otherworldly factors.

    ### Forested Equators vs Toxic Carcasses

    - **Forested Equators**:
    This model represents a lush and dense forested environment, primarily along the equatorial regions. Here, animals and plants interact in a vibrant, biodiverse setting, much like the rainforests in our world.
    
    - **Toxic Carcasses**:
    A post-apocalyptic or devastated model where much of the vegetation is dead or dying, and toxins fill the environment. Animals in this setting may have evolved or adapted to deal with high levels of toxins, making for unique interaction patterns.

    ### Complete Randomness vs Slight Correction

    - **Complete Randomness**: 
    In this model, animal behaviors and interactions are entirely random, with no discernible patterns or logic. It provides an insight into how ecosystems might evolve without any natural order.
    
    - **Slight Correction**:
    While still retaining a degree of randomness, this model introduces slight corrections or tweaks to behaviors, steering animals and plants towards more typical or expected interactions, based on some predefined rules or natural order.

    ### Full Predestination vs A Bit of Madness

    - **Full Predestination**: 
    Here, all actions, behaviors, and outcomes are pre-determined, following a set path or destiny. It's an exploration into a world where free will might be an illusion, and everything operates based on destiny or some grand design.
    
    - **A Bit of Madness**:
    While there might be some order or logic, there's an element of unpredictability or madness introduced. Animals might occasionally act out of character, or unexpected events might occur, adding a layer of chaos to the simulation.

---

These variant models offer unique perspectives on ecosystem dynamics, allowing users to explore and compare how different factors influence the evolution and interaction of species.



## Application Features

- Selection of a previously prepared configuration
- Loading of an alternative user-made configuration
- Simulation visualization with pause and resume capabilities
- Real-time stats tracking
- Stats saving to a CSV file
- Tracking of selected animals and visualization of animals with dominant genotypes

## Requirements

For the application to run, you need:

- Java 17
- JavaFX 17

## How to Run the Project

### Prerequisites

- **Java 17** installed on your machine. You can verify this by running `java -version` in your terminal or command prompt.
- Gradle installed or you can use the provided Gradle wrapper.

### Steps to Run

1. **Clone the Repository**: 
   Get a copy of the project on your local machine using the following command:
   ```
   git clone https://github.com/Adam0s007/evolution-generator.git
   ```


2. **Navigate to the Project Directory**: 
    Once cloned, access the project's root directory:
    ```
    cd evolution-generator
    ```

3. **Using IntelliJ Environment [Optional]**:
- Open IntelliJ IDEA.
- Choose `Open` or `Import Project` from the main screen.
- Navigate to the `evolution-generator` directory that you've just cloned.
- Select the `build.gradle` file and click `OK`.
- Wait for the dependencies to be fetched and the project to be indexed.
- Navigate to the `World.java` file in the project's directory structure.
- Right-click on the file and select `Run 'World.main()'` to execute the program.

4. **Without IntelliJ**
    ```
    ./gradlew build # For UNIX-based systems
    gradlew.bat build # For Windows
    ```
    After building, execute the main class (`World.java`):
    ```
    ./gradlew run # For UNIX-based systems
    gradlew.bat run # For Windows
    ```
    
    Ensure you're in the root directory of the project when executing these commands

