package mx.unam.fi.distributed.messages.repositories;

import mx.unam.fi.distributed.messages.node.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class NodeRepository {
    private static final Logger log = LoggerFactory.getLogger(NodeRepository.class);
    private final Map<Integer, Node> _hosts = Map.of(
        1, new Node("node_1", "10.5.0.5", 1, 5000),
        2, new Node("node_2", "10.5.0.6", 2, 5000),
        3, new Node("node_3", "10.5.0.7", 3, 5000)
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
}
