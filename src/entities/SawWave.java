package entities;

public class SawWave extends Wave {

    @Override
    public double generateWave(double time, double frequencia) {
        return (2 * (time * frequencia - Math.floor(time * frequencia + 0.5)));
    }

}
