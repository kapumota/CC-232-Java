/*
 * CC-232 - Semana 6, demostración: A* para el rompecabezas de 8 piezas.
 *
 * Inspirado en el proyecto 8 Puzzle de Algorithms, Sedgewick y Wayne.
 * Esta versión fue reescrita para ser autocontenida y emplea un min-heap
 * propio en lugar de java.util.PriorityQueue o algs4.jar.
 *
 * Objetivo didáctico:
 * mostrar que BinaryHeap puede priorizar estados por
 * prioridad = movimientos realizados + distancia Manhattan.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class Semana06_8PuzzleHeapDemo {
    static final class Board {
        private final int n;
        private final int[] tiles;
        private final int zero;

        Board(int[][] blocks) {
            if (blocks == null || blocks.length == 0) {
                throw new IllegalArgumentException("el tablero no puede estar vacío");
            }
            n = blocks.length;
            tiles = new int[n * n];
            int zeroIndex = -1;
            boolean[] seen = new boolean[n * n];

            for (int row = 0; row < n; row++) {
                if (blocks[row] == null || blocks[row].length != n) {
                    throw new IllegalArgumentException("el tablero debe ser cuadrado");
                }
                for (int col = 0; col < n; col++) {
                    int value = blocks[row][col];
                    if (value < 0 || value >= n * n || seen[value]) {
                        throw new IllegalArgumentException("las piezas deben formar una permutación válida");
                    }
                    seen[value] = true;
                    int index = row * n + col;
                    tiles[index] = value;
                    if (value == 0) zeroIndex = index;
                }
            }
            zero = zeroIndex;
        }

        private Board(int n, int[] tiles, int zero) {
            this.n = n;
            this.tiles = tiles;
            this.zero = zero;
        }

        int dimension() {
            return n;
        }

        int hamming() {
            int count = 0;
            for (int i = 0; i < tiles.length; i++) {
                if (tiles[i] != 0 && tiles[i] != i + 1) count++;
            }
            return count;
        }

        int manhattan() {
            int distance = 0;
            for (int i = 0; i < tiles.length; i++) {
                int value = tiles[i];
                if (value == 0) continue;
                int goal = value - 1;
                distance += Math.abs(i / n - goal / n) + Math.abs(i % n - goal % n);
            }
            return distance;
        }

        boolean isGoal() {
            return manhattan() == 0;
        }

        List<Board> neighbors() {
            List<Board> result = new ArrayList<>(4);
            int row = zero / n;
            int col = zero % n;
            int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

            for (int[] direction : directions) {
                int newRow = row + direction[0];
                int newCol = col + direction[1];
                if (newRow < 0 || newRow >= n || newCol < 0 || newCol >= n) continue;
                int other = newRow * n + newCol;
                int[] copy = Arrays.copyOf(tiles, tiles.length);
                copy[zero] = copy[other];
                copy[other] = 0;
                result.add(new Board(n, copy, other));
            }
            return result;
        }

        boolean isSolvable() {
            int inversions = 0;
            for (int i = 0; i < tiles.length; i++) {
                if (tiles[i] == 0) continue;
                for (int j = i + 1; j < tiles.length; j++) {
                    if (tiles[j] != 0 && tiles[i] > tiles[j]) inversions++;
                }
            }
            if (n % 2 == 1) return inversions % 2 == 0;
            int blankRowFromBottom = n - zero / n;
            return (inversions + blankRowFromBottom) % 2 == 1;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof Board board)) return false;
            return n == board.n && Arrays.equals(tiles, board.tiles);
        }

        @Override
        public int hashCode() {
            return 31 * n + Arrays.hashCode(tiles);
        }

        @Override
        public String toString() {
            StringBuilder out = new StringBuilder();
            for (int row = 0; row < n; row++) {
                for (int col = 0; col < n; col++) {
                    out.append(String.format("%2d ", tiles[row * n + col]));
                }
                out.append('\n');
            }
            return out.toString();
        }
    }

    static final class MinHeap<T> {
        private Object[] a = new Object[1];
        private int n;
        private final Comparator<? super T> comparator;

        MinHeap(Comparator<? super T> comparator) {
            this.comparator = comparator;
        }

        boolean isEmpty() {
            return n == 0;
        }

        int size() {
            return n;
        }

        private int parent(int i) {
            return (i - 1) / 2;
        }

        private int left(int i) {
            return 2 * i + 1;
        }

        private int right(int i) {
            return 2 * i + 2;
        }

        @SuppressWarnings("unchecked")
        private T value(int i) {
            return (T) a[i];
        }

        private void swap(int i, int j) {
            Object value = a[i];
            a[i] = a[j];
            a[j] = value;
        }

        private void resize(int capacity) {
            a = Arrays.copyOf(a, Math.max(1, capacity));
        }

        private void bubbleUp(int i) {
            while (i > 0 && comparator.compare(value(i), value(parent(i))) < 0) {
                int p = parent(i);
                swap(i, p);
                i = p;
            }
        }

        private void trickleDown(int i) {
            while (left(i) < n) {
                int smallest = left(i);
                int right = right(i);
                if (right < n && comparator.compare(value(right), value(smallest)) < 0) {
                    smallest = right;
                }
                if (comparator.compare(value(i), value(smallest)) <= 0) break;
                swap(i, smallest);
                i = smallest;
            }
        }

        void add(T item) {
            if (n == a.length) resize(2 * a.length);
            a[n] = item;
            bubbleUp(n++);
        }

        T remove() {
            if (isEmpty()) throw new NoSuchElementException("el montículo está vacío");
            T minimum = value(0);
            a[0] = a[--n];
            a[n] = null;
            if (n > 0) trickleDown(0);
            if (n > 0 && n == a.length / 4) resize(a.length / 2);
            return minimum;
        }
    }

    static final class SearchNode {
        final Board board;
        final int moves;
        final SearchNode previous;
        final int priority;

        SearchNode(Board board, int moves, SearchNode previous) {
            this.board = board;
            this.moves = moves;
            this.previous = previous;
            priority = moves + board.manhattan();
        }
    }

    static final class Solver {
        private final boolean solvable;
        private final SearchNode goal;
        private int expandedNodes;

        Solver(Board initial) {
            if (initial == null) throw new IllegalArgumentException("el tablero inicial es obligatorio");
            solvable = initial.isSolvable();
            goal = solvable ? solve(initial) : null;
        }

        private SearchNode solve(Board initial) {
            Comparator<SearchNode> order = Comparator
                    .comparingInt((SearchNode node) -> node.priority)
                    .thenComparingInt(node -> node.board.manhattan());
            MinHeap<SearchNode> open = new MinHeap<>(order);
            Map<Board, Integer> bestMoves = new HashMap<>();

            open.add(new SearchNode(initial, 0, null));
            bestMoves.put(initial, 0);

            while (!open.isEmpty()) {
                SearchNode current = open.remove();
                Integer known = bestMoves.get(current.board);
                if (known != null && current.moves > known) continue;
                expandedNodes++;
                if (current.board.isGoal()) return current;

                for (Board neighbor : current.board.neighbors()) {
                    if (current.previous != null && neighbor.equals(current.previous.board)) continue;
                    int nextMoves = current.moves + 1;
                    Integer best = bestMoves.get(neighbor);
                    if (best == null || nextMoves < best) {
                        bestMoves.put(neighbor, nextMoves);
                        open.add(new SearchNode(neighbor, nextMoves, current));
                    }
                }
            }
            return null;
        }

        boolean isSolvable() {
            return solvable;
        }

        int moves() {
            return goal == null ? -1 : goal.moves;
        }

        int expandedNodes() {
            return expandedNodes;
        }

        List<Board> solution() {
            List<Board> path = new ArrayList<>();
            for (SearchNode node = goal; node != null; node = node.previous) {
                path.add(0, node.board);
            }
            return path;
        }
    }

    public static void main(String[] args) {
        int[][] blocks = {
                {1, 2, 3},
                {4, 0, 6},
                {7, 5, 8}
        };

        Board initial = new Board(blocks);
        System.out.println("Tablero inicial:");
        System.out.println(initial);
        System.out.println("Hamming: " + initial.hamming());
        System.out.println("Manhattan: " + initial.manhattan());

        Solver solver = new Solver(initial);
        if (!solver.isSolvable()) {
            System.out.println("El tablero no tiene solución.");
            return;
        }

        System.out.println("Movimientos mínimos: " + solver.moves());
        System.out.println("Nodos extraídos del montículo: " + solver.expandedNodes());
        int step = 0;
        for (Board board : solver.solution()) {
            System.out.println("Paso " + step++ + ":");
            System.out.println(board);
        }
    }
}
