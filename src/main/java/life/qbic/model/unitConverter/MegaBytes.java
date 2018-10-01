package life.qbic.model.unitConverter;


class MegaBytes implements UnitDisplay{

    private String unit = "Mb";

    private double divisor = Math.pow(10, 6);


    @Override
    public double convertBytesToUnit(long bytes) {
        return (double) bytes/divisor;
    }

    @Override
    public String getUnitType() {
        return this.unit;
    }
}
