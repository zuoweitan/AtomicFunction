package atomic.fun;

public enum AFTokenType {

    //non-termals

    STMT, EXPR, TERM, OP,



    //terminals
    NUM, EOI, LP, RP ,DOT, PLUS, MINUS, VLINE, WHITE_SPACE, UNKNOWN_TOKEN;



    public static final int FIRST_TERMINAL_INDEX = NUM.ordinal();
    public static  final int LAST_TERMINAL_INDEX = UNKNOWN_TOKEN.ordinal();

    public static final int FIRST_NON_TERMINAL_INDEX = STMT.ordinal();
    public static final int LAST_NON_TERMINAL_INDEX = OP.ordinal();

    public static String getSymbolStr(int val) {
        return AFTokenType.values()[val].toString();
    }

    public static boolean isTerminal(int val) {
        if (FIRST_TERMINAL_INDEX <= val && val <= LAST_TERMINAL_INDEX) {
            return true;
        }

        return false;
    }
}
