package ru.example;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
//https://docs.oracle.com/en/database/oracle/oracle-database/19/arpls/DBMS_RANDOM.html
public class DBMS_Random {
    //private static final Random random = new Random();
    private final Random random = new Random();
    //Сохранение сида
    //call DBMS_RANDOM.SEED(12345);
    //SELECT dbms_random.random() from dual
    //-1817329670
    //-610488908
    //1775775349
    //call DBMS_RANDOM.SEED(12345);
    //-1817329670
    //рестарт
    //вывод данных отличается от -610488908
    //вывод данных отличается от 1775775349

    //тест сида в редДБ
    //CALL DBMS_RANDOM.SEED(100)
    //SELECT DBMS_RANDOM.RANDOM() FROM rdb$database
    //-1193959466
    //-1139614796
    //837415749
    //-1220615319
    //CALL DBMS_RANDOM.SEED(100)
    //-1193959466
    //-1139614796
    //837415749
    //-1220615319




    /*допускает null
    pl:
    --call dbms_random.initialize(null)
    --SELECT dbms_random.random() from dual -- -1193296388
    --call dbms_random.initialize(null)
    --SELECT dbms_random.random() from dual -- -1193296388

    rdb:
    call dbms_random.initialize(null);
    SELECT dbms_random.random() from RDB$DATABASE; -- -1155484576
    call dbms_random.initialize(null);
    SELECT dbms_random.random() from RDB$DATABASE; -- -1155484576
     */
    //устаревшие функции
    //(BINARY_INTEGER)
    public void initialize(int seed){

        seed(seed);
    }
    public void terminate() {
        //ни на что не влияет
        /*
            call dbms_random.initialize(12)
            SELECT dbms_random.value(1, 2) from dual --ret 1,76644
            call dbms_random.terminate()

            call dbms_random.initialize(12)
            call dbms_random.terminate()
            SELECT dbms_random.value(1, 2) from dual --ret 1,76644
         */
    }



    /*
    проверка работы сида
    если сид (строка) состоит из числа, то он преобразуется к числу

--call dbms_random.seed(10)
--SELECT dbms_random.random() from dual -- -1644652782
--call dbms_random.seed('10')
--SELECT dbms_random.random() from dual -- -1644652782
--call dbms_random.initialize('10')
--SELECT dbms_random.random() from dual -- -1644652782

    либо, initialize принимает только числа, но приобразует их в строку


    не допускает null
 */
    //(BINARY_INTEGER)
    public void seed(int seed) {
        random.setSeed(seed);
    }
    //(VARCHAR2)
    public void seed(String seed) {
        if(seed == null){
            throw new IllegalArgumentException("Seed is null.");
        }
        try{
            seed(Integer.parseInt(seed));
        }catch (Exception e){
            seed(seed.hashCode());
        }
    }

    //BINARY_INTEGER ()
    public int random() {
        return random.nextInt(); //Returns a random integer greater or equal to -power(2,31) and less than power(2,31)
    }

    //NUMBER ()
    public BigDecimal value(){
        BigDecimal val = new BigDecimal(Math.abs(random()));
        return val.divide(new BigDecimal(Integer.MAX_VALUE), 38, RoundingMode.HALF_UP);
    }
    //NUMBER (NUMBER, NUMBER)
    public BigDecimal value(BigDecimal low, BigDecimal high){
        if (low.compareTo(high) > 0) {
            BigDecimal temp = low;
            low = high;
            high = temp;
        }
        return value().multiply(high.subtract(low)).add(low);
    }

    //NUMBER ()
    public BigDecimal normal() {
        //https://torofimofu.blogspot.com/2018/02/oracle-11g-ii.html?m=1
        return BigDecimal.valueOf(random.nextGaussian());
    }



    private static final String charsetP;
    static {
        StringBuilder str0 = new StringBuilder();
        //https://autoit-script.ru/docs/appendix/ascii.htm
        for (char c = 33; c <= 126; c++) {
            str0.append(c);
        }
        charsetP = str0.toString();
    }


    //VARCHAR2 (CHAR, NUMBER)
    public String string(String opt, int len){
        /*try (PrintWriter out = new PrintWriter("C:/Users/mina987/Desktop/RedProject/log.txt")) {
            out.println(opt);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }*/
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
                charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                break;
            case 'L':  // Только строчные буквы
            case 'l':
                charset = "abcdefghijklmnopqrstuvwxyz";
                break;
            case 'A':  // Буквы и цифры
            case 'a':
                charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
                break;
            case 'X':  // Шестнадцатеричные символы
            case 'x':
                charset = "0123456789ABCDEF";
                break;
            case 'P':  // любые печатные символы
            case 'p': //aoh>Q+!i.N"SmEL:xYlMhCW!a:jo3_w#8z9]AX'hnonQHbvnu/]+yozC*T/dkxmFJ;2/~T#gJ[~2&NpWIIz-OsGBPHC-Ru/9,!aH7yW@j]= >k{S}+q;m5h{
                charset = charsetP;
                break;
            default:
                charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                break;
        }
        for (int i = 0; i < len; i++) {
            str.append(
                    charset.charAt(
                            random.nextInt(
                                    charset.length())));
        }
        return str.toString();
    }
    /*
    0: 683178
    -1: 135884
    1: 135582
    -2: 21421
    2: 21147
    -3: 1370
    3: 1351
    -4: 37
    4: 30

    test NORMAL()*/
    public static void main(String[] arg){
        /*
        Map<Integer, Integer> map = new HashMap<>();
        for(int i = 0; i < 1000000; i++){
            double a = random.nextGaussian();
            a -= a % 1;
            int b = (int) a;
            map.put(b, map.getOrDefault(b, 0) + 1);
        }
        for(Map.Entry<Integer, Integer> data : map.entrySet()){
            System.out.println(data.getKey() + ": " + data.getValue());
        }*/

    }
    /**/

}