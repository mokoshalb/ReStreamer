public class settings {
    private boolean overrideOutputFiles;
    private String audioCodec;
    private String videoCodec;
    private String format;

    public void settings(){

    }

    public void settings(boolean overrideOutputFiles, String audioCodec, String videoCodec, String format){
        this.overrideOutputFiles = overrideOutputFiles;
        this.audioCodec = audioCodec;
        this.videoCodec = videoCodec;
        this.format = format;
    }

    public boolean isOverrideOutputFiles() {
        return overrideOutputFiles;
    }

    public void setOverrideOutputFiles(boolean overrideOutputFiles) {
        this.overrideOutputFiles = overrideOutputFiles;
    }

    public String getAudioCodec() {
        return audioCodec;
    }

    public void setAudioCodec(String audioCodec) {
        this.audioCodec = audioCodec;
    }

    public String getVideoCodec() {
        return videoCodec;
    }

    public void setVideoCodec(String videoCodec) {
        this.videoCodec = videoCodec;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
