package utils;

import collection.FirstCollection;
import collection.FollowCollection;

import java.io.IOException;
import java.util.*;

// 预测分析表
public class AnalyzeTable {
    private final JudgeIsLL1 judgeIsLL1;
    private final FollowCollection followCollection;
    private final FirstCollection firstCollection;
    private final EliminateLeftRecursion eliminateLeftRecursion;
    private Map<String, List<String>> formulaSplit;
    //非终结符
    private Set<String> nonTerminalSet;
    //终结符
    private Set<String> terminalSet;
    public AnalyzeTable(String fileSrc) throws IOException {
        this.judgeIsLL1 = new JudgeIsLL1(fileSrc);
        this.followCollection = judgeIsLL1.getFollowCollection();
        this.firstCollection = followCollection.getFirstCollection();
        this.eliminateLeftRecursion = firstCollection.getEliminateLeftRecursion();
    }

    public JudgeIsLL1 getJudgeIsLL1() {
        return judgeIsLL1;
    }

    public void init() {
        // 判断是否是 LL1 文法
        if (judgeIsLL1.judgeIsLL1()) {
            nonTerminalSet = new HashSet<>();
            terminalSet = new HashSet<>();
            Map<String, Set<String>> map = firstCollection.getFirstCollectionMap();
            for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
                nonTerminalSet.add(entry.getKey());
            }
            Map<String, String> formula;
            // 是否进行了消除左递归
            if (eliminateLeftRecursion.isHappenLeftRecursion()) {
                formula = eliminateLeftRecursion.getNewFormula();
            } else {
                formula = eliminateLeftRecursion.getOldFormula();
            }
            for (Map.Entry<String, String> entry : formula.entrySet()) {
                String string = entry.getValue();
                for (int i = 0; i < string.length(); i++) {
                    String character = "" + string.charAt(i);
                    if (!nonTerminalSet.contains(character) && !character.equals("|") && !character.equals("'")) {
                        if (!character.equals("ε")) {
                            terminalSet.add(character);
                        }
                    }
                }
            }
            terminalSet.add("#");
            System.out.println("非终结符：" + nonTerminalSet);
            System.out.println("终结符：" + terminalSet);
        }
    }

    public void createTable() {
        List<String> nonTerminalList = new ArrayList<>(nonTerminalSet);
        List<String> terminalList = new ArrayList<>(terminalSet);
        String[][] table = new String[nonTerminalList.size()][terminalList.size()];
        for (int i = 0; i < nonTerminalList.size(); i++) {
            for (int j = 0; j < terminalList.size(); j++) {
                table[i][j] = findFormula(nonTerminalList.get(i), terminalList.get(j));
            }
        }

        System.out.printf("%-5s", "");
        for (String s : terminalList) {
            System.out.printf("%-13s", s);
        }
        System.out.println();
        for (int i = 0; i < nonTerminalList.size(); i++) {
            System.out.printf("%-5s", nonTerminalList.get(i));
            for (int j = 0; j < terminalList.size(); j++) {
                System.out.printf("%-13s", table[i][j]);
            }
            System.out.println();
        }
    }

    public void splitFormula() {
        Map<String, String> formula;
        formulaSplit = new HashMap<>();
        if (eliminateLeftRecursion.isHappenLeftRecursion()) {
            formula = eliminateLeftRecursion.getNewFormula();
        } else {
            formula = eliminateLeftRecursion.getOldFormula();
        }
        for (Map.Entry<String, String> entry : formula.entrySet()) {
            String[] strings = entry.getValue().split("\\|");
            List<String> list = new ArrayList<>(Arrays.asList(strings));
            formulaSplit.put(entry.getKey(), list);
        }
    }

    public String findFormula(String nonTerminal, String terminal) {
        List<String> strings = formulaSplit.get(nonTerminal);
        Set<String> follow = followCollection.getFollowCollectionMap().get(nonTerminal);
        Set<String> first;
        for (String string : strings) {
            first = searchFirstFromFormula(string);
            if (first.contains(terminal)) {
                return nonTerminal + "——>" + string;
            }
            if (first.contains("ε") || string.equals("ε")) {
                if (follow.contains(terminal)) {
                    return nonTerminal + "——>ε";
                }
            }
        }
        return "/";
    }

    public Set<String> searchFirstFromFormula(String formula) {
        Map<String, Set<String>> firstCollectionMap = firstCollection.getFirstCollectionMap();
        Set<String> set = new HashSet<>();
        String character = "" + formula.charAt(0);
        if (firstCollectionMap.containsKey(character)) {
            set = firstCollectionMap.get(character);
        } else {
            set.add(character);
        }
        return set;
    }
}
