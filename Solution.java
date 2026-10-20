package ub.cse.algo;

import java.util.ArrayList;
import java.util.HashMap;

import static ub.cse.algo.Traversals.bfsPaths;



public class Solution {

    private Info info;
    private Graph graph;
    private ArrayList<Client> clients;
    private ArrayList<Integer> bandwidths;


    /**
     * Basic Constructor
     *
     * @param info: data parsed from input file
     */
    public Solution(Info info) {
        this.info = info;
        this.graph = info.graph;
        this.clients = info.clients;
        this.bandwidths = info.bandwidths;
    }

    /**
     * Method that returns the calculated 
     * SolutionObject as found by your algorithm
     *
     * @return SolutionObject containing the paths, priorities and bandwidths
     */
    public SolutionObject outputPaths() {
        SolutionObject sol = new SolutionObject();
        //bandwidths unchanged
        sol.bandwidths = bandwidths;
        //prio unchanged, set to default
        for (Client c : clients) {
            sol.priorities.put(c.id, 1);
        }
        //base paths
        sol.paths = bfsPaths(graph, clients);

        //min distance per client path
        HashMap<Integer, Integer> dist = Traversals.bfs(graph, clients);

        //calculate the deadline for each client as product of path distance * alpha (aka the tolerance)
        //note that deadline is where penalties occur if deadline has passed before packet arrives
        HashMap<Integer, Integer> deadline = new HashMap<>();
        for (Client c : clients) {
            int d = dist.get(c.id);
            deadline.put(c.id, (int)(d * c.alpha)); //cast value to int which acts like floor function
        }

        return sol;
    }
}

