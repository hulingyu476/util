public class RealImage implements Image {
    private String fileName;

    public RealImage(String fileName){
        this.fileName = fileName;
        loadFromeDisk(fileName);
    }

    @Override
    public void display() {
        System.out.println("Displaying "+ fileName);
    }

    private void loadFromeDisk(String fileName) {
        System.out.println("Loading " + fileName);
    }

}
