package com.huce.online_music_streaming_app.service;

import com.huce.online_music_streaming_app.dto.ListeningStatsDto;
import com.huce.online_music_streaming_app.dto.TopArtistDto;
import com.huce.online_music_streaming_app.dto.TopTrackDto;
import com.huce.online_music_streaming_app.repository.PlayHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ListeningStatsService {

    private final PlayHistoryRepository playHistoryRepository;

    public ListeningStatsDto getStats(Long userId, String period, int offset) {
        LocalDate today = LocalDate.now();
        LocalDate start;
        LocalDate end;
        String unit;
        switch (period == null ? "week" : period.toLowerCase()) {
            case "month" -> {
                LocalDate firstOfThisMonth = today.withDayOfMonth(1);
                start = firstOfThisMonth.minusMonths(offset);
                end = start.plusMonths(1);
                unit = "month";
            }
            case "year" -> {
                LocalDate firstOfThisYear = today.withDayOfYear(1);
                start = firstOfThisYear.minusYears(offset);
                end = start.plusYears(1);
                unit = "year";
            }
            default -> {
                LocalDate monday = today.with(DayOfWeek.MONDAY);
                start = monday.minusWeeks(offset);
                end = start.plusWeeks(1);
                unit = "week";
            }
        }

        LocalDateTime startTs = start.atStartOfDay();
        LocalDateTime endTs = end.atStartOfDay();

        TopArtistDto topArtist = playHistoryRepository
                .topArtistsInRange(userId, startTs, endTs, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .map(row -> new TopArtistDto(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        (String) row[2],
                        ((Number) row[3]).longValue()))
                .orElse(null);

        TopTrackDto topTrack = playHistoryRepository
                .topTracksInRange(userId, startTs, endTs, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .map(row -> new TopTrackDto(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        (String) row[2],
                        (String) row[3],
                        ((Number) row[4]).longValue()))
                .orElse(null);

        Object[] agg = playHistoryRepository.aggregateInRange(userId, startTs, endTs);
        // JPA may wrap single-row aggregate as Object[][] — handle both
        Object[] row = (agg.length > 0 && agg[0] instanceof Object[]) ? (Object[]) agg[0] : agg;
        long totalPlays = row[0] == null ? 0 : ((Number) row[0]).longValue();
        long totalSeconds = row[1] == null ? 0 : ((Number) row[1]).longValue();

        return new ListeningStatsDto(
                buildLabel(unit, offset, start, end),
                start,
                end.minusDays(1),
                topArtist,
                topTrack,
                totalPlays,
                totalSeconds / 60
        );
    }

    private String buildLabel(String unit, int offset, LocalDate start, LocalDate end) {
        if (offset == 0) {
            return switch (unit) {
                case "month" -> "Tháng này";
                case "year" -> "Năm nay";
                default -> "Tuần này";
            };
        }
        if (offset == 1) {
            return switch (unit) {
                case "month" -> "Tháng trước";
                case "year" -> "Năm trước";
                default -> "Tuần trước";
            };
        }
        return start.getDayOfMonth() + " thg " + start.getMonthValue()
                + " – " + end.minusDays(1).getDayOfMonth() + " thg " + end.minusDays(1).getMonthValue();
    }
}
