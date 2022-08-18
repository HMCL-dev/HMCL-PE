package wang.switchy.hin2n.event;

public class LogChangeEvent {

    private final String txtPath;
    public LogChangeEvent(String txtPath){
        this.txtPath = txtPath;
    }

    public String getTxtPath() {
        return txtPath;
    }
}
