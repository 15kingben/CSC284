import java.math.BigInteger;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Random;
import java.util.ArrayList;



public class PatAndMat {
    public static Scanner scan = new Scanner(System.in);

    public static void main(String[] args){
      int num_problems = scan.nextInt();

      for(int i = 0; i < num_problems; i++){
        int n = scan.nextInt();
        int m = scan.nextInt();
        solve_problem(n, m);

      }
    }

    public static void solve_problem(int numcities, int numvisits){
        int[] visits = new int[numvisits + 1];
        int[] odometer = new int[numvisits + 1];
        for(int i = 0; i < visits.length; i++){
          visits[i] = scan.nextInt() - 1; // 0-index
        }
        for(int i = 0; i < visits.length; i++){
          odometer[i] = scan.nextInt(); // 0-index
        }

        int numVariables = numcities * numcities;
        int [][] constraints = new int[(numvisits) * 2][numVariables + (numvisits)* 2]; // * 2 for upper and lower bound, add that many slack variables as well
        int [] B = new int[numvisits * 2];
        // int [] C = ?????

        for(int i = 1; i < odometer.length; i++){
          int index = i-1;
          if (i != 1){
            constraints[2*(index)] = Arrays.copyOf(constraints[2*(index - 1)], constraints[0].length);
            constraints[2*index + 1] = Arrays.copyOf(constraints[2*(index - 1)], constraints[0].length);
          }
          int cityA = visits[i - 1];
          int cityB = visits[i];
          int var = cityA * numcities + cityB;
          System.out.println(cityA + " " + cityB);
          System.out.println(2*index + " " + var);
          constraints[2*index][var] += 1;
          constraints[2*index + 1][var] += 1;
          B[2*index] = odometer[i];
          B[2*index + 1] = odometer[i]+1;
        }

        int f = -1;
        for (int i = 0; i < constraints.length; i++){
          constraints[i][i+numVariables] = f;
          f *= -1;
        }


        int[][] constraints2 = constraints;

        System.out.println(numVariables + " " + numvisits);

        for(int i = 0; i < constraints2.length; i++){
          for(int j = 0; j < constraints2[0].length; j++){
            System.out.print(constraints2[i][j] + " ");
          }
          System.out.println();
        }
        System.out.println(Arrays.toString(B));

        int[] C = new int[constraints[0].length];
        for(int i = 0; i < numVariables; i++){
          C[i] = 1;
        }


        // C[0] = 1;

        // System.out.println(Arrays.toString(C));

        Simplex.simplex(constraints2, B, C);

    }
}




class Simplex{
  public static Random random = new Random();
  public static final boolean DEBUG_MODE = false;

