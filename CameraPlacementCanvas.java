// GroupID-19 (Komal 22113078_Dhruv 22114029_Himanshu Raheja22323023)
// Date: September 24, 2025
// CameraPlacementCanvas.java - Canvas for visualizing the Camera Placement Problem.

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CameraPlacementCanvas extends JPanel {

    private ArrayList<Vertex> polygon;
    private ArrayList<DoublyConnectedEdgeList> triangulation;
    private List<CameraPlacement.Camera> cameras;
    private ArrayList<Edge> trapezoids;
    private ArrayList<Edge> partitions;
    private HashMap<Integer, Vertex> vertexMap = new HashMap<>();

    public CameraPlacementCanvas() {
        setPreferredSize(new Dimension(1200, 800));
        setBackground(Color.DARK_GRAY.darker());
    }

    public void setData(ArrayList<Vertex> polygon, ArrayList<DoublyConnectedEdgeList> triangulation, List<CameraPlacement.Camera> cameras) {
        this.polygon = polygon;
        this.triangulation = triangulation;
        this.cameras = cameras;
        this.trapezoids = null;
        this.partitions = null;
        this.vertexMap.clear();
        if (polygon != null) {
            for (Vertex v : polygon) this.vertexMap.put(v.index(), v);
        }
    }

    public void setTrapezoids(ArrayList<Edge> trapezoids) { this.trapezoids = trapezoids; }
    public void setPartitions(ArrayList<Edge> partitions) { this.partitions = partitions; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (polygon == null) return;

        if (trapezoids != null) drawEdges(g2d, trapezoids, new Color(255, 165, 0, 100), new BasicStroke(1));
        if (partitions != null) drawEdges(g2d, partitions, new Color(0, 100, 255), new BasicStroke(2.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{10, 6}, 0));
        if (triangulation != null) drawTriangulation(g2d);
        if (cameras != null) drawCoverage(g2d);

        drawPolygonOutline(g2d);
        drawVertices(g2d);

        if (cameras != null) drawCameras(g2d);
        drawLabels(g2d);
    }
    
    private void drawTriangulation(Graphics2D g) {
        g.setColor(new Color(80, 80, 80));
        g.setStroke(new BasicStroke(0.75f));
        for (DoublyConnectedEdgeList tri : triangulation) {
            Path2D.Double triPath = createPathFromDcel(tri);
            g.draw(triPath);
        }
    }

    private void drawCoverage(Graphics2D g) {
        if (triangulation == null || triangulation.isEmpty()) return;
        HashMap<Integer, DoublyConnectedEdgeList> triMap = new HashMap<>();
        for (DoublyConnectedEdgeList tri : triangulation) triMap.put(tri.id(), tri);

        float hue = 0.0f;
        for (CameraPlacement.Camera cam : cameras) {
            Color color = Color.getHSBColor(hue, 0.7f, 0.9f);
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 70));
            
            for (int triId : cam.trianglesCovered) {
                DoublyConnectedEdgeList tri = triMap.get(triId);
                if (tri != null) g.fill(createPathFromDcel(tri));
            }
            hue += 1.0f / Math.max(1, cameras.size());
        }
    }
    
    private void drawPolygonOutline(Graphics2D g) {
        if (polygon.size() < 2) return;
        Path2D.Double polyPath = createPathFromVertices(polygon);
        if (polygon.size() >= 3) polyPath.closePath();
        g.setColor(Color.LIGHT_GRAY);
        g.setStroke(new BasicStroke(3f));
        g.draw(polyPath);
    }
    
    private void drawEdges(Graphics2D g, ArrayList<Edge> edges, Color color, Stroke stroke) {
        g.setColor(color);
        g.setStroke(stroke);
        for (Edge edge : edges) {
            g.drawLine((int) edge.start_vertex().x(), (int) edge.start_vertex().y(), (int) edge.end_vertex().x(), (int) edge.end_vertex().y());
        }
    }
    
    private void drawVertices(Graphics2D g) {
        g.setColor(Color.WHITE);
        for (Vertex v : polygon) g.fillOval((int)v.x() - 5, (int)v.y() - 5, 10, 10);
    }

    private void drawCameras(Graphics2D g) {
        float hue = 0.0f;
        for (CameraPlacement.Camera cam : cameras) {
            Vertex pos = vertexMap.get(cam.vertexId);
            if (pos == null) continue;
            Color color = Color.getHSBColor(hue, 0.9f, 1.0f);
            g.setColor(color);
            g.fillOval((int)pos.x() - 8, (int)pos.y() - 8, 16, 16);
            g.setStroke(new BasicStroke(2.5f));
            double angleRad = Math.toRadians(cam.orientationDeg);
            g.drawLine((int)pos.x(), (int)pos.y(), (int)(pos.x() + 30 * Math.cos(angleRad)), (int)(pos.y() + 30 * Math.sin(angleRad)));
            hue += 1.0f / Math.max(1, cameras.size());
        }
    }

    private void drawLabels(Graphics2D g) {
        g.setFont(new Font("Arial", Font.BOLD, 12));
        for (Vertex v : polygon) {
            String label = "v" + v.index() + String.format(" (%d, %d)", (int)v.x(), (int)v.y());
            g.setColor(Color.BLACK);
            g.drawString(label, (int)v.x() + 11, (int)v.y() - 9);
            g.setColor(Color.YELLOW);
            g.drawString(label, (int)v.x() + 10, (int)v.y() - 10);
        }
    }

    private Path2D.Double createPathFromVertices(ArrayList<Vertex> vertices) {
        Path2D.Double path = new Path2D.Double();
        if (vertices.isEmpty()) return path;
        path.moveTo(vertices.get(0).x(), vertices.get(0).y());
        for (int i = 1; i < vertices.size(); i++) path.lineTo(vertices.get(i).x(), vertices.get(i).y());
        return path;
    }

    private Path2D.Double createPathFromDcel(DoublyConnectedEdgeList dcel) {
        Path2D.Double path = new Path2D.Double();
        DoublyConnectedEdgeList.DCEL_Edge e = dcel.rep_edge();
        path.moveTo(e.origin().x(), e.origin().y());
        path.lineTo(e.next().origin().x(), e.next().origin().y());
        path.lineTo(e.next().next().origin().x(), e.next().next().origin().y());
        path.closePath();
        return path;
    }
}
