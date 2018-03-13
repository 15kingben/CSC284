import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Arrays;
import java.util.HashMap;



public class Palindrome {
  static ArrayList<String> words = new ArrayList<String>();
  static HashSet<Integer> forwardBoundaries = new HashSet<Integer>();
  static HashSet<Integer> backwardBoundaries = new HashSet<Integer>();
  static String largeWord = "";
  static String reverseWord = "";
  // static HashMap<DPWrapper, Integer> T;// = new HashMap<>();
  // static HashMap<DPWrapper, DPWrapper> BackPointer;// = new HashMap<>();
  static int[][][][] T;
  static DPWrapper[][][][] BackPointer;
  static boolean debugMode = false;

  public static void main(String[] args){
    Scanner scan = new Scanner(System.in);


    int num_words = scan.nextInt();


    for(int i = 0; i < num_words; i++){
      String word = scan.next();
      words.add(word);
      largeWord = largeWord.concat(word);
      reverseWord = reverseWord.concat(reverse(word));

      forwardBoundaries.add(largeWord.length());
      backwardBoundaries.add(largeWord.length() - 1);
    }

    int N = largeWord.length();

    T = new int[N][N][2][2];
    BackPointer = new DPWrapper[N][N][2][2];
    for(int d1 = 0; d1 < N; d1++){
      for(int d2 = 0; d2 < N; d2++){
        for(int d3 = 0; d3 < 2; d3++){
          for(int d4 = 0; d4 < 2; d4++){
            T[d1][d2][d3][d4] = -1;
          }
        }
      }
    }

    // T = new HashMap<>(largeWord.length() * largeWord.length() * 4);
    // BackPointer = new HashMap<>(largeWord.length() * largeWord.length() * 4);


    int i = 0; int j = largeWord.length() - 1;
    solveDP(i, j, false, false);
    solveDP(i, j, true, true);

    // if(words.size() > 1){
      solveDP(i, j, false, true);
      solveDP(i, j, true, false);
    // }

    int maxlen = -1;
    boolean maxfi = false;
    boolean maxfj = false;
    if (T[i][j][0][0] > maxlen){
      maxlen = T[i][j][0][0];
      maxfi = false;
      maxfj = false;
    }
    if (T[i][j][0][1] > maxlen){
      maxlen = T[i][j][0][1];
      maxfi = false;
      maxfj = true;
    }
    if (T[i][j][1][0] > maxlen){
      maxlen = T[i][j][1][0];
      maxfi = true;
      maxfj = false;
    }
    if (T[i][j][1][1] > maxlen){
      maxlen = T[i][j][1][1];
      maxfi = true;
      maxfj = true;
    }


    String frontHalf = "";
    String endHalf = "";
    ArrayList<Boolean> frontFlips = new ArrayList<>();
    ArrayList<Boolean> backFlips = new ArrayList<>();
    frontFlips.add(maxfi);
    backFlips.add(maxfj);

    // used so we only mark new word flips once
    int stateI = 0;
    int stateJ = j;


    if(debugMode){
      System.out.println(Arrays.toString(forwardBoundaries.toArray()));
      System.out.println(Arrays.toString(backwardBoundaries.toArray()));
    }

    DPWrapper DP = new DPWrapper(i,j,maxfi, maxfj);
    while(DP != null){
      if (debugMode)
      System.out.println(DP);
      DPWrapper nextDP = BackPointer[DP.i][DP.j][ DP.flipI ? 1 : 0][ DP.flipJ ? 1 : 0];
      // System.out.println(nextDP);
      if (nextDP == null){
        if (DP.i == DP.j){
          frontHalf = frontHalf.concat( (!DP.flipI) ? largeWord.substring(DP.i, DP.i+1) : reverseWord.substring(DP.i, DP.i+1) );

          if (forwardBoundaries.contains(DP.i) && backwardBoundaries.contains(DP.j)){
            frontFlips.add(DP.flipJ);
          }
        }
        break;
      }
      if (DP.i != nextDP.i && DP.j != nextDP.j){

        frontHalf = frontHalf.concat( (!DP.flipI) ? largeWord.substring(DP.i, DP.i + 1) : reverseWord.substring(DP.i, DP.i + 1) );
        endHalf = endHalf.concat( (!DP.flipJ) ? largeWord.substring(DP.j, DP.j + 1) : reverseWord.substring(DP.j, DP.j + 1) );
        // stateI = -1;
        // stateJ = -1;
        // if (nextDP.i > nextDP.j)
        //   break;

      }


      if (!(DP.i == 0 && DP.j == j)){
        if (forwardBoundaries.contains(DP.i) && DP.i != 0 && stateI != DP.i){
          frontFlips.add(DP.flipI);
          stateI = DP.i;
          if(debugMode)
          System.out.println("f");

        }
        if (backwardBoundaries.contains(DP.j) && DP.j != j && stateJ != DP.j){
          backFlips.add(DP.flipJ);
          stateJ = DP.j;
          if(debugMode)
          System.out.println("b");
        }
        }


      DP = nextDP;

    }
    // System.out.println(maxfi + " " + maxfj);

    System.out.println(maxlen);

    if(debugMode){
      System.out.println(Arrays.toString(frontFlips.toArray()));
      System.out.println(Arrays.toString(backFlips.toArray()));
    }
    if(frontFlips.size() + backFlips.size() > words.size()){
      frontFlips.remove(frontFlips.size() - 1);
      // System.out.println("poop");
    }



    ArrayList<String> wl = new ArrayList<>();

    int l = 0;
    for(l = 0; l < frontFlips.size(); l++){
      wl.add(!frontFlips.get(l) ? words.get(l) : reverse(words.get(l)) );
    }

    int k = backFlips.size() - 1;
    for(; k >= 0; k--){
      wl.add( !backFlips.get(k) ? words.get(l + (backFlips.size() - 1 - k)) : reverse(words.get(l + backFlips.size() - 1 - k)) );
    }

    System.out.print(wl.get(0));
    for(int p = 1; p < wl.size(); p++){
      System.out.print(" " + wl.get(p));
    }

    System.out.println();
    System.out.println(frontHalf + "" + reverse(endHalf));

    // System.out.println(largeWord.length() * largeWord.length());
    // System.out.println(T.size());

  }

