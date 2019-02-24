package mmm.file;

import djf.AppTemplate;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import djf.components.AppDataComponent;
import djf.components.AppFileComponent;
import java.io.File;
import java.util.LinkedList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.Line;
import javax.imageio.ImageIO;
import mmm.data.mmmData;
import mmm.data.DraggableStation;
import mmm.data.DraggableEnd;
import mmm.data.mmmLine;
import mmm.data.mmmState;

/**
 * This class serves as the file management component for this application,
 * providing all I/O services.
 *
 * @author Myungsuk Moon
 * @coauthor Richard McKenna
 */
public class mmmFiles implements AppFileComponent {
    // FOR JSON LOADING
    static final String JSON_NAME = "name";
    static final String JSON_LINES = "lines";
    static final String JSON_LINE_NAME = "line_name";
    static final String JSON_LINE_TEXTS = "line_texts";
    static final String JSON_LINE_STATIONS = "line_stations";
    static final String JSON_LINE_TRANSFERS = "line_transfers";
    static final String JSON_LINE_CIRCULAR = "line_circular";
    static final String JSON_LINE_COLOR = "line_color";
    static final String JSON_LINE_THICKNESS = "line_thickness";
    static final String JSON_STATIONS = "stations";
    static final String JSON_STATION_NAME = "station_name";
    static final String JSON_STATION_NAMES = "station_names";
    static final String JSON_STATION_ISONLINE = "station_isonline";
    static final String JSON_STATION_LOCATION = "station_location";
    static final String JSON_STATION_ROTATION = "station_rotation";
    static final String JSON_STATION_LINES = "station_lines";
    static final String JSON_TYPE = "type";
    static final String JSON_X = "x";
    static final String JSON_Y = "y";
    static final String JSON_CIRCULAR = "circular";
    static final String JSON_COLOR = "color";
    static final String JSON_LIST = "list";
    static final String JSON_RED = "red";
    static final String JSON_GREEN = "green";
    static final String JSON_BLUE = "blue";
    static final String JSON_ALPHA = "alpha";
    
    static final String DEFAULT_DOCTYPE_DECLARATION = "<!doctype html>\n";
    static final String DEFAULT_ATTRIBUTE_VALUE = "";
    
    // THIS IS A SHARED REFERENCE TO THE APPLICATION
    AppTemplate app;
    
    public mmmFiles(AppTemplate initApp) {
        // KEEP THE APP FOR LATER
        app = initApp;
    }
    
