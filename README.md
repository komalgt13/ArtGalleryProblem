# Art Gallery Problem and Camera Placement Problem

This project implements solutions to two computational geometry problems: the **Art Gallery Problem** and the **Camera Placement Problem**. It provides a graphical user interface (GUI) to visualize the steps involved in solving these problems, including polygon partitioning, triangulation, dual graph construction, 3-coloring, and guard/camera placement.

## Overview

### 1. **Art Gallery Problem**
The Art Gallery Problem involves determining the minimum number of guards required to cover the interior of a simple polygon. This implementation uses **Fisk's theorem**, which states that for a triangulated polygon, three colors suffice to color the vertices such that no two adjacent vertices share the same color. The guards are placed at vertices of the least frequent color.

### 2. **Camera Placement Problem**
The Camera Placement Problem is a variant of the Art Gallery Problem where cameras with a fixed **50° field of view (FOV)** are used instead of guards with 360° vision. The solution uses a **greedy set-cover algorithm** to determine the optimal placement of cameras to cover all triangles in the triangulated polygon.

---

## Project Structure

### Files and Their Roles

- **`Main.java`**: The entry point of the application. It provides a launcher GUI to choose between the Art Gallery Problem and the Camera Placement Problem.
- **`ArtGalleryGUI.java`**: GUI for visualizing the 7-step solution to the Art Gallery Problem.
- **`CameraPlacementGUI.java`**: GUI for visualizing the steps of solving the Camera Placement Problem.
- **`ArtGalleryCanvas.java`**: Handles the visualization of polygons, trapezoids, partitions, triangulations, dual graphs, coloring, and guards.
- **`CameraPlacementCanvas.java`**: Handles the visualization of polygons, triangulations, and camera placements.
- **`DoublyConnectedEdgeList.java`**: Implements the **DCEL (Doubly Connected Edge List)** data structure for representing polygons and their subdivisions.
- **`MonotonePartition.java`**: Implements the algorithm for partitioning a polygon into monotone polygons using a sweep-line approach.
- **`MonotoneTriangulation.java`**: Implements the algorithm for triangulating monotone polygons in linear time.
- **`DualGraph.java`**: Constructs the dual graph of the triangulated polygon.
- **`ThreeColoring.java`**: Implements the 3-coloring algorithm for the dual graph.
- **`CameraPlacement.java`**: Implements the greedy set-cover algorithm for solving the Camera Placement Problem.
- **`Vertex.java`**: Represents a vertex of the polygon.
- **`Edge.java`**: Represents a non-DCEL edge.
- **`SimplePair.java`**: A utility class for storing key-value pairs.

---

## Flow of the Program

### Art Gallery Problem
1. **Polygon Generation**: A simple polygon is generated with random vertices.
2. **Trapezoidalization**: The polygon is partitioned into trapezoids using a sweep-line algorithm.
3. **Monotone Partitioning**: The trapezoids are further partitioned into monotone polygons.
4. **Triangulation**: Each monotone polygon is triangulated.
5. **Dual Graph Construction**: A dual graph is constructed from the triangulated polygon.
6. **3-Coloring**: The dual graph is 3-colored using a depth-first search (DFS).
7. **Guard Placement**: Guards are placed at vertices of the least frequent color.

### Camera Placement Problem
1. **Polygon Generation**: A simple polygon is generated with random vertices.
2. **Triangulation**: The polygon is triangulated.
3. **Field of View Analysis**: The angular span of each triangle is analyzed to determine if it can be covered by a camera.
4. **Greedy Set-Cover**: Cameras are placed using a greedy algorithm to cover all triangles.

---

## Instructions to Run

1. **Prerequisites**:
   - Java Development Kit (JDK) 8 or higher.
   - A Java IDE (e.g., IntelliJ IDEA, Eclipse, or Visual Studio Code) or a terminal with `javac` and `java` commands.

2. **Steps**:
   - Clone or download the project to your local machine.
   - Open the project in your IDE or navigate to the project folder in the terminal.
   - Run the file `Main.java` by clicking on the Run button on the top right side of the Visual Studio Code IDE.
   - Another way to compile the project using the terminal is:
     ```bash
     javac Main.java
     ```
   - Run the project:
     ```bash
     java Main
     ```

3. **Usage**:
   - A launcher window will appear with two options:
     - **Classic Art Gallery Problem**: Opens the GUI for solving the Art Gallery Problem.
     - **Camera Placement Problem**: Opens the GUI for solving the Camera Placement Problem.

---

## Interpreting the Outputs

### Initial Interaction
- When the program starts, a popup window will appear asking the user to choose between the **Art Gallery Problem** and the **Camera Placement Problem**.
- After selecting an option, a new GUI window will open, allowing the user to interact with the chosen problem.

### GUI Features
- **Number of Vertices**: The user can specify the number of vertices for the polygon.
- **Reset Button**: The user can reset the polygon to generate a new random polygon with the specified number of vertices.

### Art Gallery Problem
- **Polygon Outline**: The generated polygon is displayed.
- **Trapezoids**: The polygon is partitioned into trapezoids.
- **Partitions**: The trapezoids are further divided into monotone polygons (blue dashed lines).
- **Triangulation**: The monotone polygons are triangulated (gray lines).
- **Dual Graph**: The dual graph of the triangulation is displayed (red lines).
- **3-Coloring**: Vertices are colored using three colors (red, blue, green).
- **Guards**: Guards are shown as highlighted vertices (yellow circles).

### Camera Placement Problem
- **Polygon Outline**: The generated polygon is displayed.
- **Triangulation**: The polygon is triangulated (gray lines).
- **Camera Coverage**: Cameras are displayed as colored circles, with their coverage areas shaded.

---

## Notes
- The project uses **random polygon generation**, so the results may vary between runs.
- The **50° FOV** for cameras in the Camera Placement Problem is configurable in the [CameraPlacement.java](http://_vscodecontentref_/1) file.
- The program includes debug messages and warnings for uncoverable triangles in the Camera Placement Problem.

---

## Authors
- **GroupID-19**: Komal (22113078), Dhruv (22114029), Himanshu Raheja (22323023)
- **Date**: 24 September 2025