package entities;

public class TriangleWave extends Wave {


    @Override
    public double generateWave(double time, double frequencia) {
        return Math.asin(Math.sin(frequencia * 2 * Math.PI * time)) * 2.0 / Math.PI;
    }

}
