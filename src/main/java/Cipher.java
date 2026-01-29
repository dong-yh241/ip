import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.Scanner;

public class Cipher {
    private static final String LINE = "____________________________________________________________";
    private static final String NAME = "Cipher";
    private static final int MAX_TASKS = 100;
    private static final Path DATA_PATH = Paths.get("data", "cipher.txt"); // relative + OS-independent

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        String[] type = new String[MAX_TASKS];   // "T", "D", "E"
        String[] desc = new String[MAX_TASKS];
        String[] by = new String[MAX_TASKS];
        String[] from = new String[MAX_TASKS];
        String[] to = new String[MAX_TASKS];
        boolean[] done = new boolean[MAX_TASKS];
        int size = 0;

        // Load existing tasks (Level-7)
        size = loadTasks(type, desc, by, from, to, done, size);

        // Welcome
        System.out.println(LINE);
        System.out.println("Hello! I'm " + NAME);
        System.out.println("What can I do for you?");
        System.out.println(LINE);

        while (true) {
            String input = sc.nextLine().trim();

            try {
                if (input.equals("bye")) {
                    printBlock("Bye. Hope to see you again soon!");
                    break;
                }

                if (input.isEmpty()) {
                    throw new IllegalArgumentException("Please type a command.");
                }

                // LIST
                if (input.equals("list")) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Here are the tasks in your list:\n");
                    if (size == 0) {
                        sb.append("(Empty)");
                    } else {
                        for (int i = 0; i < size; i++) {
                            sb.append(i + 1).append(".")
                              .append(formatTask(type, desc, by, from, to, done, i))
                              .append("\n");
                        }
                    }
                    printBlock(sb.toString().trim());
                    continue;
                }

                // MARK
                if (input.startsWith("mark ")) {
                    int idx = parseIndex(input.substring(5).trim(), size) - 1;
                    done[idx] = true;

                    saveTasks(type, desc, by, from, to, done, size); // Level-7 save

                    String msg = "Nice! I've marked this task as done:\n"
                            + (idx + 1) + "." + formatTask(type, desc, by, from, to, done, idx);
                    printBlock(msg);
                    continue;
                }

                // UNMARK
                if (input.startsWith("unmark ")) {
                    int idx = parseIndex(input.substring(7).trim(), size) - 1;
                    done[idx] = false;

                    saveTasks(type, desc, by, from, to, done, size); // Level-7 save

                    String msg = "OK, I've marked this task as not done yet:\n"
                            + (idx + 1) + "." + formatTask(type, desc, by, from, to, done, idx);
                    printBlock(msg);
                    continue;
                }

                // DELETE
                if (input.startsWith("delete ")) {
                    int idx = parseIndex(input.substring(7).trim(), size) - 1;
                    String removed = formatTask(type, desc, by, from, to, done, idx);

                    for (int i = idx; i < size - 1; i++) {
                        type[i] = type[i + 1];
                        desc[i] = desc[i + 1];
                        by[i] = by[i + 1];
                        from[i] = from[i + 1];
                        to[i] = to[i + 1];
                        done[i] = done[i + 1];
                    }

                    type[size - 1] = null;
                    desc[size - 1] = null;
                    by[size - 1] = null;
                    from[size - 1] = null;
                    to[size - 1] = null;
                    done[size - 1] = false;

                    size--;

                    saveTasks(type, desc, by, from, to, done, size); // Level-7 save

                    String msg = "Noted. I've removed this task:\n"
                            + removed
                            + "\nNow you have " + size + " tasks in the list.";
                    printBlock(msg);
                    continue;
                }

                // TODO
                if (input.startsWith("todo")) {
                    String d = input.substring(4).trim();
                    if (d.isEmpty()) {
                        throw new IllegalArgumentException("The description of a todo cannot be empty.");
                    }
                    size = addTask("T", d, null, null, null, type, desc, by, from, to, done, size);
                    saveTasks(type, desc, by, from, to, done, size); // Level-7 save
                    continue;
                }

                // DEADLINE
                if (input.startsWith("deadline")) {
                    String rest = input.substring(8).trim();
                    if (rest.isEmpty()) {
                        throw new IllegalArgumentException("The description of a deadline cannot be empty.");
                    }

                    String[] parts = rest.split("\\s+/by\\s+", 2);
                    if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
                        throw new IllegalArgumentException("Use: deadline <description> /by <when>");
                    }

                    size = addTask("D", parts[0].trim(), parts[1].trim(), null, null,
                            type, desc, by, from, to, done, size);
                    saveTasks(type, desc, by, from, to, done, size); // Level-7 save
                    continue;
                }

