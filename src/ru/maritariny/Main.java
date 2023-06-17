package ru.maritariny;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String[] parts = reader.readLine().split(" ");
        int n = Integer.parseInt(parts[0]); // nodes
        int m = Integer.parseInt(parts[1]); // edges

        if (n * (n-1) / 2 == m) {
            System.out.println(1);
            System.out.println(1);
            StringBuilder sb = new StringBuilder();
            for (int i = 2; i <= n; i++) {
                if (!sb.isEmpty()) {
                    sb.append(" ");
                }
                sb.append(i);
            }
            System.out.println(sb);
            return;
        }

        Map<Integer, Set<Integer>> graph = new HashMap<>(n);
        Set<Integer> allNodes = new HashSet<>(n);
        for (Integer i = 1; i <= n; i++) {
            allNodes.add(i);
            graph.put(i, new HashSet<>());
        }
        for (int i = 0; i < m; i++) {
            parts = reader.readLine().split(" ");
            Integer node1 = Integer.parseInt(parts[0]);
            Integer node2 = Integer.parseInt(parts[1]);
            graph.get(node1).add(node2);
            graph.get(node2).add(node1);
        }
        //LocalDateTime from = LocalDateTime.now();
        if ((m == 0 && n != 2) || !solve(graph, allNodes)) {
            System.out.println(-1);
        }
        reader.close();
        //LocalDateTime to = LocalDateTime.now();
       // System.out.println(Duration.between(from, to).toMillis());
    }

    private static boolean solve(Map<Integer, Set<Integer>> graph, Set<Integer> allNodes) {
        Stack<Integer> nodeStack = new Stack<>();
        List<HashSet<Integer>> teams = new ArrayList();
        while (!allNodes.isEmpty()) {
            Set<Integer> tempTeam = new HashSet<>();
            Set<Integer> team = new HashSet<>();
            Iterator<Integer> it = allNodes.iterator();
            Integer startNode = it.next();
            Set<Integer> brak = new HashSet<>();
            Set<Integer> commonVertex = new HashSet<>();

            nodeStack.push(startNode);
            allNodes.remove(startNode);
            boolean first = true;
            Set<Integer> currentEdgeList = graph.get(nodeStack.peek());
            while (!nodeStack.empty()) {
                boolean end = false;
                boolean badVertex = false;
//                Set<Integer> currentEdgeList = graph.get(nodeStack.peek());
                if (first) {
                    commonVertex = new HashSet<>(currentEdgeList);
                    commonVertex.retainAll(allNodes); // удалить уже использованные ранее узлы
                    first = false;
                }
                tempTeam.add(nodeStack.peek());
                if (currentEdgeList.size() > 0) {
                    end = true;
                    boolean hasItem = false;
                    Iterator<Integer> iterator = currentEdgeList.iterator();
                    Integer goodVertex = null;
                    while (iterator.hasNext()) {
                        Integer next = iterator.next();
                        if (allNodes.contains(next)) {
                            // есть доступ к уже добавленным вершинам
                            if (!contains(graph.get(next), tempTeam)) {
                                continue;
                            }
                            // вершина не рассматривалась ранее в этом цикле как плохая
                            if (brak.contains(next)) {
                                continue;
                            }
                            if (!commonVertex.contains(next)) {
                                continue;
                            }
                            nodeStack.push(next);
                            allNodes.remove(next);
                            goodVertex = next;
                            hasItem = true;
                            break;
                        }
                    }
                    // формирование списка общих вершин
                    if (hasItem) {
                        end = false;
                        currentEdgeList = graph.get(goodVertex);
                        commonVertex.remove(goodVertex);
                        if (!commonVertex.isEmpty()) {
                            Set<Integer> copy = new HashSet<>(commonVertex);
                            copy.retainAll(currentEdgeList);
                            if (copy.isEmpty()) {
                                // у вершины нет других общих вершин
                                badVertex = true;
                                end = true;
                            } else {
                                // обновление списка общих вершин (оставить только пересечения)
                                commonVertex.retainAll(currentEdgeList);
                            }
                        }
                    }
                } else {
                    end = true;
                }
                if (end) {
                    if (badVertex) {
                        brak.add(nodeStack.peek());
                        allNodes.add(nodeStack.peek());
                        tempTeam.remove(nodeStack.pop());
                        currentEdgeList = graph.get(nodeStack.peek());
                    } else {
                        team.add(nodeStack.pop());
                    }
                }
            }
            teams.add((HashSet<Integer>) team);
        }
        if (teams.size() != 2) {
            return false;
        }
        System.out.println(teams.get(0).size());
        for (Set<Integer> t : teams) {
            StringBuilder sb = new StringBuilder();
            for (Integer i : t) {
                if (!sb.isEmpty()) {
                    sb.append(" ");
                }
                sb.append(i);
            }
            System.out.println(sb.toString());
        }
        return true;
    }

    private static boolean contains(Set<Integer> where, Set<Integer> what){
        Iterator<Integer> iterator = what.iterator();
        while (iterator.hasNext()) {
            if (!where.contains(iterator.next())) return false;
        }
        return true;
    }
}
