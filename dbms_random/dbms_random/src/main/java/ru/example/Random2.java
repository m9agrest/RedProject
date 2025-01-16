package ru.example;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class Random2 {
    private static final long MULTIPLIER = 1664525;
    private static final long INCREMENT = 1013904223;
    private static final long MODULUS = (1L << 32);


    public static int round(int seed, int count){

        for(int i = 0; i < count; i++){
            seed = random(seed);
        }
        return seed;
    }
    public static int random(int seed){
        long seed2 = (MULTIPLIER * seed + INCREMENT) % MODULUS;
        return (int) (seed2 ); // Возвращаем только положительные числа
    }
    public static int seed(String seed){

        if(seed == null){
            throw new IllegalArgumentException("Seed is null.");
        }
        try{
            return Integer.parseInt(seed);
        }catch (Exception e){
            return seed.hashCode();
        }
    }



    public static BigDecimal value(int seed){
        BigDecimal val = new BigDecimal(Math.abs(seed));
        return val.divide(new BigDecimal(Integer.MAX_VALUE), 38, RoundingMode.HALF_UP);
    }
    public static BigDecimal value(int seed, BigDecimal low, BigDecimal high){
        if (low.compareTo(high) > 0) {
            BigDecimal temp = low;
            low = high;
            high = temp;
        }
        return value(seed).multiply(high.subtract(low)).add(low);
    }
    public static BigDecimal normal(int seed) {
        //https://torofimofu.blogspot.com/2018/02/oracle-11g-ii.html?m=1
        return BigDecimal.valueOf((new Random(seed)).nextGaussian());
    }
    public static final String charsetP;
    public static final String charsetU = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String charsetL = "abcdefghijklmnopqrstuvwxyz";
    public static final String charsetA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    public static final String charsetX = "0123456789" + charsetU;
    static {
        StringBuilder str0 = new StringBuilder();
        //https://autoit-script.ru/docs/appendix/ascii.htm
        for (char c = 32; c <= 126; c++) {
            str0.append(c);
        }
        charsetP = str0.toString();
    }
    public static String string(int seed, String opt, int len){

        if(opt == null || opt.length() != 1){
            throw new IllegalArgumentException("The opt must be a single character.");
        }
        char optC = opt.charAt(0);
        if(len <= 0){
            return null;
        }
        StringBuilder str = new StringBuilder();
        String charset;
        switch (optC) {
            case 'U': // Только заглавные буквы
            case 'u':
                charset = charsetU;
                break;
            case 'L':  // Только строчные буквы
            case 'l':
                charset = charsetL;
                break;
            case 'A':  // Буквы и цифры
            case 'a':
                charset = charsetA;
                break;
            case 'X':  // цифры и заглавные буквы символы
            case 'x':
                charset = charsetX;
                break;
            case 'P':  // любые печатные символы
            case 'p': //aoh>Q+!i.N"SmEL:xYlMhCW!a:jo3_w#8z9]AX'hnonQHbvnu/]+yozC*T/dkxmFJ;2/~T#gJ[~2&NpWIIz-OsGBPHC-Ru/9,!aH7yW@j]= >k{S}+q;m5h{
                charset = charsetP;
                break;
            default:
                charset = charsetU;
                break;
        }
        for (int i = 0; i < len; i++) {
            seed = random(seed);
            int _seed = seed < 0 ? -seed : seed;
            str.append(charset.charAt(_seed % charset.length()));

        }
        return str.toString();
    }


    public static void main(String[] args){
        /*int seed = 0;
        for(int i = 0; i < 10000; i++){
            seed = random(seed);
            System.out.println(seed);
        }*/
    }

}
