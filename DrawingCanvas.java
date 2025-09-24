import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.TreeMap;

public class DrawingCanvas extends JPanel {

    // --- Enhanced Color Palette & Styling ---
    private static final Color COLOR_BACKGROUND = Color.WHITE;
    private static final Color COLOR_POLYGON = new Color(50, 50, 50);
    private static final Color COLOR_TRAPEZOID = new Color(255, 165, 0, 150); // Orange, semi-transparent
    private static final Color COLOR_PARTITION = new Color(0, 100, 255);      // Bright Blue
    private static final Color COLOR_TRIANGULATION = new Color(220, 220, 220); // Light Gray
    private static final Color COLOR_DUAL_GRAPH = new Color(239, 71, 111);     // Coral Pink
    private static final Color COLOR_LABEL_VERTEX = new Color(0, 0, 0);
    private static final Color COLOR_LABEL_EDGE = new Color(0, 102, 0);        // Dark Green
    private static final Color COLOR_GUARD = new Color(220, 20, 60);           // Crimson Red
    
    private static final Font FONT_VERTEX = new Font("Arial", Font.BOLD, 14);
    private static final Font FONT_COORDINATE = new Font("Arial", Font.PLAIN, 11);
    private static final Font FONT_EDGE = new Font("Arial", Font.ITALIC, 12);

    // --- Data to be Drawn ---
    private ArrayList<Vertex> polygon;
    private ArrayList<Edge> trapezoids, partitionDiagonals;
    private ArrayList<DoublyConnectedEdgeList> triangulation;
    private TreeMap<Integer, ArrayList<Integer>> dualGraphAdjacencyList;
    private TreeMap<Integer, Integer> nodeColor;
    private Integer minColor;

    // --- View State Flags ---
    private boolean showTrapezoids, showPartition, showTriangulation, showDualGraph, showColoring, showGuards;

    public DrawingCanvas() {
        setPreferredSize(new Dimension(900, 700));
        setBackground(COLOR_BACKGROUND);
        resetViewFlags();
    }

    public void setData(ArrayList<Vertex> poly, ArrayList<Edge> trapz, ArrayList<Edge> diags, ArrayList<DoublyConnectedEdgeList> tris, TreeMap<Integer, ArrayList<Integer>> adjList, TreeMap<Integer, Integer> colors, Integer guardColor) {
        this.polygon = poly;
        this.trapezoids = trapz;
        this.partitionDiagonals = diags;
        this.triangulation = tris;
        this.dualGraphAdjacencyList = adjList;
        this.nodeColor = colors;
        this.minColor = guardColor;
    }

    public void resetViewFlags() {
        showTrapezoids = false;
        showPartition = false;
        showTriangulation = false;
        showDualGraph = false;
        showColoring = false;
        showGuards = false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (polygon == null) return;

        // --- Draw in Layers (Bottom to Top for correct visual hierarchy) ---
        if (showTrapezoids) drawEdges(g2d, trapezoids, COLOR_TRAPEZOID, new BasicStroke(1));
        if (showTriangulation) drawTriangulation(g2d);
        if (showPartition) drawEdges(g2d, partitionDiagonals, COLOR_PARTITION, new BasicStroke(2.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{10, 6}, 0));

        drawPolygonOutline(g2d);
        
        if (showDualGraph) drawDualGraph(g2d);
        if (showColoring) drawColoredVertices(g2d);
        if (showGuards) drawGuards(g2d);
        
        // Always draw labels on the very top so they are not obscured
        drawLabelsAndInfo(g2d);
    }

    private void drawLabelsAndInfo(Graphics2D g) {
        // Draw vertex labels (index and coordinates)
        for (int i = 0; i < polygon.size(); i++) {
            Vertex v = polygon.get(i);
            int x = (int) v.x();
            int y = (int) (-1 * v.y()); // Flip y for display

            String vertexLabel = "v" + v.index();
            String coordLabel = String.format("(%d, %d)", (int) v.x(), (int) v.y());

            g.setFont(FONT_VERTEX);
            g.setColor(COLOR_LABEL_VERTEX);
            g.drawString(vertexLabel, x + 8, y - 8);

            g.setFont(FONT_COORDINATE);
            g.setColor(Color.GRAY);
            g.drawString(coordLabel, x + 8, y + 5);
        }

        // Draw edge labels (index)
        g.setFont(FONT_EDGE);
        g.setColor(COLOR_LABEL_EDGE);
        for (int i = 0; i < polygon.size(); i++) {
            Vertex v1 = polygon.get(i);
            Vertex v2 = polygon.get((i + 1) % polygon.size()); // Wrap around for the last edge

            // Calculate midpoint of the edge
            int midX = (int) ((v1.x() + v2.x()) / 2);
            int midY = (int) ((-1 * v1.y() + -1 * v2.y()) / 2);
            
            // Calculate a small offset perpendicular to the edge for better placement
            double dx = v2.x() - v1.x();
            double dy = -v2.y() - (-v1.y());
            double length = Math.sqrt(dx * dx + dy * dy);
            if (length == 0) continue; // Avoid division by zero
            int offsetX = (int) (-12 * dy / length); // Perpendicular vector component
            int offsetY = (int) (12 * dx / length);  // Perpendicular vector component

            g.drawString("e" + (i + 1), midX + offsetX, midY + offsetY);
        }
    }
    
