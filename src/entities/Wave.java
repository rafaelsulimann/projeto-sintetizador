package entities;

import java.util.Random;

public abstract class Wave {

    public double generateWave(double time, double frequencia) {
        return 0;
    }

    public double generateNoise(){
        Random rand = new Random();

        // Gera um número aleatório de ponto flutuante entre 0.0 e 1.0
        double randomValue = rand.nextDouble();

        // Ajusta o cálculo para corresponder ao cálculo do C++
        return 2.0 * randomValue - 1.0;
    }
}
