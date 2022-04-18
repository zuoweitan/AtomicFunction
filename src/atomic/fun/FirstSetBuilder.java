package atomic.fun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 定义 ： 对于一个给定的非终结符，通过一系列语法推导后，能出现在推导最左边的所有终结符的集合，统称为该非终结符的FIRST SET集合
 * 应用 ： 自顶向下的解析表构建、自底向上的lookAhead集合的构建
 * 算法步骤 ：
 * 	1. 如果A是一个终结符，那么FIRST(A) = {A}
 * 	2. 对于以下形式的语法推导：
 * 	s-> Aa
 * 	s是非终结符，A是终结符，a是零个或多个终结符或非终结符的组合，那么A属于FIRST(s)
 * 	3. 对于推导表达式：
 * 	s->ba
 * 	s和b是非终结符，而且b不是nullable的，那么FIRST(a) = FIRST(b)
 * 	4. 对于推导表达式：
 * 	s->a1 a2 ... an b
 * 	如果a1,a2,...,an 是nullable的非终结符，b是非终结符但不是nullable或者b是终结符，那么FIRST(s) 是 FIRST(a1), FIRST(a2),..., FIRST(an), FIRST(b)的集合
 *
 */
public class FirstSetBuilder {
	
    private HashMap<Integer, Symbols> symbolMap = new HashMap<Integer, Symbols>();
    private ArrayList<Symbols> symbolArray = new ArrayList<Symbols>();
    private boolean runFirstSetPass = true;
  
	int productionCount = 0;
	
    public FirstSetBuilder() {
    	initProductions();
    }
    
    public boolean isSymbolNullable(int sym) {
    	Symbols symbol= symbolMap.get(sym);
    	if (symbol == null) {
    		return false;
    	}
    	
    	return symbol.isNullable ? true : false;
    }
    
    private void initProductions() {
    	symbolMap = AFGrammarInitializer.getInstance().getSymbolMap();
    	symbolArray = AFGrammarInitializer.getInstance().getSymbolArray();
    }
    
    public void runFirstSets() {
    	while (runFirstSetPass) {
    		runFirstSetPass = false;
    		
    		Iterator<Symbols> it = symbolArray.iterator();
    		while (it.hasNext()) {
    			Symbols symbol = it.next();
    			addSymbolFirstSet(symbol);
    		}
    		
    	}
    	
    	printAllFirstSet();
		System.out.println("============");
    }
    
    private void addSymbolFirstSet(Symbols symbol) {
    	if (isSymbolTerminals(symbol.value) == true) {
    		if (symbol.firstSet.contains(symbol.value) == false) {
    			symbol.firstSet.add(symbol.value);
    		}
    		return;
    	}
    	
    	for (int i = 0;  i < symbol.productions.size(); i++) {
    		int[] rightSize = symbol.productions.get(i);
    		if (rightSize.length == 0) {
    			continue;
    		}
    		
    		if (isSymbolTerminals(rightSize[0]) && symbol.firstSet.contains(rightSize[0]) == false) {
    			runFirstSetPass = true;
    			symbol.firstSet.add(rightSize[0]);
    		}
    		else if (isSymbolTerminals(rightSize[0]) == false) {
    			//add first set of nullable
    			int pos = 0;
    			Symbols curSymbol = null;
    			do {
    				curSymbol = symbolMap.get(rightSize[pos]);
    				if (symbol.firstSet.containsAll(curSymbol.firstSet) == false) {
    					runFirstSetPass = true;
    					
    					for (int j = 0; j < curSymbol.firstSet.size(); j++) {
    						if (symbol.firstSet.contains(curSymbol.firstSet.get(j)) == false) {
    							symbol.firstSet.add(curSymbol.firstSet.get(j));
    						}
    					}//for (int j = 0; j < curSymbol.firstSet.size(); j++)
    					
    				}//if (symbol.firstSet.containsAll(curSymbol.firstSet) == false)
    				
    				pos++;
    			}while(pos < rightSize.length && curSymbol.isNullable);
    		} // else
    		
    	}//for (int i = 0; i < symbol.productions.size(); i++)
    }
    
    private boolean isSymbolTerminals(int symbol) {
    	return AFTokenType.isTerminal(symbol);
    }
    
    public void printAllFirstSet() {
    	Iterator<Symbols> it = symbolArray.iterator();
		while (it.hasNext()) {
		    Symbols sym = it.next();
		    printFirstSet(sym);
		}
    }
    
    private void printFirstSet(Symbols symbol) {
    	
    	String s = AFTokenType.getSymbolStr(symbol.value);
    	s += "{ ";
    	for (int i = 0; i < symbol.firstSet.size(); i++) {
    		s += AFTokenType.getSymbolStr(symbol.firstSet.get(i)) + " ";
    	}
    	s += " }";
    	
    	System.out.println(s);
    	System.out.println("============");
    }
    
    public ArrayList<Integer> getFirstSet(int symbol) {
    	Iterator<Symbols> it = symbolArray.iterator();
		while (it.hasNext()) {
		    Symbols sym = it.next();
		    if (sym.value == symbol) {
		    	return sym.firstSet;
		    }
		}
		
		return null;
	
    }
    
 
}
