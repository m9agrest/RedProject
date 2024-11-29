import org.junit.jupiter.api.Test;
import ru.example.DBMS_Random;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class Testing {
    private static final int Round = 100;
    private static final int Seed = 125;

    @Test
    void testRandom(){
        boolean minus = false;
        boolean plus = false;
        boolean minusBig = false;
        boolean plusBig = false;
        for(int i = 0; i < Round; i++){
            int r = DBMS_Random.random();
            if(r >= 0){
                plus = true;
                if(r > 1500000000){
                    plusBig = true;
                }
            } else {
                minus = true;
                if(r < -1500000000){
                    minusBig = true;
                }
            }
        }
        assertTrue(minus, "Нету отрицательных результатов в random()");
        assertTrue(plus, "Нету положительных результатов в random()");
        assertTrue(minusBig, "Нету больших отрицательных результатов в random()");
        assertTrue(plusBig, "Нету больших положительных результатов в random()");
    }

    @Test
    void testValue(){
        boolean isMoreOne = false;
        boolean isLessZero = false;
        for(int i = 0; i < Round; i++){
            BigDecimal r = DBMS_Random.value();
            if(r.compareTo(BigDecimal.ONE) > 0){
                isMoreOne = true;
            }
            if(r.compareTo(BigDecimal.ZERO) < 0){
                isLessZero = true;
            }
        }
        assertFalse(isMoreOne, "value() Возвращает число больше 1");
        assertFalse(isLessZero, "value() Возвращает число меньше 0");


        boolean isMoreMax = false;
        boolean isLessMin = false;
        BigDecimal min = new BigDecimal("-2.5");
        BigDecimal max = new BigDecimal("2.5");
        for(int i = 0; i < Round; i++){
            BigDecimal r = DBMS_Random.value(min, max);
            if(r.compareTo(max) > 0){
                isMoreMax = true;
            }
            if(r.compareTo(min) < 0){
                isLessMin = true;
            }
        }
        assertFalse(isMoreMax, "value() Возвращает число больше max значения");
        assertFalse(isLessMin, "value() Возвращает число меньше min значения");

    }

    @Test
    void testSeed(){
        DBMS_Random.seed(Seed);
        int rand1 = DBMS_Random.random();
        for(int i = 0; i < Round; i++){
            DBMS_Random.random();
        }
        DBMS_Random.seed(Seed);
        int rand2 = DBMS_Random.random();
        assertEquals(rand1, rand2, "метод seed() не работает");
    }



}
