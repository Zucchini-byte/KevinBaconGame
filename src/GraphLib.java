import java.util.*;

/**
 * Beginnings of a library for graph analysis code
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2017 (with some inspiration from previous terms)
 * 
 */
public class GraphLib {
	/**
	 * Orders vertices in decreasing order by their in-degree
	 * @param g		graph
	 * @return		list of vertices sorted by in-degree, decreasing (i.e., largest at index 0)
	 */
	public static <V,E> List<V> verticesByInDegree(Graph<V,E> g) {
		List<V> vs = new ArrayList<V>();
		for (V v : g.vertices()) vs.add(v);
		vs.sort((v1, v2) -> g.inDegree(v2) - g.inDegree(v1));
		return vs;
	}

	/**
	 * Takes a random walk from a vertex, to a random one if its out-neighbors, to a random one of its out-neighbors
	 * Keeps going as along as a random number is less than "continueProb"
	 * Stops earlier if no step can be taken (i.e., reach a vertex with no out-edge)
	 * @param g			graph to walk on
	 * @param start		initial vertex (assumed to be in graph)
	 * @param keepOn		probability of continuing each time -- should be between 0 and 1 (non-inclusive)
	 * @return		a list of vertices starting with start, each with an edge to the sequentially next in the list
	 * 			    null if start isn't in graph
	 */
	public static <V,E> List<V> randomWalk(Graph<V,E> g, V start, double keepOn) {
		if (!g.hasVertex(start) || keepOn <= 0 || keepOn >= 1) return null;
		List<V> path = new ArrayList<V>();
		path.add(start);
		V curr = start;
		while (Math.random()<keepOn) {
			if (g.outDegree(curr) == 0) return path;
			// Pick a neighbor index
			int nbr = (int)(g.outDegree(curr) * Math.random());
			// Iterate through the out-neighbors the given number of times
			Iterator<V> iter = g.outNeighbors(curr).iterator();
			V next = iter.next();
			while (nbr > 0) {
				next = iter.next();
				nbr--;
			}
			// Got to the right neighbor; continue from there
			path.add(next);
			curr = next;
		}

		return path;
	}
	
	/**
	 * Takes a number of random walks from random vertices, keeping track of how many times it goes to each vertex
	 * Doesn't actually keep the walks themselves
	 * @param g			graph to walk on
	 * @param keepOn		probability of continuing each time -- should be between 0 and 1 (non-inclusive)
	 * @param numWalks	how many times to do that
	 * @return			vertex-hitting frequencies
	 */
	public static <V,E> Map<V,Integer> randomWalks(Graph<V,E> g, double keepOn, int numWalks) {
		if (keepOn <= 0 || keepOn >= 1) return null;
		
		// Initialize all frequencies to 0
		Map<V,Integer> freqs = new HashMap<V,Integer>();
		for (V v : g.vertices()) freqs.put(v, 0);
		
		for (int i=0; i<numWalks; i++) {
			// Pick a start index
			int start = (int)(g.numVertices()*Math.random());
			// Iterate through vertices till get there
			Iterator<V> iter = g.vertices().iterator();
			V curr = iter.next();
			while (start > 0) {
				curr = iter.next();
				start--;
			}
			while (Math.random()<keepOn && g.outDegree(curr)>0) {
				// Pick a neighbor index
				int nbr = (int)(g.outDegree(curr) * Math.random());
				// Iterate through the out-neighbors the given number of times
				iter = g.outNeighbors(curr).iterator();
				V next = iter.next();
				while (nbr > 0) {
					next = iter.next();
					nbr--;
				}
				// Keep frequency count
				freqs.put(next, 1+freqs.get(next));
				curr = next;
			}			
		}

		return freqs;
	}


	/**
	 * Orders vertices in decreasing order by their frequency in the map
	 * @param g		graph
	 * @return		list of vertices sorted by frequency, decreasing (i.e., largest at index 0)
	 */
	public static <V,E> List<V> verticesByFrequency(Graph<V,E> g, Map<V,Integer> freqs) {
		List<V> vs = new ArrayList<V>();
		for (V v : g.vertices()) vs.add(v);
		vs.sort((v1, v2) -> freqs.get(v2) - freqs.get(v1));
		return vs;
	}
}
