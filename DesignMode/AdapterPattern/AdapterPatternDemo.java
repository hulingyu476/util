public class AdapterPatternDemo {
    public static void main(String[] args) {
        AudioPlayer audioPlayer = new AudioPlayer();

        audioPlayer.play("mp3","Moust.mp3");
        audioPlayer.play("vlc","Very Important.vlc");
        audioPlayer.play("mp4","Show You Hand.mp4");
        audioPlayer.play("avi","Always Love You.avi");
    }
    
}
