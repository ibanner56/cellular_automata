import edu.rit.pj2.Loop;
import edu.rit.pj2.Task;
import edu.rit.pj2.vbl.IntVbl;

/**
 * Runs a Cellular Automaton for a while.
 * Created by Isaac on 4/6/2015.
 */
public class CA extends Task{
    public int[] rule;
    public int n;
    public int s;

    public int[] currentCell;
    public int[] nextCell;

    public IntVbl popcount;
    public int min_popcount;
    public int min_step;
    public int max_popcount;
    public int max_step;

    @Override
    public void main(String[] args) throws Exception {
        if(args.length < 3) {
            System.out.println("Usage: java pj2 CA <rule> <N> <S> <index> ...");
            return;
        }
        rule = new int[args[0].length()];
        String[] sRule = args[0].split("");
        for(int i = 0; i < sRule.length; i++) rule[i] = Integer.parseInt(sRule[i]);
        n = Integer.parseInt(args[1]);
        s = Integer.parseInt(args[2]);

        popcount = new IntVbl.Sum(0);
        currentCell = new int[n];
        nextCell = new int[n];
        for(int i = 3; i < args.length; i++) {
            currentCell[Integer.parseInt(args[i])] = 1;
            popcount.item += 1;
        }

        min_popcount = popcount.item;
        min_step = 0;
        max_popcount = popcount.item;
        max_step = 0;

        for(int step = 1; step <= s; step++) {
            popcount.item = 0;
            parallelFor(0, n - 1).exec(new Loop() {
                public IntVbl internalPopC;
                public void start() {
                    internalPopC = threadLocal(popcount);
                }
                public void run(int i) {
                    int[] neighborhood = new int[3];
                    neighborhood[0] = currentCell[(i - 1 + n) % n];
                    neighborhood[1] = currentCell[i];
                    neighborhood[2] = currentCell[(i + 1) % n];
                    // Ghetto binary calculation.
                    int ruleIndex = neighborhood[0] * 4
                            + neighborhood[1] * 2 + neighborhood[2];
                    nextCell[i] = rule[ruleIndex];
                    internalPopC.item += nextCell[i];
                }
            });

            // Check if we cleared either extreme.
            if(popcount.item < min_popcount) {
                min_popcount = popcount.item;
                min_step = step;
            }
            if(popcount.item > max_popcount) {
                max_popcount = popcount.item;
                max_step = step;
            }

            int[] swap = currentCell;
            currentCell = nextCell;
            nextCell = swap;
        }

        System.out.println("Smallest popcount: " + min_popcount + " at step " + min_step);
        System.out.println("Largest popcount: " + max_popcount + " at step " + max_step);
        System.out.println("Final popcount: " + popcount.item + " at step " + s);
    }
}
