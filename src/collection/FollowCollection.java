package collection;

import utils.EliminateLeftRecursion;

import java.io.IOException;
import java.util.*;

// 构建 FOLLOW 集
public class FollowCollection {
    private final EliminateLeftRecursion eliminateLeftRecursion;
    private final FirstCollection firstCollection;
    private final Map<String, Set<String>> followCollectionMap = new HashMap<>();

    public EliminateLeftRecursion getEliminateLeftRecursion() {
        return eliminateLeftRecursion;
    }

    public FirstCollection getFirstCollection() {
        return firstCollection;
    }

    public Map<String, Set<String>> getFollowCollectionMap() {
        return followCollectionMap;
    }

    public FollowCollection(String fileSrc) throws IOException {
        this.firstCollection = new FirstCollection(fileSrc);
        this.eliminateLeftRecursion = firstCollection.getEliminateLeftRecursion();
        createFollowCollection();
        System.out.println();
        for (Map.Entry<String, Set<String>> entry1 : followCollectionMap.entrySet()) {
            System.out.println(entry1.getKey() + "的 FOLLOW 集：" + entry1.getValue());
        }
    }


    public void createFollowCollection() {
        Map<String, String> formula = eliminateLeftRecursion.getNewFormula();
        for (Map.Entry<String, String> entry : formula.entrySet()) {
            searchTargetNonTerminalCharacter(entry.getKey());
        }
        Map<String, Set<String>> map = followCollectionMap;

        for (Map.Entry<String, Set<String>> setMap : firstCollection.getFirstCollectionMap().entrySet()) {
            if (followCollectionMap.containsKey(setMap.getKey())) {
                for (Map.Entry<String, Set<String>> entry2 : followCollectionMap.entrySet()) {
                    Set<String> set = entry2.getValue();
                    ListIterator<String> listIterator = new ArrayList<>(set).listIterator();
                    while (listIterator.hasNext()) {
                        String string = listIterator.next();
                        if (map.containsKey(string)) {
                            // 删除原来在 Follow 集中的代表 Follow 集的非终结符符号
                            listIterator.remove();
                            Set<String> targetList = map.get(string);
                            for (String str : targetList) {
                                // 往 Follow 集里面添加新的元素
                                listIterator.add(str);
                            }
                        }
                    }
                    Set<String> stringSet = new HashSet<>();
                    while (listIterator.hasPrevious()) {
                        String string = listIterator.previous();
                        if (!(string.length() == 1 && string.charAt(0) >= 65 && string.charAt(0) <= 90)) {
                            stringSet.add(string);
                        }
                    }
                    stringSet.add("#");
                    entry2.setValue(stringSet);
                }
            } else {
                Set<String> set = new HashSet<>();
                set.add("#");
                map.put(setMap.getKey(), set);
            }
        }
    }

    /*
     * 查找目标非终结符的末尾字符
     * 查找结果可能是终结符也可能是非终结符
     */
    public void searchTargetNonTerminalCharacter(String leftNonTerminal) {
        Map<String, String> formula;
        //如果发生了左递归
        if (eliminateLeftRecursion.isHappenLeftRecursion()) {
            formula = eliminateLeftRecursion.getNewFormula();
        } else {
            formula = eliminateLeftRecursion.getOldFormula();
        }
        String[] strings = formula.get(leftNonTerminal).split("\\|");
        for (String string : strings) {
            //待查找的式子
            for (Map.Entry<String, String> entry : formula.entrySet()) {
                // 非终结符出现的位置
                int index = string.indexOf(entry.getKey());
                // 搜索右边式子中是否有非终结符
                if (index != -1) {
                    for (int i = index; i < string.length(); i++) {
                        String flag = "" + string.charAt(i);
                        if (i + 1 < string.length() && string.charAt(i + 1) == '\'') {
                            flag = flag + "'";
                        }
                        if (flag.equals(entry.getKey())) {
                            if (entry.getKey().length() == 1 && i + 1 < string.length() && string.charAt(i + 1) == '\'') {
                                continue;
                            }
                            Set<String> set = new HashSet<>();
                            // 如果非终结符出现在最末尾
                            if (i == (string.length() - entry.getKey().length())) {
                                // 如果该终结符的 follow 集还未出现，则存放对应的 follow 集标识，后面再补上
                                if (followCollectionMap.containsKey(entry.getKey())) {
                                    // 如果已经出现，则直接赋值给它
                                    set = followCollectionMap.get(entry.getKey());
                                }
                                if (!leftNonTerminal.equals(entry.getKey())) {
                                    set.add(leftNonTerminal);
                                    followCollectionMap.put(entry.getKey(), set);
                                }
                            } else {
                                if (followCollectionMap.containsKey(flag)) {
                                    set = followCollectionMap.get(flag);
                                }
                                String nextCharacter = "" + string.charAt(i + 1);
                                if (i + 2 < string.length() && string.charAt(i + 2) == '\'') {
                                    nextCharacter = nextCharacter + string.charAt(i + 2);
                                }
                                // 如果后面的符号不是非终结符，则直接加入到 follow 集中
                                if (firstCollection.getFirstCollectionMap().containsKey(nextCharacter)) {
                                    set.addAll(eliminateFirstCollectionEmptySet(nextCharacter));
                                    //查看后面的非终结符中是否存在 ε 空集，如果存在则要再添加 follow 集
                                    if (isContainEmptySet(nextCharacter)) {
                                        set.add(leftNonTerminal);
                                    }
                                } else {
                                    set.add(nextCharacter);
                                }
                                followCollectionMap.put(entry.getKey(), set);
                            }
                        }
                    }
                }
            }
        }
    }

    // 消除 First 集中的 ε
    public Set<String> eliminateFirstCollectionEmptySet(String nonTerminal) {
        Set<String> set = firstCollection.getFirstCollectionMap().get(nonTerminal);
        Set<String> result = new HashSet<>();
        // 去除 First 集中的 ε
        for (String character : set) {
            if (!character.equals("ε")) {
                result.add(character);
            }
        }
        return result;
    }

    // 查看相应 First 集中是否存在 ε
    public boolean isContainEmptySet(String nonTerminal) {
        Set<String> set = firstCollection.getFirstCollectionMap().get(nonTerminal);
        for (String string : set) {
            if (string.equals("ε")) {
                return true;
            }
        }
        return false;
    }
}

