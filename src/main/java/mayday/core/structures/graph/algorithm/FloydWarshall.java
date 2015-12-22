package mayday.core.structures.graph.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;

public class FloydWarshall 
{
	private double[][] D;
	private Node[][] P;
	private Map<Node, Integer> nodeMap;
	
	public FloydWarshall(Graph g) 
	{
		int i=0;
		nodeMap=new HashMap<Node, Integer>();
		Node[] nodes=new Node[g.getNodes().size()];
		for(Node n: g.getNodes())
		{
			nodeMap.put(n,i);
			nodes[i]=n;
			++i;
		}
		Edge[] edges=(Edge[]) g.getEdges().toArray(new Edge[g.getEdges().size()]);
		calcShortestPaths(nodes, edges);		
	}
	
	private void calcShortestPaths(Node[] nodes, Edge[] edges) 
	{
		D = initializeWeight(nodes, edges);
		P = new Node[nodes.length][nodes.length];

		for(int k=0; k<nodes.length; k++){
			for(int i=0; i<nodes.length; i++){
				for(int j=0; j<nodes.length; j++){
					if(D[i][k] != Double.MAX_VALUE && D[k][j] != Double.MAX_VALUE && D[i][k]+D[k][j] < D[i][j]){
						D[i][j] = D[i][k]+D[k][j];
						P[i][j] = nodes[k];
					}
				}
			}
		}
	}

	public double getShortestDistance(Node source, Node target){
		return D[nodeMap.get(source)][nodeMap.get(target)];
	}
	
	
	public List<Node> getShortestCycle(Node n)
	{
		return getShortestPath(n, n);
	}
	

	public List<Node> getShortestPath(Node source, Node target){
		if(D[nodeMap.get(source)][nodeMap.get(target)] == Double.MAX_VALUE){
			return new ArrayList<Node>();
		}
		ArrayList<Node> path = getIntermediatePath(source, target);
		path.add(0, source);
		path.add(target);
		return path;
	}

	private ArrayList<Node> getIntermediatePath(Node source, Node target){
		if(D == null){
			throw new IllegalArgumentException("Must call calcShortestPaths(...) before attempting to obtain a path.");
		}
		if(P[nodeMap.get(source)][nodeMap.get(target)] == null){
			return new ArrayList<Node>();
		}
		ArrayList<Node> path = new ArrayList<Node>();
		path.addAll(getIntermediatePath(source, P[nodeMap.get(source)][nodeMap.get(target)]));
		path.add(P[nodeMap.get(source)][nodeMap.get(target)]);
		path.addAll(getIntermediatePath(P[nodeMap.get(source)][nodeMap.get(target)], target));
		return path;
	}

	private double[][] initializeWeight(Node[] nodes, Edge[] edges){
		double[][] Weight = new double[nodes.length][nodes.length];
		for(int i=0; i<nodes.length; i++){
			Arrays.fill(Weight[i], Double.MAX_VALUE);
		}
		for(Edge e : edges){
			Weight[nodeMap.get(e.getSource())][nodeMap.get(e.getTarget())] = e.getWeight();
		}
		return Weight;
	}
	
}
