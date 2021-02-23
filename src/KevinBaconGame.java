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
    private Graph<String, Set<String>> BFS;
    private String command = "";

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

    public void startGame(String center){
        createGraph();
        BFS = GraphLib.bfs(gameGraph, center);
        printCommand();
        printCenter(center);
        Scanner sc = new Scanner(System.in);
        command = sc.next();
        while(!command.equals("q")){
            // list top/bottom center of universe, sorted by average separation
            if(command.equals("c")){
                int bestCenters = sc.nextInt();

                if(Math.abs(bestCenters) > gameGraph.numVertices()){
                    System.out.println("Please enter a number that's smaller or equal to the total of " + gameGraph.numVertices());
                    try {
                        bestCenters = sc.nextInt();
                    }
                    catch (InputMismatchException e){
                        System.out.println("Please insert a number");

                    }
                }

                // make a PriorityQueue of a Map<center, average separation>
                Map<String, Double> map = new HashMap<>();
                Queue<String> queue = new PriorityQueue<String>();

                if(bestCenters > 0) {
                    System.out.println("Top " + bestCenters + " center, sorted by average separation");
                    queue = new PriorityQueue<String>((String s1, String s2) -> (int) ((map.get(s2) - map.get(s1)) * 100));
                }
                else{
                    System.out.println("Bottom " + Math.abs(bestCenters) + " center, sorted by average separation");
                    queue = new PriorityQueue<String>((String s1, String s2) -> (int) ((map.get(s1) - map.get(s2)) * 100));

                }

                // put value in map
                for( String s:gameGraph.vertices()){
                    double avgSeparation = GraphLib.averageSeparation(GraphLib.bfs(gameGraph,s),s);
                    map.put(s, avgSeparation);
                    queue.add(s);
                }




                for(int i = 0; i < Math.abs(bestCenters) ; i++){
                    String s = queue.remove();
//                    System.out.println(s);
                    System.out.printf( "%4.2f %s\n", map.get(s), s );
                }



            }
            // list actors sorted by degree, with degree between low and high
            else if(command.equals("d")){
                int low = Integer.parseInt(sc.next());
                int high = Integer.parseInt(sc.next());
                Map<Integer, Set<String>> degreeList = new HashMap<>();
                System.out.println("List of actors sorted by degree: ");

                for (String vertex : gameGraph.vertices()) {
                    if (gameGraph.inDegree(vertex) >= low && gameGraph.inDegree(vertex) <= high) {
                        if (degreeList.get(gameGraph.inDegree(vertex)) == null) {
                            degreeList.put(gameGraph.inDegree(vertex), new HashSet<>());
                        }
                        degreeList.get(gameGraph.inDegree(vertex)).add(vertex);
                    }
                }

                for (int degree : degreeList.keySet()) {
                    System.out.println("List of actors with " + degree + " degrees: ");
                    for (String actor : degreeList.get(degree)) {
                        System.out.println(actor);
                    }
                }

            }
            // list actors with infinite separation from the current center
            else if(command.equals("i")){
                System.out.println(GraphLib.missingVertices(gameGraph, BFS));

            }
            // <name> find path from <name> to current center of the universe
            else if(command.equals("p")){
                String name = sc.nextLine();
                name = name.replaceAll("\\s", "");
                while(!gameGraph.hasVertex(name)){
                    System.out.println("Name not found, please input a valid name");
                    name = sc.nextLine();
                }

                List<String> path = GraphLib.getPath(BFS, name);

                for(int i = 0; i<path.size()-1; i++){
                    System.out.println(path.get(i) + " was in " + BFS.getLabel(path.get(i), path.get(i+1)) + " with " + path.get(i+1) );
                }

            }
            //  <low> <high>: list actors sorted by non-infinite separation from the current center,
            //  with separation between low and high
            else if(command.equals("s")){
                int low = Integer.parseInt(sc.next());
                int high = Integer.parseInt(sc.next());
                Map<Integer, Set<String>> degreeSeparationList = new HashMap<>();
                Set<String> visited= new HashSet<>();
                visited.add(center);
                listBySeperation(degreeSeparationList, 1, center, visited, low, high);

                System.out.println("Here are actors sorted by " + low+ " to " +high+" separations from " + center);
                for(int num: degreeSeparationList.keySet()) {
                    if (!degreeSeparationList.get(num).isEmpty()) {


                        System.out.println(num + " separations from " + center + ":");

                        for (String actor : degreeSeparationList.get(num)) {
                            System.out.println(actor);
                        }
                    }
                }

            }
            // changes the center of the universe
            else if(command.equals("u")){
                center = sc.nextLine();
                center = center.replaceAll("\\s", "");
                while(!gameGraph.hasVertex(center)){
                    System.out.println("Name not found, please input a valid name");
                    center = sc.nextLine();
                }
                printCenter(center);

            }
            else if(command.equals("n")) printCommand();
            else{
                System.out.println("Invalid Command");
            }

            command = sc.next();

        }

        sc.close();

    }

    public void printCommand(){
        System.out.println("Commands:" +
                "\nc <#>: list top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation" +
                "\nd <low> <high>: list actors sorted by degree, with degree between low and high" +
                "\ni: list actors with infinite separation from the current center" +
                "\np <name>: find path from <name> to current center of the universe" +
                "\ns <low> <high>: list actors sorted by non-infinite separation from the current center, with separation between low and high" +
                "\nu <name>: make <name> the center of the universe" +
                "\nn: print the commands again"+
                "\nq: quit game");
    }

    public void printCenter(String center){
        BFS = GraphLib.bfs(gameGraph, center);
        System.out.println(center + " is now the center of the acting universe, connected to " + BFS.numVertices() + "/" + gameGraph.numVertices()
                + " actors with average separation " + GraphLib.averageSeparation(BFS, center));

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

    public void listBySeperation(Map<Integer, Set<String>> sepList, int level, String center, Set<String> visited,
                                 int low, int high){
        if (level> high) return;
        Queue<String> list= new PriorityQueue<>();

        for(String child: BFS.inNeighbors(center)){
            if(!visited.contains(child)){
                visited.add(child);

                if(sepList.get(level)== null){
                    sepList.put(level, new HashSet<>());
                }
                if(!(level< low)){
                    sepList.get(level).add(child);
                }

                listBySeperation(sepList, level+1, child, visited, low, high);
            }

        }

    }




    public static void main(String[] args){

       KevinBaconGame game = new KevinBaconGame();
       game.startGame("Kevin Bacon");
    }
}
