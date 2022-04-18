package atomic.fun;

public class BottomUpParser {
    public static void main(String[] args) {
    	/*
    	 * 把ProductionManager , FirstSetBuilder 两个类的初始化移到CGrammarInitializer
    	 * 将SymbolDefine 修改成CTokenType, 确定表达式的first set集合运算正确
    	 */
    	AFProductionManager productionManager = AFProductionManager.getProductionManager();
    	productionManager.initProductions();
    	productionManager.printAllProductions();
    	productionManager.runFirstSetAlgorithm();
    	

    	
    	AFGrammarStateManager stateManager = AFGrammarStateManager.getGrammarManager();
    	stateManager.buildTransitionStateMachine();

    	System.out.println("Input string for parsing:");
    	LRStateTableParser parser = new LRStateTableParser(new Lexer());
    	parser.parse();


		float a = .23f;
		float b = 11.f;
		float c = 11.2f;
    	
    }
}
