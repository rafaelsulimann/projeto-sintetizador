public class App2 {
    public static void main(String[] args) {
        double oitavaBase = 110.0;
        double tom = Math.pow(2.0, 1.0 / 12);

        int midiNote = 47; // C4
        double hzFrequency = Math.pow(2, (midiNote - 69) / 12.0) * 440;

        System.out.printf("HZ = %.2f%n", hzFrequency);

        System.out.println(tom);
        System.out.printf("46 = " + String.format("%.2f",oitavaBase*Math.pow(tom, 2)));
    }
}
