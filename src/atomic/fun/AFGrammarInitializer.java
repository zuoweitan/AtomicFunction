package atomic.fun;

import java.util.ArrayList;
import java.util.HashMap;

public class AFGrammarInitializer {

    private volatile static AFGrammarInitializer instance = null;
    private HashMap<Integer, ArrayList<AFProduction>> productionMap = new HashMap<>();
    private HashMap<Integer, Symbols> symbolMap = new HashMap<>();
    private ArrayList<Symbols> symbolArray = new ArrayList<Symbols>();
    public  static AFGrammarInitializer getInstance() {
        if (instance == null) {
            instance = new AFGrammarInitializer();
        }

        return instance;

    }

    private AFGrammarInitializer() {

    }

    public HashMap<Integer, ArrayList<AFProduction>> getProductionMap() {
        return productionMap;
    }

    public HashMap<Integer, Symbols> getSymbolMap() {
        return symbolMap;
    }

    public ArrayList<Symbols> getSymbolArray() {
        return symbolArray;
    }


    public void initVariableDecalationProductions() {

        productionMap.clear();


        grammarInit();

//        /**
//         * stmt -> stmt op stmt
//         */
//        int productionNum = 0;
//        ArrayList<Integer> right = null;
//        right = getProductionRight( new int[]{AFTokenType.STMT.ordinal(), AFTokenType.OP.ordinal(), AFTokenType.STMT.ordinal() });
//        AFProduction production = new AFProduction(productionNum, AFTokenType.STMT.ordinal(), 0, right);
//        productionNum++;
//        addProduction(production, false);
//
//        /**
//         * stmt -> (stmt)
//         */
//        right = getProductionRight(new int[]{AFTokenType.LP.ordinal(), AFTokenType.STMT.ordinal(), AFTokenType.RP.ordinal()});
//        production = new AFProduction(productionNum, AFTokenType.STMT.ordinal(), 0, right);
//        productionNum++;
//        addProduction(production, false);
//
//        /**
//         * stmt -> term
//         */
//        right = getProductionRight(new int[]{AFTokenType.TERM.ordinal()});
//        production = new AFProduction(productionNum, AFTokenType.STMT.ordinal(), 0, right);
//        productionNum++;
//        addProduction(production, false);
//
//        /**
//         * term -> NUM
//         */
//        right = getProductionRight(new int[]{AFTokenType.NUM.ordinal()});
//        production = new AFProduction(productionNum, AFTokenType.TERM.ordinal(), 0, right);
//        productionNum++;
//        addProduction(production, false);
//
//        /**
//         * op -> 丨
//         */
//        right = getProductionRight(new int[]{AFTokenType.VLINE.ordinal()});
//        production = new AFProduction(productionNum, AFTokenType.OP.ordinal(), 0, right);
//        productionNum++;
//        addProduction(production, false);
//
//        /**
//         * op -> +
//         */
//        right = getProductionRight(new int[]{AFTokenType.PLUS.ordinal()});
//        production = new AFProduction(productionNum, AFTokenType.OP.ordinal(), 0, right);
//        productionNum++;
//        addProduction(production, false);
//
//        /**
//         * op -> ·
//         */
//        right = getProductionRight(new int[]{AFTokenType.DOT.ordinal()});
//        production = new AFProduction(productionNum, AFTokenType.OP.ordinal(), 0, right);
//        productionNum++;
//        addProduction(production, false);
//
//        /**
//         * op -> -
//         */
//        right = getProductionRight(new int[]{AFTokenType.MINUS.ordinal()});
//        production = new AFProduction(productionNum, AFTokenType.OP.ordinal(), 0, right);
//        productionNum++;
//        addProduction(production, false);


        addTerminalToSymbolMapAndArray();
    }

