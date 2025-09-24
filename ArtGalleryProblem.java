// // GroupID-21 (14114053_14114071) - Sharanpreet Singh & Vaibhav Gosain
// // Date: March 15, 2018
// // ArtGalleryProblem.java - This file contains the main code for the project.
// // The entire flow of code can be determined from this file.

// import java.util.*;
// import java.util.InputMismatchException; // Added for error handling
// import javax.swing.JFrame;
// import javax.swing.SwingUtilities; // Added for proper GUI threading

// public class ArtGalleryProblem {

//     public static final int MAX = 1000;
//     public static final double EPSILON = 1e-4;
//     public static ArrayList<Vertex> vertices = new ArrayList<Vertex>();

//     public static void main(String[] args) {

//         int n = 0;
//         int i;
//         Scanner input = new Scanner(System.in);
//         Random rand = new Random();

//         // --- MODIFIED: Added robust input validation ---
//         while (true) { // Loop until we get a valid number
//             try {
//                 System.out.println("Please enter the number of points:");
//                 n = input.nextInt();
//                 if (n >= 3) {
//                     break; // Exit loop if input is valid (a polygon needs at least 3 vertices)
//                 } else {
//                     System.out.println("Error: A polygon must have at least 3 vertices. Please try again.");
//                 }
//             } catch (InputMismatchException e) {
//                 System.out.println("Error: Invalid input. Please enter a whole number.");
//                 input.next(); // Clear the bad input from the scanner
//             }
//         }
        
//         n = Math.min(n, MAX);
//         System.out.println("The value of n entered is: " + Integer.toString(n));
//         System.out.println("The vertices initialized are: ");
//         TreeSet<Integer> tSet = new TreeSet<Integer>();
//         for (i = 0; i < n; i++) {
//             vertices.add(new Vertex());
//             vertices.get(i).setX(rand.nextInt(n) + 1);
//             int temp_y = rand.nextInt(n) + 1;
//             while (tSet.contains(temp_y))
//                 temp_y = rand.nextInt(n) + 1;
//             tSet.add(temp_y);
//             vertices.get(i).setY(-1 * (temp_y)); // operating in fourth quadrant for computation purposes
//             vertices.get(i).setIndex(i);
//             System.out.println("( " + Double.toString(vertices.get(i).x()) + "," + Double.toString(vertices.get(i).y()) + " )");
//         }

//         int lowest_index = 0;
//         for (i = 0; i < n; i++) {
//             if (vertices.get(i).y() < vertices.get(lowest_index).y()) {
//                 lowest_index = i;
//             } else if (vertices.get(i).y() == vertices.get(lowest_index).y() && vertices.get(i).x() < vertices.get(lowest_index).x()) {
//                 lowest_index = i;
//             }
//         }

//         double tempy, tempx, length;
//         for (i = 0; i < n; i++) {
//             tempx = vertices.get(i).x() - vertices.get(lowest_index).x();
//             tempy = vertices.get(i).y() - vertices.get(lowest_index).y();
//             length = Math.pow(Math.pow(tempx, 2) + Math.pow(tempy, 2), 0.5);
//             vertices.get(i).setAngle(Math.acos(tempx / length));
//             vertices.get(i).setAngle(vertices.get(i).angle() * (-1.0));
//         }

//         long start_time = System.currentTimeMillis();

//         Collections.sort(vertices, new Comparator<Vertex>() {
//             public int compare(Vertex a, Vertex b) {
//                 if (Math.abs(a.angle() - b.angle()) < EPSILON)
//                     return Double.compare(a.y(), b.y()) < 0.0 ? 1 : -1;
//                 return Double.compare(a.angle(), b.angle()) < 0.0 ? 1 : -1;
//             }
//         });

//         for (i = 0; i < n; i++) {
//             vertices.get(i).setIndex(i + 1);
//         }

//         DoublyConnectedEdgeList dcel = new DoublyConnectedEdgeList(vertices);
//         MonotonePartition monPart = new MonotonePartition(dcel);
//         ArrayList<DoublyConnectedEdgeList> monPolygons = new ArrayList<DoublyConnectedEdgeList>();

//         for (Integer k : monPart.partition().keySet()) {
//             monPolygons.add(monPart.partition().get(k));
//         }

//         MonotoneTriangulation monTriangulation = new MonotoneTriangulation(monPolygons);
//         ArrayList<DoublyConnectedEdgeList> triangulation = monTriangulation.triangulateMonotonePolygon();
//         DualGraph dualGraph = new DualGraph(triangulation, vertices);
//         dualGraph.construct();
//         TreeMap<Integer, ArrayList<Integer>> adjacencyList = dualGraph.getAdjacencyList();
//         ThreeColoring threeColoring = new ThreeColoring();
//         TreeMap<Integer, Integer> nodeColor = threeColoring.threeColor(triangulation, vertices);
//         TreeMap<Integer, Integer> colorFreq = new TreeMap<Integer, Integer>();

