import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JFrame;

import entities.EnvelopeADSR;
import entities.SawWave;
import entities.SineWave;
import entities.SquareWave;
import entities.TriangleWave;
import entities.Wave;

public class App {

    private static boolean isPlaying = false;
    private static SourceDataLine line;
    private static AudioFormat format;
    private static Wave wave;
    private static EnvelopeADSR adsr;
    private static final int SAMPLE_RATE = 44100;
    private static final int BUFFER_SIZE = 2048; // Buffer menor para reduzir a latência
    // Variável para armazenar o momento da pressão da tecla
    private static long triggerOnTimeMillis;

    public static void main(String[] args) throws LineUnavailableException {
        // Configurações de formato de áudio
        final int BITS_PER_SAMPLE = 16;
        final int CHANNELS = 1;
        final boolean SIGNED = true;
        final boolean BIG_ENDIAN = false;

        // Define o formato de áudio (taxa de amostragem, bits por amostra, canais,
        // etc.)
        format = new AudioFormat(SAMPLE_RATE, BITS_PER_SAMPLE, CHANNELS, SIGNED, BIG_ENDIAN);

        /**
         * final boolean SIGNED = true; // Dados de áudio assinados
         * 
         * Este atributo refere-se à forma como os valores de áudio são representados.
         * Em sistemas de áudio digital, as amostras de áudio podem ser "assinadas" ou
         * "não assinadas".
         * Valores Assinados (SIGNED = true): Significa que o intervalo de valores cobre
         * tanto números positivos quanto negativos. Por exemplo, em uma amostra de 16
         * bits, os valores podem variar de -32768 a 32767. Isso é útil para representar
         * ondas sonoras, pois elas têm partes positivas e negativas (acima e abaixo da
         * linha de base, respectivamente).
         * Valores Não Assinados: Os valores variam apenas no campo positivo. Por
         * exemplo, em 16 bits, variariam de 0 a 65535. Isso não é típico para áudio,
         * pois não representa bem a natureza das ondas sonoras.
         * Portanto, SIGNED = true é usado para representar corretamente as ondas
         * sonoras que têm oscilações acima e abaixo de um ponto central (zero).
         * final boolean BIG_ENDIAN = false; // Ordem dos bytes (little endian é comum)
         * 
         * Esta configuração determina a ordem na qual os bytes de uma amostra de áudio
         * são armazenados e transmitidos. Em sistemas de computador, a ordem dos bytes
         * pode ser "big endian" ou "little endian".
         * Big Endian (BIG_ENDIAN = true): Significa que os bytes mais significativos
         * são armazenados e transmitidos primeiro. Por exemplo, para o número 0x1234, o
         * byte 0x12 seria armazenado/transmitido antes do 0x34.
         * Little Endian (BIG_ENDIAN = false): Aqui, os bytes menos significativos são
         * armazenados e transmitidos primeiro. No mesmo exemplo, 0x34 seria
         * armazenado/transmitido antes do 0x12.
         * A escolha entre big endian e little endian depende do formato de áudio e da
         * plataforma. Little endian é comum em muitos sistemas de computador e formatos
         * de áudio, portanto, é frequentemente a escolha padrão, como indicado por
         * BIG_ENDIAN = false.
         * Em resumo, SIGNED = true é usado para representar corretamente a natureza das
         * ondas sonoras, e BIG_ENDIAN = false é escolhido para alinhar com a ordem de
         * bytes comum em sistemas de computador e formatos de áudio. Essas
         * configurações garantem que os dados de áudio sejam interpretados corretamente
         * tanto pelo software quanto pelo hardware durante a reprodução.
         */

        // Obtém uma linha de dados de áudio para reprodução
        line = AudioSystem.getSourceDataLine(format);

        // Criando Amp Envelope
        adsr = new EnvelopeADSR();
        // Define o tipo da onda e sua amplitude
        wave = new SineWave();

        // Abre a linha de áudio com um buffer menor
        line.open(format, BUFFER_SIZE);
        // Cria e configura a janela
        JFrame frame = new JFrame("Sound Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        String keyboard = "awsedrftgyhu";
        double oitavaBase = 70.0;
        double tom = Math.pow(2.0, 1.0 / 12);

        // Adiciona um KeyListener
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                char keyChar = e.getKeyChar();
                int key = keyboard.indexOf(keyChar);
                if (key != -1 && !isPlaying) {
                    double hzFrequency = oitavaBase * Math.pow(tom, key);
                    // Armazena o momento atual em milissegundos
                    triggerOnTimeMillis = System.currentTimeMillis();
                    adsr.noteOn(0.0); // O triggerOnTime sempre será 0.0, pois é o início do pressionamento
                    startAudio(hzFrequency);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                char keyChar = e.getKeyChar();
                int key = keyboard.indexOf(keyChar);
                if (key != -1 && isPlaying) {
                    // Calcula o tempo decorrido desde o pressionamento da tecla
                    long triggerOffTimeMillis = System.currentTimeMillis() - triggerOnTimeMillis;
                    double triggerOffTimeSeconds = triggerOffTimeMillis / 1000.0; // Converte para segundos

                    adsr.noteOff(triggerOffTimeSeconds);
                    double hzFrequency = oitavaBase * Math.pow(tom, key);

                    stopAudio(hzFrequency);
                }
            }
        });

        // Mostra a janela
        frame.setVisible(true);
    }

    private static void startAudio(double hzFrequency) {

        isPlaying = true;
        line.start(); // Inicia a reprodução na linha existente

        // Inicia uma thread para tocar o áudio
        new Thread(() -> {
            double time = 0; // Inicializa o tempo
            double timeIncrement = 1.0 / SAMPLE_RATE; // Calcula o incremento de tempo para cada amostra

            while (isPlaying) {
                double sample = adsr.getADSAmplitude(time) * wave.generateWave(time, hzFrequency); // Gera a amostra
                                                                                                   // usando
                                                                                                   // o tempo atual
                byte[] data = new byte[2];
                int sampleInt = (int) (sample * 32767);
                data[0] = (byte) (sampleInt & 0xFF);
                data[1] = (byte) ((sampleInt >> 8) & 0xFF);
                line.write(data, 0, 2);

                time += timeIncrement; // Incrementa o tempo
            }

        }).start();
    }

    private static void stopAudio(double hzFrequency) {
        isPlaying = false;
        // Aguarda um pouco para garantir que a thread da nota original tenha terminado
        if (adsr.getReleaseTime() <= 0.01) {
            line.stop();
            return;
        }
        
        new Thread(() -> {
            double time = 0; // Inicializa o tempo
            double timeIncrement = 1.0 / SAMPLE_RATE; // Calcula o incremento de tempo para cada amostra
            double adsrVolume = 0.1;
            while (!isPlaying && adsrVolume > 0.0) {
                adsrVolume = adsr.getReleaseAmplitude(time);
                if (adsrVolume <= 0.0) {
                    adsrVolume = 0.0;
                    line.stop();
                    break;
                }
                double sample = adsrVolume * wave.generateWave(time, hzFrequency); // Gera a amostra usando o
                byte[] data = new byte[2];
                int sampleInt = (int) (sample * 32767);
                data[0] = (byte) (sampleInt & 0xFF);
                data[1] = (byte) ((sampleInt >> 8) & 0xFF);
                line.write(data, 0, 2);
    
                time += timeIncrement; // Incrementa o tempo
            }
        }).start();
    }

}