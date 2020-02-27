import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFmpegUtils;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;

import java.io.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class controller implements Initializable {
    @FXML private TableView<stream> streamsTable;
    @FXML private TableColumn<stream, Integer> id;
    @FXML private TableColumn<stream, String> input;
    @FXML private TableColumn<stream, String> output;
    @FXML private TableColumn<stream, Boolean> status;
    @FXML private TableColumn<stream, String> logData;

    public void addStream(ActionEvent event) throws Exception{
        File file = new File("streams.xml");
        JAXBContext jaxbContext = JAXBContext.newInstance(streams.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        streams list = (streams) jaxbUnmarshaller.unmarshal(file);
        list.getStreams().removeIf((stream stream) -> (stream.getId() == 0));
        List<stream> streamList = list.getStreams();
        addController setAdd = new addController(streamList);
        streamsTable.getItems().setAll(setAdd.getStream());
        streamsTable.refresh();
    }

    @FXML
    public void deleteStream(ActionEvent event) throws Exception{
        Alert alert;
        if(streamsTable.getSelectionModel().isEmpty()){
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Remove Stream");
            alert.setContentText("Select a stream to remove.");
        }else {
            JAXBContext jaxbContext = JAXBContext.newInstance(streams.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            streams streamData = (streams) jaxbUnmarshaller.unmarshal(new File("streams.xml"));
            Integer removeId = streamsTable.getItems().get(streamsTable.getSelectionModel().getSelectedIndex()).getId();
            streamData.getStreams().removeIf((stream stream) -> removeId.equals(stream.getId()));
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(streamData, new File("streams.xml"));
            streamsTable.getItems().setAll(parseUserList());
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Remove Stream");
            alert.setContentText("Stream Removed Successfully.");
        }
        alert.show();
    }

    @FXML
    public void deleteAllStream(ActionEvent event) throws Exception{
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to continue? (this action will remove all stream records!)", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Remove All");
        alert.showAndWait();
        if(alert.getResult() == ButtonType.YES) {
            JAXBContext jaxbContext = JAXBContext.newInstance(streams.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            streams streamData = (streams) jaxbUnmarshaller.unmarshal(new File("streams.xml"));
            streamData.getStreams().removeIf((stream stream) -> (stream.getId() != 0));
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(streamData, new File("streams.xml"));
            streamsTable.getItems().setAll(parseUserList());
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Remove All Stream");
            alert.setContentText("All Streams Removed Successfully.");
            alert.show();
        }
    }

    @FXML
    public void startStream() throws JAXBException, IOException {
        Alert alert;
        if(streamsTable.getSelectionModel().isEmpty()){
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Start Stream");
            alert.setContentText("Select a stream to start.");
        }else {
            JAXBContext jaxbContext = JAXBContext.newInstance(streams.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            streams streamData = (streams) jaxbUnmarshaller.unmarshal(new File("streams.xml"));
            int streamID = streamsTable.getItems().get(streamsTable.getSelectionModel().getSelectedIndex()).getId();
            String source = streamData.getStreams().get(streamID).getInput();
            String destination = streamData.getStreams().get(streamID).getOutput();
            streamData.getStreams().get(streamID).setStatus(true);
            FFmpeg ffmpeg = new FFmpeg("/ffmpeg/bin/ffmpeg.exe");
            FFprobe ffprobe = new FFprobe("/ffmpeg/bin/ffprobe.exe");
            FFmpegBuilder builder = new FFmpegBuilder()
                    .setInput(source)
                    .addOutput(destination)
                    .setFormat("flv")
                    .setAudioCodec("aac")
                    .setVideoCodec("libx264")
                    .done();
            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
            executor.createJob(builder).run();
            FFmpegProbeResult in = ffprobe.probe(source);
            FFmpegJob job = executor.createJob(builder, new ProgressListener() {
                // Using the FFmpegProbeResult determine the duration of the input
                final double duration_ns = in.getFormat().duration * TimeUnit.SECONDS.toNanos(1);
                @Override
                public void progress(Progress progress) {
                    double percentage = progress.out_time_ns / duration_ns;
                    // Print out interesting information about the progress
                    System.out.println(String.format(
                            "[%.0f%%] status:%s frame:%d time:%s ms fps:%.0f speed:%.2fx",
                            percentage * 100,
                            progress.status,
                            progress.frame,
                            FFmpegUtils.toTimecode(progress.out_time_ns, TimeUnit.NANOSECONDS),
                            progress.fps.doubleValue(),
                            progress.speed
                    ));
                }
            });
            job.run();
            streamsTable.getItems().setAll(parseUserList());
            streamsTable.refresh();
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Start Stream");
            alert.setContentText("Stream Started Successfully.");
        }
        alert.show();
    }

    @FXML
    public void stopStream(){

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        input.setCellValueFactory(new PropertyValueFactory<>("input"));
        output.setCellValueFactory(new PropertyValueFactory<>("output"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        logData.setCellValueFactory(new PropertyValueFactory<>("logData"));
        try {
            streamsTable.getItems().setAll(parseUserList());
        } catch (JAXBException | IOException e) {
            e.printStackTrace();
        }
    }

    public List<stream> parseUserList() throws JAXBException, IOException {
        ObservableList<stream> stream = FXCollections.observableArrayList();
        File file = new File("streams.xml");
        if(file.createNewFile()){
            FileWriter myWriter = new FileWriter("streams.xml");
            myWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<streams>\n" +
                    "    <stream id=\"0\">\n" +
                    "        <input>http</input>\n" +
                    "        <logData>log...log</logData>\n" +
                    "        <output>rtmp</output>\n" +
                    "        <status>false</status>\n" +
                    "    </stream>\n" +
                    "</streams>");
            myWriter.close();
        }
        JAXBContext jaxbContext = JAXBContext.newInstance(streams.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        streams list = (streams) jaxbUnmarshaller.unmarshal(file);
        List<stream> streamList = list.getStreams();
        for (stream streamData : streamList) {
            if(streamData.getId() == 0){
                continue;
            }
            stream.add(new stream(streamData.getId(), streamData.getInput(), streamData.getOutput(), streamData.isStatus(), streamData.getLogData()));
        }
        return stream;
    }
}