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

    Map<String, Double> sepMap;
    private List<String> sortedAvg;



    private String command = "";

    public KevinBaconGame(){
        try{
            movieMap = textToMap("inputs/moviesTest.txt");
            actorMap = textToMap("inputs/actorsTest.txt");
            movieToActors = match("inputs/movie-actorsTest.txt");
//            movieMap = textToMap("inputs/movies.txt");
//            actorMap = textToMap("inputs/actors.txt");
//            movieToActors = match("inputs/movie-actors.txt");


        } catch (Exception e) {
            e.printStackTrace();
        }

        gameGraph = new AdjacencyMapGraph<>();

    }

    public void startGame(String center){
        // create graphs
        createGraph();
        BFS = GraphLib.bfs(gameGraph, center);

        // print commands and center
        printCommand();
        printCenter(center);

        // user input
        Scanner sc = new Scanner(System.in);
        command = sc.next();

        // while the command is not "q"
        while(!command.equals("q")){
            // list top/bottom center of universe, sorted by average separation
            if(command.equals("c")){
                try {
                    int bestCenters = sc.nextInt();

                    if (Math.abs(bestCenters) > BFS.numVertices()) {
                        System.out.println("Please enter a number that's smaller or equal to the total of " + BFS.numVertices());
                            bestCenters = sc.nextInt();
                    }


                if(sortedAvg == null ) {
                    // just to see how long it takes to run
                    long startTime = System.currentTimeMillis();
                    //  Map of actor to average separation and list of actors
                    sepMap = new HashMap<String, Double>();
                    sortedAvg = new ArrayList<String>();

                    // put value in map and match them to their average separation
                    for (String s : BFS.vertices()) {
                        double avgSeparation = GraphLib.averageSeparation(GraphLib.bfs(gameGraph, s), s);
                            sepMap.put(s, avgSeparation);
                            sortedAvg.add(s);
                    }

                    // sort the list
                    Comparator<String> c = (String s1, String s2) -> (int) ((sepMap.get(s2) - sepMap.get(s1)) * 1000);
                    sortedAvg.sort(c);

                    // prints out the time it took for the calculation
                    System.out.println("This took " + ((System.currentTimeMillis() - startTime) / 1000 ) + " seconds");
                }

                // prints out the best centers
                if(bestCenters > 0) {
                    System.out.println("Top " + bestCenters + " center of the universe sorted by the average separation");
                    for (int i = 0; i < bestCenters; i++) {
                        String s = sortedAvg.get(i);

                        System.out.printf("%4.10f %s\n", sepMap.get(s), s);
                    }
                }

                // if bestCenter is negative
                else {
                    System.out.println("Bottom " + Math.abs(bestCenters) + " center of the universe sorted by the average separation");
                    for (int i = 0; i < Math.abs(bestCenters); i++) {
                        String s = sortedAvg.get(sortedAvg.size() - 1 - i);

                        System.out.printf("%4.10f %s\n", sepMap.get(s), s);
                    }
                }
                }
                catch( InputMismatchException e){
                    System.out.println("Please insert a number");
                }



            }
            // list actors sorted by degree, with degree between low and high
            else if(command.equals("d")){
                try {
                    // low and high keep track of the lower limit and higher limit passed in by the user
                    int low = Integer.parseInt(sc.next());
                    int high = Integer.parseInt(sc.next());

                    //throws an exception if the low is greater than high- which would be an invalid input.
                    if(low> high) throw new Exception();


                    //the key of this map is the degree
                    //the value is a set of actors with <key> degrees
                    Map<Integer, Set<String>> degreeList = new HashMap<>();
                    System.out.println("List of actors sorted by degree: ");

                    //goes through each vertex in the gameGraph
                    for (String vertex : gameGraph.vertices()) {

                        //if a vertex's degree is less than or equal to low and greater than or equal to high,
                        //adds it to the set that is stored at <degree> key in map
                        if (gameGraph.inDegree(vertex) >= low && gameGraph.inDegree(vertex) <= high) {

                            //if the set that's supposed to be at <degree> key is null
                            // then create a new set there- this happens when this is the first vertex being added at that degree.
                            if (degreeList.get(gameGraph.inDegree(vertex)) == null) {
                                degreeList.put(gameGraph.inDegree(vertex), new HashSet<>());
                            }
                            //adds the vertex to the set at map ket<degree>
                            degreeList.get(gameGraph.inDegree(vertex)).add(vertex);
                        }
                    }

                    //goes through each degree in map and prints out the actors at that degree
                    for (int degree : degreeList.keySet()) {
                        System.out.println("List of actors with " + degree + " degrees: ");
                        for (String actor : degreeList.get(degree)) {
                            System.out.println(actor);
                        }
                    }

                }catch (Exception e){

                    // if any of the input are invalid and an exception is thrown, prints out this message. the user can
                    //re-enter new commands
                    System.out.println("Invalid input");

                }

            }
            // list actors with infinite separation from the current center
            else if(command.equals("i")){

                System.out.println(GraphLib.missingVertices(gameGraph, BFS));

            }
            // <name> find path from <name> to current center of the universe
            else if(command.equals("p")){
                String name = sc.nextLine();
                // remove white space before and after
                name = name.trim();

                // check for valid name
                while(!gameGraph.hasVertex(name)){
                    System.out.println("Name not found, please input a valid name");
                    name = sc.nextLine();
                }

                // create a list of path
                List<String> path = GraphLib.getPath(BFS, name);
                System.out.println(name + "'s number is " + (path.size() -1));

                // print out the path with movie name
                for(int i = 0; i<path.size()-1; i++){
                    System.out.println(path.get(i) + " was in " + BFS.getLabel(path.get(i), path.get(i+1)) + " with " + path.get(i+1) );
                }

            }
            //  <low> <high>: list actors sorted by non-infinite separation from the current center,
            //  with separation between low and high
            else if(command.equals("s")){
                try {
                    //keeps track of the low and high bounds of the separations
                    int low = Integer.parseInt(sc.next());
                    int high = Integer.parseInt(sc.next());

                    //throws an exception if the low is greater than high- which would be an invalid input.
                    if(low> high) throw new Exception();

                    //map's key is the degree of seperation and value is a set of actors at that degree of separation.
                    Map<Integer, Set<String>> degreeSeparationList = new HashMap<>();

                    //keeps track of all the "visited" actors so we dont keep looping over the same actors again and again
                    Set<String> visited = new HashSet<>();
                    visited.add(center); //adds center to the visited list

                    //calls the helper method
                    listBySeparation(degreeSeparationList, 1, center, visited, low, high);

                    System.out.println("Here are actors sorted by " + low + " to " + high + " separations from " + center);
                    for (int num : degreeSeparationList.keySet()) {
                        //if (!degreeSeparationList.get(num).isEmpty()) {
                        System.out.println(num + " separations from " + center + ":");

                        for (String actor : degreeSeparationList.get(num)) {
                            System.out.println(actor);
                        }
                        //}
                    }
                } catch (Exception e){
                    System.out.println("Invalid Input");

                }

            }
            // changes the center of the universe
            else if(command.equals("u")){
                center = sc.nextLine();
                // remove white space before and after
                center = center.trim();

                // if gameGraph does not contain input
                while(!gameGraph.hasVertex(center)){
                    System.out.println("Name not found, please input a valid name");
                    center = sc.nextLine();
                }

                // change BFS graph
                BFS = GraphLib.bfs(gameGraph, center);
                printCenter(center);


            }
            else if(command.equals("n")) printCommand();
            else{
                System.out.println("Invalid Command");
            }

            command = sc.next();

        }
        System.out.println("Thanks for playing!!");

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
        System.out.println(center + " is now the center of the acting universe, connected to " + (BFS.numVertices()-1)  + "/" + gameGraph.numVertices()
                + " actors with average separation " + GraphLib.averageSeparation(BFS, center));

    }


    public static Map<Integer, String> textToMap(String filePath) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        Map<Integer, String> res = new HashMap<Integer, String>();
        String line;

        // while there are something to read
        while((line = br.readLine()) != null){
            // split by "|"
            String[] split = line.split("\\|");

            Integer key = Integer.parseInt(split[0]);
            // insert item if map doesn't contain key
            if(!res.containsKey(key)){
                res.put(key, split[1]);
            }

        }
        br.close();

        return res;

    }

    /**
     * Use to match the movie-actor file to a map of Movie and the actors in it.
     * @param pathFile
     * @return
     * @throws Exception
     */
    public static Map<Integer,Set<Integer>> match(String pathFile) throws Exception{
        BufferedReader br = new BufferedReader(new FileReader(pathFile));

        Map<Integer, Set<Integer>> res = new HashMap<>();
        String line;

        // while the reader still have lines to read
        while( (line = br.readLine()) != null ){
            // splits the line by "|"
            String[] split = line.split("\\|");

            Integer key = Integer.parseInt(split[0]);
            Integer item = Integer.parseInt(split[1]);

            // if the map doesn't contain key then create a set<>, else just add it
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
        // insert all the actors into gameGraph
        for(Integer key: actorMap.keySet()){
            gameGraph.insertVertex(actorMap.get(key));
        }

        // insert Undirected edges between the vertices
        for( Integer movie: movieToActors.keySet()){
            // get a movie and all the actors in the movie
            for(Integer v: movieToActors.get(movie)){

                for(Integer u: movieToActors.get(movie)){
                    // if the actors are not the same add undirected edges
                    if(!v.equals(u)) {
                        // there are currently no edge, add the edge. The edge is a set therefore you can't just add it.
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

    //Helper method: takes it the map that keeps a set of actors at the key <degree separated>
    //also takes in the level we are at- level is used to determine ath which degree of separation we are at
    //also takes in the visited list to make sure we dont visit the same actor twice
    //and takes in low and high to make sure that we only care about actors in degrees separation between these two
    //center here keeps track of the current "center being talked about"- changes with recursion call. it doesnt refer to the actual
    //center of the game.
    public void listBySeparation(Map<Integer, Set<String>> sepList, int level, String center, Set<String> visited,
                                 int low, int high){
        //it will just return if the current level is higher than the upper limit set by the user
        if (level> high) return;

        //a priority queue that keeps track of actors to visit on this level
        Queue<String> list= new PriorityQueue<>();


        //goes through each of the "centers" inNeighbors (children)
        for(String child: BFS.inNeighbors(center)){

            //if this neighbor isn't yet visited, add it to the visited list.
            if(!visited.contains(child)){
                visited.add(child);

                //if level isn't less than low, add it to the
                if(!(level< low)){
                    //if the set at this separation level in the map is null, create a new set at the key <level>
                    if(sepList.get(level)== null){
                        sepList.put(level, new HashSet<>());
                    }

                    //add this neighbor to the set at the key of this separation level
                    sepList.get(level).add(child);
                }

                //recursively call this method but change the level by one and make the new "center" the current inNeighbor (child)
                listBySeparation(sepList, level+1, child, visited, low, high);
            }

        }

    }


    public static void main(String[] args){

       KevinBaconGame game = new KevinBaconGame();
       game.startGame("Kevin Bacon");
    }
}
