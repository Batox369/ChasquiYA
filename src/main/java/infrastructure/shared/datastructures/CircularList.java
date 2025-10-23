package infrastructure.shared.datastructures;

import domain.model.Conductor;

import java.util.ArrayList;
import java.util.List;

public class CircularList {
    private Node tail;
    private int size = 0;

    private static class Node {
        Conductor data;
        Node next;
        Node(Conductor d) { data = d; }
    }

    public boolean isEmpty() { return size == 0; }
    public int size() { return size; }

    public void add(Conductor c) {
        Node node = new Node(c);
        if (tail == null) {
            tail = node;
            tail.next = tail;
        } else {
            node.next = tail.next;
            tail.next = node;
            tail = node;
        }
        size++;
    }

    public boolean removeByName(String nombre) {
        if (tail == null) return false;
        Node prev = tail;
        Node cur = tail.next;
        for (int i = 0; i < size; i++) {
            if (cur.data.getNombre().equalsIgnoreCase(nombre)) {
                if (size == 1) {
                    tail = null;
                } else {
                    prev.next = cur.next;
                    if (cur == tail) tail = prev;
                }
                size--;
                return true;
            }
            prev = cur;
            cur = cur.next;
        }
        return false;
    }

    public List<Conductor> toList() {
        List<Conductor> list = new ArrayList<>();
        if (tail == null) return list;
        Node cur = tail.next;
        for (int i = 0; i < size; i++) {
            list.add(cur.data);
            cur = cur.next;
        }
        return list;
    }

    @Override
    public String toString() {
        if (tail == null) return "(vacía)";
        StringBuilder sb = new StringBuilder();
        Node cur = tail.next;
        for (int i = 0; i < size; i++) {
            sb.append(cur.data.getNombre());
            if (i < size - 1) sb.append(" -> ");
            cur = cur.next;
        }
        sb.append(" -> (vuelve al inicio)");
        return sb.toString();
    }
}