    /**
     * This method is for saving user work, which in the case of this
     * application means the data that together draws the logo.
     * 
     * @param data The data management component for this application.
     * 
     * @param filePath Path (including file name/extension) to where
     * to save the data to.
     * 
     * @throws IOException Thrown should there be an error writing 
     * out data to the file.
     */
    @Override
    public void saveData(AppDataComponent data, String filePath) throws IOException {
        // GET THE DATA
	mmmData dataManager = (mmmData) data;
        
        // GET THE NAME OF THE METRO SYSTEM
        String metroName = app.getGUI().getFileController().getFileName();
        if (metroName == null) {
            metroName = "NO NAME";
        }

	// NOW BUILD LINE OBJECTS TO SAVE
	JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
	HashMap<String, mmmLine> lines = dataManager.getLines();
	for (HashMap.Entry<String, mmmLine> entry : lines.entrySet()) {  
            // SAVE LINE INFORMATION   
            String lineName = entry.getValue().getLineName();
            JsonObject lineColor = makeJsonColorObject(entry.getValue().getLineColor());
            boolean isCircular = entry.getValue().isCircular();
                        
            // THEN GO THROUGH SHAPES IN LINE'S LIST
            JsonArrayBuilder linesArrayBuilder = Json.createArrayBuilder();
            JsonArrayBuilder textsArrayBuilder = Json.createArrayBuilder();
            LinkedList<Shape> shapes = entry.getValue().getShapes();
            for (Shape s : shapes) {      
                // ONLY SAVE STATIONS
                if (s instanceof DraggableStation) {
                    DraggableStation station = ((DraggableStation) s);
                    String stationName = station.getName().getText();
                    double x = station.getCenterX();
                    double y = station.getCenterY();
                    boolean isOnLine = station.isOnLine();
                    int location = station.getLocation();
                    int rotation = station.getRotation();
                    
                    // THEN GO THROUGH ALL LINES THAT THE STATION IS ON
                    JsonArrayBuilder stationLinesArrayBuilder = Json.createArrayBuilder();
                    LinkedList<String> stationLines = station.getLines();
                    for (String stationLine : stationLines) { 
                        stationLinesArrayBuilder.add(stationLine);
                    }
                    JsonArray stationLinesArray = stationLinesArrayBuilder.build();

                    JsonObject shapeJson = Json.createObjectBuilder()
                            .add(JSON_STATION_NAME, stationName)
                            .add(JSON_X, x)
                            .add(JSON_Y, y)
                            .add(JSON_STATION_ISONLINE, isOnLine)
                            .add(JSON_STATION_LOCATION, location)
                            .add(JSON_STATION_ROTATION, rotation)
                            .add(JSON_STATION_LINES, stationLinesArray).build();
                    linesArrayBuilder.add(shapeJson);
                }
                // OR ONLY SAVE LINE END TEXTS
                else if (s instanceof DraggableEnd) {
                    DraggableEnd text = ((DraggableEnd) s);
                    String name = text.getText();
                    double x = text.getX();
                    double y = text.getY();

                    JsonObject shapeJson = Json.createObjectBuilder()
                            .add(JSON_LINE_NAME, name)
                            .add(JSON_X, x)
                            .add(JSON_Y, y).build();
                    textsArrayBuilder.add(shapeJson);
                }
            }
            JsonArray lineStationsArray = linesArrayBuilder.build();
            JsonArray lineTextsArray = textsArrayBuilder.build();
            
            // THEN GO THROUGH TRANSFER STATIONS IN LINE'S LIST
            JsonArrayBuilder transfersArrayBuilder = Json.createArrayBuilder();
            LinkedList<String> transfers = entry.getValue().getTransfers();
            for (String s : transfers) { 
                transfersArrayBuilder.add(s);
            }
            JsonArray lineTransfersArray = transfersArrayBuilder.build();
                        
            JsonObject lineJson = Json.createObjectBuilder()
                    .add(JSON_LINE_NAME, lineName)
                    .add(JSON_LINE_COLOR, lineColor)
                    .add(JSON_LINE_CIRCULAR, isCircular)
                    .add(JSON_LINE_TEXTS, lineTextsArray)
                    .add(JSON_LINE_STATIONS, lineStationsArray)
                    .add(JSON_LINE_TRANSFERS, lineTransfersArray).build();
            arrayBuilder.add(lineJson);
	}  
        JsonArray linesArray = arrayBuilder.build();
       
        // NOW BUILD STATION OBJECTS TO SAVE
	arrayBuilder = Json.createArrayBuilder();
        HashMap<String, DraggableStation> stations = dataManager.getStations();
	for (HashMap.Entry<String, DraggableStation> entry : stations.entrySet()) {
            String stationName = entry.getValue().getName().getText();
            double x = entry.getValue().getCenterX();
            double y = entry.getValue().getCenterY();
            boolean isOnLine = entry.getValue().isOnLine();
            int location = entry.getValue().getLocation();
            int rotation = entry.getValue().getRotation();
            
            // THEN GO THROUGH ALL LINES THAT THE STATION IS ON
            JsonArrayBuilder stationLinesArrayBuilder = Json.createArrayBuilder();
            LinkedList<String> stationLines = entry.getValue().getLines();
            for (String s : stationLines) { 
                stationLinesArrayBuilder.add(s);
            }
            JsonArray stationLinesArray = stationLinesArrayBuilder.build();
  
	    JsonObject stationJson = Json.createObjectBuilder()
                    .add(JSON_STATION_NAME, stationName)
                    .add(JSON_X, x)
                    .add(JSON_Y, y)
                    .add(JSON_STATION_ISONLINE, isOnLine)
                    .add(JSON_STATION_LOCATION, location)
                    .add(JSON_STATION_ROTATION, rotation)
                    .add(JSON_STATION_LINES, stationLinesArray).build();
	    arrayBuilder.add(stationJson);
	}
	JsonArray stationsArray = arrayBuilder.build();
              
	// THEN PUT IT ALL TOGETHER IN A JsonObject
	JsonObject dataManagerJSO = Json.createObjectBuilder()
		.add(JSON_NAME, metroName)
		.add(JSON_LINES, linesArray)
                .add(JSON_STATIONS, stationsArray).build();
	
	// AND NOW OUTPUT IT TO A JSON FILE WITH PRETTY PRINTING
	Map<String, Object> properties = new HashMap<>(1);
	properties.put(JsonGenerator.PRETTY_PRINTING, true);
	JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
	StringWriter sw = new StringWriter();
	JsonWriter jsonWriter = writerFactory.createWriter(sw);
	jsonWriter.writeObject(dataManagerJSO);
	jsonWriter.close();

	// INIT THE WRITER
	OutputStream os = new FileOutputStream(filePath);
	JsonWriter jsonFileWriter = Json.createWriter(os);
	jsonFileWriter.writeObject(dataManagerJSO);
	String prettyPrinted = sw.toString();
	PrintWriter pw = new PrintWriter(filePath);
	pw.write(prettyPrinted);
	pw.close();    
    }
    
