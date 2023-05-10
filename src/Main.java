import utils.AnalyzeTable;

import java.io.IOException;

/**
 * @program: coding
 * @author: cxy621
 * @create: 2022-12-07 11:43
 **/
public class Main {
    public static void main(String[] args) throws IOException {
        AnalyzeTable analyzeTable = new AnalyzeTable("./src/input.txt");
        analyzeTable.init();
        //如果不是LL1算法就不用构造预测分析表
        if (analyzeTable.getJudgeIsLL1().judgeIsLL1()) {
            analyzeTable.splitFormula();
            System.out.println();
            System.out.println("预测分析表为：");
            analyzeTable.createTable();
        }
    }
}
