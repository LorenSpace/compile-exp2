package utils;

import collection.FirstCollection;
import collection.FollowCollection;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// 判断是不是LL1文法
public class JudgeIsLL1 {

    private final FollowCollection followCollection;
    private final FirstCollection firstCollection;

    public JudgeIsLL1(String fileSrc) throws IOException {
        this.followCollection = new FollowCollection(fileSrc);
        this.firstCollection = followCollection.getFirstCollection();
        //是否是LL1文法
        boolean isLL1 = judgeIsLL1();
        System.out.println();
        if (isLL1) {
            System.out.println("该文法是LL1文法");
        } else {
            System.out.println("该文法不是LL1文法");
        }
    }

    public FollowCollection getFollowCollection() {
        return followCollection;
    }

    // 判断是否是 LL1 文法
    public boolean judgeIsLL1() {
        EliminateLeftRecursion eliminateLeftRecursion = followCollection.getEliminateLeftRecursion();
        boolean isHappenLeftRecursion = eliminateLeftRecursion.isHappenLeftRecursion();
        Map<String, String> formula;
        // 如果发生了消除左递归
        if (isHappenLeftRecursion) {
            formula = eliminateLeftRecursion.getNewFormula();
        } else {
            formula = eliminateLeftRecursion.getOldFormula();
        }
        return formulaLoop(formula);
    }

    public boolean formulaLoop(Map<String, String> formula) {
        for (Map.Entry<String, String> entry : formula.entrySet()) {
            String rightFormula = entry.getValue();
            String[] strings = rightFormula.split("\\|");
            for (int i = 0; i < strings.length; i++) {
                for (int j = i; j < strings.length; j++) {
                    if (i != j) {
                        boolean flag = judgeIsIntersect(entry.getKey(), strings[i], strings[j]);
                        if (!flag) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    // 判断式子是否是交集
    public boolean judgeIsIntersect(String leftNonTerminal, String string1, String string2) {
        Set<String> set1 = new HashSet<>();
        Set<String> set2 = new HashSet<>();
        searchFirstCharacter(string1, set1);
        searchFirstCharacter(string2, set2);
        Map<String, Set<String>> followCollectionMap = followCollection.getFollowCollectionMap();
        Map<String, Set<String>> firstCollectionMap = firstCollection.getFirstCollectionMap();
        //如果存在 ε 的话
        if (set1.contains("ε") || set2.contains("ε")) {
            boolean flag = compareFirstAndFollow(firstCollectionMap.get(leftNonTerminal), followCollectionMap.get(leftNonTerminal));
            if (!flag) {
                return false;
            }
        }
        //不论存在不存在ε都要判断这个，两个条件不是互斥的，都得进行判断
        for (String stingX : set1) {
            for (String stringY : set2) {
                if (stingX.equals(stringY)) {
                    return false;
                }
            }
        }
        return true;
    }

    // 比较 First 集和 Follow 集是否有交集
    public boolean compareFirstAndFollow(Set<String> first, Set<String> follow) {
        if (follow == null || follow.size() == 0) {
            return true;
        }
        for (String sting1 : first) {
            for (String string2 : follow) {
                if (sting1.equals(string2)) {
                    return false;
                }
            }
        }
        return true;
    }

    // 查找右边式子中的 First 集
    public void searchFirstCharacter(String nonTerminal, Set<String> set) {
        String string = "" + nonTerminal.charAt(0);
        Map<String, Set<String>> firstCollectionMap = firstCollection.getFirstCollectionMap();
        if (firstCollectionMap.containsKey(string)) {
            set.addAll(firstCollectionMap.get(string));
        } else {
            set.add(string);
        }
    }

}