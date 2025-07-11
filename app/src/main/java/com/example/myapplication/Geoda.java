package com.example.myapplication;

public class Geoda {
    public enum Tipo {
        BRONCE("Geoda de Bronce", 50),
        ORO("Geoda de Oro", 30),
        DIAMANTE("Geoda de Diamante", 15),
        GALACTICA("Geoda Galáctica", 5);
        
        private final String nombre;
        private final int probabilidad;
        
        Tipo(String nombre, int probabilidad) {
            this.nombre = nombre;
            this.probabilidad = probabilidad;
        }
        
        public String getNombre() {
            return nombre;
        }
        
        public int getProbabilidad() {
            return probabilidad;
        }
    }
    
    public static Tipo obtenerGeodaAleatoria() {
        int random = (int) (Math.random() * 100) + 1;
        int acumulado = 0;
        
        for (Tipo tipo : Tipo.values()) {
            acumulado += tipo.getProbabilidad();
            if (random <= acumulado) {
                return tipo;
            }
        }
        
        return Tipo.BRONCE; // Fallback
    }
} 