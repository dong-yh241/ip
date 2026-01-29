import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Scanner;

public class Cipher {
    private static final String LINE = "____________________________________________________________";
    private static final String NAME = "Cipher";
    private static final int MAX_TASKS = 100;

    private static final Path DATA_PATH = Paths.get("data", "cipher.txt");

    private static final DateTimeFormatter INPUT_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter INPUT_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    private static final DateTimeFormatter OUT_DATE =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter OUT_DATE_TIME =
            DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm", Locale.ENGLISH);

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        String[] type = new String[MAX_TASKS]; 
        String[] desc = new String[MAX_TASKS];
        boolean[] done = new boolean[MAX_TASKS];

        LocalDateTime[] byDateTime = new LocalDateTime[MAX_TASKS];     
        LocalDateTime[] fromDateTime = new LocalDateTime[MAX_TASKS];   
        LocalDateTime[] toDateTime = new LocalDateTime[MAX_TASKS];    

        int size = 0;
        size = loadTasks(type, desc, done, byDateTime, fromDateTime, toDateTime, size);

        System.out.println(LINE);
        System.out.println("Hello! I'm " + NAME);
        System.out.println("What can I do for you?");
        System.out.println(LINE);

        while (true) {
            String input = sc.nextLine().trim();

            try {
                if (input.equals("bye")) {
                    System.out.println(LINE);
                    System.out.println("Bye. Hope to see you again soon!");
                    System.out.println(LINE);
                    break;
                }

                if (input.isEmpty()) {
                    throw new IllegalArgumentException("Please type a command.");
                }

                if (input.equals("list")) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Here are the tasks in your list:\n");
                    if (size == 0) {
                        sb.append("(Empty)");
                    } else {
                        for (int i = 0; i < size; i++) {
                            sb.append(i + 1).append(".")
                                    .append(formatTask(type, desc, done, byDateTime, fromDateTime, toDateTime, i))
                                    .append("\n");
                        }
                    }
                    printBlock(sb.toString().trim());
                    continue;
                }

                if (input.startsWith("mark ")) {
                    int idx = parseIndex(input.substring(5).trim(), size) - 1;
                    done[idx] = true;

                    saveTasks(type, desc, done, byDateTime, fromDateTime, toDateTime, size);

                    String msg = "Nice! I've marked this task as done:\n"
                            + (idx + 1) + "." + formatTask(type, desc, done, byDateTime, fromDateTime, toDateTime, idx);
                    printBlock(msg);
                    continue;
                }

                if (input.startsWith("unmark ")) {
                    int idx = parseIndex(input.substring(7).trim(), size) - 1;
                    done[idx] = false;

                    saveTasks(type, desc, done, byDateTime, fromDateTime, toDateTime, size);

                    String msg = "OK, I've marked this task as not done yet:\n"
                            + (idx + 1) + "." + formatTask(type, desc, done, byDateTime, fromDateTime, toDateTime, idx);
                    printBlock(msg);
                    continue;
                }

                if (input.startsWith("delete ")) {
                    int idx = parseIndex(input.substring(7).trim(), size) - 1;
                    String removed = formatTask(type, desc, done, byDateTime, fromDateTime, toDateTime, idx);

                    for (int i = idx; i < size - 1; i++) {
                        type[i] = type[i + 1];
                        desc[i] = desc[i + 1];
                        done[i] = done[i + 1];
                        byDateTime[i] = byDateTime[i + 1];
                        fromDateTime[i] = fromDateTime[i + 1];
                        toDateTime[i] = toDateTime[i + 1];
                    }

                    type[size - 1] = null;
                    desc[size - 1] = null;
                    done[size - 1] = false;
                    byDateTime[size - 1] = null;
                    fromDateTime[size - 1] = null;
                    toDateTime[size - 1] = null;

                    size--;

                    saveTasks(type, desc, done, byDateTime, fromDateTime, toDateTime, size);

                    String msg = "Noted. I've removed this task:\n"
                            + removed
                            + "\nNow you have " + size + " tasks in the list.";
                    printBlock(msg);
                    continue;
                }

                if (input.startsWith("todo")) {
                    String d = input.substring(4).trim();
                    if (d.isEmpty()) {
                        throw new IllegalArgumentException("The description of a todo cannot be empty.");
                    }
                    ensureCapacity(size);

                    type[size] = "T";
                    desc[size] = d;
                    done[size] = false;

                    printBlock("Got it. I've added this task:\n"
                            + formatTask(type, desc, done, byDateTime, fromDateTime, toDateTime, size)
                            + "\nNow you have " + (size + 1) + " tasks in the list.");

                    size++;
                    saveTasks(type, desc, done, byDateTime, fromDateTime, toDateTime, size);
                    continue;
                }

                if (input.startsWith("deadline")) {
                    String rest = input.substring(8).trim();
                    if (rest.isEmpty()) {
                        throw new IllegalArgumentException("The description of a deadline cannot be empty.");
                    }

                    String[] parts = rest.split("\\s+/by\\s+", 2);
                    if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
                        throw new IllegalArgumentException("Use: deadline <description> /by <yyyy-MM-dd> [HHmm]");
                    }
                    ensureCapacity(size);

                    String d = parts[0].trim();
                    LocalDateTime by = parseDateOrDateTime(parts[1].trim());

                    type[size] = "D";
                    desc[size] = d;
                    done[size] = false;
                    byDateTime[size] = by;

                    printBlock("Got it. I've added this task:\n"
                            + formatTask(type, desc, done, byDateTime, fromDateTime, toDateTime, size)
                            + "\nNow you have " + (size + 1) + " tasks in the list.");

                    size++;
                    saveTasks(type, desc, done, byDateTime, fromDateTime, toDateTime, size);
                    continue;
                }