//         for (Integer k : nodeColor.keySet()) {
//             if (colorFreq.containsKey(nodeColor.get(k)))
//                 colorFreq.put(nodeColor.get(k), colorFreq.get(nodeColor.get(k)) + 1);
//             else
//                 colorFreq.put(nodeColor.get(k), 1);
//         }

//         Integer minFreq = n;
//         Integer minColor = 0;
//         for (Integer col : colorFreq.keySet()) {
//             minFreq = Math.min(minFreq, colorFreq.get(col));
//         }
//         for (Integer col : colorFreq.keySet()) {
//             if (colorFreq.get(col).equals(minFreq)) {
//                 minColor = col;
//                 break;
//             }
//         }

//         long end_time = System.currentTimeMillis();
//         System.out.println("Time taken = " + (end_time - start_time) + " ms");

//         // --- MODIFIED: Wrapped all GUI creation in SwingUtilities.invokeLater ---
//         final int finalN = n; // Need to use a final variable inside the lambda
//         final Integer finalMinColor = minColor;
//         SwingUtilities.invokeLater(() -> {
//             //Draw the simple polygon
//             DrawGraph mainPanel = new DrawGraph(monPolygons, finalN);
//             JFrame frame = new JFrame("DrawGraph");
//             frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//             frame.getContentPane().add(mainPanel);
//             frame.pack();
//             frame.setLocationByPlatform(true);
//             frame.setVisible(true);

//             //Draw the trapezoidal lines
//             DrawTrapezoidalization trapezoidalPanel = new DrawTrapezoidalization(monPart.partition(), monPart.trapezoidalization(), finalN);
//             JFrame frameT = new JFrame("DrawTrapezoidalization");
//             frameT.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//             frameT.getContentPane().add(trapezoidalPanel);
//             frameT.pack();
//             frameT.setLocationByPlatform(true);
//             frameT.setVisible(true);

//             DrawMonotonePartition monPanel = new DrawMonotonePartition(monPart.partition(), monPart.trapezoidalization(), finalN);
//             JFrame frameM = new JFrame("DrawMonotonePartition");
//             frameM.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//             frameM.getContentPane().add(monPanel);
//             frameM.pack();
//             frameM.setLocationByPlatform(true);
//             frameM.setVisible(true);

//             //Draw the traingulation of polygon obtained from triangulating monotone polygons
//             DrawTriangulation triangulationPanel = new DrawTriangulation(triangulation, finalN);
//             JFrame frameTri = new JFrame("DrawTriangulation");
//             frameTri.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//             frameTri.getContentPane().add(triangulationPanel);
//             frameTri.pack();
//             frameTri.setLocationByPlatform(true);
//             frameTri.setVisible(true);

//             //Draw the dual graph of triangulation
//             DrawDualGraph dualPanel = new DrawDualGraph(triangulation, adjacencyList, finalN);
//             JFrame frameDual = new JFrame("DrawDualGraph");
//             frameDual.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//             frameDual.getContentPane().add(dualPanel);
//             frameDual.pack();
//             frameDual.setLocationByPlatform(true);
//             frameDual.setVisible(true);

//             //Draw the three coloring of triangulation obtained from dfs on dual graph
//             DrawThreeColoring threeColoringPanel = new DrawThreeColoring(triangulation, adjacencyList, finalN, nodeColor);
//             JFrame frameThreeColoring = new JFrame("DrawThreeColoring");
//             frameThreeColoring.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//             frameThreeColoring.getContentPane().add(threeColoringPanel);
//             frameThreeColoring.pack();
//             frameThreeColoring.setLocationByPlatform(true);
//             frameThreeColoring.setVisible(true);

//             // --- MODIFIED: Corrected typo from DrawVertexGaurd to DrawVertexGuard ---
//             //Draw the vertex guards as obtained from Fisk's theorem
//             DrawVertexGuard vertexGuardPanel = new DrawVertexGuard(triangulation, adjacencyList, finalN, nodeColor, finalMinColor);
//             JFrame frameVertexGuard = new JFrame("DrawVertexGuard"); // Also corrected frame title
//             frameVertexGuard.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//             frameVertexGuard.getContentPane().add(vertexGuardPanel);
//             frameVertexGuard.pack();
//             frameVertexGuard.setLocationByPlatform(true);
//             frameVertexGuard.setVisible(true);
//         });
//     }
// }
