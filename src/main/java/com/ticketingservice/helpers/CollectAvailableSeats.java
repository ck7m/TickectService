package com.ticketingservice.helpers;

import com.ticketingservice.model.Level;
import com.ticketingservice.model.Seat;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class CollectAvailableSeats {

    private static class AvailableSeatsByLevel {
        private Map<Integer, Integer> availableSeatsByRow = new HashMap<>();
        private int availableSeats;
    }

    private Map<Integer, AvailableSeatsByLevel> collectedDataByLevelId = new LinkedHashMap<Integer, AvailableSeatsByLevel>();
    private int totalAvailableSeats;

    public Integer getAvailableSeatsByRow(int levelId, int row) {
        Map<Integer, Integer> availableSeatsByRow = collectedDataByLevelId.get(levelId).availableSeatsByRow;
        return availableSeatsByRow.containsKey(row) ? availableSeatsByRow.get(row) : 0;
    }

    public Integer getAvailableSeatsByLevel(int levelId) {
        return collectedDataByLevelId.get(levelId).availableSeats;
    }

    public int getTotalAvailableSeats() {
        return totalAvailableSeats;
    }

    private AvailableSeatsByLevel collectAvailableSeats(Level level) {
        /*for(Seat[] row: level.getSeats()){
            // numOfAvailableSeats += Arrays.stream(row).sorted(comparator).filter(seat->(seat==null)).count();
           Seat seat;
           for(int j=0;j < level.getColumns();j++){
               seat = row[j];
               if(seat == null || seat.getStatus() == SeatStatus.AVAILABLE){
                   numOfAvailableSeats += level.getColumns()-j;
                   break;
               }
           }
          }*/
        AvailableSeatsByLevel levelData = new AvailableSeatsByLevel();
        for (int i = 1; i <= level.getRows(); i++) {
            Map<Integer, Seat> row = level.getSeatsMap().get(i);
            if (row == null) {
                levelData.availableSeatsByRow.put(i, level.getColumns());
                levelData.availableSeats += level.getColumns();
            } else {
                int remainingSeats = level.getColumns() - row.size();
                levelData.availableSeatsByRow.put(i, remainingSeats);
                levelData.availableSeats += remainingSeats;
            }
        }
        return levelData;
    }

    public static CollectAvailableSeats collectAvailableSeats(Set<Level> levels) {
        Function<Set<Level>, CollectAvailableSeats> collector = input -> {
            CollectAvailableSeats result = new CollectAvailableSeats();
            for (Level level : input) {
                AvailableSeatsByLevel availableSeatsByLevel = result.collectAvailableSeats(level);
                result.collectedDataByLevelId.put(level.getLevelId(), availableSeatsByLevel);
                result.totalAvailableSeats += availableSeatsByLevel.availableSeats;
            }
            return result;
        };
        return collector.apply(levels);
    }

}
