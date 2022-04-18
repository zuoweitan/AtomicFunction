package atomic.fun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Stack;


public class AFGrammarState {
	public static int stateNumCount = 0;
	private boolean printInfo = false;
	private boolean transitionDone = false;
    public  int stateNum = -1;
    private AFGrammarStateManager stateManager = AFGrammarStateManager.getGrammarManager();
    private ArrayList<AFProduction> productions = new ArrayList<>();
    private HashMap<Integer, AFGrammarState> transition = new HashMap<>();
    private ArrayList<AFProduction> closureSet = new ArrayList<>();
    private AFProductionManager productionManager = AFProductionManager.getProductionManager();
    private HashMap<Integer, ArrayList<AFProduction>> partition = new HashMap<>();
    private ArrayList<AFProduction> mergedProduction = new ArrayList<AFProduction>();

    public static void  increateStateNum() {
    	stateNumCount++;
    }

    public boolean isTransitionMade() {
    	return transitionDone;
    }

    public AFGrammarState(ArrayList<AFProduction> productions) {
    	this.stateNum = stateNumCount;
    	
    	this.productions = productions;
    	
    	if (stateNum == 7) {
    	    System.out.println("closure set of state 7:" + productions.size());
    	}

    	this.closureSet.addAll(this.productions);
    }
    
    public void stateMerge(AFGrammarState state) {
    	if (this.productions.contains(state.productions) == false) {
    		for (int i = 0; i < state.productions.size(); i++) {
    			if (this.productions.contains(state.productions.get(i)) == false
    					&& mergedProduction.contains(state.productions.get(i)) == false
    					) {
    				mergedProduction.add(state.productions.get(i));
    			}
    		}
    	}
    	
    }
    
    public void print() {
    	System.out.println("State Number: " + stateNum);
    	for (int i = 0; i < productions.size(); i++) {
    		productions.get(i).print();
    	}
    	
    	for (int i = 0; i < mergedProduction.size(); i++) {
    		mergedProduction.get(i).print();	
    	}
    }
    
    public void printTransition() {
    	for (Entry<Integer, AFGrammarState> entry: transition.entrySet()) {
    		System.out.println("transfter on " + AFTokenType.getSymbolStr(entry.getKey()) + " to state ");
    		entry.getValue().print();
    		System.out.print("\n");
    	}
    }
    
    public void createTransition() {
    	if (transitionDone == true) {
    		return;
    	}
    	
    	transitionDone = true;
  
    	
    	makeClosure();
    	
    	partition();
  
    	makeTransition();
   
    	printInfo = true;
    	
    }
    
    private void makeClosure() {
    	int debug = 0;
    	if (stateNum == 1) {
    		debug = 1;
    	}
    	
    	Stack<AFProduction> productionStack = new Stack<AFProduction>();
    	for (int i = 0; i < productions.size(); i++) {
    		productionStack.push(productions.get(i));
    	}
    	
    //	System.out.println("---begin make closure----");
    	
    	while (productionStack.empty() == false) {
			AFProduction production = productionStack.pop();
    		System.out.println("\nproduction on top of stack is : ");
    		production.print();
    		production.printBeta();
    		
    		
    		if (AFTokenType.isTerminal(production.getDotSymbol()) == true) {
    			    System.out.println("symbol after dot is not non-terminal, ignore and prcess next item");
    			    continue;	
    			}
    		
    		int symbol = production.getDotSymbol();
    		ArrayList<AFProduction> closures = productionManager.getProduction(symbol);
    		ArrayList<Integer> lookAhead = production.computeFirstSetOfBetaAndC();
    		
    		Iterator it = closures.iterator();
    		while (it.hasNext()) {
				AFProduction oldProduct = (AFProduction) it.next();
				AFProduction newProduct = (oldProduct).cloneSelf();
    			
    			
    			newProduct.addLookAheadSet(lookAhead);
    			
    			
    			if (closureSet.contains(newProduct) == false) {  
    				System.out.println("push and add new production to stack and closureSet");
    				
    				closureSet.add(newProduct);
    				productionStack.push(newProduct);
    				System.out.println("Add new production:");
    				newProduct.print();
    				removeRedundantProduction(newProduct);	
    			}
    			else {
    				System.out.println("the production is already exist!");
    			}
    			
    		}
    	
    		
    	}
    	
    	//printClosure();
    	//System.out.println("----end make closure----");
    	
    }
    
