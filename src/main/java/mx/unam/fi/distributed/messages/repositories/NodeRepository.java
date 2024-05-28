package mx.unam.fi.distributed.messages.repositories;

import mx.unam.fi.distributed.messages.node.Node;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Repository
public class NodeRepository {

    private static final Lock lock = new ReentrantLock();

    private final Map<Integer, Node> _hosts = Map.of(
        1, new Node("node_1", "172.16.114.128", 1, 5000),
        2, new Node("node_2", "172.16.114.129", 2, 5000),
        3, new Node("node_3", "172.16.114.130", 3, 5000)
        //4, new Node("node_4", "172.16.114.131", 4, 5000)
    );

    private final Map<Integer, Node> hosts = new HashMap<>(_hosts);

    @Value("${app.server.node_n}")
    private int node_n;

    public void addNode(int id) {
        hosts.put(id, _hosts.get(id));
    }

    public void removeNode(int id) {
        hosts.remove(id);
    }

    public Node getNode(int id) {
        return hosts.get(id);
    }

    public Node getNextNode(int id) {
        int currNode = id;
        for (int i = 0; i < 3; i++) {
            currNode = getNextId(currNode);
            if (hosts.containsKey(currNode)) {
                return hosts.get(currNode);
            }
        }
        return hosts.get(id);
    }

    private int getNextId(int id) {
        if (id < 3)
            return id + 1;
        return 1;
    }

    public List<Node> getNodes() {
        return new ArrayList<>(hosts.values());
    }

    public List<Integer> getNodesId() {
        return this.hosts.keySet().stream().toList();
    }

    public boolean containsNode(int id) {
        return hosts.values().stream().anyMatch(n -> n.id() == id);
    }

    public List<Node> getOtherNodes() {
        return getNodes().stream().filter(i -> i.id() != node_n).toList();
    }
}
