package atomic.fun.biz;

public class AFOPNode {
    private String name;
    public AFOPNode(String name) {
        this.name = name;
    }

    public boolean isSeq() {
        return "|".equals(name);
    }

    public boolean isPlus() {
        return "+".equals(name);
    }

    public boolean isMinus() {
        return "-".equals(name);
    }

    public boolean isDot() {
        return "·".equals(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "AFOPNode{" +
                "name='" + name + '\'' +
                '}';
    }
}