    private void grammarInit() {
        /**
         * stmt -> expr
         */
        int productionNum = 0;
        ArrayList<Integer> right = null;
        right = getProductionRight( new int[]{AFTokenType.EXPR.ordinal() });
        AFProduction production = new AFProduction(productionNum, AFTokenType.STMT.ordinal(), 0, right);
        productionNum++;
        addProduction(production, false);

        /**
         * expr -> expr op expr
         */
        right = getProductionRight(new int[]{AFTokenType.EXPR.ordinal(), AFTokenType.OP.ordinal(), AFTokenType.EXPR.ordinal()});
        production = new AFProduction(productionNum, AFTokenType.EXPR.ordinal(), 0, right);
        productionNum++;
        addProduction(production, false);

        /**
         * expr -> (expr)
         */
        right = getProductionRight(new int[]{AFTokenType.LP.ordinal(), AFTokenType.EXPR.ordinal(), AFTokenType.RP.ordinal()});
        production = new AFProduction(productionNum, AFTokenType.EXPR.ordinal(), 0, right);
        productionNum++;
        addProduction(production, false);

        /**
         * expr -> term
         */
        right = getProductionRight(new int[]{AFTokenType.TERM.ordinal()});
        production = new AFProduction(productionNum, AFTokenType.EXPR.ordinal(), 0, right);
        productionNum++;
        addProduction(production, false);

        /**
         * term -> NUM
         */
        right = getProductionRight(new int[]{AFTokenType.NUM.ordinal()});
        production = new AFProduction(productionNum, AFTokenType.TERM.ordinal(), 0, right);
        productionNum++;
        addProduction(production, false);

        /**
         * op -> 丨
         */
        right = getProductionRight(new int[]{AFTokenType.VLINE.ordinal()});
        production = new AFProduction(productionNum, AFTokenType.OP.ordinal(), 0, right);
        productionNum++;
        addProduction(production, false);

        /**
         * op -> +
         */
        right = getProductionRight(new int[]{AFTokenType.PLUS.ordinal()});
        production = new AFProduction(productionNum, AFTokenType.OP.ordinal(), 0, right);
        productionNum++;
        addProduction(production, false);

        /**
         * op -> ·
         */
        right = getProductionRight(new int[]{AFTokenType.DOT.ordinal()});
        production = new AFProduction(productionNum, AFTokenType.OP.ordinal(), 0, right);
        productionNum++;
        addProduction(production, false);

        /**
         * op -> -
         */
        right = getProductionRight(new int[]{AFTokenType.MINUS.ordinal()});
        production = new AFProduction(productionNum, AFTokenType.OP.ordinal(), 0, right);
        productionNum++;
        addProduction(production, false);
    }

    private void addProduction(AFProduction production, boolean nullable) {

        ArrayList<AFProduction> productionList = productionMap.get(production.getLeft());

        if (productionList == null) {
            productionList = new ArrayList<AFProduction>();
            productionMap.put(production.getLeft(), productionList);
        }

        if (productionList.contains(production) == false) {
            productionList.add(production);
        }

        addSymbolMapAndArray(production, nullable);

    }

    private void addSymbolMapAndArray(AFProduction production, boolean nullable) {
        //add symbol array and symbol map
        int[] right = new int[production.getRight().size()];
        for (int i = 0; i < right.length; i++) {
            right[i] = production.getRight().get(i);
        }

        if (symbolMap.containsKey(production.getLeft())) {
            symbolMap.get(production.getLeft()).addProduction(right);
        } else {
            ArrayList<int[]> productions = new ArrayList<int[]>();
            productions.add(right);
            Symbols symObj = new Symbols(production.getLeft(), nullable, productions);
            symbolMap.put(production.getLeft(), symObj);
            symbolArray.add(symObj);
        }
    }

    private void addTerminalToSymbolMapAndArray() {
        for (int i = AFTokenType.FIRST_TERMINAL_INDEX; i <= AFTokenType.LAST_TERMINAL_INDEX; i++) {
            Symbols symObj = new Symbols(i, false, null);
            symbolMap.put(i, symObj);
            symbolArray.add(symObj);
        }
    }

    private ArrayList<Integer> getProductionRight(int[] arr) {
        ArrayList<Integer> right = new ArrayList<Integer>();
        for (int i = 0; i < arr.length; i++) {
            right.add(arr[i]);
        }

        return right;
    }
}
