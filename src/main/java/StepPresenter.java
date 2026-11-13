import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class StepPresenter {
    public void showState(SimulationSnapshot s) {
        System.out.println();
        System.out.println("================================================================");

        int currentGenerated = s.getSourceStatistics().stream()
                .mapToInt(SourceStatistics::getGeneratedCount)
                .sum();

        System.out.printf(Locale.US, "ШАГ %d | t = %.3f | поступило: %d/%d%n",
                s.getStepNumber(), s.getCurrentTime(), currentGenerated, s.getMaxOrders());
        System.out.println("Событие: " + s.getLastAction());

        System.out.println("ИСТОЧНИКИ (ИЗ1 — экспоненциальные интервалы)");
        Map<Integer, Double> arrivalRates = new HashMap<>();

        for (OrderSource source : s.getSources()) {
            arrivalRates.put(source.getSourceId(), source.getArrivalRate());
        }

        for (SourceStatistics stats : s.getSourceStatistics()) {
            System.out.printf(Locale.US,
                    " И%d (λ=%.2f): создано=%d, завершено=%d, отказов=%d (%.1f%%), ожид.ср=%.3f, обслуж.ср=%.3f%n",
                    stats.getSourceId(),
                    arrivalRates.get(stats.getSourceId()),
                    stats.getGeneratedCount(),
                    stats.getCompletedCount(),
                    stats.getRejectedCount(),
                    stats.getRejectionPercent(),
                    stats.getMeanWaitingTime(),
                    stats.getMeanServiceTime());
        }

        Order[] slots = s.getBufferSlots();
        System.out.printf("БУФЕР (Д1ОЗ1, Д1ОО1, Д2Б1): %d/%d, указатель -> ячейка %d%n",
                s.getBufferSize(), slots.length, s.getBufferPointer() + 1);

        for (int i = 0; i < slots.length; i++) {
            String content = slots[i] == null ? "[пусто]" : slots[i].toString();
            System.out.printf(" [%d] %-14s %s%n", i + 1, content,
                    i == s.getBufferPointer() ? "<-- УБ" : "");
        }

        System.out.println("ПРИБОРЫ (Д2П1, ПЗ2)");
        for (Server server : s.getServers()) {
            if (server.isBusy()) {
                System.out.printf(Locale.US,
                        " П%d: ЗАНЯТ %s, завершение=%.3f, загрузка=%.1f%%%n",
                        server.getId(), server.getCurrentOrder(),
                        server.getPlannedCompletionTime(),
                        server.getUtilization(s.getCurrentTime()) * 100);
            } else {
                System.out.printf(Locale.US, " П%d: СВОБОДЕН, загрузка=%.1f%%%n",
                        server.getId(), server.getUtilization(s.getCurrentTime()) * 100);
            }
        }

        System.out.println("================================================================");
    }

    public void showFinal(SimulationSnapshot s) {
        int generated = 0;
        int rejected = 0;
        int completed = 0;

        for (SourceStatistics stats : s.getSourceStatistics()) {
            generated += stats.getGeneratedCount();
            rejected += stats.getRejectedCount();
            completed += stats.getCompletedCount();
        }

        System.out.printf(Locale.US,
                "ИТОГ: поступило=%d, обработано=%d, отказов=%d, Pотк=%.2f%%%n",
                generated, completed, rejected,
                generated == 0 ? 0 : 100.0 * rejected / generated);

        for (SourceStatistics stats : s.getSourceStatistics()) {
            System.out.printf(Locale.US,
                    " И%d: Tож=%.3f, Dож=%.3f, Tобсл=%.3f, Dобсл=%.3f, Tсист=%.3f%n",
                    stats.getSourceId(), stats.getMeanWaitingTime(), stats.getWaitingVariance(),
                    stats.getMeanServiceTime(), stats.getServiceVariance(),
                    stats.getMeanTimeInSystem());
        }
    }
}