                if (input.startsWith("event")) {
                    String rest = input.substring(5).trim();
                    if (rest.isEmpty()) {
                        throw new IllegalArgumentException("The description of an event cannot be empty.");
                    }

                    String[] p1 = rest.split("\\s+/from\\s+", 2);
                    if (p1.length < 2 || p1[0].trim().isEmpty() || p1[1].trim().isEmpty()) {
                        throw new IllegalArgumentException("Use: event <description> /from <yyyy-MM-dd HHmm> /to <yyyy-MM-dd HHmm>");
                    }
                    String d = p1[0].trim();

                    String[] p2 = p1[1].split("\\s+/to\\s+", 2);
                    if (p2.length < 2 || p2[0].trim().isEmpty() || p2[1].trim().isEmpty()) {
                        throw new IllegalArgumentException("Use: event <description> /from <yyyy-MM-dd HHmm> /to <yyyy-MM-dd HHmm>");
                    }
                    ensureCapacity(size);

                    LocalDateTime start = parseDateTimeOnly(p2[0].trim());
                    LocalDateTime end = parseDateTimeOnly(p2[1].trim());

                    type[size] = "E";
                    desc[size] = d;
                    done[size] = false;
                    fromDateTime[size] = start;
                    toDateTime[size] = end;

                    printBlock("Got it. I've added this task:\n"
                            + formatTask(type, desc, done, byDateTime, fromDateTime, toDateTime, size)
                            + "\nNow you have " + (size + 1) + " tasks in the list.");

                    size++;
                    saveTasks(type, desc, done, byDateTime, fromDateTime, toDateTime, size);
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

    private static void ensureCapacity(int size) {
        if (size >= MAX_TASKS) {
            throw new IllegalArgumentException("Task list is full.");
        }
    }

    private static LocalDateTime parseDateOrDateTime(String raw) {
        try {
            if (raw.contains(" ")) {
                return LocalDateTime.parse(raw, INPUT_DATE_TIME);
            }
            LocalDate d = LocalDate.parse(raw, INPUT_DATE);
            return d.atStartOfDay();
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Invalid date format. Use yyyy-MM-dd or yyyy-MM-dd HHmm (e.g., 2019-12-02 1800).");
        }
    }

    private static LocalDateTime parseDateTimeOnly(String raw) {
        try {
            return LocalDateTime.parse(raw, INPUT_DATE_TIME);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Invalid date/time format. Use yyyy-MM-dd HHmm (e.g., 2019-12-02 1400).");
        }
    }

    private static String formatBy(LocalDateTime dt) {
        if (dt == null) {
            return "(by: ?)";
        }
        if (dt.getHour() == 0 && dt.getMinute() == 0) {
            return "(by: " + dt.toLocalDate().format(OUT_DATE) + ")";
        }
        return "(by: " + dt.format(OUT_DATE_TIME) + ")";
    }

    private static String formatFromTo(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return "(from: ? to: ?)";
        }
        return "(from: " + start.format(OUT_DATE_TIME) + " to: " + end.format(OUT_DATE_TIME) + ")";
    }