                // EVENT
                if (input.startsWith("event")) {
                    String rest = input.substring(5).trim();
                    if (rest.isEmpty()) {
                        throw new IllegalArgumentException("The description of an event cannot be empty.");
                    }

                    String[] p1 = rest.split("\\s+/from\\s+", 2);
                    if (p1.length < 2 || p1[0].trim().isEmpty() || p1[1].trim().isEmpty()) {
                        throw new IllegalArgumentException("Use: event <description> /from <start> /to <end>");
                    }
                    String d = p1[0].trim();

                    String[] p2 = p1[1].split("\\s+/to\\s+", 2);
                    if (p2.length < 2 || p2[0].trim().isEmpty() || p2[1].trim().isEmpty()) {
                        throw new IllegalArgumentException("Use: event <description> /from <start> /to <end>");
                    }
                    String start = p2[0].trim();
                    String end = p2[1].trim();

                    size = addTask("E", d, null, start, end, type, desc, by, from, to, done, size);
                    saveTasks(type, desc, by, from, to, done, size); // Level-7 save
                    continue;
                }

                throw new IllegalArgumentException("I'm sorry, but I don't know what that means :-(");

            } catch (IllegalArgumentException e) {
                System.out.println(LINE);
                System.out.println("OOPS!!! " + e.getMessage());
                System.out.println(LINE);
            }
        }

        sc.close();
    }

    // ---------- Level-7: Storage (load/save) ----------

    private static int loadTasks(String[] type, String[] desc, String[] by, String[] from, String[] to,
                                 boolean[] done, int size) {
        // If file doesn't exist at start, that's OK.
        if (!Files.exists(DATA_PATH)) {
            return size;
        }

        try (BufferedReader br = Files.newBufferedReader(DATA_PATH)) {
            String line;
            while ((line = br.readLine()) != null && size < MAX_TASKS) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                // Accept both "T | 1 | read book" and "T|1|read book"
                String[] parts = line.split("\\s*\\|\\s*");
                if (parts.length < 3) {
                    // corrupted line: skip
                    continue;
                }

                String t = parts[0].trim();
                String doneFlag = parts[1].trim();
                String d = parts[2].trim();

                boolean isDone = "1".equals(doneFlag);

                if ("T".equals(t)) {
                    type[size] = "T";
                    desc[size] = d;
                    done[size] = isDone;
                    size++;
                } else if ("D".equals(t)) {
                    if (parts.length < 4) {
                        continue; // corrupted line
                    }
                    type[size] = "D";
                    desc[size] = d;
                    by[size] = parts[3].trim();
                    done[size] = isDone;
                    size++;
                } else if ("E".equals(t)) {
                    if (parts.length < 5) {
                        continue; // corrupted line
                    }
                    type[size] = "E";
                    desc[size] = d;
                    from[size] = parts[3].trim();
                    to[size] = parts[4].trim();
                    done[size] = isDone;
                    size++;
                } else {
                    // unknown type: skip
                }
            }
        } catch (IOException e) {
            // If reading fails, don't crash; just start with empty list.
            // (No extra printing to keep outputs stable for earlier levels.)
            return 0;
        }

        return size;
    }

    private static void saveTasks(String[] type, String[] desc, String[] by, String[] from, String[] to,
                                  boolean[] done, int size) {
        try {
            // Ensure folder exists (e.g., ./data/)
            Path parent = DATA_PATH.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            try (BufferedWriter bw = Files.newBufferedWriter(DATA_PATH,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

                for (int i = 0; i < size; i++) {
                    bw.write(serializeTask(type, desc, by, from, to, done, i));
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            // Don't crash the chatbot if saving fails.
        }
    }

    private static String serializeTask(String[] type, String[] desc, String[] by, String[] from, String[] to,
                                        boolean[] done, int i) {
        int doneFlag = done[i] ? 1 : 0;
        if ("T".equals(type[i])) {
            return "T | " + doneFlag + " | " + desc[i];
        } else if ("D".equals(type[i])) {
            return "D | " + doneFlag + " | " + desc[i] + " | " + by[i];
        } else { // "E"
            return "E | " + doneFlag + " | " + desc[i] + " | " + from[i] + " | " + to[i];
        }
    }

    // ---------- Existing helpers ----------

    private static int addTask(
            String t, String d, String deadlineBy, String eventFrom, String eventTo,
            String[] type, String[] desc, String[] by, String[] from, String[] to, boolean[] done, int size) {

        type[size] = t;
        desc[size] = d;
        by[size] = deadlineBy;
        from[size] = eventFrom;
        to[size] = eventTo;
        done[size] = false;

        String msg = "Got it. I've added this task:\n"
                + formatTask(type, desc, by, from, to, done, size)
                + "\nNow you have " + (size + 1) + " tasks in the list.";
        printBlock(msg);

        return size + 1;
    }

    private static int parseIndex(String raw, int size) {
        if (raw.isEmpty()) {
            throw new IllegalArgumentException("Please provide a task number.");
        }
        int n;
        try {
            n = Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Task number must be an integer.");
        }
        if (n < 1 || n > size) {
            throw new IllegalArgumentException("Task number is out of range.");
        }
        return n;
    }

    private static void printBlock(String msg) {
        System.out.println(LINE);
        System.out.println(msg);
        System.out.println(LINE);
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
