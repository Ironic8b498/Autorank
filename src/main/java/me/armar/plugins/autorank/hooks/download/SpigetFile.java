package me.armar.plugins.autorank.hooks.download;

public class SpigetFile {
    private SpigetType type;
    private double size;
    private String unit;
    private String url;

    public SpigetFile(SpigetType type, double size, String unit, String url) {
        this.type = type;
        this.size = size;
        this.unit = unit;
        this.url = url;
    }

    public SpigetType getType() {
        return this.type;
    }

    public void setType(SpigetType type) {
        this.type = type;
    }

    public double getSize() {
        return this.size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public String getUnit() {
        return this.unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
