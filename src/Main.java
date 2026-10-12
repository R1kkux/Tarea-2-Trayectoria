import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    static Scanner sc = new Scanner(System.in).useLocale(Locale.US);

    public static void main(String[] args) {
        System.out.println("\nSeleccione un metodo para registrar los puntos: ");
        System.out.println("\t1. Usar los puntos de la figura 1 del enunciado");
        System.out.println("\t2. Importar los puntos desde un archivo externo");
        System.out.println("\t3. Ingresar los puntos manualmente");

        System.out.print("> ");
        int eleccion = sc.nextInt();

        List<Punto> trayectoriaOriginal = new ArrayList<>();

        String filePath = "";

        if (eleccion == 1) {
            filePath = "src/Trayectorias/puntosFigura1.csv";
        } else if (eleccion == 2) {
            System.out.println("\nEl archivo debe colocarse en la carpeta \"src\\Trayectorias\"");
            System.out.println("El nombre del archivo debe tener la forma: <trayectoria_(cantPuntos).csv>");
            System.out.println("\tA continuacion, introduzca el valor que tiene cantPuntos en el nombre del archivo:");

            System.out.print("> ");
            int cantPuntos = sc.nextInt();

             filePath = "src/Trayectorias/trayectoria_" +cantPuntos +".csv";
        } else if (eleccion == 3) {
            trayectoriaOriginal = crearManaulemtne();
        }

        if (!(filePath.isBlank())) {
            try {
                trayectoriaOriginal = leerArchivo(filePath);
            } catch (TrayException e) {
                System.out.println(e.getMessage());
            }
        }

        System.out.print("\nA continuacion, ingrese la tolerancia al momento de simplificar la trayectoria: ");
        double tolerancia = sc.nextDouble();

        long inicio1 = System.nanoTime();
        List<Punto> simplificada = RaumerDouglasPeucker(trayectoriaOriginal, tolerancia);
        long final1 = System.nanoTime();

        long duracion1 = final1 - inicio1;
        double tiempo = duracion1 / 1_000_000.0;

                System.out.println("\n\n\t == Resultados finales ==");

        System.out.println("> La trayectoria original contiene " + trayectoriaOriginal.size() + " puntos.");

        System.out.println("> La trayectoria simplificada contiene "+ simplificada.size() + " puntos.");
        System.out.println("\t = La tolerancia utilizada fue de " + tolerancia + " unidades.");

        int puntosEliminados = trayectoriaOriginal.size() -  simplificada.size();

        System.out.println("\n> Un total de " +puntosEliminados +" puntos fueron eliminados");
        System.out.println("\t Esto corresponde al " + ((100.00 * puntosEliminados) / trayectoriaOriginal.size()) +"% de los puntos originales.");

        System.out.println("\n> El programa tardo " +tiempo + " milisegundos en simplificar la trayectoria.");
    }

    public static List<Punto> RaumerDouglasPeucker(List<Punto> trayectoria, double tolerancia) {
        if (trayectoria.size() <= 2) {
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

        // Si la distancia máxima es mayor a la tolerancia, se conserva Q y se divide el problema
        if (maxDistancia > tolerancia) {
            List<Punto> subTrayectoriaIzq = RaumerDouglasPeucker(trayectoria.subList(0, indiceMax + 1), tolerancia);
            List<Punto> subTrayectoriaDer = RaumerDouglasPeucker(trayectoria.subList(indiceMax, trayectoria.size()), tolerancia);

            trayectoriaSimplificada.addAll(subTrayectoriaIzq.subList(0, subTrayectoriaIzq.size() - 1));
            trayectoriaSimplificada.addAll(subTrayectoriaDer);
        } else {    // Si no, se descartan los puntos intermedios añadiendo solamente los puntos de inicio y fin a la nueva trayectoria
            trayectoriaSimplificada.add(inicio);
            trayectoriaSimplificada.add(fin);
        }

        return trayectoriaSimplificada;
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

    public static List<Punto> leerArchivo(String filePath) {
        File archivo = new File(filePath);
        List<Punto> trayectoriaOriginal = new  ArrayList<>();

        String linea;

        try (Scanner lector = new Scanner(archivo)) {
            while (lector.hasNextLine()) {
                linea = lector.nextLine().trim();

                if ((linea.isBlank()) || (linea.charAt(0) == 'x')) {
                    continue;
                }

                String[] puntos = linea.split(",");
                trayectoriaOriginal.add(new Punto
                        (Double.parseDouble(puntos[0].trim()),
                                Double.parseDouble(puntos[1].trim())));
            }
        } catch (FileNotFoundException e) {
            throw new TrayException("No existe o no pudo ser cargado el archivo " + filePath);
        }

        return trayectoriaOriginal;
    }

    public static List<Punto> crearManaulemtne() {
        System.out.println("Escribe los puntos de la siguiente forma: <x,y>");
            System.out.println("\t Ingresa un par de letras para dejar de ingresar pares");

            List<Punto> puntos = new ArrayList<>();
            String[] texto;
        try {
            while (true) {
                System.out.print("> ");
                texto = sc.next().trim().split(",");

                if (texto.length != 2) {
                    System.out.println("Se ha ingresado incorrectamente el par.");
                    continue;
                }

                puntos.add(new Punto(Double.parseDouble(texto[0]), Double.parseDouble(texto[1])));
            }
        } catch (NumberFormatException e) {
            System.out.println("Se han terminado de escribir pares. Se han añadido " +puntos.size() +" puntos");
        }

        return puntos;
    }
}