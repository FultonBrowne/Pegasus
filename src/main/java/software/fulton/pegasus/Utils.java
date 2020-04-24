package software.fulton.pegasus;

public class Utils {
    public String[] parseWord(String string){
        return string.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase().split("\\s+");
    }
}
