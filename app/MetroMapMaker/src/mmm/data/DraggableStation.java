package mmm.data;

import java.util.LinkedList;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

/**
 *
 * @author myungsuk
 */
public class DraggableStation extends Circle implements Draggable {
    double centerX;
    double centerY;
    Text name;
    boolean isOnLine;
    LinkedList<String> lines;
    int location;
    int rotation;
  
    public DraggableStation(double x, double y) {
        setCenterX(x);
	setCenterY(y);
        centerX = x;
	centerY = y;
        lines = new LinkedList();
        location = 1;
        rotation = 1;
    }

    @Override
    public mmmState getStartingState() {
        return mmmState.ADD_STATION_MODE;
    }

    @Override
    public void update(double x, double y) {
        centerX = x;
        centerY = y;
    }
    
    @Override
    public void move(double x, double y) {
        setCenterX(x);
        setCenterY(y);
        centerX = x;
        centerY = y;
    }

    @Override
    public void drag(double x, double y) {
        double diffX = x - centerX;
	double diffY = y - centerY;
	double newX = getCenterX() + diffX;
	double newY = getCenterY() + diffY;
	setCenterX(newX);
	setCenterY(newY);
	centerX = x;
	centerY = y;
    }
    
    public void moveLabel() {
        if (location == 1) {
            name.yProperty().unbind();
            name.yProperty().bind(this.centerYProperty().add(30));
            location = 2;
        }
        else if (location == 2) {
            name.xProperty().unbind();
            name.xProperty().bind(this.centerXProperty().subtract(90));
            location = 3;
        }
        else if (location == 3) {
            name.yProperty().unbind();
            name.yProperty().bind(this.centerYProperty().subtract(30));
            location = 4;
        }
        else {
            name.xProperty().unbind();
            name.xProperty().bind(this.centerXProperty().add(30));
            location = 1;
        }
    }
    
    public void moveLabel(int location) {
        if (location == 1) {
            name.yProperty().unbind();
            name.yProperty().bind(this.centerYProperty().add(30));
            location = 2;
        }
        else if (location == 2) {
            name.xProperty().unbind();
            name.xProperty().bind(this.centerXProperty().subtract(90));
            location = 3;
        }
        else if (location == 3) {
            name.yProperty().unbind();
            name.yProperty().bind(this.centerYProperty().subtract(30));
            location = 4;
        }
        else {
            name.xProperty().unbind();
            name.xProperty().bind(this.centerXProperty().add(30));
            location = 1;
        }
    }
    
    public void rotateLabel() {
        if (rotation == 1) {
            name.setRotate(90);
            rotation = 2;
        }
        else if (rotation == 2) {
            name.setRotate(180);
            rotation = 3;
        }
        else if (rotation == 3) {
            name.setRotate(270);
            rotation = 4;
        }
        else {
            name.setRotate(0);
            rotation = 1;
        }
    }
    
    public void rotateLabel(int rotation) {
        if (rotation == 1) {
            name.setRotate(90);
            rotation = 2;
        }
        else if (rotation == 2) {
            name.setRotate(180);
            rotation = 3;
        }
        else if (rotation == 3) {
            name.setRotate(270);
            rotation = 4;
        }
        else {
            name.setRotate(0);
            rotation = 1;
        }
    }
    
    
    public String cT(double x, double y) {
	return "(x,y): (" + x + "," + y + ")";
    }

    @Override
    public double getWidth() {
        return getRadius() * 2;
    }

    @Override
    public double getHeight() {
        return getRadius() * 2;
    }

    @Override
    public String getShapeType() {
        return STATION;
    }
    
    public Text getName() {
        return name;
    }
    
    public void setName(DraggableEnd initName) {
        name = initName;
    }

    public void initNewName() {
        name = new Text();
    }
    
    public boolean isOnLine() {
        return isOnLine;
    }
    
    public void setIsOnLine(boolean value) {
        isOnLine = value;
    }
    
    public LinkedList<String> getLines() {
        return lines;
    }
    
    public int getLocation() {
        return location;
    }
    
    public int getRotation() {
        return rotation;
    }
}