  public static void simplex(int[][] constraints,int[] btrans, int[] ctrans){
    //phase 1
    int n = ctrans.length; int m = btrans.length;
    Rational[][] table = init2d(constraints);
    Rational[] obj = init1d(ctrans);
    Rational maxValue = new Rational(Rational.ZERO);

    //convert maximization to minimization? i think?
    for(int i = 0; i < obj.length; i++){
      // obj[i] = obj[i].mult(new Rational(-1));
    }

    Rational[] B = init1d(btrans);
    // make B positive in all entries;
    for(int i = 0; i < B.length; i++){
      if (B[i].compareTo(Rational.ZERO) < 0){
        for (int j = 0; j < table[i].length; j++){
          table[i][j] = table[i][j].mult(new Rational(-1));
        }
        B[i] = B[i].mult(new Rational(-1));
      }
    }

    Rational[][] phase1Table = new Rational[table.length][table[0].length + table.length];
    for(int i = 0; i < table.length; i++){
      for(int j = 0; j < table[0].length; j++){
        phase1Table[i][j] = table[i][j];
      }
    }
    // copy the original table and add m artificial variables in an identity matrix
    for(int i = 0; i < table.length; i++){
      for(int j = table[0].length; j < phase1Table[0].length; j++){
        phase1Table[i][j] = (i == table.length - 1 - (j - table[0].length)) ? new Rational(1) : new Rational(Rational.ZERO);
      }
    }

    // dump2d(table);
    // dump2d(phase1Table);
    // System.out.println(Arrays.toString(obj));

    // the obj is min {artificial} which ends up being this: (all zeros in artificials and sum of -non artificials elsewhere)
    Rational[] phase1Obj = new Rational[phase1Table[0].length];
    for(int j = 0; j < table[0].length; j++){
      Rational sum = new Rational(Rational.ZERO);
      for(int i = 0; i < table.length; i++){
        sum = sum.minus(phase1Table[i][j]);
      }
      // phase1Obj[j] = Rational.ZERO;
      phase1Obj[j] = sum;
    }
    for(int j = table[0].length; j < phase1Obj.length; j++){
      // phase1Obj[j] = new Rational(-1);
      phase1Obj[j] = Rational.ZERO;
    }

    // similar for max value
    Rational phase1MaxValue = new Rational(Rational.ZERO);
    for(int i = 0; i < B.length; i++){
      phase1MaxValue = phase1MaxValue.minus(B[i]);
    }

    Rational feasTest = solve_problem(phase1Table, phase1Obj, B, phase1MaxValue);
    if (feasTest == null){
      return;
    }

    // System.out.println("feasTest " + feasTest);
    if (feasTest.compareTo(Rational.ZERO) != 0){
      // System.out.println(feasTest);
      System.out.println("INFEASIBLE");
      return;
    }

    //Take feasible basic solution from part 1
    Rational[][] phase2Table = new Rational[table.length][table[0].length];
    for(int i = 0; i < table.length; i++){
      for(int j = 0; j < table[0].length; j++){
        phase2Table[i][j] = phase1Table[i][j];
      }
    }

    //phase 2
    Rational[] cb = new Rational[table.length];
    for(int i = 0; i < cb.length; i++)
      cb[i] = Rational.ZERO;
    for(int i = 0; i < table.length; i++){
      for(int j = 0; j < table[0].length; j++){
        if (phase1Table[i][j].equals(new Rational(1)) && phase1Obj[j].equals(new Rational(0))){
          cb[i] = obj[j];
        }
      }
    }


    Rational[] phase2Obj = new Rational[obj.length];
    for(int j = 0; j < phase2Obj.length; j++){
      Rational sum = Rational.ZERO;
      sum = sum.minus(obj[j]);
      for(int i = 0; i < table.length; i++){
        sum = sum.plus(cb[i].mult(phase2Table[i][j]));
      }
      phase2Obj[j] = sum;
    }

    Rational sum = Rational.ZERO;
    for(int i = 0; i < table.length; i++){
      sum = sum.plus(cb[i].mult(B[i]));
    }

    Rational solution = solve_problem(phase2Table, phase2Obj, B, sum);
    if (solution == null){
      System.out.println("UNBOUNDED");
      return;
    }

    Rational[] outputSolution = new Rational[table[0].length];
    for(int j = 0; j < table[0].length; j++){
      if (phase2Obj[j].equals(Rational.ZERO)){
        for(int i = 0; i < table.length; i++){
          if (phase2Table[i][j].equals(new Rational(1))){
            outputSolution[j] = B[i];
            break;
          }
        }
      } else {
        outputSolution[j] = Rational.ZERO;
      }
    }

    System.out.println(solution);
    for(int i = 0; i < outputSolution.length; i++)
      System.out.print(outputSolution[i] + " ");
    System.out.println();
  }

  // public static void solve_problem(int[][] constraints, int[] btrans, int[] ctrans){
  public static Rational solve_problem(Rational[][] table, Rational[] obj, Rational[] B, Rational maxValue){
    // int n = ctrans.length; int m = btrans.length;
    // Rational[][] table = init2d(constraints);
    // Rational[] obj = init1d(ctrans);
    // Rational maxValue = new Rational(Rational.ZERO);
    // for(int i = 0; i < obj.length; i++){
    //   obj[i] = obj[i].mult(new Rational(-1));
    // }
    // Rational[] B = init1d(btrans);
    if(DEBUG_MODE)
    debug_dump(table, obj, B, maxValue);

    while(true){


      int pcol = minIndex(obj);
      if(DEBUG_MODE)
      System.out.println("MINDEX " + pcol);
      if (pcol < 0){
        if(DEBUG_MODE){
        System.out.println("MaxValue: " + maxValue);
        System.out.println("Final Tableau");
        debug_dump(table, obj, B, maxValue);}
        return maxValue;
        // for(Rational r : obj){
        //
        // }
        // return;
      } else {
        if(DEBUG_MODE)
        debug_dump(table, obj, B, maxValue);
        int pivot = getPivotRow(table, B, pcol);
        // System.out.println("pivot: " + pivot + " " + pcol);
        if (pivot < 0){
          //UNBOUNDED
          // System.out.println("UNBOUNDED");
          return null;
        }
        maxValue = clear_column(table, obj, B, maxValue, pivot, pcol);
      }

    }
  }

  private static Rational clear_column(Rational[][] table, Rational[] obj, Rational[] B, Rational maxValue, int pivot, int pcol){
    Rational ratio = new Rational(1);
    ratio = ratio.divide(table[pivot][pcol]);

    for(int j = 0; j < table[0].length; j++){
      table[pivot][j] = table[pivot][j].mult(ratio);
    }

    B[pivot] = B[pivot].mult(ratio);


    for(int m = 0; m < table.length; m++){
      if (m == pivot)
        continue;
      Rational ratio2 = table[m][pcol].divide(table[pivot][pcol]).mult(new Rational(-1));
      for(int j = 0; j < table[0].length; j++){
        table[m][j] = table[m][j].plus(table[pivot][j].mult(ratio2));
      }
      B[m] = B[m].plus(B[pivot].mult(ratio2));
      assert (table[m][pcol].equals(Rational.ZERO));
    }

    Rational ratio3 = obj[pcol].divide(table[pivot][pcol]).mult(new Rational(-1));

    for(int j = 0; j < table[0].length; j++){
      obj[j] = obj[j].plus(table[pivot][j].mult(ratio3));
    }
    maxValue = maxValue.plus(B[pivot].mult(ratio3));

    return maxValue;
  }


