/**
 * An implementation of ExpansionCodeBookInterface using an array.
 */

public class ArrayCodeBook implements ExpansionCodeBookInterface {
    private static final int R = 256;        // alphabet size
    private String[] codebook;
    private int W;       // current codeword width
    private int minW;    // minimum codeword width
    private int maxW;    // maximum codeword width
    private int L;       // maximum number of codewords with 
                         // current codeword width (L = 2^W)
    private int code;    // next available codeword value
    private int change;
    public ArrayCodeBook(int minW, int maxW,boolean flushIfFull){
        this.maxW = maxW;
        this.minW = minW;
        change=0;//if change is greater than 1 in initialize, it doesnt flushIfFull; otherwise flushIfFull;
        if(!flushIfFull) change=1;
        initialize(change);
    }
    public int size(){
        return code;
    }


    public int getCodewordWidth(boolean flushIfFull){    
        if(code>=L&&W<16) return W+1;
        else if(code>=L&&W<16&&flushIfFull) return 9;
        return W;
    }
    
    private void initialize(int change){
        if(change==2) return;
        if(change==1) change++;
        codebook = new String[1 << maxW];
        W = minW;
        L = 1<<W;
        code = 0;
        // initialize symbol table with all 1-character strings
        for (int i = 0; i < R; i++)
            add("" + (char) i, false);
        add("", false); //R is codeword for EOF
    }

    public void add(String str, boolean flushIfFull) {
        boolean haveRoom = false;
        if(code < L){
            haveRoom = true;
        }
        else{
            if(W<maxW){
              W++;
              L=1<<W;
              haveRoom=true;
            }
          }
        if(!haveRoom&&change==0) initialize(change);
        if(haveRoom){
            codebook[code] = str;
            code++;
        }
    }

    public String getString(int codeword) {
        return codebook[codeword];
    }
    
}