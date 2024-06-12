import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Map;

//Kostikova Polina ISE-02 :)
public class assig2 {
    static boolean[] visited;

    public static void main(String[] args) throws FileNotFoundException {
        System.setIn(new FileInputStream("input.txt"));
        String answer = "";
        Map<String, Integer> statesmap = new HashMap<String, Integer>();
        Scanner in = new Scanner(System.in);
        String readtype = in.next();
        String type = readtype.substring(6, readtype.length() - 1);
        String readstates = in.next();
        if (readstates.split("=")[1].equals("[]") | !(readstates.split("=")[0].equals("states"))) {
            System.out.println("E1: Input file is malformed");
            System.exit(0);
        }
        // read states and save it in Map
        String[] states2 = readstates.substring(8, readstates.length() - 1).split(",");
        List<String> list = new ArrayList<>();
        for (int i = 0; i < states2.length; i++) {
            if (!list.contains(states2[i])) {
                list.add(states2[i]);
            }
        }
        String[] states = new String[list.size()];
        list.toArray(states);
        for (int i = 0; i < states.length; i++) {
            statesmap.put(states2[i], i);

        }
        // initialize matrices
        String[][] start = new String[states.length][states.length];

        String[][] matrix = new String[states.length][states.length];

        String readalphabet = in.next();
        if (readalphabet.contains(",]")) {
            System.out.println("E1: Input file is malformed");
            System.exit(0);
        }
        if (readalphabet.split("=")[1].equals("[]") | !(readalphabet.split("=")[0].equals("alphabet"))) {
            System.out.println("E1: Input file is malformed");
            System.exit(0);
        }
        String[] alphabet = readalphabet.substring(10, readalphabet.length() - 1).split(",");

        // read initial state
        String readinitial = in.next();
        if (readinitial.split("=")[1].equals("[]")) {
            System.out.println("E2: Initial state is not defined");
            System.exit(0);
        }
        String initial = readinitial.substring(9, readinitial.length() - 1);
        if (!(statesmap.containsKey(initial))) {
            System.out.println("E4: A state '" + initial + "' is not in the set of states");
            System.exit(0);
        }

        // read accepting
        String readaccepting = in.next();
        if (!(readaccepting.split("=")[0].equals("accepting"))) {
            System.out.println("E1: Input file is malformed");
            System.exit(0);
        }

        if (readaccepting.split("=")[1].equals("[]")) {
            System.out.println("E3: Set of accepting states is empty");
            System.exit(0);
        }
        String[] accepting1 = readaccepting.substring(11, readaccepting.length() - 1).split(",");

        HashSet<String> besduplicates = new HashSet<String>();
        for (int i = 0; i < accepting1.length; i++) {
            besduplicates.add(accepting1[i]);
            if (!statesmap.containsKey(accepting1[i])) {
                System.out.println("E4: A state '" + accepting1[i] + "' is not in the set of states");
                System.exit(0);
            }
        }
        String[] accepting = besduplicates.toArray(new String[besduplicates.size()]);

        Arrays.sort(accepting);
        // read transitions
        String readtransitions = in.next();
        if (!(readtransitions.split("=")[0].equals("transitions"))) {
            System.out.println("E1: Input file is malformed");
            System.exit(0);
        }

        String[] transitions = readtransitions.substring(13, readtransitions.length() - 1).split(",");
        // check for connectedness

        for (int i = 0; i < transitions.length; i++) {
            if (transitions[i].split(">")[1].equals("")) {
                System.out.println("E1: Input file is malformed");
                System.exit(0);
            }
            if (!(Arrays.stream(states).anyMatch(transitions[i].split(">")[0]::equals))) {
                System.out.println(
                        "E4: A state '" + transitions[i].split(">")[0] + "' is not in the set of states");
                System.exit(0);
            }

            else if (!(Arrays.stream(alphabet).anyMatch(transitions[i].split(">")[1]::equals))) {
                System.out.println(
                        "E5: A transition '" + transitions[i].split(">")[1] + "' is not represented in the alphabet");
                System.exit(0);
            } else if (!(Arrays.stream(states).anyMatch(transitions[i].split(">")[2]::equals))) {
                System.out.println(
                        "E4: A state '" + transitions[i].split(">")[2] + "' is not in the set of states");
                System.exit(0);
            }
            if (start[statesmap.get(transitions[i].split(">")[0])][statesmap
                    .get(transitions[i].split(">")[2])] == null) {
                start[statesmap.get(transitions[i].split(">")[0])][statesmap
                        .get(transitions[i].split(">")[2])] = transitions[i].split(">")[1];
            } else {
                start[statesmap.get(transitions[i].split(">")[0])][statesmap
                        .get(transitions[i].split(">")[2])] += "|" + transitions[i].split(">")[1];
            }
        }

        List<String> fsa1 = new ArrayList<>();
        for (int i = 0; i < transitions.length; i++) {
            if (fsa1.contains(transitions[i])) {
                System.out.println("E1: Input file is malformed");
                System.exit(0);
            } else {
                fsa1.add(transitions[i]);
            }
        }

        dfs(states, transitions);
        // check for non-deterministic
        if (type.equals("deterministic")) {
            List<String> fsa = new ArrayList<>();
            for (int i = 0; i < transitions.length; i++) {
                if (fsa.contains(transitions[i].split(">")[0] + transitions[i].split(">")[1])) {
                    System.out.println("E7: FSA is non-deterministic");
                    System.exit(0);
                } else {
                    fsa.add(transitions[i].split(">")[0] + transitions[i].split(">")[1]);

                }
            }
        }
        for (int j = 0; j < states.length; j++) {
            if (start[j][j] == null) {
                start[j][j] = "eps";
            } else {
                start[j][j] += "|eps";
            }
            for (int k = 0; k < states.length; k++) {
                if (j != k && start[j][k] == null) {
                    start[j][k] = "{}";
                }
            }
        }

        // kleene's algorithm
        for (int k = 0; k < states.length; k++) {
            for (int i = 0; i < states.length; i++) {
                for (int j = 0; j < states.length; j++) {
                    matrix[i][j] = "(" + start[i][k] + ")(" +
                            start[k][k] + ")*(" + start[k][j] + ")|(" + start[i][j] + ")";
                }
            }
            start = Arrays.stream(matrix).map(String[]::clone).toArray(String[][]::new);
        }
        int initialindex = statesmap.get(initial);

        for (int i = 0; i < accepting.length; i++) {
            int accept = statesmap.get(accepting[i]);
            if (answer.equals("")) {
                answer = "(" + matrix[initialindex][accept] + ")";
            } else {
                answer += "|(" + matrix[initialindex][accept] + ")";
            }
        }

        if (answer.equals("")) {
            answer = "{}";
        }
        System.out.println(answer);

    }

    public static void dfs(String[] states, String[] transitions) {
        visited = new boolean[states.length];
        visited[0] = true;
        dfsvisit(states[0], states, transitions);
        for (int i = 0; i < states.length; i++) {
            if (!visited[i]) {
                System.out.println("E6: Some states are disjoint");
                System.exit(0);
            }
        }
    }

    public static void dfsvisit(String state, String[] states, String[] transitions) {
        for (int i = 0; i < transitions.length; i++) {
            if (transitions[i].split(">")[0].equals(state)) {
                String toState = transitions[i].split(">")[2];
                for (int j = 0; j < states.length; j++) {
                    if (states[j].equals(toState) && !visited[j]) {
                        visited[j] = true;
                        dfsvisit(states[j], states, transitions);
                    }

                }
            }
        }
    }
}