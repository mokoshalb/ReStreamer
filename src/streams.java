import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "streams")
public class streams {

    private List<stream> streams;

    public List<stream> getStreams() {
        return streams;
    }

    @XmlElement(name = "stream")
    public void setStreams(List<stream> streams) {
        this.streams = streams;
    }

    @Override
    public String toString() {
        return streams.toString();
    }
}