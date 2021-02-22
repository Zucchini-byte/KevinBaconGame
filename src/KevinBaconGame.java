import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class KevinBaconGame {
    private Map<Integer, String> movieMap;
    private Map<Integer, String> actorMap;
    private Map<Integer, Set<Integer>> movieToActors;
    private Graph<String, Set<String>> gameGraph;

    public KevinBaconGame(){
        try{
            movieMap = textToMap("inputs/moviesTest.txt");
            actorMap = textToMap("inputs/actorsTest.txt");
            movieToActors = match("inputs/movie-actorsTest.txt");

        } catch (Exception e) {
            e.printStackTrace();
        }

        gameGraph = new AdjacencyMapGraph<>();


    }


    public static Map<Integer, String> textToMap(String filePath) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        Map<Integer, String> res = new HashMap<Integer, String>();
        String line;

        while((line = br.readLine()) != null){
            String[] split = line.split("\\|");
            Integer key = Integer.parseInt(split[0]);
            if(!res.containsKey(key)){
                res.put(key, split[1]);
            }
        }
        br.close();

        return res;

    }

    public static Map<Integer,Set<Integer>> match(String pathFile) throws Exception{
        BufferedReader br = new BufferedReader(new FileReader(pathFile));

        Map<Integer, Set<Integer>> res = new HashMap<Integer, Set<Integer>>();
        String line;
        while( (line = br.readLine()) != null ){
            String[] split = line.split("\\|");
            Integer key = Integer.parseInt(split[0]);
            Integer item = Integer.parseInt(split[1]);

            if(!res.containsKey(key)){
                Set<Integer> set = new HashSet<>();
                set.add(item);
                res.put(key, set);
            }
            else{
                res.get(key).add(item);
            }


        }


        br.close();

        return res;

    }

    public void createGraph(){
        gameGraph = new AdjacencyMapGraph<>();
        for(Integer key: actorMap.keySet()){
            gameGraph.insertVertex(actorMap.get(key));
        }
        for( Integer movie: movieToActors.keySet()){
            // set<string>
            for(Integer v: movieToActors.get(movie)){
                for(Integer u: movieToActors.get(movie)){
                    if(v != u) {
                        if(gameGraph.getLabel(actorMap.get(u), actorMap.get(v)) == null){
                            HashSet<String> set = new HashSet<>();
                            set.add(movieMap.get(movie));
                            gameGraph.insertUndirected(actorMap.get(u), actorMap.get(v), set);
                        }
                        else {
                            gameGraph.getLabel(actorMap.get(u), actorMap.get(v)).add(movieMap.get(movie));
                        }
                    }
                }
            }
        }
    }



    public static void main(String[] args){

       KevinBaconGame game = new KevinBaconGame();
//        System.out.println(game.actorMap);
//        System.out.println(game.movieMap);
//        System.out.println(game.movieToActors);
        game.createGraph();
//        System.out.println(game.gameGraph);
        Graph<String, Set<String>> t = new AdjacencyMapGraph<>();
        t = (GraphLib.bfs(game.gameGraph, "Dartmouth (Earl thereof)"));
//        System.out.println(t.outNeighbors("Dartmouth (Earl thereof)"));
        System.out.println(t);
        System.out.println(GraphLib.getPath(t, "Alice"));
    }
}
