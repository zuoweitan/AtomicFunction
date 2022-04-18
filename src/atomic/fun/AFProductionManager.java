package atomic.fun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AFProductionManager {

    private static AFProductionManager self = null;

    FirstSetBuilder firstSetBuilder = new FirstSetBuilder();

    private HashMap<Integer, ArrayList<AFProduction>> productionMap = new HashMap<Integer, ArrayList<AFProduction>>();

    public static AFProductionManager getProductionManager() {
        if (self == null) {
            self = new AFProductionManager();
        }

        return self;
    }

    public void initProductions() {
        AFGrammarInitializer cGrammarInstance =  AFGrammarInitializer.getInstance();
        cGrammarInstance.initVariableDecalationProductions();
        productionMap = cGrammarInstance.getProductionMap();

    }

    public void runFirstSetAlgorithm() {
        firstSetBuilder.runFirstSets();
    }

    public FirstSetBuilder getFirstSetBuilder() {
        return firstSetBuilder;
    }

    public void printAllProductions() {
        for (Map.Entry<Integer, ArrayList<AFProduction>> entry : productionMap.entrySet()) {
            ArrayList<AFProduction> list = entry.getValue();
            for (int i = 0; i < list.size(); i++) {
                list.get(i).print();
                System.out.print("\n");
            }
        }
    }


    public ArrayList<AFProduction> getProduction(int left) {
        return productionMap.get(left);
    }

    public AFProduction getProductionByIndex(int index) {

        for (Map.Entry<Integer, ArrayList<AFProduction>> item : productionMap.entrySet()) {
            ArrayList<AFProduction> productionList = item.getValue();
            for (int i = 0; i < productionList.size(); i++) {
                if (productionList.get(i).getProductionNum() == index) {
                    return productionList.get(i);
                }
            }
        }

        return null;
    }

    private AFProductionManager() {

    }
}
