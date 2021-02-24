import java.util.*;

/**
 * Beginnings of a library for graph analysis code
 *
 * based off of @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2017 (with some inspiration from previous terms)
 *
 */
public class GraphLib {

	/**
	 * BFS to find shortest path tree for a current center of the universe. Return a path tree as a Graph.
	 * @param g A graph
	 * @param source The vertex we start from. (Center of the Universe)
	 * @param <V>
	 * @param <E>
	 * @return a graph of
	 */
	public static <V,E> Graph<V,E> bfs(Graph<V,E> g, V source){
		Graph<V,E> pathTree = new AdjacencyMapGraph<V,E>();
		Set<V> visited = new HashSet<>();
		Queue<V> queue = new LinkedList<>();
//		enqueue the start vertex s onto the queue
		visited.add(source);
		queue.add(source);
		pathTree.insertVertex(source);

//		remember that s has been added
//		repeat until we find the goal vertex or the queue is empty:
		while( !queue.isEmpty()) {

//		dequeue the next vertex u from the queue
			V u = queue.remove();
//		(maybe do something while here)
			//		for all vertices v that are adjacent to u
			for (V adjacent : g.outNeighbors(u)) {
				//		if haven't already added v
				if (!visited.contains(adjacent)) {
					//	enqueue v onto the queue
					//	remember that v has been added
					visited.add(adjacent);
					queue.add(adjacent);
					//insert the current neighbor we are looking at into the pathTree
					//make the current neighbor point to the parent with the correct label
					pathTree.insertVertex(adjacent);
					pathTree.insertDirected(adjacent, u, g.getLabel(adjacent, u));
				}
			}

		}
		//return the BFS pathTree
		return pathTree;
	}

	/**
	 * Given a shortest path tree and a vertex, construct a path from the
	 * vertex back to the center of the universe.
	 * @param tree tree from BST
	 * @param v - from current vertex
	 * @param <V>
	 * @param <E>
	 * @return
	 */
	public static <V,E> List<V> getPath(Graph<V,E> tree, V v){
		//An arrayList that has the path from v to center, in order
		ArrayList<V> path = new ArrayList<V>();
		//current is set to v that is passed in
		V current = v;

		//current is added to the pathTree
		path.add(current);

		//as long as there are outDegrees for the current, we keep going
		while(tree.outDegree(current)>0 ){

			// for each outNeighbor of current, set current to the neighbor(which in this case is only one neighbor since its a BFS
			for(V u :tree.outNeighbors(current)) current = u;
			//adds the new current to the path
			path.add(current);
		}

		//return path
		return path;
	}

	/**
	 * Given a graph and a subgraph (here shortest path tree),
	 * determine which vertices are in the graph but not the subgraph
	 * (here, not reached by BFS).
	 * @param graph
	 * @param subgraph
	 * @param <V>
	 * @param <E>
	 * @return
	 */
	public static <V,E> Set<V> missingVertices(Graph<V,E> graph, Graph<V,E> subgraph){
		//a set that will contain all the missing vertices
		Set<V> missing = new HashSet<V>();
		//this set that will keep track of all the vertices in subgraph
		Set<V> subgraphVert = new HashSet<>();

		//goes through each vertex in subgraph and adds it to the set subgraphVert
		for(V v: subgraph.vertices()){
			subgraphVert.add(v);
		}

		//goes through each vertex in the graph and if it isn't found in the set of vertices of subgraph,
		//adds it to the set containing missing vertices
		for(V v: graph.vertices()){
			if(!subgraphVert.contains(v)){
				missing.add(v);
			}
		}

		//return the set of missing vertices.
		return missing;



	}


	/**
	 * Find the average distance-from-root in a shortest path tree, without enumerating all the paths.
	 * Warning: this one takes some thinking to do correctly and efficiently; don't put it off for the last minute.
	 * Hint: the average is the sum divided by the number, and computing the sum is now a tree recursion problem
	 * (in which you need to pass information from parent to child, too).
	 * @param tree
	 * @param root - center of the universe
	 * @param <V>
	 * @param <E>
	 * @return
	 */
	public static <V,E> double averageSeparation(Graph<V,E> tree, V root){

		int sum = 0;
		int count = tree.numVertices()-1;
		// recursive method to get the totalSteps
		sum = totalStep(tree, root, 1);


		return (double)sum / count;
	}

	public static <V,E> int totalStep(Graph<V,E> tree, V root, int path){
		int total = 0;
		// for all the in neighbors of root
		for(V v: tree.inNeighbors(root)){
			// add the current distance from the center to total
			total += path ;
			// repeat for their in neighbors and increase current distance from center by 1
			total += totalStep(tree, v, path+1);
		}

		// return total
		return total;
	}
}
