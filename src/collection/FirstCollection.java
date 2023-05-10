package collection;

import utils.EliminateLeftRecursion;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// 构建FIRST集
public class FirstCollection {
    private final EliminateLeftRecursion eliminateLeftRecursion;
    private final Map<String, Set<String>> firstCollectionMap = new HashMap<>();

    public Map<String, Set<String>> getFirstCollectionMap() {
        return firstCollectionMap;
    }

    public EliminateLeftRecursion getEliminateLeftRecursion() {
        return eliminateLeftRecursion;
    }

    public FirstCollection(String fileSrc) throws IOException {
        // 消除左递归
        this.eliminateLeftRecursion = new EliminateLeftRecursion(fileSrc);
        eliminateLeftRecursion.eliminateLeftRecursion();
        // 生成 First 集
        createFirstCollection();
    }

    public void createFirstCollection() {
        Map<String, String> formula = eliminateLeftRecursion.getNewFormula();
        for (Map.Entry<String, String> entry : formula.entrySet()) {
            String leftNonTerminal = entry.getKey();
            Set<String> set = new HashSet<>();
            searchFirstCharacter(leftNonTerminal, set);
            firstCollectionMap.put(leftNonTerminal, set);
        }

        System.out.println();
        for (Map.Entry<String, Set<String>> entry : firstCollectionMap.entrySet()) {
            System.out.println(entry.getKey() + "的 FIRST 集：" + entry.getValue().toString());
        }
    }


    // 递归向下查找式子第一个开头的字符
    public void searchFirstCharacter(String nonTerminal, Set<String> set) {
        Map<String, String> formula = eliminateLeftRecursion.getNewFormula();
        String[] strings = formula.get(nonTerminal).split("\\|");
        for (String string : strings) {
            String firstCharacter = "%s".formatted(string.charAt(0));
            // 首字符是否是非终结符，若是则继续递归查找，若不是则直接添加进FIRST集合
            if (formula.containsKey(firstCharacter)) {
                searchFirstCharacter(firstCharacter, set);
            } else {
                set.add(firstCharacter);
            }
        }
    }
}