  public static void solveDP(int i, int j, boolean flipI, boolean flipJ){
    // System.out.println(i + " " + j + " " + flipI + " " + flipJ);
    int flipI_ = flipI ? 1 : 0;
    int flipJ_ = flipJ ? 1 : 0;
    if (i == j){
      // T.put(new DPWrapper(i, j, flipI, flipJ), (flipI == flipJ) ? 1 : -10000000);
      T[i][j][flipI_][flipJ_] = (flipI == flipJ) ? 1 : -10000000;
      return;
    } else if(i > j){
      // T.put(new DPWrapper(i, j, flipI, flipJ), (flipI == flipJ) ? 0 : -10000000);
      T[i][j][flipI_][flipJ_] = (flipI == flipJ) ? 0 : -10000000;
      return;
    } else {
      char charI = (!flipI) ? largeWord.charAt(i) : reverseWord.charAt(i);
      char charJ = (!flipJ) ? largeWord.charAt(j) : reverseWord.charAt(j);

      // possible flips of next word if at boundary
      ArrayList<Boolean> nextFlipsI = new ArrayList<>();
      ArrayList<Boolean> nextFlipsJ = new ArrayList<>();
      nextFlipsI.add(flipI); nextFlipsJ.add(flipJ);
      if (forwardBoundaries.contains(i + 1))
        nextFlipsI.add(!flipI);
      if (backwardBoundaries.contains(j - 1))
        nextFlipsJ.add(!flipJ);

        int maxLength = -1000000000;
        DPWrapper maxBP = null;


        for(boolean fi : nextFlipsI){
          for(boolean fj : nextFlipsJ){
            int fi_ = fi ? 1 : 0;
            int fj_ = fj ? 1 : 0;
            DPWrapper DP = new DPWrapper(i+1, j-1, fi, fj);
            if (charI == charJ){
              // if(!T.containsKey(DP)){
              if(T[i+1][j-1][fi_][fj_] == -1){
                solveDP(i+1, j-1, fi, fj);
              }

              if(2 + T[i+1][j-1][fi_][fj_] > maxLength){ //
                maxLength = T[i+1][j-1][fi_][fj_] + 2;  //
                maxBP = DP;
              }
            } else {
              DP = new DPWrapper(i+1, j, fi, flipJ);
              if(T[i+1][j][fi_][flipJ_] == -1){
                solveDP(i+1, j, fi, flipJ);
              }

              if(T[i+1][j][fi_][flipJ_] > maxLength){
                maxLength = T[i+1][j][fi_][flipJ_];
                maxBP = DP;
              }
              ///
              DP = new DPWrapper(i, j-1, flipI, fj);
              if(T[i][j-1][flipI_][fj_] == -1){
                solveDP(i, j-1, flipI, fj);
              }

              if(T[i][j-1][flipI_][fj_] > maxLength){
                maxLength = T[i][j-1][flipI_][fj_];
                maxBP = DP;
              }
            }


          }
        }

        assert (maxBP != null);
        // DPWrapper key = new DPWrapper(i, j, flipI, flipJ);
        // T.put(key, maxLength);
        T[i][j][flipI_][flipJ_] = maxLength;
        // BackPointer.put(key, maxBP);
        BackPointer[i][j][flipI_][flipJ_] = maxBP;


      return;
    }

  }

  public static String reverse(String s){
    char[] c = s.toCharArray();
    for (int left=0, right = c.length - 1; left < right ; left++ ,right--) {
            char tmp = c[left];
            c[left] = c[right];
            c[right]=tmp;
        }
    return new String(c);
  }
}


class DPWrapper {
  public int i; public int j; public boolean flipI; public boolean flipJ;
  public DPWrapper(int i, int j, boolean flipI, boolean flipJ){
    this.i = i; this.j = j; this.flipI = flipI; this.flipJ = flipJ;
  }

  @Override
  public boolean equals(Object o){
    if (o == null)
            return false;
    if (getClass() != o.getClass())
        return false;
    DPWrapper o2 = (DPWrapper) o;
    return (i == o2.i) && (j == o2.j) && (flipI == o2.flipI) && (flipJ == o2.flipJ);
  }

  // Effective Java
  @Override
  public int hashCode(){
    int hc = 17;
    hc = 37*hc + i;
    hc = 37*hc + j;
    hc = 37*hc + (flipI ? 1 : 0);
    hc = 37*hc + (flipJ ? 1 : 0);
    return hc;
  }

  @Override
  public String toString(){
    return i + " " + j + " " + flipI + " " + flipJ;
  }
}