  private static int getPivotRow(Rational[][] table, Rational[] B, int pcol){
    Rational[] poptions = B.clone();
    for (int i = 0; i < table.length; i++){
      if (table[i][pcol].compareTo(Rational.ZERO) <= 0){
        poptions[i] = new Rational(-1);
      } else
      poptions[i] = poptions[i].divide(table[i][pcol]);
    }

    Rational minRow = new Rational(-1);
    int mindex = -1;
    for(int i = 0; i < poptions.length; i++){
      if (poptions[i].compareTo(Rational.ZERO) < 0)
        continue;
      if (minRow.compareTo(Rational.ZERO) < 0){
        minRow = poptions[i];
        mindex = i;
      } else if (poptions[i].compareTo(minRow) == 0){
        if (random.nextBoolean()){
          minRow = poptions[i];
          mindex = i;
        }
      } else if(poptions[i].compareTo(minRow) < 0){
        minRow = poptions[i];
        mindex = i;
      }
    }
    return mindex;
  }

  // pick most negative entry in objective
  private static int minIndex(Rational[] row){
    int mindex = -1;
    Rational minv = new Rational(Rational.ZERO);
    for(int i = 0; i < row.length; i++){
      if (row[i].compareTo(minv) < 0){ //maxmin
        minv = row[i];
        mindex = i;
      } else if(row[i].compareTo(minv) == 0 && minv.compareTo(Rational.ZERO) != 0){
        if(random.nextBoolean()){
          minv = row[i];
          mindex = i;
        }
      }
    }
    return mindex;
  }

  private static int[][] copy2d(int[][] base){
    int [][] ret = new int[base.length][];
    for(int i = 0; i < base.length; i++)
      ret[i] = base[i].clone();
    return ret;
  }

  public static Rational[][] init2d(int[][] base){
    Rational[][] ret = new Rational[base.length][base[0].length];
    for(int i = 0; i < ret.length; i++){
      for(int j = 0; j < ret[0].length; j++){
        ret[i][j] = new Rational(base[i][j]);
      }
    }
    return ret;
  }

  public static Rational[] init1d(int[] base){
    Rational[] ret = new Rational[base.length];
    for(int i = 0; i < ret.length; i++){
      ret[i] = new Rational(base[i]);
    }
    return ret;
  }

  public static void dump2d(Object[][] base){
    for(int i = 0; i < base.length; i++){
      System.out.println(Arrays.toString(base[i]));
    }
  }

  private static void debug_dump(Rational[][] table, Rational[] obj, Rational[] B, Rational maxValue){
    System.out.println("Tableau: ");
    dump2d(table);
    System.out.println("\nobj: ");
    System.out.println(Arrays.toString(obj));
    System.out.println("\nB: ");
    System.out.println(Arrays.toString(B));
    System.out.println("\nmaxv: ");
    System.out.println(maxValue);
  }
}

class Rational implements Comparable<Rational> {
  public BigInteger denom;
  public BigInteger num;

  public final static Rational ZERO = new Rational(0);

  public Rational(int num, int denom){
      this.num = BigInteger.valueOf(num);
      this.denom = BigInteger.valueOf(denom);
      reduce();
  }

  public Rational(BigInteger num, BigInteger denom){
    this.num = num;
    this.denom = denom;
    reduce();
  }

  public Rational(Rational r){
    num = r.num;
    denom = r.denom;
    reduce();
  }

  public Rational(int num){
    this(num, 1);
  }

  public void reduce(){
     BigInteger gcd = num.gcd(denom);
     num = num.divide(gcd);
     denom = denom.divide(gcd);
     if (denom.compareTo(BigInteger.ZERO) < 0) {
         this.denom = this.denom.multiply(BigInteger.valueOf(-1));
         this.num = this.num.multiply(BigInteger.valueOf(-1));
     }
  }

  public Rational plus(Rational r){
    BigInteger retnum = this.num.multiply(r.denom).add(r.num.multiply(this.denom));
    BigInteger retdenom = this.denom.multiply(r.denom);
    return new Rational(retnum, retdenom);
  }

  public Rational mult(Rational r){
    return new Rational(this.num.multiply(r.num), this.denom.multiply(r.denom));
  }

  public Rational minus(Rational r){
    return plus(r.mult(new Rational(-1)));
  }

  public Rational recip(){
    return new Rational(this.denom, this.num);
  }

  public Rational divide(Rational r){
    return this.mult(r.recip());
  }

  public int compareTo(Rational r){
    return num.multiply(r.denom).compareTo(denom.multiply(r.num));
  }

  public boolean equals(Rational r){
    if (r == null){
      return false;
    } else {
      return this.compareTo(r) == 0;
    }
  }

  public String toString(){
    if (denom.equals(BigInteger.valueOf(1)))
      return this.num.toString();
    else
      return this.num.toString() + '/' + this.denom.toString();
  }
}
