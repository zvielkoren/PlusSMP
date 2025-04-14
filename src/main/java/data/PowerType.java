package data;

public class PowerType<T> {
    public static final String DEFAULT_POWER_UNIT = "unit";
    private final String name;
    private final String unit;

    public PowerType(String name, String unit) {
        this.name = name;
        this.unit = unit;
    }
} 