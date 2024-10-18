package ru.example;

public class Math {
    public static short extractBit(long n, short i) {
        return (short) ((n >> i) & 1);
    }
}