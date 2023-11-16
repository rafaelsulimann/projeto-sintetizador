package entities;

public class SquareWave extends Wave{


    @Override
    public double generateWave(double time, double frequencia) {
        return this.sign(Math.sin(frequencia * 2 * Math.PI * time));

    }

    private int sign(double numero){
        if (numero > 0) {
            return 1;
        } else if (numero < 0) {
            return -1;
        } else {
            return 0;
        }
    }

}
