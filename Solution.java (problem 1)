//SOLUTION FOR PROBLEM 1


package ub.cse.algo;

import java.util.ArrayList;

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

        sol.bandwidths = bandwidths;
        sol.paths = bfsPaths(graph, clients);

        for (int i = 0; i < clients.size(); i++) {
            int id = clients.get(i).id;
            sol.priorities.put(id, 1);
        }

        return sol;
    }
}
