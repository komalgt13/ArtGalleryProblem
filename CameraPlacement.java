// GroupID-19 (Komal 22113078_Dhruv 22114029_Himanshu Raheja22323023)
// Date: September 24, 2025
// CameraPlacement.java - Solves the camera-placement variant of the Art Gallery problem using greedy set-cover with fixed FOV.

import java.util.*;

public class CameraPlacement {
    private static final double FOV_DEGREES = 50.0;

    public static class Camera {
        public int vertexId;
        public double orientationDeg;
        public Set<Integer> trianglesCovered;

        public Camera(int vId, double orient, Set<Integer> covered) {
            this.vertexId = vId;
            this.orientationDeg = orient;
            this.trianglesCovered = new HashSet<>(covered);
        }

        @Override
        public String toString() {
            return String.format("Camera at vertex %d orient=%.2f deg covers triangles %s",
                                 vertexId, orientationDeg, trianglesCovered.toString());
        }
    }

    public static List<Camera> solve(ArrayList<DoublyConnectedEdgeList> triangulation, ArrayList<Vertex> vertices) {
        TreeMap<Integer, ArrayList<CircInterval>> intervalsByVertex = new TreeMap<>();
        HashMap<Integer, Boolean> triangleCoverable = new HashMap<>();

        for (DoublyConnectedEdgeList tri : triangulation) {
            int triId = tri.id();
            triangleCoverable.put(triId, false);

            Vertex[] triVerts = {
                new Vertex(tri.rep_edge().origin()),
                new Vertex(tri.rep_edge().next().origin()),
                new Vertex(tri.rep_edge().next().next().origin())
            };

            for (int i = 0; i < 3; i++) {
                Vertex v = triVerts[i];
                Vertex v1 = triVerts[(i + 1) % 3];
                Vertex v2 = triVerts[(i + 2) % 3];

                double a1 = angleDeg(v, v1);
                double a2 = angleDeg(v, v2);
                double span = circularSpan(a1, a2);

                if (span <= FOV_DEGREES + 1e-9) {
                    double allowance = (FOV_DEGREES - span) / 2.0;
                    double mid = norm360(a1 + circularDiff(a1, a2) / 2.0);
                    
                    double start = norm360(mid - allowance);
                    double end = norm360(mid + allowance);

                    CircInterval ci = new CircInterval(start, end, triId);
                    intervalsByVertex.computeIfAbsent(v.index(), k -> new ArrayList<>()).add(ci);
                    triangleCoverable.put(triId, true);
                }
            }
        }
        
        reportUncoverableTriangles(triangulation, triangleCoverable);
        TreeMap<Integer, ArrayList<PairDoubleSet>> candidates = buildCandidates(intervalsByVertex);
        return greedySetCover(triangulation, candidates);
    }

    private static TreeMap<Integer, ArrayList<PairDoubleSet>> buildCandidates(TreeMap<Integer, ArrayList<CircInterval>> intervalsByVertex) {
        TreeMap<Integer, ArrayList<PairDoubleSet>> candidatesByVertex = new TreeMap<>();
        for (int vid : intervalsByVertex.keySet()) {
            ArrayList<Event> events = new ArrayList<>();
            for (CircInterval ci : intervalsByVertex.get(vid)) {
                events.add(new Event(ci.start, 1, ci.triangleId));
                events.add(new Event(ci.end, -1, ci.triangleId));
                if (ci.start > ci.end) {
                    events.add(new Event(0.0, 1, ci.triangleId));
                    events.add(new Event(360.0, -1, ci.triangleId));
                }
            }

            events.sort((a, b) -> {
                if (Math.abs(a.ang - b.ang) > 1e-9) {
                    return Double.compare(a.ang, b.ang);
                }
                return Integer.compare(b.type, a.type);
            });

            HashSet<Integer> activeTriangles = new HashSet<>();
            int maxCoverage = 0;
            ArrayList<Sector> bestSectors = new ArrayList<>();
            double lastAngle = 0.0;

            for (Event ev : events) {
                if (ev.ang > lastAngle && !activeTriangles.isEmpty()) {
                    if (activeTriangles.size() > maxCoverage) {
                        maxCoverage = activeTriangles.size();
                        bestSectors.clear();
                    }
                    if (activeTriangles.size() == maxCoverage) {
                        bestSectors.add(new Sector(lastAngle, ev.ang, new HashSet<>(activeTriangles)));
                    }
                }
                if (ev.type == 1) activeTriangles.add(ev.triId);
                else activeTriangles.remove(ev.triId);
                lastAngle = ev.ang;
            }

            ArrayList<PairDoubleSet> candidateList = new ArrayList<>();
            for (Sector s : bestSectors) {
                candidateList.add(new PairDoubleSet(norm360((s.start + s.end) / 2.0), s.covered));
            }
            candidatesByVertex.put(vid, candidateList);
        }
        return candidatesByVertex;
    }

