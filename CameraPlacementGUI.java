// GroupID-19 (Komal 22113078_Dhruv 22114029_Himanshu Raheja22323023)
// Date: September 24, 2025
// CameraPlacementGUI.java - GUI interface for visualizing the steps of finding minimum cameras


import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class CameraPlacementGUI extends JFrame {

    private final CameraPlacementCanvas canvas;
    private final JTextField pointsField;
    private final JButton btnGenerate, btnTrapezoid, btnPartition, btnTriangulate, btnFindCameras, btnReset;

    private ArrayList<Vertex> polygon;
    private DoublyConnectedEdgeList mainDcel;
    private ArrayList<DoublyConnectedEdgeList> monotonePolygons;
    private ArrayList<DoublyConnectedEdgeList> triangulation;
    private ArrayList<Edge> trapezoids;
    private ArrayList<Edge> partitionDiagonals;
    private List<CameraPlacement.Camera> cameras;

    public CameraPlacementGUI() {
        super("Art Gallery Camera Placement (50° FOV)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        canvas = new CameraPlacementCanvas();
        add(canvas, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new GridLayout(2, 1));
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        controlPanel.add(topRow);
        controlPanel.add(bottomRow);

        topRow.add(new JLabel("Number of Vertices:"));
        pointsField = new JTextField("25", 4);
        topRow.add(pointsField);
        btnGenerate = new JButton("1. Generate Polygon");
        topRow.add(btnGenerate);
        btnReset = new JButton("Start Over");
        topRow.add(btnReset);

        bottomRow.setBorder(BorderFactory.createTitledBorder("Algorithm Steps"));
        btnTrapezoid = new JButton("2. Trapezoidalization");
        btnPartition = new JButton("3. Monotone Partitions");
        btnTriangulate = new JButton("4. Triangulation");
        btnFindCameras = new JButton("5. Find Camera Placements");
        bottomRow.add(btnTrapezoid);
        bottomRow.add(btnPartition);
        bottomRow.add(btnTriangulate);
        bottomRow.add(btnFindCameras);

        add(controlPanel, BorderLayout.SOUTH);

        setupActions();
        resetToInitialState();

        pack();
        setLocationRelativeTo(null);
    }

    private void setupActions() {
        btnGenerate.addActionListener(e -> performStep1_GeneratePolygon());
        btnTrapezoid.addActionListener(e -> performStep2_Trapezoidalize());
        btnPartition.addActionListener(e -> performStep3_Partition());
        btnTriangulate.addActionListener(e -> performStep4_Triangulate());
        btnFindCameras.addActionListener(e -> performStep5_FindCameras());
        btnReset.addActionListener(e -> resetToInitialState());
    }

    private void resetToInitialState() {
        polygon = null; mainDcel = null; triangulation = null;
        trapezoids = null; partitionDiagonals = null; cameras = null;
        monotonePolygons = null;
        
        canvas.setData(null, null, null);
        canvas.repaint();

        pointsField.setEnabled(true);
        btnGenerate.setEnabled(true);
        btnTrapezoid.setEnabled(false);
        btnPartition.setEnabled(false);
        btnTriangulate.setEnabled(false);
        btnFindCameras.setEnabled(false);
    }
    
    private void performStep1_GeneratePolygon() {
        try {
            int n = Integer.parseInt(pointsField.getText());
            if (n < 3) throw new NumberFormatException();
            
            resetToInitialState();
            polygon = generateSimplePolygon(n, canvas.getWidth() - 100, canvas.getHeight() - 100);
            for(int i = 0; i < polygon.size(); i++) polygon.get(i).setIndex(i + 1);
            mainDcel = new DoublyConnectedEdgeList(polygon);

            canvas.setData(polygon, null, null);
            canvas.repaint();
            
            pointsField.setEnabled(false);
            btnGenerate.setEnabled(false);
            btnTrapezoid.setEnabled(true);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid integer >= 3.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performStep2_Trapezoidalize() {
        MonotonePartition monPart = new MonotonePartition(mainDcel);
        trapezoids = monPart.trapezoidalization();
        partitionDiagonals = monPart.getDiagonals();
        monotonePolygons = new ArrayList<>(monPart.partition().values());
        
        canvas.setData(polygon, null, null);
        canvas.setTrapezoids(trapezoids);
        canvas.repaint();

        btnTrapezoid.setEnabled(false);
        btnPartition.setEnabled(true);
    }
    
    private void performStep3_Partition() {
        canvas.setPartitions(partitionDiagonals);
        canvas.repaint();
        
        btnPartition.setEnabled(false);
        btnTriangulate.setEnabled(true);
    }

    private void performStep4_Triangulate() {
        if (monotonePolygons == null) {
            System.err.println("Error: Monotone partitions have not been calculated yet.");
            return;
        }
        
        MonotoneTriangulation monTriangulation = new MonotoneTriangulation(monotonePolygons);
        triangulation = monTriangulation.triangulateMonotonePolygon();

        canvas.setData(polygon, triangulation, null);
        canvas.repaint();
        
        btnTriangulate.setEnabled(false);
        btnFindCameras.setEnabled(true);
    }

    private void performStep5_FindCameras() {
        if (triangulation == null) return;
        
        cameras = CameraPlacement.solve(triangulation, polygon);
        
        canvas.setData(polygon, triangulation, cameras);
        canvas.repaint();
        
        btnFindCameras.setEnabled(false);
    }
    
    private ArrayList<Vertex> generateSimplePolygon(int n, int width, int height) {
        ArrayList<Vertex> points = new ArrayList<>();
        Random rand = new Random();
        
        while (points.size() < n) {
            points.add(new Vertex(rand.nextInt(width) + 50, rand.nextInt(height) + 50));
        }

        points.sort(Comparator.comparingDouble(Vertex::y).thenComparingDouble(Vertex::x));
        Vertex lowest = points.get(0);

        for (Vertex p : points) {
            p.setAngle(Math.atan2(p.y() - lowest.y(), p.x() - lowest.x()));
        }
        
        points.subList(1, points.size()).sort(Comparator.comparingDouble(Vertex::angle));
        return points;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CameraPlacementGUI().setVisible(true));
    }
}

