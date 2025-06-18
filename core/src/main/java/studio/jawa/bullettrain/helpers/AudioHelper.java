package studio.jawa.bullettrain.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;

public class AudioHelper {
    private static final HashMap<String, Sound> sounds = new HashMap<>();
    private static final HashMap<String, Music> musics = new HashMap<>();

    public static void loadSound(String id, String path) {
        Sound sound = Gdx.audio.newSound(Gdx.files.internal(path));
        sounds.put(id, sound);
    }

    public static void loadMusic(String id, String path, boolean looping, float volume) {
        Music music = Gdx.audio.newMusic(Gdx.files.internal(path));
        music.setLooping(looping);
        music.setVolume(volume);
        musics.put(id, music);
    }

    public static void playSound(String id) {
        Sound sound = sounds.get(id);
        if (sound != null) {
            sound.play();
        }
    }

    public static void playMusic(String id) {
        Music music = musics.get(id);
        if (music != null) {
            music.play();
        }
    }

    public static void stopMusic(String id) {
        Music music = musics.get(id);
        if (music != null) {
            music.stop();
        }
    }

    public static boolean isMusicPlaying(String id) {
        Music music = musics.get(id);
        return music != null && music.isPlaying();
    }


    public static void dispose() {
        for (Sound s : sounds.values()) s.dispose();
        for (Music m : musics.values()) m.dispose();
    }
}

