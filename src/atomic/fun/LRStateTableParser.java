package atomic.fun;

import atomic.fun.biz.AFOPNode;
import atomic.fun.biz.BaseAFTask;
import atomic.fun.biz.OPGetter;
import atomic.fun.biz.TaskGetter;

import java.util.HashMap;
import java.util.Stack;


public class LRStateTableParser {
	private Lexer lexer;
	int    lexerInput = 0;
	int    nestingLevel = 0;
	String text = "";

	private Object attributeForParentNode = null;
	private Stack<Integer> statusStack = new Stack<Integer>();
	HashMap<Integer, HashMap<Integer, Integer>> lrStateTable = null;
    public LRStateTableParser(Lexer lexer) {
    	this.lexer = lexer;
    	statusStack.push(0);
    	valueStack.push(null);
    	lexer.advance();
    	lexerInput = lexer.lookAhead;
    	lrStateTable = AFGrammarStateManager.getGrammarManager().getLRStateTable();
		System.out.println("lrStateTable = " + lrStateTable);
    }
    
    private Stack<Object> valueStack = new Stack<Object>();
    private Stack<Integer> parseStack = new Stack<Integer>();
    
    private void showCurrentStateInfo(int stateNum) {
    	System.out.println("current input is :" + AFTokenType.getSymbolStr(lexerInput));
    	
    	System.out.println("current state is: ");
		AFGrammarState state = AFGrammarStateManager.getGrammarManager().getGrammarState(stateNum);
		state.print();
    }
    
    public void parse() {
    
        while (true) {

        	Integer action = getAction(statusStack.peek(), lexerInput);

        	if (action == null) {
        		//解析出错
        		System.err.println("The input is denied");
    			return;
        	}

        	if (action > 0) {
        		//showCurrentStateInfo(action);

        		//shift 操作
                statusStack.push(action);
    			text = lexer.yytext;

    			parseStack.push(lexerInput);

    			if (AFTokenType.isTerminal(lexerInput)) {
    				System.out.println("Shift for input: " + AFTokenType.values()[lexerInput].toString());

    				lexer.advance();
        			lexerInput = lexer.lookAhead;
        			valueStack.push(null);
    			} else {
    				lexerInput = lexer.lookAhead;
    			}
    			
        	} else {
        		if (action == 0) {
        			System.out.println("The input can be accepted");

					Object lastStat = valueStack.peek();

					if(lastStat instanceof BaseAFTask) {
						((BaseAFTask) lastStat).run();
					}

					System.out.println(lastStat);
					return;
        		}
        		
        		int reduceProduction = - action;
        		AFProduction product = AFProductionManager.getProductionManager().getProductionByIndex(reduceProduction);
        		System.out.println("reduce by product: ");
        		product.print();

        		takeActionForReduce(reduceProduction);
        	

        		int rightSize = product.getRight().size();
        		while (rightSize > 0) {
        			parseStack.pop();
        			valueStack.pop();
        			statusStack.pop();
        			rightSize--;
        		}
        		
        		lexerInput = product.getLeft();
    			parseStack.push(lexerInput);
    			valueStack.push(attributeForParentNode);
        	}
        }
	}
    
    private void takeActionForReduce(int productNum) {
		System.out.println("takeActionForReduce productNum = " + productNum + " text = " + text);

		switch (productNum) {
			//term -> num
			case 4:
				attributeForParentNode = TaskGetter.createTask(text);
				break;
			//expr -> term
			case 3:
				attributeForParentNode = valueStack.peek();
				break;
			//expr -> expr op expr
			case 1:
				attributeForParentNode = valueStack.get(valueStack.size() - 3);
				AFOPNode op = (AFOPNode) valueStack.get(valueStack.size() - 2);
				BaseAFTask left = (BaseAFTask) attributeForParentNode;
				BaseAFTask right = (BaseAFTask) valueStack.peek();
				attributeForParentNode = TaskGetter.connectTask(left, op, right);
				System.out.println("attributeForParentNode = " + attributeForParentNode);
				break;
			//op -> +
			case 5:
			//op -> +
			case 6:
			//op -> ·
			case 7:
			//op -> -
			case 8:
				attributeForParentNode = OPGetter.createOPNode(text);
				break;
		}
    }
    
    private Integer getAction(Integer currentState, Integer currentInput) {
    	HashMap<Integer, Integer> jump = lrStateTable.get(currentState);
    	if (jump != null) {
    		Integer next = jump.get(currentInput);
    		if (next != null) {
    			return next;
    		}
    	}
    	
    	return null;
    }
    
}
