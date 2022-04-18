package atomic.fun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;


public class AFGrammarStateManager {
    private ArrayList<AFGrammarState> stateList = new ArrayList<>();
    private ArrayList<AFGrammarState> compressedStateList = new ArrayList<>();
    private volatile static AFGrammarStateManager self = null;
    private HashMap<AFGrammarState, HashMap<Integer, AFGrammarState>> transitionMap = new HashMap<>();

	/**
	 * 构建状态机 ，给出reduce信息，基于以上信息构建跳转表
	 */
    private HashMap<Integer, HashMap<Integer, Integer>> lrStateTable = new HashMap<Integer, HashMap<Integer, Integer>>();
    //是否对状态机网络进行压缩
    private boolean isTransitionTableCompressed = true;

    public static AFGrammarStateManager getGrammarManager()  {
    	if (self == null) {
    		self = new AFGrammarStateManager();
    	}

    	return self;
    }

    private AFGrammarStateManager() {
    	
    }
    
    public HashMap<Integer, HashMap<Integer, Integer>> getLRStateTable() {
    	Iterator it = null;
        if (isTransitionTableCompressed) {
        	it = compressedStateList.iterator();
        } else {
        	it = stateList.iterator();
        }
        
        while (it.hasNext()) {
        	AFGrammarState state = (AFGrammarState) it.next();
        	HashMap<Integer, AFGrammarState> map = transitionMap.get(state);
        	HashMap<Integer, Integer> jump = new HashMap<Integer, Integer>();
        
            if (map != null) {
            	for (Entry<Integer, AFGrammarState> item : map.entrySet()) {
            		jump.put(item.getKey(), item.getValue().stateNum);
            	}
            }
            
            HashMap<Integer, Integer> reduceMap = state.makeReduce();
        	if (reduceMap.size() > 0) {
        		for (Entry<Integer, Integer> item : reduceMap.entrySet()) {
        			
        			jump.put(item.getKey(), -(item.getValue()));
        		}
        	}
        	
        	lrStateTable.put(state.stateNum, jump);
        }
        
        return lrStateTable;
    }
    
    public void buildTransitionStateMachine() {
    	AFProductionManager productionManager = AFProductionManager.getProductionManager();
    	AFGrammarState state = getGrammarState(productionManager.getProduction(AFTokenType.STMT.ordinal()));
    	
    	/*
    	 * 初始化节点0后，开始构建整个状态机网络，网络的构建类似一种链式反应，节点0生成节点1到5，每个子节点继续生成相应节点
    	 */
    	state.createTransition();
    	
    	//打印最后形成的状态机网络
    	printStateMap();
    	
    	printReduceInfo();
    }
    
    public AFGrammarState getGrammarState(int stateNum) {
    	  Iterator it = null;
          if (isTransitionTableCompressed) {
          	it = compressedStateList.iterator();
          } else {
          	it = stateList.iterator();
          }
          
      	while(it.hasNext()) {
			AFGrammarState state = (AFGrammarState) it.next();
      		if (state.stateNum == stateNum) {
      			return state;
      		}
      	}
      	
      	return null;
    }
    
    public AFGrammarState getGrammarState(ArrayList<AFProduction> productionList) {
    	/*
    	 * 要生成新的状态节点时，需要查找给定表达式所对应的节点是否已经存在，如果存在，就不必要构造
    	 * 新的节点
    	 */
		AFGrammarState state = new AFGrammarState(productionList);
    	
    	if (stateList.contains(state) == false) {
    		stateList.add(state);
			AFGrammarState.increateStateNum();
    		return state;
    	}
    	
    	for (int i = 0; i < stateList.size(); i++) {
			if (stateList.get(i).equals(state)) {
				state =  stateList.get(i);
			}
		}
    	
    	return state;
    	
    }
    
    
    public void addTransition(AFGrammarState from, AFGrammarState to, int on) {
    	if (isTransitionTableCompressed) {
    		/*
    		 * 压缩时，把相似的节点找到，然后将相似节点合并
    		 */
    		from = getAndMergeSimilarStates(from);
        	to   = getAndMergeSimilarStates(to);	
    	}
    	/*
    	System.out.println("add transition from: ");
    	from.print();
    	System.out.println("on: " + CTokenType.getSymbolStr(on) + " to : ");
    	to.print();
    	*/
    	HashMap<Integer, AFGrammarState> map = transitionMap.get(from);
    	if (map == null) {
    		map = new HashMap<>();
    	}
    	
    	
    	
    	map.put(on, to);
    	transitionMap.put(from, map);
    }
    
    private AFGrammarState getAndMergeSimilarStates(AFGrammarState state) {
    	Iterator it = stateList.iterator();
		AFGrammarState currentState = null, returnState = state;
    	
    	while(it.hasNext()) {
    	    currentState = (AFGrammarState) it.next();
    	    
    		if (currentState.equals(state) == false && currentState.checkProductionEqual(state, true) == true) {
    			/*
    			System.out.println("\nFind similar stats: ");
    			currentState.print();
    			System.out.println("=========");
    			state.print();
    			*/
    			if (currentState.stateNum < state.stateNum) {
    				currentState.stateMerge(state);
    				returnState = currentState;
    			}
    			else {
    				state.stateMerge(currentState);
    				returnState = state;
    			}
    			
    			//System.out.println("combined state is:");
    			//returnState.print();
    			break;
    		}
    	}
    	
    	if (compressedStateList.contains(returnState) == false) {
    		compressedStateList.add(returnState);
    	}
    	
    	return returnState;
    }
    
    public void printStateMap() {
    	System.out.println("Map size is: " + transitionMap.size());
    	
    	for (Entry<AFGrammarState, HashMap<Integer, AFGrammarState>> entry : transitionMap.entrySet()) {
			AFGrammarState from = entry.getKey();
    		System.out.println("********begin to print a map row********");
    		System.out.println("from state: ");
    		from.print();
    		
    		HashMap<Integer, AFGrammarState> map = entry.getValue();
    		for (Entry<Integer, AFGrammarState> item : map.entrySet()) {
    			int symbol = item.getKey();
    			System.out.println("on symbol: " + AFTokenType.getSymbolStr(symbol));
    			System.out.println("to state: ");
				AFGrammarState to = item.getValue();
    			to.print();
    		}
    		 		
    		System.out.println("********end a map row********");
    	}
    }
    
    public void printReduceInfo() {
        System.out.println("\nShow reduce for each state: ");
        Iterator it = null;
        if (isTransitionTableCompressed) {
        	it = compressedStateList.iterator();
        } else {
        	it = stateList.iterator();
        }
        
    	while(it.hasNext()) {
			AFGrammarState state = (AFGrammarState) it.next();
    		    state.print();
    		    
    			HashMap<Integer, Integer> map = state.makeReduce();
    			if (map.entrySet().size() == 0) {
    				System.out.println("in this state, can not take any reduce action\n");
    			}
    			
        		for (Entry<Integer, Integer> entry : map.entrySet()) {
        			System.out.println("Reduce on symbol: " + AFTokenType.getSymbolStr(entry.getKey()) +
        					" to Production " + entry.getValue());
        		}
    		
    	} 	
    }
    
   
    
}
