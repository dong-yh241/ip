import java.util.Scanner;

public class Cipher {
    private static final String LINE = "____________________________________________________________";
    private static final String NAME = "Cipher";
    private static final int MAX_TASKS = 100;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        String[] type = new String[MAX_TASKS];   
        String[] desc = new String[MAX_TASKS];   
        String[] by = new String[MAX_TASKS];     
        String[] from = new String[MAX_TASKS];   
        String[] to = new String[MAX_TASKS];     
        boolean[] done = new boolean[MAX_TASKS]; 

        int size = 0;

        System.out.println(LINE);
        System.out.println("Hello! I'm " + NAME);
        System.out.println("What can I do for you?");
        System.out.println(LINE);

        while (true) {
            String input = sc.nextLine().trim();

            if (input.equals("bye")) {
                System.out.println(LINE);
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(LINE);
                break;
            }

            if (input.equals("list")) {
                System.out.println(LINE);
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < size; i++) {
                    System.out.println((i + 1) + "." + formatTask(type, desc, by, from, to, done, i));
                }
                System.out.println(LINE);
                continue;
            }

            if (input.startsWith("mark ")) {
                int n = Integer.parseInt(input.substring(5).trim());
                int idx = n - 1;
                done[idx] = true;

                System.out.println(LINE);
                System.out.println("Nice! I've marked this task as done:");
                System.out.println((idx + 1) + "." + formatTask(type, desc, by, from, to, done, idx));
                System.out.println(LINE);
                continue;
            }

            if (input.startsWith("unmark ")) {
                int n = Integer.parseInt(input.substring(7).trim());
                int idx = n - 1;
                done[idx] = false;

                System.out.println(LINE);
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println((idx + 1) + "." + formatTask(type, desc, by, from, to, done, idx));
                System.out.println(LINE);
                continue;
            }

            if (input.startsWith("todo")) {
                String d = input.substring(4).trim();

                type[size] = "T";
                desc[size] = d;
                by[size] = null;
                from[size] = null;
                to[size] = null;
                done[size] = false;
                size++;

                System.out.println(LINE);
                System.out.println("Got it. I've added this task:");
                System.out.println(formatTask(type, desc, by, from, to, done, size - 1));
                System.out.println("Now you have " + size + " tasks in the list.");
                System.out.println(LINE);
                continue;
            }

            if (input.startsWith("deadline")) {
                String rest = input.substring(8).trim();
                String[] parts = rest.split("\\s+/by\\s+", 2);
                String d = parts[0].trim();
                String when = (parts.length > 1) ? parts[1].trim() : "";

                type[size] = "D";
                desc[size] = d;
                by[size] = when;
                from[size] = null;
                to[size] = null;
                done[size] = false;
                size++;

                System.out.println(LINE);
                System.out.println("Got it. I've added this task:");
                System.out.println(formatTask(type, desc, by, from, to, done, size - 1));
                System.out.println("Now you have " + size + " tasks in the list.");
                System.out.println(LINE);
                continue;
            }

            if (input.startsWith("event")) {
                String rest = input.substring(5).trim();
                String[] p1 = rest.split("\\s+/from\\s+", 2);
                String d = p1[0].trim();

                String start = "";
                String end = "";
                if (p1.length > 1) {
                    String[] p2 = p1[1].split("\\s+/to\\s+", 2);
                    start = p2[0].trim();
                    end = (p2.length > 1) ? p2[1].trim() : "";
                }

                type[size] = "E";
                desc[size] = d;
                by[size] = null;
                from[size] = start;
                to[size] = end;
                done[size] = false;
                size++;

                System.out.println(LINE);
                System.out.println("Got it. I've added this task:");
                System.out.println(formatTask(type, desc, by, from, to, done, size - 1));
                System.out.println("Now you have " + size + " tasks in the list.");
                System.out.println(LINE);
                continue;
            }

            type[size] = "T";
            desc[size] = input;
            by[size] = null;
            from[size] = null;
            to[size] = null;
            done[size] = false;
            size++;

            System.out.println(LINE);
            System.out.println("Got it. I've added this task:");
            System.out.println(formatTask(type, desc, by, from, to, done, size - 1));
            System.out.println("Now you have " + size + " tasks in the list.");
            System.out.println(LINE);
        }

        sc.close();
    }

    private static String formatTask(
            String[] type, String[] desc, String[] by, String[] from, String[] to, boolean[] done, int i) {

        String status = done[i] ? "X" : " ";
        String t = type[i];

        if ("T".equals(t)) {
            return "[T][" + status + "] " + desc[i];
        } else if ("D".equals(t)) {
            return "[D][" + status + "] " + desc[i] + " (by: " + by[i] + ")";
        } else { // "E"
            return "[E][" + status + "] " + desc[i] + " (from: " + from[i] + " to: " + to[i] + ")";
        }
    }
}
