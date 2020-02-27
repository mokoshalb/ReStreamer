import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.xml.bind.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class addController{
    private TextField source, destination;
    private List<stream> stream = FXCollections.observableArrayList();

    public addController(List<stream> list){
        Stage window = new Stage();
        Label sourceLabel = new Label("Stream Source:");
        sourceLabel.setLayoutX(256.0);
        sourceLabel.setLayoutY(14.0);
        sourceLabel.setFont(Font.font(14));
        Label destinationLabel = new Label("Output Server:");
        destinationLabel.setLayoutX(259.0);
        destinationLabel.setLayoutY(68.0);
        destinationLabel.setFont(Font.font(14));
        source = new TextField();
        source.setPromptText("http://192.11.42.8/live.m3u8");
        source.setLayoutX(8.0);
        source.setLayoutY(38.0);
        source.setPrefHeight(25.0);
        source.setPrefWidth(583.0);
        destination = new TextField();
        destination.setPromptText("rtmp://192.11.42.8/live/ufc");
        destination.setLayoutX(6.0);
        destination.setLayoutY(88.0);
        destination.setPrefHeight(25.0);
        destination.setPrefWidth(583.0);
        Button addButton = new Button("Add Stream");
        addButton.setLayoutX(280.0);
        addButton.setLayoutY(132.0);
        addButton.setFont(Font.font(14));
        addButton.setMnemonicParsing(false);
        addButton.setOnAction(event -> {
            try {
                JAXBContext contextObj = JAXBContext.newInstance(streams.class);
                Unmarshaller unmarshaller = contextObj.createUnmarshaller();
                streams streams = (streams) unmarshaller.unmarshal(new File("streams.xml"));
                XPath xpath = XPathFactory.newInstance().newXPath();
                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse("streams.xml");
                int id = Integer.parseInt(xpath.evaluate("//streams/stream[last()]/@id", doc)) + 1;
                stream stream = new stream(id, source.getText().trim(), destination.getText().trim(), false, "");
                List<stream> streamList = streams.getStreams();
                streamList.add(stream);
                streams.setStreams(streamList);
                Marshaller marshaller = contextObj.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                marshaller.marshal(streams, new File("streams.xml"));
                this.setStream(streamList);
                window.close();
            } catch (JAXBException | ParserConfigurationException | IOException | XPathExpressionException | SAXException e) {
                e.printStackTrace();
            }
        });
        AnchorPane layout = new AnchorPane();
        layout.prefHeight(176);
        layout.prefWidth(600);
        layout.getChildren().addAll(source, destination, sourceLabel, destinationLabel, addButton);
        Scene addScene = new Scene(layout);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setScene(addScene);
        window.setTitle("Add Stream");
        window.setResizable(false);
        window.setOnCloseRequest(e -> setStream(list));
        window.showAndWait();
    }

    public List<stream> getStream() {
        stream.removeIf((stream stream) -> (stream.getId() == 0));
        return stream;
    }

    public void setStream(List<stream> stream) {
        this.stream = stream;
    }
}
