# ATP Project – Maze Generation, Search & GUI System

## Overview
This project was developed as part of the **Advanced Topics in Programming** course at **Ben-Gurion University of the Negev**.  
Created collaboratively by **Hadar Shir** and **Aviel Itzhak**, it implements a full software solution for generating, solving, and visualizing mazes.  
The system integrates algorithmic problem-solving, client-server communication, data compression, and a graphical user interface.

---

## 🧩 Project Structure
The project is divided into three main parts, representing progressive development stages.

### **Part A – Maze Generation & Search Algorithms**
Implemented core algorithms for generating and solving mazes.

- **Maze Generators:**  
  `EmptyMazeGenerator`, `SimpleMazeGenerator`, and `MyMazeGenerator` (based on Depth-First Search).
- **Search Algorithms:**  
  `BreadthFirstSearch`, `DepthFirstSearch`, and `BestFirstSearch`, all extending an abstract framework (`AState`, `ISearchable`, `ASearchingAlgorithm`).
- **Main Entities:**  
  `Maze`, `Position`, and `Solution` classes representing the maze grid, start and goal positions, and solution path.
- **Highlights:**  
  Object-Oriented Design, polymorphism, and code reuse through interfaces and abstract classes.

---

### **Part B – Streams, Threads & Servers**
Expanded the project to include compression and networking.

- **Compression:**  
  Implemented custom classes such as `MyCompressorOutputStream`, `MyDecompressorInputStream`, and their “Simple” counterparts to efficiently save and load mazes.
- **Server–Client Architecture:**  
  Implemented `Server`, `Client`, and strategy classes (`ServerStrategyGenerateMaze`, `ServerStrategySolveSearchProblem`) using multi-threading and design patterns.
- **Configuration Management:**  
  Added a `Config.properties` file managed via a Singleton class to control server settings (thread pool size, chosen algorithms, etc.).
- **Highlights:**  
  Multithreading, concurrency control, sockets, and configuration management.

---

### **Part C – GUI & MVVM Architecture**
Developed a **Graphical User Interface (GUI)** using **JavaFX** based on the **MVVM (Model–View–ViewModel)** pattern.

- **Features:**  
  - Visual maze display and live solution animation.  
  - User interaction for maze size, start and goal positions.  
  - Dynamic server communication and result visualization.  
- **Technologies:**  
  JavaFX, Property Binding, Event Handling, and Scene Builder.
- **Highlights:**  
  Clean separation of concerns using MVVM design pattern, enhanced user experience, and modular design.

---

## ⚙️ Technologies & Tools
- **Languages:** Java  
- **Frameworks:** JavaFX, Maven  
- **Design Patterns:** Singleton, Strategy, Observer, MVVM  
- **Networking:** TCP/IP Sockets, Client–Server communication  
- **Tools:** IntelliJ IDEA, GitHub, Log4j2 for logging  
- **Version Control:** Git  

---

## 🚀 Key Features
- Maze generation and search algorithms (DFS, BFS, BestFS)  
- Compression and decompression for efficient data storage  
- Multithreaded servers handling client requests concurrently  
- Configuration file for flexible runtime settings  
- Intuitive JavaFX interface for visualization and control  

---

## 👩‍💻 Authors
Developed collaboratively by:  
- **Hadar Shir**  
- **Aviel Itzhak**  
**B.Sc. Information Systems and Software Engineering**  
**Ben-Gurion University of the Negev**

---

## 📚 Notes
This project demonstrates advanced software engineering principles, including algorithm design, layered architecture, concurrency, networking, and GUI development.
It represents a complete end-to-end software solution — from backend logic to a functional user interface.
