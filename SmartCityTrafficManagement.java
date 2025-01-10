import java.util.*;

public class SmartCityTrafficManagement {


    static class Graph {
        private final Map<String, List<Edge>> adjList = new HashMap<>();

        public void addIntersection(String name) {
            adjList.putIfAbsent(name, new ArrayList<>());
        }

        public void addRoad(String from, String to, int weight) {
            adjList.get(from).add(new Edge(to, weight));
        }

        public List<Edge> getEdges(String node) {
            return adjList.getOrDefault(node, new ArrayList<>());
        }

        static class Edge {
            String destination;
            int weight;

            public Edge(String destination, int weight) {
                this.destination = destination;
                this.weight = weight;
            }
        }
    }


    static class Vehicle {
        String id;
        String currentLocation;
        String destination;

        public Vehicle(String id, String currentLocation, String destination) {
            this.id = id;
            this.currentLocation = currentLocation;
            this.destination = destination;
        }
    }
    static class TrafficLight {
        enum State { GREEN, YELLOW, RED }

        private State state;
        private int timer;

        public TrafficLight() {
            this.state = State.RED;
            this.timer = 0;
        }

        public void updateState() {
            timer++;
            if (state == State.RED && timer >= 10) {
                state = State.GREEN;
                timer = 0;
            } else if (state == State.GREEN && timer >= 10) {
                state = State.YELLOW;
                timer = 0;
            } else if (state == State.YELLOW && timer >= 5) {
                state = State.RED;
                timer = 0;
            }
        }

        public State getState() {
            return state;
        }
    }


    static class CongestionQueue {
        private final PriorityQueue<IntersectionCongestion> queue;

        public CongestionQueue() {
            this.queue = new PriorityQueue<>(Comparator.comparingInt(c -> -c.congestionLevel));
        }

        public void addOrUpdate(String intersection, int congestionLevel) {
            queue.add(new IntersectionCongestion(intersection, congestionLevel));
        }

        public IntersectionCongestion poll() {
            return queue.poll();
        }

        static class IntersectionCongestion {
            String intersection;
            int congestionLevel;

            public IntersectionCongestion(String intersection, int congestionLevel) {
                this.intersection = intersection;
                this.congestionLevel = congestionLevel;
            }
        }
    }


    public static List<String> calculateRoute(Graph graph, String start, String destination) {
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<Map.Entry<String, Integer>> pq = new PriorityQueue<>(Comparator.comparingInt(Map.Entry::getValue));

        for (String node : graph.adjList.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
        }
        distances.put(start, 0);
        pq.add(new AbstractMap.SimpleEntry<>(start, 0));

        while (!pq.isEmpty()) {
            String current = pq.poll().getKey();

            if (current.equals(destination)) break;

            for (Graph.Edge edge : graph.getEdges(current)) {
                int newDist = distances.get(current) + edge.weight;
                if (newDist < distances.get(edge.destination)) {
                    distances.put(edge.destination, newDist);
                    previous.put(edge.destination, current);
                    pq.add(new AbstractMap.SimpleEntry<>(edge.destination, newDist));
                }
            }
        }

        List<String> path = new ArrayList<>();
        for (String at = destination; at != null; at = previous.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }

    public static void main(String[] args) {

        Graph graph = new Graph();
        graph.addIntersection("A");
        graph.addIntersection("B");
        graph.addIntersection("C");
        graph.addRoad("A", "B", 5);
        graph.addRoad("B", "C", 10);
        graph.addRoad("A", "C", 20);

        TrafficLight trafficLight = new TrafficLight();

        Vehicle vehicle = new Vehicle("V1", "A", "C");


        List<String> route = calculateRoute(graph, vehicle.currentLocation, vehicle.destination);
        System.out.println("Optimal Route: " + route);


        for (int i = 0; i < 30; i++) {
            trafficLight.updateState();
            System.out.println("Time: " + i + " - Traffic Light State: " + trafficLight.getState());
        }
    }
}