    private void drawPolygonOutline(Graphics2D g) {
        Polygon poly = new Polygon();
        for (Vertex v : polygon) {
            poly.addPoint((int) v.x(), (int) (-1 * v.y()));
        }
        g.setColor(COLOR_POLYGON);
        g.setStroke(new BasicStroke(3));
        g.drawPolygon(poly);
    }
    
    private void drawEdges(Graphics2D g, ArrayList<Edge> edges, Color color, Stroke stroke) {
        g.setColor(color);
        g.setStroke(stroke);
        for (Edge edge : edges) {
            Vertex start = edge.start_vertex();
            Vertex end = edge.end_vertex();
            g.drawLine((int) start.x(), (int) (-1 * start.y()), (int) end.x(), (int) (-1 * end.y()));
        }
    }

    private void drawTriangulation(Graphics2D g) {
        g.setColor(COLOR_TRIANGULATION);
        g.setStroke(new BasicStroke(1));
        for (DoublyConnectedEdgeList triangleDCEL : triangulation) {
            Polygon triPoly = new Polygon();
            DoublyConnectedEdgeList.DCEL_Edge current = triangleDCEL.rep_edge();
            do {
                triPoly.addPoint((int) current.origin().x(), (int) (-1 * current.origin().y()));
                current = current.next();
            } while (current != triangleDCEL.rep_edge());
            g.drawPolygon(triPoly);
        }
    }

    private void drawDualGraph(Graphics2D g) {
        if (dualGraphAdjacencyList == null) return;
        
        TreeMap<Integer, Vertex> centroids = new TreeMap<>();
        for (DoublyConnectedEdgeList tri : triangulation) {
            centroids.put(tri.id(), tri.getCentroid());
        }

        g.setColor(COLOR_DUAL_GRAPH);
        g.setStroke(new BasicStroke(2));
        for (Integer triId : dualGraphAdjacencyList.keySet()) {
            Vertex center1 = centroids.get(triId);
            for (Integer neighborId : dualGraphAdjacencyList.get(triId)) {
                if (triId < neighborId) {
                    Vertex center2 = centroids.get(neighborId);
                    g.drawLine((int)center1.x(), (int)(-1*center1.y()), (int)center2.x(), (int)(-1*center2.y()));
                }
            }
        }
    }

    private void drawColoredVertices(Graphics2D g) {
        final int POINT_SIZE = 14;
        Color[] colors = {new Color(220, 20, 60), new Color(30, 144, 255), new Color(50, 205, 50)}; // Crimson, DodgerBlue, LimeGreen

        for (Vertex v : polygon) {
            Integer colorIndex = nodeColor.get(v.index());
            if (colorIndex != null) {
                g.setColor(colors[colorIndex % 3]);
                g.fillOval((int) v.x() - POINT_SIZE / 2, (int) (-1 * v.y()) - POINT_SIZE / 2, POINT_SIZE, POINT_SIZE);
            }
        }
    }

    private void drawGuards(Graphics2D g) {
        final int GUARD_SIZE = 22;
        final int INNER_SIZE = 10;
        
        for (Vertex v : polygon) {
            if (nodeColor.get(v.index()).equals(minColor)) {
                int x = (int) v.x();
                int y = (int) (-1 * v.y());
                
                g.setColor(new Color(255, 215, 0, 150)); // Gold, semi-transparent
                g.fillOval(x - GUARD_SIZE / 2, y - GUARD_SIZE / 2, GUARD_SIZE, GUARD_SIZE);

                g.setColor(COLOR_GUARD);
                g.fillOval(x - INNER_SIZE / 2, y - INNER_SIZE / 2, INNER_SIZE, INNER_SIZE);
            }
        }
    }
    
    // --- Setters for View State ---
    public void setShowTrapezoids(boolean showTrapezoids) { this.showTrapezoids = showTrapezoids; }
    public void setShowPartition(boolean showPartition) { this.showPartition = showPartition; }
    public void setShowTriangulation(boolean showTriangulation) { this.showTriangulation = showTriangulation; }
    public void setShowDualGraph(boolean showDualGraph) { this.showDualGraph = showDualGraph; }
    public void setShowColoring(boolean showColoring) { this.showColoring = showColoring; }
    public void setShowGuards(boolean showGuards) { this.showGuards = showGuards; }
}