    private static List<Camera> greedySetCover(ArrayList<DoublyConnectedEdgeList> triangulation, TreeMap<Integer, ArrayList<PairDoubleSet>> candidates) {
        HashSet<Integer> remainingTriangles = new HashSet<>();
        for (DoublyConnectedEdgeList tri : triangulation) remainingTriangles.add(tri.id());
        List<Camera> solution = new ArrayList<>();

        while (!remainingTriangles.isEmpty()) {
            int bestCoverCount = 0;
            Camera bestCamera = null;

            for (int vid : candidates.keySet()) {
                for (PairDoubleSet pds : candidates.get(vid)) {
                    Set<Integer> currentCover = new HashSet<>(pds.set);
                    currentCover.retainAll(remainingTriangles);
                    
                    if (currentCover.size() > bestCoverCount) {
                        bestCoverCount = currentCover.size();
                        bestCamera = new Camera(vid, pds.value, currentCover);
                    }
                }
            }
            
            if (bestCoverCount == 0) {
                System.out.println("Stopping greedy cover. Uncovered triangles remain: " + remainingTriangles);
                break;
            }
            
            solution.add(bestCamera);
            remainingTriangles.removeAll(bestCamera.trianglesCovered);
        }
        return solution;
    }
    
    private static void reportUncoverableTriangles(ArrayList<DoublyConnectedEdgeList> triangulation, HashMap<Integer, Boolean> coverableMap) {
        ArrayList<Integer> uncovered = new ArrayList<>();
        for (DoublyConnectedEdgeList t : triangulation) {
            if (!coverableMap.getOrDefault(t.id(), false)) {
                uncovered.add(t.id());
            }
        }
        if (!uncovered.isEmpty()) {
            System.out.println("Warning: The following triangles have an angular span > " + FOV_DEGREES +
                               "° from all of their vertices and cannot be covered by this method: " + uncovered);
        }
    }

    private static double angleDeg(Vertex v, Vertex q) { double a = Math.toDegrees(Math.atan2(q.y() - v.y(), q.x() - v.x())); return a < 0 ? a + 360.0 : a; }
    private static double circularSpan(double a, double b) { double d = Math.abs(a - b); return d <= 180.0 ? d : 360.0 - d; }
    private static double circularDiff(double from, double to) { double d = to - from; if (d > 180) d -= 360; if (d < -180) d += 360; return d; }
    private static double norm360(double a) { double v = a % 360.0; return v < 0 ? v + 360.0 : v; }

    private static class CircInterval { double start, end; int triangleId; CircInterval(double s, double e, int tid) { start = norm360(s); end = norm360(e); triangleId = tid; } }
    private static class Event { double ang; int type; int triId; Event(double a, int t, int id) { ang = a; type = t; triId = id; } }
    private static class Sector { double start, end; HashSet<Integer> covered; Sector(double s, double e, HashSet<Integer> c) { start = s; end = e; covered = c; } }
    private static class PairDoubleSet { double value; HashSet<Integer> set; PairDoubleSet(double v, HashSet<Integer> s) { value = v; set = s; } }
}
