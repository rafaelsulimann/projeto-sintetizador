package entities;
public class SineWave extends Wave{

    @Override
    public double generateWave(double time, double frequencia) {
        return Math.sin(frequencia * 2 * Math.PI * time);
    }
    
}
