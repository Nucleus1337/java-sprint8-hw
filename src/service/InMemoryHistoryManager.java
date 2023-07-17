package service;

import model.Task;
import util.Node;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList customHistory = new CustomLinkedList();
    private final Map<Long, Node> taskIdToNode = new HashMap<>();

    @Override
    public void add(Task task) {
        long taskId = task.getId();

        if (taskIdToNode.containsKey(taskId)) {
            customHistory.removeNode(taskIdToNode.get(taskId));
        }

        Node node = customHistory.linkLast(task);
        taskIdToNode.put(taskId, node);
    }

    @Override
    public List<Task> getHistory() {
        return customHistory.getTasks();
    }

    @Override
    public void remove(long taskId) {
        Node node = taskIdToNode.get(taskId);

        if (node == null) return;

        customHistory.removeNode(node);

        taskIdToNode.remove(taskId);
    }

    private static class CustomLinkedList {
        Node head;
        Node tail;

        private int size = 0;

        public Node linkLast(Task task) {
            final Node oldTail = tail;
            final Node newNode = new Node(oldTail, task, null);

            tail = newNode;

            if (oldTail == null) {
                head = newNode;
            } else {
                oldTail.setNext(newNode);
            }

            size++;

            return newNode;
        }

        public int size() {
            return this.size;
        }

        public List<Task> getTasks() {
            List<Task> tasks = new ArrayList<>();
            Node node = head;

            for (int i = 0; i < size; i++) {
                tasks.add(node.getData());
                node = node.getNext();
            }

            return tasks;
        }

        public void removeNode(Node node) {
            Node prevNode = node.getPrev();
            Node nextNode = node.getNext();

            if (prevNode != null) {
                prevNode.setNext(nextNode);
            } else {
                head = nextNode;
            }

            if (nextNode != null) {
                nextNode.setPrev(prevNode);
            } else {
                tail = prevNode;
            }

            size--;
        }
    }
}
