import java.util.ArrayList;
import java.util.List;

public class Main {
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

        double epsilon = 0.5;

        System.out.println("Trayectoria Original: " + trayectoriaOriginal.size() + " puntos.");

        List<Punto> simplificada = simplificarRDP(trayectoriaOriginal, epsilon);

        System.out.println("Trayectoria Simplificada (epsilon = " + epsilon + "): " + simplificada.size() + " puntos.");
        for(Punto p : simplificada) {
            System.out.println(p);
        }
    }

    public static double distanciaPerpendicular(Punto p, Punto a, Punto b) {
        double area = Math.abs((b.y - a.y) * p.x - (b.x - a.x) * p.y + b.x * a.y - b.y * a.x);
        double base = Math.sqrt(Math.pow(b.y - a.y, 2) + Math.pow(b.x - a.x, 2));

        if (base == 0) {
            // a y b son el mismo punto, retorna la distancia euclidiana de p a a
            return Math.sqrt(Math.pow(p.x - a.x, 2) + Math.pow(p.y - a.y, 2));
        }
        return area / base;
    }

    public static List<Punto> simplificarRDP(List<Punto> trayectoria, double epsilon) {
        if (trayectoria.size() < 3) {
            return trayectoria;
        }

        double maxDistancia = 0.0;
        int indiceMax = 0;

        Punto inicio = trayectoria.getFirst();
        Punto fin = trayectoria.getLast();

        // Se busca el punto intermedio Q más lejano al segmento S (inicio-fin)
        for (int i = 1; i < trayectoria.size() - 1; i++) {
            double distanciaActual = distanciaPerpendicular(trayectoria.get(i), inicio, fin);

            if (distanciaActual > maxDistancia) {
                maxDistancia = distanciaActual;
                indiceMax = i;
            }
        }

        List<Punto> trayectoriaSimplificada = new ArrayList<>();

        // Si la distancia máxima es mayor a epsilon, se conserva Q y se divide el problema
        if (maxDistancia > epsilon) {
            List<Punto> subTrayectoriaIzq = simplificarRDP(trayectoria.subList(0, indiceMax + 1), epsilon);
            List<Punto> subTrayectoriaDer = simplificarRDP(trayectoria.subList(indiceMax, trayectoria.size()), epsilon);

            trayectoriaSimplificada.addAll(subTrayectoriaIzq.subList(0, subTrayectoriaIzq.size() - 1));
            trayectoriaSimplificada.addAll(subTrayectoriaDer);
        } else {
            // Si la distancia es menor o igual a epsilon, se descartan los puntos intermedios
            trayectoriaSimplificada.add(inicio);
            trayectoriaSimplificada.add(fin);
        }

        return trayectoriaSimplificada;
    }
}