package ub.cse.algo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.PriorityQueue;

import static ub.cse.algo.Traversals.bfsPaths;


public class Solution {

    private Info info;
    private Graph graph;
    private ArrayList<Client> clients;
    private ArrayList<Integer> bandwidths;

    //usage map: key = current node id, value is arraylist where idx = time tick and value at idx is
    //  how many packets are present at each tick
    private HashMap<Integer, ArrayList<Integer>> usage = new HashMap<>();

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
        //base paths to compare later
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

        for (Client c : clients) {
            ArrayList<Integer> path = new ArrayList<>();
            path = routing(c.id, deadline.get(c.id));
            if (path == null)
                path = sol.paths.get(c.id);
            reserve(path);
            sol.paths.put(c.id, path);
        }

        //need to check if bandwidth is exceeded at any node at any tick such that a delay may exist, so track packets around and get totals at each tick
            //then check if there are nodes we can reroute, and reroute them
        return sol;
    }

    //find the first tick that a packet is free to move at u -- reads from usage
    private int freeTick(int u, int tick) {
        int cap = bandwidths.get(u);
        ArrayList<Integer> usedList = usage.get(u);
        if (usedList == null) {
            return tick;
        }
        while(tick < usedList.size() && usedList.get(tick) >= cap) {
            tick++;
        }

        return tick;
    }

    //modified dijkstra to find route based on arrival time tick at target destination
    private ArrayList<Integer> routing(int target /*client*/, int deadline) {
        int src = graph.contentProvider;
        int size = graph.size();

        int parent[] = new int[size];   //track prev of each node
        int time[] = new int[size]; //track time tick of each packet's arrival at respective client
        Arrays.fill(parent, -1);
        Arrays.fill(time, Integer.MAX_VALUE);

        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
        pq.add(new int[]{0, src});

        while (!pq.isEmpty()) {
            int[] front = pq.poll();
            int tick = front[0];
            int u = front[1];

            if (u == target) break;
            if (tick > time[u]) continue;

            //get the first free tick of the current node that will let the packet through
            int free = freeTick(u, tick);
            if (free == Integer.MAX_VALUE) continue; //no bandwidth
            int next = free + 1;
            if (free > deadline) continue; //no payment

            for (int w : graph.get(u)) {    //get next neighbor set up and added to pq
                if (w == src) continue;
                if (next < time[w]) {
                    time[w] = next;
                    parent[w] = u;
                    pq.add(new int[]{next, w});
                }
            }
        }

        if (time[target] > deadline) return null;   //missed deadline at target client

        ArrayList<Integer> path = new ArrayList<>();
        for (int c = target; c != -1; c = parent[c])
            path.add(0, c); //prepend c
        return path;
    }

    private int reserve(ArrayList<Integer> path) {  //update usage to help routing
        int curr = 0;

        for (int i = 0; i < path.size(); i++) {
            int u = path.get(i);
            int tick = freeTick(u, curr);
            ArrayList<Integer> use = usage.get(u);  //read from usage

            if (use == null) {
                use = new ArrayList<>();
                usage.put(u, use);  //update if null at current node
            }

            while (use.size() <= tick)
                use.add(0);

            use.set(tick, use.get(tick) +1);    //set new value
            curr = tick + 1;

        }

        return curr;
    }

}

