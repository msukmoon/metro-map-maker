package mmm.data;

import javafx.scene.shape.Rectangle;

/**
 *
 * @author myungsuk
 */
public class DraggableImage extends Rectangle implements Draggable {
    double startX;
    double startY;

    public DraggableImage(double x, double y) {
        setX(x);
	setY(y);
	startX = x;
	startY = y;
    }
        
    @Override
    public mmmState getStartingState() {
	return mmmState.ADD_IMAGE_MODE;
    }
    
    @Override
    public void update(double x, double y) {
	startX = x;
	startY = y;	
    }
    
    @Override
    public void move(double x, double y) {
        setX(x);
        setY(y);
        startX = x;
        startY = y;
    }
    
    @Override
    public void drag(double x, double y) {
        double diffX = x - startX;
	double diffY = y - startY;
	double newX = getX() + diffX;
	double newY = getY() + diffY;
	xProperty().set(newX);
	yProperty().set(newY);
	startX = x;
	startY = y;
    }
    
    public void size(double x, double y) {
	widthProperty().set(x);
	heightProperty().set(y);	
    }
    
    public String cT(double x, double y) {
	return "(x,y): (" + x + "," + y + ")";
    }
    
    @Override
    public String getShapeType() {
	return IMAGE;
    }
}
