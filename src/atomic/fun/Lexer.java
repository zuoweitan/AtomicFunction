package atomic.fun;

import java.util.HashMap;
import java.util.Scanner;


public class Lexer {
    
    public int lookAhead = AFTokenType.UNKNOWN_TOKEN.ordinal();
    
    public String yytext = "";
    public int yyleng = 0;
    public int yylineno = 0;
    
    private String input_buffer = "";
    private String current = "";
    private HashMap<String, Integer> keywordMap = new HashMap<String, Integer>();
    
    public Lexer() {
    	initKeyWordMap();
    }
    
    private void initKeyWordMap() {

    }
    
    private boolean isAlnum(char c) {
    	if (Character.isAlphabetic(c) == true ||
    		    Character.isDigit(c) == true) {
    		return true;
    	}
    	
    	return false;
    }

	private int lex() {

		while (true) {

			while (current == "") {
				Scanner s = new Scanner(System.in);
				while (true) {
					String line = s.nextLine();
					if (line.equals("end")) {
						break;
					}
					input_buffer += line;
				}
				s.close();

				if (input_buffer.length() == 0) {
					current = "";
					return AFTokenType.EOI.ordinal();
				}

				current = input_buffer;
				++yylineno;
				current.trim();
			}//while (current == "")

			if (current.isEmpty()) {
				return AFTokenType.EOI.ordinal();
			}

			for (int i = 0; i < current.length(); i++) {

				yyleng = 0;
				yytext = current.substring(i, i + 1);
				switch (current.charAt(i)) {
					case '|': current = current.substring(1); return AFTokenType.VLINE.ordinal();
					case '+': current = current.substring(1); return AFTokenType.PLUS.ordinal();
					case '-': current = current.substring(1); return AFTokenType.MINUS.ordinal();
					case '·': current = current.substring(1); return AFTokenType.DOT.ordinal();
					case '(': current = current.substring(1); return AFTokenType.LP.ordinal();
					case ')': current = current.substring(1); return AFTokenType.RP.ordinal();

					case '\n':
					case '\t':
					case ' ':
						current = current.substring(1);
						return AFTokenType.WHITE_SPACE.ordinal();


					default:
						if (isAlnum(current.charAt(i)) == false) {
							return AFTokenType.UNKNOWN_TOKEN.ordinal();
						}
						else {

							while (i < current.length() && isAlnum(current.charAt(i))) {
								i++;
								yyleng++;
							} // while (isAlnum(current.charAt(i)))

							yytext = current.substring(0, yyleng);
							current = current.substring(yyleng);
							return AFTokenType.NUM.ordinal();
						}

				} //switch (current.charAt(i))
			}//  for (int i = 0; i < current.length(); i++)

		}//while (true)
	}
    
    public boolean match(int token) {
    	if (lookAhead == -1) {
    		lookAhead = lex();
    	}
    	
    	return token == lookAhead;
    }
    
    public void advance() {
    	lookAhead = lex();
    	while (lookAhead == AFTokenType.WHITE_SPACE.ordinal()) {
    		lookAhead = lex();
    	}
    }
    
   
}
