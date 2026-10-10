import java.util.ArrayList;
import java.util.List;

// Algoritmo Ramer-Douglas-Peucker basado en la técnica Dividir para Reinar
public class Main {

    // Clase interna para representar las coordenadas de un punto
    static class Punto {
        double x, y;
        public Punto(double x, double y) {
            this.x = x;
            this.y = y;
        }
        @Override
        public String toString() {
            return String.format("(%.1f, %.1f)", x, y);
        }
    }

    // Metodo auxiliar para calcular la distancia perpendicular de un punto p a una línea ab
    public static double distanciaPerpendicular(Punto p, Punto a, Punto b) {
        double area = Math.abs((b.y - a.y) * p.x - (b.x - a.x) * p.y + b.x * a.y - b.y * a.x);
        double base = Math.sqrt(Math.pow(b.y - a.y, 2) + Math.pow(b.x - a.x, 2));

        if (base == 0) {
            // a y b son el mismo punto, retorna la distancia euclidiana de p a a
            return Math.sqrt(Math.pow(p.x - a.x, 2) + Math.pow(p.y - a.y, 2));
        }
        return area / base;
    }

    // Algoritmo principal Ramer-Douglas-Peucker utilizando Dividir para Reinar
    public static List<Punto> simplificarRDP(List<Punto> trayectoria, double epsilon) {
        // Si la trayectoria tiene menos de 3 puntos, no hay puntos intermedios que eliminar
        if (trayectoria.size() < 3) {
            return trayectoria;
        }

        double maxDistancia = 0.0;
        int indiceMax = 0;
        Punto inicio = trayectoria.get(0);
        Punto fin = trayectoria.get(trayectoria.size() - 1);

        // Paso 1 - Buscar el punto intermedio Q más lejano al segmento S (inicio-fin)
        for (int i = 1; i < trayectoria.size() - 1; i++) {
            double distanciaActual = distanciaPerpendicular(trayectoria.get(i), inicio, fin);
            if (distanciaActual > maxDistancia) {
                maxDistancia = distanciaActual;
                indiceMax = i;
            }
        }

        List<Punto> trayectoriaSimplificada = new ArrayList<>();

        // Paso 3 - Si la distancia máxima es mayor a epsilon, se conserva Q y se divide el problema
        if (maxDistancia > epsilon) {
            // Llamadas recursivas (Dividir para Reinar)[cite: 1, 105]
            List<Punto> subTrayectoriaIzq = simplificarRDP(trayectoria.subList(0, indiceMax + 1), epsilon);
            List<Punto> subTrayectoriaDer = simplificarRDP(trayectoria.subList(indiceMax, trayectoria.size()), epsilon);

            // Paso 4 - Concatenar resultados cuidando de no repetir el punto Q[cite: 1]
            trayectoriaSimplificada.addAll(subTrayectoriaIzq.subList(0, subTrayectoriaIzq.size() - 1));
            trayectoriaSimplificada.addAll(subTrayectoriaDer);
        } else {
            // Paso 2 - Si es menor o igual a epsilon, se descartan los puntos intermedios[cite: 1]
            trayectoriaSimplificada.add(inicio);
            trayectoriaSimplificada.add(fin);
        }

        return trayectoriaSimplificada;
    }

    public static void main(String[] args) {
        // Creación de la trayectoria T de prueba de la Figura 1[cite: 1, 2]
        List<Punto> trayectoriaOriginal = new ArrayList<>();
        trayectoriaOriginal.add(new Punto(0, 0));       // P0
        trayectoriaOriginal.add(new Punto(1, 0.2));     // P1
        trayectoriaOriginal.add(new Punto(2, -0.1));    // P2
        trayectoriaOriginal.add(new Punto(3, 3));       // P3
        trayectoriaOriginal.add(new Punto(4, 6.1));     // P4
        trayectoriaOriginal.add(new Punto(5, 6));       // P5
        trayectoriaOriginal.add(new Punto(6, 6.2));     // P6
        trayectoriaOriginal.add(new Punto(7, 6));       // P7

        double epsilon = 0.5; // Tolerancia dada en el enunciado[cite: 1]

        System.out.println("Trayectoria Original: " + trayectoriaOriginal.size() + " puntos.");

        List<Punto> simplificada = simplificarRDP(trayectoriaOriginal, epsilon);

        System.out.println("Trayectoria Simplificada (epsilon = " + epsilon + "): " + simplificada.size() + " puntos.");
        for(Punto p : simplificada) {
            System.out.println(p);
        }
    }
}