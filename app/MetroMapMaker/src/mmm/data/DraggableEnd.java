package mmm.data;

import javafx.scene.text.Text;

/**
 *
 * @author myungsuk
 */
public class DraggableEnd extends Text implements Draggable {
    double x;
    double y;
    boolean circular;
   
    public DraggableEnd(double x, double y) {
        setX(x);
	setY(y);
        this.x = x;
	this.y = y;    
        this.circular = false;
    }
    
    @Override
    public mmmState getStartingState() {
        return mmmState.ADD_LINE_MODE;
    }

    @Override
    public void update(double x, double y) {
        this.x = x;
	this.y = y; 
    }
    
    @Override
    public void move(double x, double y) {
        setX(x);
        setY(y);
        this.x = x;
        this.y = y;
    }

    @Override
    public void drag(double x, double y) {
        if (!circular) {
            double diffX = x - this.x;
            double diffY = y - this.y;
            double newX = getX() + diffX;
            double newY = getY() + diffY;
            xProperty().set(newX);
            yProperty().set(newY);
            this.x = x;
            this.y = y;
        }
    }
    
    public void updateX(double x) {
        this.x = x;
    }
    
    public void updateY(double y) {
        this.y = y;
    }
               
    public String cT(double x, double y) {
	return "(x,y): (" + x + "," + y + ")";
    }
    
    public boolean isCircular() {
        return circular;
    }
    
    public void setCircular(boolean value) {
        circular = value;
    }

    @Override
    public double getWidth() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getHeight() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getShapeType() {
        return END;
    }
      
}
