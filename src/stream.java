import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "stream")
public class stream {
    private int id;
    private String input;
    private String output;
    private boolean status;
    private String logData;

    public stream(){
        this.id = 0;
        this.input = "";
        this.output = "";
        this.status = false;
        this.logData = "";
    }

    public stream(int id, String input, String output, boolean status, String logData){
        this.id = id;
        this.input = input;
        this.output = output;
        this.status = status;
        this.logData = logData;
    }

    @XmlAttribute
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @XmlElement
    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    @XmlElement
    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    @XmlElement
    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @XmlElement
    public String getLogData() {
        return logData;
    }

    public void setLogData(String logData) {
        this.logData = logData;
    }
}
