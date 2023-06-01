package tankrotationexample;

import tankrotationexample.game.Sound;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

public class Resources {
    private static final Map<String, BufferedImage> sprites = new HashMap<>();
    private static final Map<String, List<BufferedImage>> animations = new HashMap<>();
    private static final Map<String, Integer> animationInfo = new HashMap<>() {{
        put("bullet", 32);
        put("nuke", 24);
    }};
    private static final Map<String, Sound> sounds = new HashMap<>();


    private static BufferedImage loadSprite(String path) throws IOException {
        return ImageIO.read(
                Objects
                        .requireNonNull(
                                Resources
                                        .class
                                        .getClassLoader()
                                        .getResource(path),
                                "Resource %s is not found".formatted(path))
        );
    }

    private static Sound loadSound (String path) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        AudioInputStream ais = AudioSystem.getAudioInputStream(
                Resources.class.getClassLoader().getResources(path).nextElement());
        Clip c = AudioSystem.getClip();
        c.open(ais);
        Sound s = new Sound(c);
        s.setVolume(0.2f);
        return s;
    }
    private static void initSprites() {
        try {
            Resources.sprites.put("tank1", loadSprite("tank/tank1.png"));
            Resources.sprites.put("tank2", loadSprite("tank/tank2.png"));
            Resources.sprites.put("menu", loadSprite("menu/title.png"));
            Resources.sprites.put("win", loadSprite("menu/win.png"));
            Resources.sprites.put("lose", loadSprite("menu/lose.png"));
            Resources.sprites.put("bullet", loadSprite("bullet/bullet.jpg"));
            Resources.sprites.put("rocket1", loadSprite("bullet/rocket1.png"));
            Resources.sprites.put("rocket2", loadSprite("bullet/rocket2.png"));
            Resources.sprites.put("floor", loadSprite("floor/bg.JPG"));
            Resources.sprites.put("wall", loadSprite("walls/unbreak.jpg"));
            Resources.sprites.put("break1", loadSprite("walls/break1.PNG"));
            Resources.sprites.put("break2", loadSprite("walls/break2.PNG"));
            Resources.sprites.put("speed", loadSprite("powerups/speed.PNG"));
            Resources.sprites.put("health", loadSprite("powerups/health.png"));
            Resources.sprites.put("shield", loadSprite("powerups/shield.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static BufferedImage getSprite(String type) {
        if (!Resources.sprites.containsKey(type)){
            throw new RuntimeException("%s is missing from resource map".formatted(type));
        }
        return Resources.sprites.get(type);
    }

    private static void initAnimations() {
        String animationBasePath = "animations/%s/expl_%04d.png";
        Resources.animationInfo.forEach((animationName, frameCount) -> {
            List<BufferedImage> temp = new ArrayList<>(frameCount);
            try {
                for (int i = 0; i < frameCount; i++) {
                    String framePath = animationBasePath.formatted(animationName, i);
                    temp.add(loadSprite(framePath));
                }
                Resources.animations.put(animationName, temp);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    public static List<BufferedImage> getAnimation(String type) {
        if (!Resources.animations.containsKey(type)){
            throw new RuntimeException("%s is missing from resource map".formatted(type));
        }
        return Resources.animations.get(type);
    }

    private static void initSounds() {
        try {
            Resources.sounds.put("bullet", Resources.loadSound("sounds/bullet.wav"));
            Resources.sounds.put("pickup", Resources.loadSound("sounds/pickup.wav"));
            Resources.sounds.put("hit", Resources.loadSound("sounds/shotexplosion.wav"));
            Resources.sounds.put("fire", Resources.loadSound("sounds/shotfiring.wav"));
            Resources.sounds.put("background", Resources.loadSound("sounds/BGM1.wav"));
            Resources.sounds.put("win", Resources.loadSound("sounds/win.wav"));
            Resources.sounds.put("lose", Resources.loadSound("sounds/lose.wav"));
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    public static Sound getSound(String type) {
        if (!Resources.sounds.containsKey(type)){
            throw new RuntimeException("%s is missing from resource map".formatted(type));
        }
        return Resources.sounds.get(type);
    }

    public static void loadAssets() {
        initSprites();
        initAnimations();
        initSounds();
    }
}