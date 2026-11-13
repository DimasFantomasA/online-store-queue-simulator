import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

public final class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            run(scanner, args);
        } catch (NoSuchElementException e) {
            System.out.println("Ввод прерван.");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка параметров: " + e.getMessage());
        }
    }

    private static void run(Scanner scanner, String[] args) {
        boolean interactive = args.length == 0;
        if (args.length > 4) {
            throw new IllegalArgumentException("Ожидаются: заказы, приборы, ячейки, seed.");
        }

        int maxOrders = interactive ? readInt(scanner, "Количество заказов", 20)
                : Integer.parseInt(args[0]);
        int serverCount = interactive ? readInt(scanner, "Количество приборов", 2)
                : args.length > 1 ? Integer.parseInt(args[1]) : 2;
        int bufferCapacity = interactive ? readInt(scanner, "Размер буфера", 3)
                : args.length > 2 ? Integer.parseInt(args[2]) : 3;

        if (maxOrders <= 0 || serverCount <= 0 || bufferCapacity <= 0) {
            throw new IllegalArgumentException("Заказы, приборы и ячейки задаются целыми числами больше нуля.");
        }

        int sourceCount = interactive ? readInt(scanner, "Количество источников", 2) : 2;
        double minServiceTime = interactive
                ? readDouble(scanner, "Минимальное время обслуживания", 2.0, 0) : 2.0;
        double maxServiceTime = interactive
                ? readDouble(scanner, "Максимальное время обслуживания",
                Math.max(5.0, minServiceTime), minServiceTime) : 5.0;
        long seed = interactive ? readSeed(scanner)
                : args.length > 3 ? Long.parseLong(args[3]) : new Random().nextLong();

        List<OrderSource> sources = new ArrayList<>();
        for (int i = 0; i < sourceCount; i++) {
            int id = i + 1;
            double defaultRate = id == 1 ? 0.70 : 0.50;
            double rate = interactive
                    ? readDouble(scanner, "Интенсивность λ источника И" + id, defaultRate, 0)
                    : defaultRate;
            sources.add(new OrderSource(id, rate, new Random(seed + 11L * id)));
        }

        List<Server> servers = new ArrayList<>();
        for (int i = 0; i < serverCount; i++) {
            int id = i + 1;
            servers.add(new Server(id, minServiceTime, maxServiceTime, new Random(seed + 100L + id)));
        }

        SimulationEngine engine = new SimulationEngine(
                maxOrders, sources, servers, new CircularBuffer(bufferCapacity));
        StepPresenter presenter = new StepPresenter();

        System.out.println("Seed прогона: " + seed);
        presenter.showState(engine.getSnapshot());
        System.out.println("Enter = один шаг, a = до конца, q = выход");

        while (!engine.isFinished()) {
            System.out.print("Следующий шаг [Enter/a/q]: ");
            if (!scanner.hasNextLine()) {
                break;
            }

            String action = scanner.nextLine().trim().toLowerCase(Locale.ROOT);
            if (action.equals("q")) {
                break;
            }
            if (action.equals("a")) {
                while (engine.step()) {
                    presenter.showState(engine.getSnapshot());
                }
                break;
            }
            if (action.isEmpty()) {
                engine.step();
                presenter.showState(engine.getSnapshot());
            } else {
                System.out.println("Используйте Enter, a или q.");
            }
        }

        if (engine.isFinished()) {
            presenter.showFinal(engine.getSnapshot());
        } else {
            System.out.println("Работа остановлена до завершения моделирования.");
        }
    }

    private static int readInt(Scanner scanner, String label, int defaultValue) {
        while (true) {
            System.out.printf("%s [%d]: ", label, defaultValue);
            String input = scanner.nextLine().trim();
            try {
                int value = input.isEmpty() ? defaultValue : Integer.parseInt(input);
                if (value > 0) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Введите целое число больше нуля.");
        }
    }

    private static double readDouble(Scanner scanner, String label, double defaultValue, double min) {
        while (true) {
            System.out.printf("%s [%s]: ", label, defaultValue);
            String input = scanner.nextLine().trim().replace(',', '.');
            try {
                double value = input.isEmpty() ? defaultValue : Double.parseDouble(input);
                if (Double.isFinite(value) && value > 0 && value >= min) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println(min == 0 ? "Введите положительное конечное число."
                    : "Введите конечное число не меньше " + min + ".");
        }
    }

    private static long readSeed(Scanner scanner) {
        while (true) {
            System.out.print("Seed [Enter — случайный, 42 — прежний сценарий]: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return new Random().nextLong();
            }
            try {
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("Введите целое число типа long или нажмите Enter.");
            }
        }
    }
}