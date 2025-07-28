package Validators;

public class HashingFunction {
    private static final int P = 137;
    private static final int MOD = 1_000_000_007;
    private static final int N = 100_005;
    private static final int[] pw = new int[N];

    static {

        pw[0] = 1;
        for (int i = 1; i < N; i++) {
            pw[i] = (int)((1L * pw[i - 1] * P) % MOD);
        }
    }


    public synchronized static int getHash(String s) {
        int hash = 0;
        for (int i = 0; i < s.length(); i++) {
            hash = (int)((hash + 1L * s.charAt(i) * pw[i]) % MOD);
        }
        return hash;
    }
}
