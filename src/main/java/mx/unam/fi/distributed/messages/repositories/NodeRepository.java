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

/**
 * Repositorio para almacenar y administrar el estado actual de los
 * nodos en el sistema distribuido.
 */
@Repository
public class NodeRepository {

    private static final Lock lock = new ReentrantLock();

    // Mapa de nodos -> Clave:ID del nodo | Valor: Info de la instancia del nodo
    private final Map<Integer, Node> _hosts = Map.of(
        1, new Node("node_1", "172.16.114.128", 1, 5000),
        2, new Node("node_2", "172.16.114.129", 2, 5000),
        3, new Node("node_3", "172.16.114.130", 3, 5000)
        //4, new Node("node_4", "172.16.114.131", 4, 5000)
    );

    private final Map<Integer, Node> hosts = new HashMap<>(_hosts);

    @Value("${app.server.node_n}")
    private int node_n;

    // Agregar nodo
    public void addNode(int id) {
        hosts.put(id, _hosts.get(id));
    }

    // Elimina nodo
    public void removeNode(int id) {
        hosts.remove(id);
    }

    // Obtener nodo por ID
    public Node getNode(int id) {
        return hosts.get(id);
    }

    // Obtener siguiente nodo disponible por ID
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

    // Obtiene el ID del siguiente nodo
    private int getNextId(int id) {
        if (id < 3)
            return id + 1;
        return 1;
    }

    // Lista todos los nodos en el repositorio
    public List<Node> getNodes() {
        return new ArrayList<>(hosts.values());
    }

    //Lista todos los ID de los nodos
    public List<Integer> getNodesId() {
        return this.hosts.keySet().stream().toList();
    }

    // Verifica si el repositorio contiene un nodo con un ID dado
    public boolean containsNode(int id) {
        return hosts.values().stream().anyMatch(n -> n.id() == id);
    }

    // Obtiene una lista de todos los nodos excepto el nodo actual
    public List<Node> getOtherNodes() {
        return getNodes().stream().filter(i -> i.id() != node_n).toList();
    }
}