    private void removeRedundantProduction(AFProduction product) {
    	boolean removeHappended = true;
    	
    	while (removeHappended) {
    		removeHappended = false;
    		
    		Iterator it = closureSet.iterator();
    		while (it.hasNext()) {
				AFProduction item = (AFProduction) it.next();
    			if (product.coverUp(item)) {
    				removeHappended = true;
    				closureSet.remove(item);
    				if (stateNum == 1) {
    				 //   System.out.print("remove redundant production: ");
        				//item.print();
    				}
    		
    				break;
    			}
    		}
    	}
    }
    
  
    private void printClosure() {
    	if (printInfo) {
    		return;
    	}
    	
    	System.out.println("ClosueSet is: ");
    	for (int i = 0; i < closureSet.size(); i++) {
    		closureSet.get(i).print();
    	}
    }
    
    private void partition() {
    	for (int i = 0; i < closureSet.size(); i++) {
    		int symbol = closureSet.get(i).getDotSymbol();
    		if (symbol == AFTokenType.UNKNOWN_TOKEN.ordinal()) {
    			continue;
    		}
    		
    		ArrayList<AFProduction> productionList = partition.get(symbol);
    		if (productionList == null) {
    			productionList = new ArrayList<>();
    			partition.put(closureSet.get(i).getDotSymbol(), productionList);
    		}
    		
    		if (productionList.contains(closureSet.get(i)) == false) {
    	        productionList.add(closureSet.get(i));	
    		}
    	}
    	
    	
    	
    //	printPartition();
    }
    
    private void printPartition() {
    	if (printInfo) {
    		return;
    	}
    	
    	for(Entry<Integer, ArrayList<AFProduction>> entry : partition.entrySet()) {
    		
    		System.out.println("partition for symbol: " + AFTokenType.getSymbolStr(entry.getKey()));
    		
    		ArrayList<AFProduction> productionList = entry.getValue();
    		for (int i = 0; i < productionList.size(); i++) {
    			productionList.get(i).print();
    		}
    	}
    }
    
    private AFGrammarState makeNextGrammarState(int left) {
    	ArrayList<AFProduction> productionList = partition.get(left);
    	ArrayList<AFProduction> newStateProductionList = new ArrayList<>();
    	
    	for (int i = 0; i < productionList.size(); i++) {
			AFProduction production = productionList.get(i);
    		newStateProductionList.add(production.dotForward());
    	}
    	
    	return  stateManager.getGrammarState(newStateProductionList);
    }
    
    private void makeTransition() {
    	
    	for (Entry<Integer, ArrayList<AFProduction>> entry : partition.entrySet()) {
    //		System.out.println("\n====begin print transition info ===");
    		AFGrammarState nextState = makeNextGrammarState(entry.getKey());
    		transition.put(entry.getKey(), nextState);
    	//	System.out.println("from state " + stateNum + " to State " + nextState.stateNum + " on " + 
    	//	CTokenType.getSymbolStr(entry.getKey()));
    		//System.out.println("----State " + nextState.stateNum + "------");
    	//	nextState.print();
    		
    		stateManager.addTransition(this, nextState, entry.getKey());
    	}
    	
    	extendFollowingTransition();
    }
    
    private void extendFollowingTransition() {
    	for (Entry<Integer, AFGrammarState> entry : transition.entrySet()) {
			AFGrammarState state = entry.getValue();
    		if (state.isTransitionMade() == false) {
    			state.createTransition();
    		}
    	}
    }
    
    @Override
    public boolean equals(Object obj) {
    	return checkProductionEqual(obj, false);
    }
    
    public boolean checkProductionEqual(Object obj, boolean isPartial) {
		AFGrammarState state = (AFGrammarState)obj;
    	 
    	if (state.productions.size() != this.productions.size()) {
    		return false;
    	}
    	
    	int equalCount = 0;
    	
    	for (int i = 0; i < state.productions.size(); i++) {
             for (int j = 0; j < this.productions.size(); j++) {
            	 if (isPartial == false) {
               	  if (state.productions.get(i).equals(this.productions.get(j)) == true) {
         				equalCount++;
         				break;
         			 }
                 }
                 else {
               	    if (state.productions.get(i).productionEequals(this.productions.get(j)) == true) {
               	    	equalCount++;
               	    	break;
               	    }
                 }
             }
    			
    		}
    	
    		
    	return equalCount == state.productions.size();
    }
    
    public HashMap<Integer, Integer> makeReduce() {
    	HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
    	reduce(map, this.productions);
    	reduce(map, this.mergedProduction);
    	
    	return map;
    }
    
    private void  reduce(HashMap<Integer, Integer> map, ArrayList<AFProduction> productions) {
    	for (int i = 0; i < productions.size(); i++) {
    		if (productions.get(i).canBeReduce()) {
    			ArrayList<Integer> lookAhead = productions.get(i).getLookAheadSet();
    			for (int j = 0; j < lookAhead.size(); j++) {
    				map.put(lookAhead.get(j), (productions.get(i).getProductionNum()));
    			}
    		}
    	}
    }
    
    
}
