import org.junit.jupiter.api.Test;
import ru.example.DBMS_Random;
import ru.example.Random2;

import java.math.BigDecimal;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestingSQL {
    private static final int Round = 50;
    static Connection connection = connect();

    static String FROM = "RDB$DATABASE";
    static String PREFIX = "RAND_";


    static Connection connect(){
        try {
            Connection connection = DriverManager.getConnection("jdbc:firebirdsql://localhost:3050/employee.fdb", "sysdba", "RedDatabase");
            /*
            Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "SYSTEM", "RedDatabase");
            FROM = "DUAL";
            PREFIX = "";
             */
            connection.setAutoCommit(false);
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test void testSeed(){
        executeVoid("SEED(10)");
        int a = executeInt("RANDOM()");
        executeInt("RANDOM()");
        executeInt("RANDOM()");
        executeInt("RANDOM()");
        executeVoid("SEED(10)");
        int b = executeInt("RANDOM()");
        assertEquals(a,b, "Результаты не равны");
    }
    @Test void testSeedString(){
        executeVoid("SEED('10')");
        int a = executeInt("RANDOM()");
        executeVoid("SEED(10)");
        int b = executeInt("RANDOM()");
        assertEquals(a,b, "Результаты не равны");
    }
    @Test void testSeedString2(){
        executeVoid("SEED(10)");
        executeVoid("SEED('ten')");
        int a = executeInt("RANDOM()");
        executeVoid("SEED(10)");
        int b = executeInt("RANDOM()");
        assertNotEquals(a,b, "Результаты равны");
    }
    @Test void testInitialize(){
        executeVoid("SEED(10)");
        int a = executeInt("RANDOM()");
        executeVoid("initialize(10)");
        int b = executeInt("RANDOM()");
        assertEquals(a,b, "Результаты не равны");
    }
    @Test void testValue(){
        boolean isMorePlus = false;
        boolean isLessMinus = false;
        for(int i = 0; i < Round; i++){
            BigDecimal r = executeBigDecimal(PREFIX + "VALUE()");
            if(r.compareTo(BigDecimal.ONE) > 0){
                isMorePlus = true;
            }
            if(r.compareTo(BigDecimal.valueOf(-1)) < 0){
                isLessMinus = true;
            }
        }
        assertFalse(isMorePlus, "value() Возвращает число больше 1");
        assertFalse(isLessMinus, "value() Возвращает число меньше -1");
    }
    @Test void testValue2(){
        boolean isMorePlus = false;
        boolean isLessMinus = false;
        for(int i = 0; i < Round; i++){
            BigDecimal r = executeBigDecimal(PREFIX + "VALUE(100,-100)");
            if(r.compareTo(BigDecimal.valueOf(100)) > 0){
                isMorePlus = true;
            }
            if(r.compareTo(BigDecimal.valueOf(-100)) < 0){
                isLessMinus = true;
            }
        }
        assertFalse(isMorePlus, "value() Возвращает число больше 100");
        assertFalse(isLessMinus, "value() Возвращает число меньше -100");
    }
    @Test void testRandom(){
        boolean minus = false;
        boolean plus = false;
        boolean minusBig = false;
        boolean plusBig = false;
        for(int i = 0; i < Round; i++){
            int r = executeInt("RANDOM()");
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
    @Test void testNormal(){
        Map<Integer, Integer> map = new HashMap<>();
        for(int i = 0; i < Round; i++){
            int b = executeBigDecimal("NORMAL()").intValue();
            map.put(b, map.getOrDefault(b, 0) + 1);
        }
        for(Map.Entry<Integer, Integer> data : map.entrySet()){
            System.out.println(data.getKey() + ": " + data.getValue());
        }
    }
    @Test void testString(){
        checkStringPart("A", Random2.charsetA);
        checkStringPart("U", Random2.charsetU);
        checkStringPart("L", Random2.charsetL);
        checkStringPart("X", Random2.charsetX);
        checkStringPart("P", Random2.charsetP);
        checkStringPart("D", Random2.charsetU);//default
    }
    static void checkStringPart(String opt, String charset){
        for(int i = -1; i <= 5; i++){
            checkString(executeString(PREFIX + "STRING('" + opt + "'," + i + ")"), i, charset, opt);
        }
        checkString(executeString(PREFIX + "STRING('" + opt + "'," + 100 + ")"), 100, charset, opt);
    }
    static void checkString(String string, int len, String charset, String opt){
        if(len < 1){
            assertNull(string, "Строка должна быть null");
            return;
        }
        assertEquals(string.length(), len, "Строка неправильного размера");
        for(char symbol : string.toCharArray()){
            assertTrue(charset.contains(String.valueOf(symbol)), "Символа [" + symbol + "] нет в charset " + opt + " len " + len);
        }
    }
    /**
     * Каждая сгенерированая буква меняет сид
     */
    @Test void testStringRand(){
        executeVoid("SEED(10)");
        executeString(PREFIX + "STRING('p', 3)");
        int a = executeInt("RANDOM()");

        executeVoid("SEED(10)");
        executeInt("RANDOM()");
        executeInt("RANDOM()");
        executeInt("RANDOM()");
        int b = executeInt("RANDOM()");
        assertEquals(a,b, "Результаты не равны");
    }
    /**
     * Проверка работы сида в разных сессиях
     */
    @Test void testSession(){
        Connection connection2 = connect();
        executeVoid(connection, "SEED(10)");
        executeVoid(connection2, "SEED(10)");
        int a1 = executeInt(connection, "RANDOM()");
        int b1 = executeInt(connection, "RANDOM()");
        int a2 = executeInt(connection2, "RANDOM()");
        int b2 = executeInt(connection2, "RANDOM()");
        assertEquals(a1,a2, "Результаты не равны");
        assertEquals(b1,b2, "Результаты не равны");
    }

    static int executeInt(String sql){
        return executeInt(connection, sql);
    }
    static void executeVoid(String sql){
        executeVoid(connection, sql);
    }
    static String executeString(String sql){
        return executeString(connection, sql);
    }
    static BigDecimal executeBigDecimal(String sql){
        return executeBigDecimal(connection, sql);
    }
    static int executeInt(Connection connection, String sql) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT DBMS_RANDOM." + sql + " FROM " + FROM);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) { // Переходим к первой строке, если она существует
                return rs.getInt(1); // Извлекаем значение из первой колонки
            } else {
                throw new SQLException("No data found in the result set.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    static void executeVoid(Connection connection, String sql){
        try {
            PreparedStatement stmt = connection.prepareStatement("CALL DBMS_RANDOM." + sql);
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    static String executeString(Connection connection, String sql){
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT DBMS_RANDOM." + sql + " FROM " + FROM);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) { // Переходим к первой строке, если она существует
                return rs.getString(1); // Извлекаем значение из первой колонки
            } else {
                throw new SQLException("No data found in the result set.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    static BigDecimal executeBigDecimal(Connection connection, String sql){
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT DBMS_RANDOM." + sql + " FROM " + FROM);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) { // Переходим к первой строке, если она существует
                return rs.getBigDecimal(1); // Извлекаем значение из первой колонки
            } else {
                throw new SQLException("No data found in the result set.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
