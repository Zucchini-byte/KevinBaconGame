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
		while( !queue.isEmpty()){

//		dequeue the next vertex u from the queue
			V u = queue.remove();
//		(maybe do something while here)
			//		for all vertices v that are adjacent to u
			for(V adjacent: g.outNeighbors(u)){
				//		if haven't already added v
				if(!visited.contains(adjacent)){
				//		enqueue v onto the queue
				//		remember that v has been added
					visited.add(adjacent);
					queue.add(adjacent);
					pathTree.insertVertex(adjacent);
					pathTree.insertDirected(adjacent, u,g.getLabel(adjacent, u));
				}
			}

		}

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
		ArrayList<V> path = new ArrayList<V>();
		V current = v;
		path.add(current);
		while(tree.outDegree(current)>0 ){
			for(V u :tree.outNeighbors(current)) current = u;
			path.add(current);
		}

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
		Set<V> missing = new HashSet<V>();
		Set<V> subgraphVert = new HashSet<>();

		for(V v: subgraph.vertices()){
			subgraphVert.add(v);
		}

		for(V v: graph.vertices()){
			if(!subgraphVert.contains(v)){
				missing.add(v);
			}
		}

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

		sum = totalStep(tree, root, 1);


		return (double)sum / count;
	}

	public static <V,E> int totalStep(Graph<V,E> tree, V root, int path){
		int total = 0;
		for(V v: tree.inNeighbors(root)){
			total += path ;
			total += totalStep(tree, v, path+1);
		}


		return total;
	}
}
