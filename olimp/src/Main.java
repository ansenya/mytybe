import java.util.*;

public class Main {

    public static int n;
    public static int a;
    public static int b;
    public static int count;
    public static int fcount;
    public static Stack<String> values = new Stack<>();
    public static ArrayList<String> pre = new ArrayList<>();

    public static void rec(int year, int k1, int k2) {
        if (year != 0) {
            values.push(year + " " + k1 + " " + k2);
        }

        if (year > n || k1 + k2 > a + b) {
//            System.out.println(values);
            int v1 = Integer.parseInt(values.get(values.size() - 1).split(" ")[1]);
            int v2 = Integer.parseInt(values.get(values.size() - 1).split(" ")[2]);
            if (a == v1 && b == v2) {
                for (int i = 0; i < values.size() - 1; i++) {
                    pre.add((Integer.parseInt(values.get(i + 1).split(" ")[1]) - Integer.parseInt(values.get(i).split(" ")[1])) + " " + (Integer.parseInt(values.get(i + 1).split(" ")[2]) - Integer.parseInt(values.get(i).split(" ")[2])));
                }

                Map<String, Integer> countMap = new HashMap<>();

                for (String element : pre) {
                    String[] pair = element.split(" ");

                    String key = pair[0] + " " + pair[1];

                    countMap.put(key, countMap.getOrDefault(key, 0) + 1);
                }

                System.out.println(countMap);

            }

            fcount = Math.max(count, fcount);
            count = 0;
            pre.clear();
            return;
        }

        for (int i = 1; i < Math.max(a, b); i++) {
            rec(year + i, k1, k2);
            values.pop();

            rec(year + i, k1 + i, k2);
            values.pop();

            rec(year + i, k1, k2 + i);
            values.pop();

            rec(year + i, k1 + i, k2 + i);
            values.pop();
        }

    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        n = in.nextInt();
        a = in.nextInt();
        b = in.nextInt();


        rec(0, 0, 0);
        System.out.println(fcount);
    }
}