package mmm.data;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 *
 * @author jays
 */
public class mmmRoute {
    DraggableStation startStation;
    DraggableStation endStation;
    LinkedList<mmmLine> lines;
    LinkedList<DraggableStation> transferStations;
    
    public mmmRoute(DraggableStation initStartStation, DraggableStation initEndStation) {
        startStation = initStartStation;
        endStation = initEndStation;
        lines = new LinkedList();
        transferStations = new LinkedList();
    }
    
    @Override
    public mmmRoute clone() {
        mmmRoute clonedRoute = new mmmRoute(this.startStation, this.endStation);
        for (mmmLine line : lines) {
            clonedRoute.getLines().add(line);
        }
        for (DraggableStation station : transferStations) {
            clonedRoute.getTransferStations().add(station);
        }
        return clonedRoute;
    }
    
    public void addBoarding(mmmLine boardingLine, DraggableStation boardingStation) {
        lines.add(boardingLine);
        transferStations.add(boardingStation);
    }
    
    public boolean hasLineWithStation(DraggableStation testStation) {
        for (mmmLine testLine : lines) {
            if (testLine.getShapes().contains(testStation)) {
                return true;
            }
        }
        return false;
    }
    
    public LinkedList<DraggableStation> getStationsOnRoute() {
        LinkedList<DraggableStation> stationsOnRoute = new LinkedList();
        
        int counter = 0;
        DraggableStation beforeStation = null;
        ListIterator itr = transferStations.listIterator();
        if (itr.hasNext()) {
            beforeStation = (DraggableStation) itr.next();
        }
        while (itr.hasNext() && beforeStation != null) {
            DraggableStation afterStation = (DraggableStation) itr.next();
            LinkedList<DraggableStation> stationsToAdd
                    = getStationsInBetween(lines.get(counter), beforeStation, afterStation);
            beforeStation = afterStation;
            for (DraggableStation station : stationsToAdd) {
                stationsOnRoute.add(station);
                
            }  
            counter++;
        }
        LinkedList<DraggableStation> stationsToAdd
                    = getStationsInBetween(lines.get(counter), transferStations.get(counter), endStation);
        for (DraggableStation station : stationsToAdd) {
                stationsOnRoute.add(station);
                
        }  
        return stationsOnRoute;
    }
    
    public LinkedList<DraggableStation> getStationsInBetween(mmmLine line, DraggableStation start, DraggableStation end) {
        LinkedList<DraggableStation> stationsInBetween = new LinkedList();
        int startIndex = line.getShapes().indexOf(start);
        int endIndex = line.getShapes().indexOf(end);
        int counter = 0;
        
        if (startIndex < endIndex) {
            counter = startIndex;
            ListIterator itr = line.getShapes().listIterator(startIndex);
            while (itr.hasNext() && counter <= endIndex) {
                Object shape = itr.next();
                if (shape instanceof DraggableStation) {
                    stationsInBetween.add((DraggableStation) shape);
                }
                counter++;
            }
        }
        else if (startIndex == endIndex) {
            stationsInBetween.add((DraggableStation) line.getShapes().get(startIndex));
        }
        else {
            counter = startIndex;
            ListIterator itr = line.getShapes().listIterator(startIndex);
            while (itr.hasPrevious() && counter >= endIndex) {
                Object shape = itr.previous();
                if (shape instanceof DraggableStation) {
                    stationsInBetween.add((DraggableStation) shape);
                }
                counter--;
            }
        }
        return stationsInBetween;
    }

    public DraggableStation getStartStation() {
        return startStation;
    }
    
    public DraggableStation getEndStation() {
        return endStation;
    }
    
    public LinkedList<mmmLine> getLines() {
        return lines;
    }
    
    public LinkedList<DraggableStation> getTransferStations() {
        return transferStations;
    }
}