    private static int loadTasks(String[] type, String[] desc, boolean[] done,
                                 LocalDateTime[] byDateTime, LocalDateTime[] fromDateTime, LocalDateTime[] toDateTime,
                                 int size) {
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

                String[] parts = line.split("\\s*\\|\\s*");
                if (parts.length < 3) {
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
                        continue;
                    }
                    type[size] = "D";
                    desc[size] = d;
                    done[size] = isDone;
                    byDateTime[size] = parseStoredDeadline(parts[3].trim());
                    size++;
                } else if ("E".equals(t)) {
                    if (parts.length < 5) {
                        continue;
                    }
                    type[size] = "E";
                    desc[size] = d;
                    done[size] = isDone;
                    fromDateTime[size] = parseStoredDateTime(parts[3].trim());
                    toDateTime[size] = parseStoredDateTime(parts[4].trim());
                    size++;
                }
            }
        } catch (IOException e) {
            return 0; 
        }

        return size;
    }

    private static LocalDateTime parseStoredDeadline(String raw) {
        try {
            if (raw.contains(" ")) {
                return LocalDateTime.parse(raw, INPUT_DATE_TIME);
            }
            return LocalDate.parse(raw, INPUT_DATE).atStartOfDay();
        } catch (DateTimeParseException e) {
            return null; 
        }
    }

    private static LocalDateTime parseStoredDateTime(String raw) {
        try {
            return LocalDateTime.parse(raw, INPUT_DATE_TIME);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static void saveTasks(String[] type, String[] desc, boolean[] done,
                                  LocalDateTime[] byDateTime, LocalDateTime[] fromDateTime, LocalDateTime[] toDateTime,
                                  int size) {
        try {
            Path parent = DATA_PATH.getParent();
            if (parent != null) {
                Files.createDirectories(parent); 
            }

            try (BufferedWriter bw = Files.newBufferedWriter(
                    DATA_PATH, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

                for (int i = 0; i < size; i++) {
                    bw.write(serializeTask(type, desc, done, byDateTime, fromDateTime, toDateTime, i));
                    bw.newLine();
                }
            }
        } catch (IOException e) {
        }
    }

    private static String serializeTask(String[] type, String[] desc, boolean[] done,
                                        LocalDateTime[] byDateTime, LocalDateTime[] fromDateTime, LocalDateTime[] toDateTime,
                                        int i) {
        int doneFlag = done[i] ? 1 : 0;

        if ("T".equals(type[i])) {
            return "T | " + doneFlag + " | " + desc[i];
        } else if ("D".equals(type[i])) {
            LocalDateTime dt = byDateTime[i];
            String stored;
            if (dt == null) {
                stored = "";
            } else if (dt.getHour() == 0 && dt.getMinute() == 0) {
                stored = dt.toLocalDate().format(INPUT_DATE);
            } else {
                stored = dt.format(INPUT_DATE_TIME);
            }
            return "D | " + doneFlag + " | " + desc[i] + " | " + stored;
        } else { 
            LocalDateTime s = fromDateTime[i];
            LocalDateTime e = toDateTime[i];
            String storedS = (s == null) ? "" : s.format(INPUT_DATE_TIME);
            String storedE = (e == null) ? "" : e.format(INPUT_DATE_TIME);
            return "E | " + doneFlag + " | " + desc[i] + " | " + storedS + " | " + storedE;
        }
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

    private static String formatTask(String[] type, String[] desc, boolean[] done,
                                     LocalDateTime[] byDateTime, LocalDateTime[] fromDateTime, LocalDateTime[] toDateTime,
                                     int i) {

        String status = done[i] ? "X" : " ";
        String t = type[i];

        if ("T".equals(t)) {
            return "[T][" + status + "] " + desc[i];
        } else if ("D".equals(t)) {
            return "[D][" + status + "] " + desc[i] + " " + formatBy(byDateTime[i]);
        } else { 
            return "[E][" + status + "] " + desc[i] + " " + formatFromTo(fromDateTime[i], toDateTime[i]);
        }
    }
}
