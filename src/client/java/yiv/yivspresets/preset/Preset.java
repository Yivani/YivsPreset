package yiv.yivspresets.preset;

public class Preset {
    public String id;
    public String name;
    public PresetProfiles.Profile profile;

    public Preset() {}

    public Preset(String id, String name, PresetProfiles.Profile profile) {
        this.id = id; this.name = name; this.profile = profile;
    }
}
