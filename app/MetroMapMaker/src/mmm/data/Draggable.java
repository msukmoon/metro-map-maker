package mmm.data;

/**
 *
 * @author myungsuk
 */
public interface Draggable {
    public static final String STATION = "STATION";
    public static final String IMAGE = "IMAGE";
    public static final String LABEL = "LABEL";
    public static final String END = "END";
    public mmmState getStartingState();
    public void update(double x, double y);
    public void move(double x, double y);
    public void drag(double x, double y);
    public double getWidth();
    public double getHeight();
    public String getShapeType();
}
