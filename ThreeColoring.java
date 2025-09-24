import java.util.*;
/* Important assertion is that the dcel's in triangulation list
*  are sorted by their ids which are numbers from 1 to n
*  where n is the number of triangles, check this assertion if required
*/
public class ThreeColoring {
  private TreeMap<Integer,Integer> id_to_index;
  public TreeMap<Integer,Integer> threeColor(ArrayList<DoublyConnectedEdgeList> triangulation, ArrayList<Vertex> vertices) {
    for(DoublyConnectedEdgeList dcel: triangulation) {
      assert dcel.node_count() == 3;
    }
    id_to_index = new TreeMap<Integer,Integer>();

    TreeMap<Integer,Integer> nodeColor = new TreeMap<Integer,Integer>();
    DualGraph dualGraph  = new DualGraph(triangulation,vertices);
    dualGraph.construct();
    TreeMap<Integer,Boolean> visited = new TreeMap<Integer,Boolean>();
    for(int i = 0; i < triangulation.size(); i++) {
      visited.put(triangulation.get(i).id(),false);
      id_to_index.put(triangulation.get(i).id(),i);
    }


    DFS(dualGraph, triangulation, nodeColor, visited);
    return nodeColor;
  }

  public void DFS(DualGraph dualGraph, ArrayList<DoublyConnectedEdgeList> triangulation, TreeMap<Integer,Integer> nodeColor, TreeMap<Integer,Boolean> visited) {
    int src = triangulation.get(0).id();
    DoublyConnectedEdgeList dcel = triangulation.get(0);
    DoublyConnectedEdgeList.DCEL_Edge tempEdge = dcel.rep_edge();
    for(int i = 0; i < 3; i++) {
      nodeColor.put(tempEdge.origin().id(),i);
      tempEdge = tempEdge.next();
    }
    visited.put(src,true);
    for(int k: dualGraph.getAdjacencyList().get(src)) {
      if(!visited.get(k))
        DFSUtil(dualGraph, triangulation, nodeColor, k, visited);
    }
  }

  public void DFSUtil(DualGraph dualGraph, ArrayList<DoublyConnectedEdgeList> triangulation, TreeMap<Integer,Integer> nodeColor, int src, TreeMap<Integer,Boolean> visited) {
    DoublyConnectedEdgeList dcel = triangulation.get(id_to_index.get(src));
    DoublyConnectedEdgeList.DCEL_Edge tempEdge = dcel.rep_edge();
    int color = 0;
    for(int i = 0; i < 3; i++) {
      if(nodeColor.containsKey(tempEdge.origin().id())) {
        color += nodeColor.get(tempEdge.origin().id());
      }
      tempEdge = tempEdge.next();
    }

    for(int i = 0; i < 3; i++) {
      if(!nodeColor.containsKey(tempEdge.origin().id())) {
        nodeColor.put(tempEdge.origin().id(),(3-color)%3);
      }
      tempEdge = tempEdge.next();
    }
    visited.put(src,true);
    for(int k: dualGraph.getAdjacencyList().get(src)) {
      if(!visited.get(k))
        DFSUtil(dualGraph, triangulation, nodeColor, k, visited);
    }
  }
}
