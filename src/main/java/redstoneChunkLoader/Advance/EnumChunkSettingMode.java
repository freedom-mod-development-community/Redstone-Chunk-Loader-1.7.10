package redstoneChunkLoader.Advance;

public enum EnumChunkSettingMode {
    None(1), Removing(2), Adding(3), Forced(4);
    public final int id;

    EnumChunkSettingMode(int id) {
        this.id = id;
    }

    public static EnumChunkSettingMode getMode(int id) {
        for (EnumChunkSettingMode mode : values()) {
            if (mode.id == id) {
                return mode;
            }
        }
        return None;
    }

}