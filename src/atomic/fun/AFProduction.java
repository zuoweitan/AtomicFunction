package atomic.fun;

import java.util.ArrayList;

public class AFProduction {

    private int dotPos = 0;
    private boolean printDot = false;
    private int left = 0;
    private ArrayList<Integer> right = null;

    //用来解决shift与reduce和reduce与reduce矛盾
    private ArrayList<Integer> lookAhead = new ArrayList<Integer>();
    private int productionNum = -1;

    public AFProduction(int productionNum, int left, int dot, ArrayList<Integer> right) {
        this.left = left;
        this.right = right;
        this.productionNum = productionNum;

        if (dot >= right.size()) {
            dot = right.size();
        }

        lookAhead.add(AFTokenType.EOI.ordinal());

        this.dotPos = dot;
    }


    public AFProduction dotForward() {
        AFProduction product = new AFProduction(productionNum, this.left, dotPos+1, this.right);

        product.lookAhead = new ArrayList<Integer>();
        for (int i = 0; i < this.lookAhead.size(); i++) {
            product.lookAhead.add(this.lookAhead.get(i));
        }

        return  product;
    }

    public AFProduction cloneSelf() {

        AFProduction product = new AFProduction(productionNum, this.left, dotPos, this.right);

        product.lookAhead = new ArrayList<Integer>();
        for (int i = 0; i < this.lookAhead.size(); i++) {
            product.lookAhead.add(this.lookAhead.get(i));
        }

        return  product;
    }

    public ArrayList<Integer> computeFirstSetOfBetaAndC() {
        /*
         * 计算 First(β C)
         * 将β 和 C ,前后相连再计算他们的First集合，如果β 里面的每一项都是nullable的，那么
         * First(β C) 就是 First(β) 并上First(C), 由于C 必定是终结符的组合，所以First(C)等于C的第一个非终结符
         * 例如C = {+, * , EOI} , First(C) = {+}
         */

        ArrayList<Integer> set = new ArrayList<Integer>();
        set.addAll(lookAhead);

        AFProductionManager manager = AFProductionManager.getProductionManager();

        for (int i = dotPos + 1; i < right.size(); i++) {
            ArrayList<Integer> firstSet = manager.getFirstSetBuilder().getFirstSet(right.get(i));

            for (int j = 0; j < firstSet.size(); j++) {
                if (set.contains(firstSet.get(j)) == false) {
                    set.add(firstSet.get(j));
                }
            }

            if (manager.getFirstSetBuilder().isSymbolNullable(right.get(i)) == false) {
                break;
            }
        }

        return set;
    }

    public void printBeta() {
        System.out.print("Beta part of production is: ");
        for (int i = dotPos + 1; i < right.size(); i++) {
            System.out.print(AFTokenType.getSymbolStr(right.get(i)) + " ");
        }

        if (dotPos+1 >= right.size()) {
            System.out.print("null");
        }

        System.out.print("\n");
    }

    public void addLookAheadSet(ArrayList<Integer> list) {
        lookAhead = list;
    }


    public int getLeft() {
        return left;
    }

    public ArrayList<Integer> getRight() {
        return right;
    }

    public int getDotPosition() {
        return dotPos;
    }

    public int getDotSymbol() {
        if (dotPos >= right.size()) {
            return AFTokenType.UNKNOWN_TOKEN.ordinal();
        }
        return right.get(dotPos);
    }

    @Override
    public boolean equals(Object obj) {
        /*
         * 判断两个表达式是否相同有两个条件，一是表达式相同，而是对应的Look ahead 集合也必须一致
         */
        AFProduction product = (AFProduction)obj;

        if (this.productionEequals(product) && this.lookAheadSetComparing(product) == 0) {
            return true;
        }

        return false;
    }



    public boolean coverUp(AFProduction product) {
        /*
         * 如果表达式相同，但是表达式1的look ahead 集合 覆盖了表达式2的look ahead 集合，
         * 那么表达式1 就覆盖 表达式2
         */
        if (this.productionEequals(product) && this.lookAheadSetComparing(product) > 0) {
            return true;
        }

        return false;
    }

    public  boolean productionEequals(AFProduction product) {
        if (this.left != product.getLeft()) {
            return false;
        }

        if (this.right.equals(product.getRight()) == false) {
            return false;
        }

        if (this.dotPos != product.getDotPosition()) {
            return false;
        }


        return true;
    }


    public int lookAheadSetComparing(AFProduction product) {
        if (this.lookAhead.size() > product.lookAhead.size()) {
            return 1;
        }

        if (this.lookAhead.size() < product.lookAhead.size()) {
            return -1;
        }

        if (this.lookAhead.size() == product.lookAhead.size()) {
            for (int i = 0; i < this.lookAhead.size(); i++) {
                if (this.lookAhead.get(i) != product.lookAhead.get(i)) {
                    return -1;
                }
            }
        }

        return 0;
    }

    public void print() {
        System.out.print(AFTokenType.getSymbolStr(left) + " -> " );
        for (int i = 0; i < right.size(); i++) {
            if (i == dotPos) {
                printDot = true;
                System.out.print(".");
            }

            System.out.print(AFTokenType.getSymbolStr(right.get(i)) + " ");
        }

        if (printDot == false) {
            System.out.print(".");
        }

        System.out.print("look ahead set: { ");
        for (int i = 0; i < lookAhead.size(); i++) {
            System.out.print(AFTokenType.getSymbolStr(lookAhead.get(i)) + " ");
        }
        System.out.println("}");
    }

    public boolean canBeReduce() {
        return dotPos >= right.size();
    }

    public int  getProductionNum() {
        return productionNum;
    }

    public ArrayList<Integer>  getLookAheadSet() {
        return lookAhead;
    }

}
