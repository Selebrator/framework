package uniolunisaar.adam.ds.circuits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import uniol.apt.util.Pair;

/**
 *
 * @author Manuel Gieseking
 */
public class AigerFileOptimizedGates extends AigerFile {

    private final Map<String, Pair<Gate, Integer>> andGates = new HashMap<>();
    private boolean withOpt = true; // currently seems to do not have any real influence on the toyexamples of the test suite
    private int nb_gates = -1;
    private final Map<Integer, Integer> replacements = new HashMap<>();

    public AigerFileOptimizedGates(boolean withOpt) {
        this.withOpt = withOpt;
    }

    private List<IntGate> getIntGates() {
        List<IntGate> gates = new ArrayList<>();
        for (Map.Entry<String, Pair<Gate, Integer>> entry : andGates.entrySet()) {
            Pair<Gate, Integer> value = entry.getValue();
            gates.add(new IntGate(value.getSecond(), getIndex(value.getFirst().getIn1()), getIndex(value.getFirst().getIn2())));
        }
        return gates;
    }

    @Override
    List<IntGate> getGates() {
        // gates
        // OLD VERSION: directly output the set of gates
//        StringBuilder gates = new StringBuilder();
//        for (Map.Entry<String, Pair<Gate, Integer>> entry : andGates.entrySet()) {
//            Pair<Gate, Integer> value = entry.getValue();
//            gates.append(value.getSecond()).append(" ").append(getIndex(value.getFirst().getIn1())).append(" ").append(getIndex(value.getFirst().getIn2())).append("\n");
//        }      
        // NEW Version first delete some unneccessary gates
        List<IntGate> gates = getIntGates();
        if (withOpt) {
            gates = optimizeGates(gates);
        }

        return gates;
    }

    @Override
    int getIndex(String id) {
        int index = super.getIndex(id);
        if (replacements.containsKey(index)) {
            return replacements.get(index);
        }
        return index;
    }

    @Override
    int getGateIndex(String identifier) {
        if (andGates.containsKey(identifier)) {
            return andGates.get(identifier).getSecond();
        }
        return -1;
    }

    /**
     * Currently, we only replace gates which have twice the same input.
     *
     * The problem is that we reduce the number of gates, i.e., lines we write
     * into the file, but do not reduce the number of indices we use.
     *
     * @param gates
     * @return
     */
    private List<IntGate> optimizeGates(List<IntGate> gates) {
        List<Pair<Integer, IntGate>> toRemove = new ArrayList<>();
        do { // if there is still s.th. to remove repeat
            // safely (i.e. replace all the output of the gate using indizes with the replacement) delete all gates
            for (Pair<Integer, IntGate> pair : toRemove) {
                gates.remove(pair.getSecond());
                int out = pair.getSecond().out;
                int replace = pair.getFirst();
                // check the gates
                for (IntGate gate : gates) {
                    // positive
                    if (gate.in1 == out) {
                        gate.in1 = replace;
                    } else if (gate.in1 == out + 1) { // negation
                        gate.in1 = (replace % 2 == 0) ? replace + 1 : replace - 1;
                    }
                    if (gate.in2 == out) {
                        gate.in2 = replace;
                    } else if (gate.in2 == out + 1) { // negation
                        gate.in2 = (replace % 2 == 0) ? replace + 1 : replace - 1;
                    }
                }
                // update the ids for the output and latches
                Map<Integer, Integer> puts = new HashMap<>();
                for (Map.Entry<Integer, Integer> entry : replacements.entrySet()) {
                    Integer key = entry.getKey();
                    Integer value = entry.getValue();
                    if (value == out) {
                        puts.put(key, replace);
                    } else if (value == out + 1) {
                        puts.put(key, (replace % 2 == 0) ? replace + 1 : replace - 1);
                    }
                }
                puts.put(out, replace);
                puts.put(out + 1, (replace % 2 == 0) ? replace + 1 : replace - 1);
                replacements.putAll(puts);
            }
            toRemove.clear();
            for (int i = 0; i < gates.size(); i++) {
                IntGate gate = gates.get(i);
                if (gate.in1 == gate.in2) {
                    // find gates with the same inputs
                    toRemove.add(new Pair<>(gate.in1, gate));
                } else if (gate.in1 % 2 == 0 && gate.in2 == gate.in1 + 1
                        || gate.in2 % 2 == 0 && gate.in1 == gate.in2 + 1) {
                    // find gates where one input is the negation of the other, i.e.,  
                    // gates with in2 = !in1, ergo replace with false
                    toRemove.add(new Pair<>(0, gate));
                } else if (gate.in1 == 0 || gate.in2 == 0) {
                    // find gates where one input is zero 
                    toRemove.add(new Pair<>(0, gate));
                } else {
                    // find gates which are commutativ or equal to another
                    for (int j = i + 1; j < gates.size(); j++) {
                        IntGate gate1 = gates.get(j);
                        if (((gate1.in1 == gate.in1 && gate1.in2 == gate.in2) // check equal
                                || (gate1.in1 == gate.in2 && gate1.in2 == gate.in1))) // commutative
                        {
//                            toRemove.add(new Pair<>(gate.out, gat e1)); // the higher ids would be preserved
                            toRemove.add(new Pair<>(gate1.out, gate));
                        }
                    }
                }
            }
        } while (!toRemove.isEmpty());
        nb_gates = gates.size();
        return gates;
    }

    @Override
    public int getNbOfGates() {
        if (nb_gates == -1) {
            return andGates.entrySet().size();
        }
        return nb_gates;
    }

    @Override
    int getMaxVarIdx(List<IntGate> gates) {
        if (withOpt) {
            // cannot do return inputs.size() + latches.size() + getNbOfGates();
            // since we don't squash the indexes
            int max = 0;
            for (Integer value : getInputs().values()) {
                if (value > max) {
                    max = value;
                }
            }
            for (IntGate gate : gates) {
                if (gate.out > max) {
                    max = gate.out;
                }
            }
            return max / 2;
        }
        return super.getMaxVarIdx(gates);
    }

    @Override
    void putGate(String out, String in1, String in2) {
        andGates.put(out, new Pair<>(new Gate(out, in1, in2), idx));
        idx += 2;
    }

    public void setWithOpt(boolean withOpt) {
        this.withOpt = withOpt;
    }

}
