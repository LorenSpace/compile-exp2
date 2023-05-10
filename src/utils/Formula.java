package utils;

// 建一个 utils.Formula 类保存数据
public class Formula {
    //右侧式子
    private final String right;
    //左侧非终结符
    private final String left;
    //是否已经进行过间接消除左递归
    private boolean recursionFlag;

    public Formula(String left, String right) {
        this.right = right;
        this.left = left;
    }

    public String getRight() {
        return right;
    }

    public String getLeft() {
        return left;
    }

    public boolean isRecursionFlag() {
        return recursionFlag;
    }

    public void setRecursionFlag(boolean recursionFlag) {
        this.recursionFlag = recursionFlag;
    }

    @Override
    public String toString() {
        return right + "——>" + left;
    }
}

