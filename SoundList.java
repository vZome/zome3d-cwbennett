import java.applet.AudioClip;
import javax.swing.*;
import java.net.URL;

/**
 * Loads and holds a bunch of audio files whose locations are specified
 * relative to a fixed base URL.
 */
class SoundList extends java.util.Hashtable {
    JApplet applet;
    URL baseURL;

    public SoundList(URL baseURL, int numberOfSounds)
//    public SoundList(URL baseURL)
    {
        super(numberOfSounds);
//        super(2);
        this.baseURL = baseURL;
    }

    public void startLoading(String relativeURL) {
        new SoundLoader(this, baseURL, relativeURL);
    }

    public AudioClip getClip(String relativeURL) {
        return (AudioClip)get(relativeURL);
    }

    public void putClip(AudioClip clip, String relativeURL) {
        put(relativeURL, clip);
    }
}
