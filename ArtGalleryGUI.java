import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.TreeMap;

public class ArtGalleryGUI extends JFrame {

    // --- GUI Components ---
    private final DrawingCanvas canvas;
    private final JTextField pointsField;
    private final JButton btnStep1, btnStep2, btnStep3, btnStep4, btnStep5, btnStep6, btnStep7;
    private final JButton btnReset;

    // --- Algorithm Data (Computed progressively) ---
    private ArrayList<Vertex> polygonVertices;
    private DoublyConnectedEdgeList mainDCEL;
    private ArrayList<Edge> trapezoids;
    private ArrayList<Edge> partitionDiagonals;
    private ArrayList<DoublyConnectedEdgeList> monotonePolygons;
    private ArrayList<DoublyConnectedEdgeList> triangulation;
    private TreeMap<Integer, ArrayList<Integer>> dualGraphAdjacencyList;
    private TreeMap<Integer, Integer> nodeColor;
    private Integer minColor;

    public ArtGalleryGUI() {
        super("Art Gallery Problem - Step-by-Step Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        canvas = new DrawingCanvas();
        add(canvas, BorderLayout.CENTER);

        // --- Control Panel Setup with 7 Steps ---
        JPanel controlPanel = new JPanel(new GridLayout(2, 1));
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        controlPanel.add(topRow);
        controlPanel.add(bottomRow);
        
        topRow.add(new JLabel("Vertices:"));
        pointsField = new JTextField("30", 4);
        topRow.add(pointsField);
        btnStep1 = new JButton("1. Create Polygon");
        topRow.add(btnStep1);
        btnReset = new JButton("Reset");
        topRow.add(btnReset);
        
        bottomRow.setBorder(BorderFactory.createTitledBorder("Algorithm Steps"));
        btnStep2 = new JButton("2. Trapezoidalization");
        btnStep3 = new JButton("3. Monotone Partitions");
        btnStep4 = new JButton("4. Triangulation");
        btnStep5 = new JButton("5. Dual Graph");
        btnStep6 = new JButton("6. 3-Coloring");
        btnStep7 = new JButton("7. Find Guards");
        
        bottomRow.add(btnStep2);
        bottomRow.add(btnStep3);
        bottomRow.add(btnStep4);
        bottomRow.add(btnStep5);
        bottomRow.add(btnStep6);
        bottomRow.add(btnStep7);
        
        add(controlPanel, BorderLayout.SOUTH);

        setupActions();
        resetToInitialState();

        pack();
        setLocationRelativeTo(null);
    }

    private void setupActions() {
        btnStep1.addActionListener(e -> performStep1_GeneratePolygon());
        btnStep2.addActionListener(e -> performStep2_Trapezoidalize());
        btnStep3.addActionListener(e -> performStep3_Partition());
        btnStep4.addActionListener(e -> performStep4_Triangulate());
        btnStep5.addActionListener(e -> performStep5_DualGraph());
        btnStep6.addActionListener(e -> performStep6_3Coloring());
        btnStep7.addActionListener(e -> performStep7_FindGuards());
        btnReset.addActionListener(e -> resetToInitialState());
    }

    private void resetToInitialState() {
        polygonVertices = null; mainDCEL = null; trapezoids = null;
        partitionDiagonals = null; monotonePolygons = null; triangulation = null;
        dualGraphAdjacencyList = null; nodeColor = null; minColor = null;
        
        canvas.setData(null, null, null, null, null, null, null);
        canvas.resetViewFlags();
        canvas.repaint();

        pointsField.setEnabled(true);
        btnStep1.setEnabled(true);
        btnStep2.setEnabled(false);
        btnStep3.setEnabled(false);
        btnStep4.setEnabled(false);
        btnStep5.setEnabled(false);
        btnStep6.setEnabled(false);
        btnStep7.setEnabled(false);
    }
    
    private void performStep1_GeneratePolygon() {
        try {
            int n = Integer.parseInt(pointsField.getText());
            if (n < 3) throw new NumberFormatException();
            
            resetToInitialState();
            polygonVertices = generateSimplePolygon(n, canvas.getWidth() - 100, canvas.getHeight() - 100);
            mainDCEL = new DoublyConnectedEdgeList(polygonVertices);

            canvas.setData(polygonVertices, null, null, null, null, null, null);
            canvas.repaint();
            
            pointsField.setEnabled(false);
            btnStep1.setEnabled(false);
            btnStep2.setEnabled(true);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid integer (>= 3).", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performStep2_Trapezoidalize() {
        MonotonePartition monPart = new MonotonePartition(mainDCEL);
        trapezoids = monPart.trapezoidalization();
        partitionDiagonals = monPart.getDiagonals();
        monotonePolygons = new ArrayList<>(monPart.partition().values());

        canvas.setData(polygonVertices, trapezoids, null, null, null, null, null);
        canvas.setShowTrapezoids(true);
        canvas.repaint();

        btnStep2.setEnabled(false);
        btnStep3.setEnabled(true);
    }
    
    private void performStep3_Partition() {
        canvas.setData(polygonVertices, trapezoids, partitionDiagonals, null, null, null, null);
        canvas.setShowPartition(true);
        canvas.repaint();
        
        btnStep3.setEnabled(false);
        btnStep4.setEnabled(true);
    }

    private void performStep4_Triangulate() {
        MonotoneTriangulation monTriangulation = new MonotoneTriangulation(monotonePolygons);
        triangulation = monTriangulation.triangulateMonotonePolygon();

        canvas.setData(polygonVertices, trapezoids, partitionDiagonals, triangulation, null, null, null);
        canvas.setShowTriangulation(true);
        canvas.repaint();
        
        btnStep4.setEnabled(false);
        btnStep5.setEnabled(true);
    }

    private void performStep5_DualGraph() {
        DualGraph dualGraph = new DualGraph(triangulation, polygonVertices);
        dualGraph.construct();
        dualGraphAdjacencyList = dualGraph.getAdjacencyList();
        
        canvas.setData(polygonVertices, trapezoids, partitionDiagonals, triangulation, dualGraphAdjacencyList, null, null);
        canvas.setShowDualGraph(true);
        canvas.repaint();

        btnStep5.setEnabled(false);
        btnStep6.setEnabled(true);
    }

    private void performStep6_3Coloring() {
        ThreeColoring threeColoring = new ThreeColoring();
        nodeColor = threeColoring.threeColor(triangulation, polygonVertices);

        canvas.setData(polygonVertices, trapezoids, partitionDiagonals, triangulation, dualGraphAdjacencyList, nodeColor, null);
        canvas.setShowColoring(true);
        canvas.repaint();

        btnStep6.setEnabled(false);
        btnStep7.setEnabled(true);
    }

    private void performStep7_FindGuards() {
        TreeMap<Integer, Integer> colorFreq = new TreeMap<>();
        for (Integer k : nodeColor.keySet()) {
            colorFreq.merge(nodeColor.get(k), 1, Integer::sum);
        }
        minColor = colorFreq.entrySet().stream().min(java.util.Map.Entry.comparingByValue()).get().getKey();
        
        canvas.setData(polygonVertices, trapezoids, partitionDiagonals, triangulation, dualGraphAdjacencyList, nodeColor, minColor);
        canvas.setShowGuards(true);
        canvas.repaint();
        
        btnStep7.setEnabled(false);
    }
    
    private ArrayList<Vertex> generateSimplePolygon(int n, int width, int height) {
        ArrayList<Vertex> points = new ArrayList<>();
        Random rand = new Random();
        java.util.HashSet<Point> uniquePoints = new java.util.HashSet<>();

        while (points.size() < n) {
            int x = rand.nextInt(width - 100) + 50;
            int y = rand.nextInt(height - 100) + 50;
            if (uniquePoints.add(new Point(x, y))) {
                points.add(new Vertex(x, -y));
            }
        }

        int lowestIndex = 0;
        for (int i = 1; i < n; i++) {
            if (points.get(i).y() < points.get(lowestIndex).y() ||
               (points.get(i).y() == points.get(lowestIndex).y() && points.get(i).x() < points.get(lowestIndex).x())) {
                lowestIndex = i;
            }
        }

        Vertex lowest = points.get(lowestIndex);
        points.remove(lowestIndex);

        for (Vertex p : points) {
            double dx = p.x() - lowest.x();
            double dy = p.y() - lowest.y();
            p.setAngle(Math.atan2(dy, dx));
        }

        points.sort(Comparator.comparingDouble(Vertex::angle));
        points.add(0, lowest);

        for (int i = 0; i < points.size(); i++) {
            points.get(i).setIndex(i + 1);
        }
        return points;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ArtGalleryGUI().setVisible(true));
    }
}