    private JsonArray makeJsonStationNamesObject(LinkedList<Shape> list) {
        
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Shape s : list) {
            if (s instanceof DraggableStation) {
                arrayBuilder.add(((DraggableStation) s).getName().getText());
            }   
        }
        return arrayBuilder.build();
    }
        
    private JsonObject makeJsonColorObject(Color color) {
	JsonObject colorJson = Json.createObjectBuilder()
		.add(JSON_RED, color.getRed())
		.add(JSON_GREEN, color.getGreen())
		.add(JSON_BLUE, color.getBlue())
		.add(JSON_ALPHA, color.getOpacity()).build();
	return colorJson;
    }
      
    /**
     * This method loads data from a JSON formatted file into the data 
     * management component and then forces the updating of the workspace
     * such that the user may edit the data.
     * 
     * @param data Data management component where we'll load the file into.
     * 
     * @param filePath Path (including file name/extension) to where
     * to load the data from.
     * 
     * @throws IOException Thrown should there be an error reading
     * in data from the file.
     */
    @Override
    public void loadData(AppDataComponent data, String filePath) throws IOException {
	// CLEAR THE OLD DATA OUT
	mmmData dataManager = (mmmData)data;
	dataManager.resetData();
	
	// LOAD THE JSON FILE WITH ALL THE DATA THEN CREATE JSON ARRAY
	JsonObject json = loadJSONFile(filePath);
        String metroName = json.getString(JSON_NAME);
        JsonArray jsonLineArray = json.getJsonArray(JSON_LINES);
        
        // SET METRO MAP NAME
        app.getGUI().getFileController().setFileName(metroName);
        
	// AND NOW LOAD ALL THE SHAPES
	for (int i = 0; i < jsonLineArray.size(); i++) {
            // GET THE JSON OBJECT
	    JsonObject JsonLine = jsonLineArray.getJsonObject(i);
            
            // GET ALL VALUES FROM THE JSON OBJECT
            // READ JSON OBJECT
            String lineName = JsonLine.getString(JSON_LINE_NAME);
            Color lineColor = loadColor(JsonLine, JSON_LINE_COLOR);
            boolean isCircular = JsonLine.getBoolean(JSON_LINE_CIRCULAR);
            JsonArray texts = JsonLine.getJsonArray(JSON_LINE_TEXTS);
            JsonArray stations = JsonLine.getJsonArray(JSON_LINE_STATIONS);
            JsonArray transfers = JsonLine.getJsonArray(JSON_LINE_TRANSFERS);

            // CREATE LINE
            mmmLine newLine = new mmmLine(lineName, lineColor);
                              
            // THEN ADD IT TO THE HASH MAP
            dataManager.getLines().put(lineName, newLine);
            
            // UPDATE THE COMBOBOX
            dataManager.getMetroLineComboBox().getItems().add(lineName);
                       
            // SET VALUES
            newLine.setCircular(isCircular);
            loadTransfers(transfers, newLine);

            // LOAD START TEXT OF THE LINE
            Line firstConnection = loadStartText(texts, dataManager, newLine);
            
            // LOAD ALL STATIONS IN LINE
	    Line lastConnection = loadStation(stations, dataManager, newLine, firstConnection);
            
            // LOAD END TEXT OF THE LINE
            loadEndText(texts, dataManager, newLine, lastConnection);
	}
        
        // SET THE STATE
        dataManager.setState(mmmState.SELECTING_MODE);
    }
    
    private double getDataAsDouble(JsonObject json, String dataName) {
	JsonValue value = json.get(dataName);
	JsonNumber number = (JsonNumber)value;
	return number.bigDecimalValue().doubleValue();	
    }
    
    private void loadTransfers(JsonArray transfersJson, mmmLine line) {
        // GET LIST FROM LINE
        LinkedList<String> transfers = line.getTransfers();
        
        // THEN ADD TRANSFERS
        for (int i = 0; i < transfersJson.size(); i++) {
            // GET THE TRANSFER
	    String transfer = transfersJson.getString(i);
            
            // THEN ADD IT TO THE LIST
            transfers.add(transfer);
        }
    }
    
    private Line loadStartText(JsonArray texts, mmmData data, mmmLine line) {
        // GET LIST FROM LINE
        LinkedList<Shape> shapes = line.getShapes();
        
        // GET THE STATION JSON OBJECT
        JsonObject textObject = texts.getJsonObject(0);
        
        // GET VALUES FROM THE OBJECT
        String textName = textObject.getString(JSON_LINE_NAME);
        int x = textObject.getInt(JSON_X);
        int y = textObject.getInt(JSON_Y);
        
        // SET TEXT
        DraggableEnd newStart = new DraggableEnd(x, y);
        newStart.update(x, y);
        newStart.setText(textName);
        
        // SET LINE
        Line newLine = new Line();
        newLine.setStroke(line.getLineColor());
        newLine.setStrokeWidth(5);
        
        // THEN BIND THEM TOGETHER
        newLine.startXProperty().bind(newStart.xProperty());
        newLine.startYProperty().bind(newStart.yProperty().add(20));
        
        // THEN ADD TO THE LIST
        shapes.add(newStart);
        shapes.add(newLine);
        data.addShape(newLine);
        data.addShape(newStart);  
        
        return newLine;
    }
    
    private void loadEndText(JsonArray texts, mmmData data, mmmLine line, Line connection) {
        // GET LIST FROM LINE
        LinkedList<Shape> shapes = line.getShapes();
        
        // GET THE STATION JSON OBJECT
        JsonObject textObject = texts.getJsonObject(1);
        
        // GET VALUES FROM THE OBJECT
        String textName = textObject.getString(JSON_LINE_NAME);
        int x = textObject.getInt(JSON_X);
        int y = textObject.getInt(JSON_Y);
        
        // SET TEXT
        DraggableEnd newEnd = new DraggableEnd(x, y);
        newEnd.update(x, y);
        newEnd.setText(textName);
        
        // THEN BIND THEM TOGETHER
        connection.endXProperty().bind(newEnd.xProperty());
        connection.endYProperty().bind(newEnd.yProperty().subtract(20));
     
        // THEN ADD TO THE LIST
        shapes.add(newEnd);
        data.addShape(newEnd);  
    }
    
    private Line loadStation(JsonArray stations, mmmData data, mmmLine line, Line connection) {
        // GET LIST FROM LINE
        LinkedList<Shape> shapes = line.getShapes();
        
        // THEN ADD STATIONS
        Line oldLine = connection;   
        for (int i = 0; i < stations.size(); i++) {
            // GET THE STATION JSON OBJECT
	    JsonObject stationObject = stations.getJsonObject(i);
            
            // GET VALUES FROM THE OBJECT
            String stationName = stationObject.getString(JSON_STATION_NAME);
            int x = stationObject.getInt(JSON_X);
            int y = stationObject.getInt(JSON_Y);
            boolean isOnLine = stationObject.getBoolean(JSON_STATION_ISONLINE);
            int location = stationObject.getInt(JSON_STATION_LOCATION);
            int rotation = stationObject.getInt(JSON_STATION_ROTATION);
            
            // CHECK IF THE STATION IS ALREADY ON THE MAP
            if (!data.getStations().containsKey(stationName)) {
                // SET STATION
                DraggableStation newStation = new DraggableStation(x, y);
                newStation.update(x, y);

                // SET STATION TEXT
                newStation.initNewName();
                newStation.getName().setText(stationName);

                // THEN BIND THE STATION LABEL
                newStation.getName().xProperty().bind(newStation.centerXProperty().add(25));
                newStation.getName().yProperty().bind(newStation.centerYProperty().subtract(10));

                // SET STATION VALUE
                newStation.setRadius(10);
                newStation.setFill(Color.WHITE);
                newStation.setStrokeWidth(2);
                newStation.setStroke(Color.BLACK);            
                newStation.setIsOnLine(isOnLine);
                newStation.getLines().add(line.getLineName());
                newStation.moveLabel(location - 1);
                newStation.rotateLabel(rotation - 1);

                // SET LINE
                Line newLine = new Line();
                newLine.setStroke(line.getLineColor());
                newLine.setStrokeWidth(5);

                // AND BIND LINES AND STATIONS
                oldLine.endXProperty().bind(newStation.centerXProperty());
                oldLine.endYProperty().bind(newStation.centerYProperty());
                newLine.startXProperty().bind(newStation.centerXProperty());
                newLine.startYProperty().bind(newStation.centerYProperty());

                // SYNC WITH WORKSPACE
                data.addStationToComboBox(stationName);

                // THEN ADD TO THE LIST
                shapes.add(newStation);
                shapes.add(newLine);
                data.getStations().put(stationName, newStation);
                data.addShape(newLine);
                data.addShape(newStation);
                data.addShape(newStation.getName());

                oldLine = newLine;
            }
            else {
                // GET THE EXISTING STATION
                DraggableStation oldStation = data.getStations().get(stationName);
                
                // UPDATE THE EXISTING STATION
                oldStation.getLines().add(line.getLineName());
                
                // SET LINE
                Line newLine = new Line();
                newLine.setStroke(line.getLineColor());
                newLine.setStrokeWidth(5);

                // AND BIND LINES AND STATIONS
                oldLine.endXProperty().bind(oldStation.centerXProperty());
                oldLine.endYProperty().bind(oldStation.centerYProperty());
                newLine.startXProperty().bind(oldStation.centerXProperty());
                newLine.startYProperty().bind(oldStation.centerYProperty());

                // THEN ADD TO THE LIST
                shapes.add(oldStation);
                shapes.add(newLine);
                data.addShape(newLine);
                data.removeShape(oldStation);
                data.removeShape(oldStation.getName());
                data.addShape(oldStation);
                data.addShape(oldStation.getName());

                oldLine = newLine;
            }  
	}
        return oldLine;
    }
    
    private Color loadColor(JsonObject json, String colorToGet) {
	JsonObject jsonColor = json.getJsonObject(colorToGet);
	double red = getDataAsDouble(jsonColor, JSON_RED);
	double green = getDataAsDouble(jsonColor, JSON_GREEN);
	double blue = getDataAsDouble(jsonColor, JSON_BLUE);
	double alpha = getDataAsDouble(jsonColor, JSON_ALPHA);
	Color loadedColor = new Color(red, green, blue, alpha);
	return loadedColor;
    }

    // HELPER METHOD FOR LOADING DATA FROM A JSON FORMAT
    private JsonObject loadJSONFile(String jsonFilePath) throws IOException {
	InputStream is = new FileInputStream(jsonFilePath);
	JsonReader jsonReader = Json.createReader(is);
	JsonObject json = jsonReader.readObject();
	jsonReader.close();
	is.close();
	return json;
    }
    
    /**
     * This method is for exporting user work into JSON and image file format.
     * 
     * @param data The data management component for this application.
     * 
     * @param filePath Path (including file name/extension) to where
     * to save the data to.
     * 
     * @throws IOException Thrown should there be an error writing 
     * out data to the file.
     */
    @Override
    public void exportData(AppDataComponent data, String filePath) throws IOException {
        // GET THE DATA
	mmmData dataManager = (mmmData) data;
        
        // GET THE NAME OF THE METRO SYSTEM
        String metroName = app.getGUI().getFileController().getFileName();
        if (metroName == null) {
            metroName = "NO NAME";
        }

	// NOW BUILD LINE OBJECTS TO SAVE
	JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
	HashMap<String, mmmLine> lines = dataManager.getLines();
	for (HashMap.Entry<String, mmmLine> entry : lines.entrySet()) {
            String lineName = entry.getValue().getLineName();
            boolean circular = entry.getValue().isCircular();
            JsonObject colorJson = makeJsonColorObject(entry.getValue().getLineColor());
            JsonArray stationNamesJson = makeJsonStationNamesObject(entry.getValue().getShapes());
  
	    JsonObject lineJson = Json.createObjectBuilder()
                    .add(JSON_NAME, lineName)
                    .add(JSON_CIRCULAR, circular)
                    .add(JSON_COLOR, colorJson)
                    .add(JSON_STATION_NAMES, stationNamesJson).build();
	    arrayBuilder.add(lineJson);
	}
	JsonArray linesArray = arrayBuilder.build();
	
        // THEN BUILD STATION OBJECTS TO SAVE
        arrayBuilder = Json.createArrayBuilder();
        HashMap<String, DraggableStation> stations = dataManager.getStations();
	for (HashMap.Entry<String, DraggableStation> entry : stations.entrySet()) {
            String stationName = entry.getValue().getName().getText();
            double x = entry.getValue().getCenterX();
            double y = entry.getValue().getCenterY();
  
	    JsonObject stationJson = Json.createObjectBuilder()
                    .add(JSON_NAME, stationName)
                    .add(JSON_X, x)
                    .add(JSON_Y, y).build();
	    arrayBuilder.add(stationJson);
	}
	JsonArray stationsArray = arrayBuilder.build();
        
        
	// THEN PUT IT ALL TOGETHER IN A JsonObject
	JsonObject dataManagerJSO = Json.createObjectBuilder()
		.add(JSON_NAME, metroName)
		.add(JSON_LINES, linesArray)
                .add(JSON_STATIONS, stationsArray)
		.build();
	
	// AND NOW OUTPUT IT TO A JSON FILE WITH PRETTY PRINTING
	Map<String, Object> properties = new HashMap<>(1);
	properties.put(JsonGenerator.PRETTY_PRINTING, true);
	JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
	StringWriter sw = new StringWriter();
	JsonWriter jsonWriter = writerFactory.createWriter(sw);
	jsonWriter.writeObject(dataManagerJSO);
	jsonWriter.close();

	// INIT THE WRITER
	OutputStream os = new FileOutputStream(filePath);
	JsonWriter jsonFileWriter = Json.createWriter(os);
	jsonFileWriter.writeObject(dataManagerJSO);
	String prettyPrinted = sw.toString();
	PrintWriter pw = new PrintWriter(filePath);
	pw.write(prettyPrinted);
	pw.close();        
    }
    
    /**
     * This method is for exporting user work into JSON and image file format.
     * 
     * @param data The data management component for this application.
     * 
     * @param filePath Path (including file name/extension) to where
     * to save the data to.
     * 
     * @throws IOException Thrown should there be an error writing 
     * out data to the file.
     */
    @Override
    public void exportImage(AppDataComponent data, String filePath) throws IOException {
        // GET THE DATA
	mmmData dataManager = (mmmData) data;
        
        // GET THE IMAGE
        WritableImage image = dataManager.takeSnapShot();
        
        // CREATE NEW FILE
        File file = new File(filePath);
        
        // THEN WRITE FILE WITH GIVEN IMAGE
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
    }
    
    /**
     * This method is provided to satisfy the compiler, but it
     * is not used by this application.
     */
    @Override
    public void importData(AppDataComponent data, String filePath) throws IOException {
	// AGAIN, WE ARE NOT USING THIS IN THIS ASSIGNMENT
    }
}
