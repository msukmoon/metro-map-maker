package mmm.data;

import java.util.LinkedList;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

/**
 *
 * @author myungsuk
 */
public class mmmLine {
    String lineName;
    Color lineColor;
    boolean circular; 
    LinkedList<Shape> shapes;
    LinkedList<String> transfers;
    
    public mmmLine(String initLineName, Color initLineColor) {
        lineName = initLineName;
        lineColor = initLineColor;
        circular = false;
        shapes = new LinkedList();
        transfers = new LinkedList();
    }
         
    public DraggableStation findTransferStation(mmmLine intersectingLine) {
        for (Shape s : shapes) {
            if (s instanceof DraggableStation) {
                if (((DraggableStation) s).getLines().contains(intersectingLine.getLineName())) {
                    return (DraggableStation) s;
                }
            }
        }
        return null;
    }
    
    public String getLineName() {
        return lineName;
    }
    
    public void setLineName(String initLineName) {
        lineName = initLineName;
    }
    
    public Color getLineColor() {
        return lineColor;
    }
    
    public void setLineColor(Color initLineColor) {
        lineColor = initLineColor;
    }
    
    public boolean isCircular() {
        return circular;
    }
    
    public void setCircular(boolean value) {
        circular = value;
    }
    
    public LinkedList<Shape> getShapes() {
        return shapes;
    }
    
    public LinkedList<String> getTransfers() {
        return transfers;
    }
}
