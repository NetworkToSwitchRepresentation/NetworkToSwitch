import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class run_experiments {

    static Random rnd;


    static double[][] sumAnswersAdd = new double[11][6];
    static double[][] sumAnswerSizesAdd = new double[11][6];

    public static void testAdd(int cin, int cout, int k, int n, int no_spin) {
        for (int it = 0; it < 100; it++) {
            int[] ins = getCapacity(no_spin*cin, no_spin*k);
            int[] outs = getCapacity(no_spin*cout, no_spin*n);
            int[][] matr = generatePairs(k, n);
            int[] must = calculateMust(ins, matr, no_spin);
            //System.err.println(Arrays.toString(must));
            int[] diff = calculateDiff(outs, must);
            Integer[] indexes = getSortedIdices(diff);
            for (int cap = 0; cap <= 30*no_spin; cap += 3*no_spin) {
                int left = cap;
                int cnt = 0;
                int cntSize = 0;
                for (int i: indexes) {
                    if (left >= diff[i]) {
                        left -= Math.max(0, diff[i]);
                        cnt++;
                    }
                    else {
                        cntSize += outs[i];
                    }
                }
                sumAnswerSizesAdd[cap / (3 * no_spin)][(cout - 150)/10] += cntSize;
                sumAnswersAdd[cap / (3 * no_spin)][(cout - 150) / 10] += cnt;
            }
        }
    }



    static double[][] sumAnswersRemove = new double[11][6];
    static double[][] sumAnswerSizesRemove = new double[11][6];

    public static void testRemove(int cin, int cout, int k, int n, int no_spin) {
        for (int it = 0; it < 100; it++) {
            int[] insa = getCapacity(no_spin*cin, no_spin * k);
            int[] outsa = getCapacity(no_spin*cout, no_spin * n);
            int[][] matr = generatePairs(k, n);
            int[] musta = calculateMust(insa, matr, no_spin);
            int[] diffa = calculateDiff(outsa, musta);
            for (int cap = 0; cap <= 10*no_spin; cap+=no_spin) {
                int[] ins = insa.clone();
                int[] outs = outsa.clone();
                int[] diff = diffa.clone();

                for (int icap = 0; icap < cap; icap++) {
                    int minnum = -1;
                    for (int j = 0; j < diff.length; j++) {
                        if (diff[j] > 0  &&  (minnum == -1 || diff[j] < diff[minnum])) {
                            minnum = j;
                        }
                    }

                    if (minnum == -1) {
                        break;
                    }

                    int toDecrease = -1;
                    int bestQQ = 0;

                    int minnum_v = minnum % n;

                    int spin_id = minnum / n;

                    for (int i= 0; i < k; i++) {
                        if (matr[i][minnum_v] == 0 || ins[i+spin_id*k] <= 1) {
                            continue;
                        }
                        int costQQ = 0;
                        for (int j = 0; j < n; j++) {
                            if (matr[i][j] == 1 && diff[j+spin_id*n] > 0) {
                                costQQ++;
                            }
                        }
                        if (costQQ > bestQQ) {
                            bestQQ = costQQ;
                            toDecrease = i;
                        }

                    }
                    ins[toDecrease+spin_id*k]--;
                    for (int j = 0; j < n; j++) {
                        if (matr[toDecrease][j] == 1) {
                            diff[j+spin_id*n]--;
                        }
                    }
                }

                for (int i = 0; i < diff.length; i++) {
                    if (diff[i] > 0) {
                        sumAnswerSizesRemove[cap/no_spin][(cout - 150) / 10] += outs[i];
                    } else {
                        sumAnswersRemove[cap/no_spin][(cout-150)/10]++;
                    }

                }

            }
        }
    }


    private static int required(int x, int[] insa, int[] outsa, int[] diffa, int[][] matr, int n) {
        int[] ins = insa.clone();
        int[] outs = outsa.clone();
        int[] diff = diffa.clone();
        for (int icap = 0; icap < x; icap++) {
            int toDecrease = -1;
            int bestQQ = -1;

            for (int i= 0; i < ins.length; i++) {
                if (ins[i] <= 1) {
                    continue;
                }
                int costQQ = 0;
                for (int j = 0; j < outs.length; j++) {
                    if (matr[i%n][j%n] == 1 && (i / n == j / n) && diff[j] > 0) {
                        costQQ++;
                    }
                }
                if (costQQ > bestQQ) {
                    bestQQ = costQQ;
                    toDecrease = i;
                }

            }
            ins[toDecrease]--;
            for (int j = 0; j < outs.length; j++) {
                if (matr[toDecrease%n][j%n] == 1 && (toDecrease / n == j / n)) {
                    diff[j]--;
                }
            }
        }
        int sum = 0;
        for (int i = 0; i < diff.length; i++) {
            if (diff[i] > 0) {
                sum += diff[i];
            }
        }

        return sum;
    }



    static double[][] sumDeltas = new double[11][6];

    public static void testAddRemove(int cin, int cout, int k, int n, int no_spin) {
        for (int it = 0; it < 100; it++) {
            int[] insa = getCapacity(no_spin*cin, no_spin * k);
            int[] outsa = getCapacity(no_spin*cout, no_spin * n);
            int[][] matr = generatePairs(k, n);
            int[] musta = calculateMust(insa, matr, no_spin);
            int[] diffa = calculateDiff(outsa, musta);
            for (int cap = 0; cap <= 30*no_spin; cap+=3*no_spin) {
                int l = -1;
                int r = cin-insa.length;
                while (l < r-1) {
                    int x = (l+r)/2;
                    if (required(x, insa, outsa, diffa, matr, n) > cap) {
                        l = x;
                    } else {
                        r = x;
                    }
                }
                sumDeltas[cap / 3 / no_spin][(cout-150)/10] += r;
            }
        }
    }

    private static Integer[] getSortedIdices(int[] diff) {
        Integer[] arr = new Integer[diff.length];
        for (int i = 0; i < diff.length; i++) {
            arr[i] = i;
        }
        Arrays.sort(arr, Comparator.comparingInt(o -> diff[o]));
        return arr;
    }

    private static int[] calculateDiff(int[] outs, int[] must) {
        int[] ans = new int[outs.length];
        for (int i = 0; i < outs.length; i++) {
            ans[i] =  must[i]-outs[i];
        }
        return ans;
    }

    private static int[] calculateMust(int[] ins, int[][] matr, int no_spin) {
        int[] ans = new int[matr[0].length*no_spin];
        for (int sp = 0; sp < no_spin; sp++) {
            for (int i = 0; i < matr[0].length; i++) {
                for (int j = 0; j < matr.length; j++) {
                    ans[i+sp*matr[0].length] += ins[j+sp*matr.length] * matr[j][i];
                }
            }
        }
        return ans;
    }

    private static int[][] generatePairs(int k, int n) {
        int[][] ans = new int[k][n];
        for (int i = 0; i < n; i++) {
            for (int id = 0; id < 2; id++) {
                int t = rnd.nextInt(k);
                while (t == i || ans[t][i] == 1) {
                    t = rnd.nextInt(k);
                }
                ans[t][i] = 1;
            }
        }
        return ans;
    }

    private static int[] getCapacity(int cin, int k) {
        int[] ans = new int[k];
        for (int i = 0; i < cin; i++) {
            if (i < ans.length) {
                ans[i]++;
            } else {
                ans[rnd.nextInt(k)]++;
            }
        }
        return ans;
    }

    public static void main(String[] args) {
        rnd = new Random(239);
        testAdd(100, 200, 10, 10,2);
        testAdd(100, 190, 10, 10,2);
        testAdd(100, 180, 10, 10,2);
        testAdd(100, 170, 10, 10,2);
        testAdd(100, 160, 10, 10,2);
        testAdd(100, 150, 10, 10,2);
        System.out.println();
        for (int i = 0; i <  sumAnswersAdd.length; i++) {
            System.out.print(i*6+",");
            for (int j = 0; j < sumAnswersAdd[i].length; j++) {
                System.out.printf("\t%.2f,", sumAnswersAdd[i][j]*0.01);
            }
            System.out.println();
        }
        System.out.println();


        for (int i = 0; i <  sumAnswersAdd.length; i++) {
            System.out.print(i*6+",");
            for (int j = 0; j < sumAnswersAdd[i].length; j++) {
                System.out.printf("\t%.2f,", sumAnswerSizesAdd[i][j]*0.01/(150+j*10.0)/2);
            }
            System.out.println();
        }
        System.out.println();

        rnd = new Random(239);
        testRemove(100, 200, 10, 10,2);
        testRemove(100, 190, 10, 10, 2);
        testRemove(100, 180, 10, 10,2);
        testRemove(100, 170, 10, 10,2);
        testRemove(100, 160, 10, 10,2);
        testRemove(100, 150, 10, 10,2);
        System.out.println();


        for (int i = 0; i <  sumAnswersRemove.length; i++) {
            System.out.print(i*2+",");
            for (int j = 0; j < sumAnswersRemove[i].length; j++) {
                System.out.printf("\t%.2f,", sumAnswersRemove[i][j]*0.01);
            }
            System.out.println();
        }
        System.out.println();


        for (int i = 0; i <  sumAnswersRemove.length; i++) {
            System.out.print(i*2+",");
            for (int j = 0; j < sumAnswersRemove[i].length; j++) {
                System.out.printf("\t%.2f,", sumAnswerSizesRemove[i][j]*0.01/(150+j*10.0)/2);
            }
            System.out.println();
        }
        System.out.println();



        rnd = new Random(239);
        testAddRemove(100, 200, 10, 10, 2);
        testAddRemove(100, 190, 10, 10, 2);
        testAddRemove(100, 180, 10, 10, 2);
        testAddRemove(100, 170, 10, 10, 2);
        testAddRemove(100, 160, 10, 10, 2);
        testAddRemove(100, 150, 10, 10, 2);

        for (int i = 0; i <  sumAnswersRemove.length; i++) {
            System.out.print(6*i+",");
            for (int j = 0; j < sumAnswersRemove[i].length; j++) {
                System.out.printf("\t%.2f,", sumDeltas[i][j]*0.01);
            }
            System.out.println();
        }
        System.out.println();

    }

}
