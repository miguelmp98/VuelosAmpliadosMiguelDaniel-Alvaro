package vista;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import com.mongodb.MongoClient;

import controlador.MenuMongoDB;
import modelo.Vuelo;

public class main {
	public static void main(String[] args) throws IOException, SQLException {
		System.out.println("Bienvenido a la gestión de tus vuelos.");

		seleccionarMetodo();

	}

	public static void seleccionarMetodo() {
		MenuMongoDB MongoDB = new MenuMongoDB();
		Scanner sc = new Scanner(System.in);
		MongoClient mongo = MongoDB.crearConexion();

		boolean salir = false;
		int opcion;

		while (!salir) {

			System.out.println("1. Comprar Vuelo");
			System.out.println("2. Cancelar Vuelo");
			System.out.println("3. Modificar Vuelo");
			System.out.println("4. Salir");

			try {

				opcion = sc.nextInt();
				switch (opcion) {
				case 1:

					ArrayList<Vuelo> AVuelos = MongoDB.mostrarMongo(mongo);
					System.out.println("Vuelos Disponibles: ");
					for (Vuelo i : AVuelos) {
						System.out.println("Codigo Vuelo: " + i.getCodigo_vuelo() + ", ORIGEN: " + i.getOrigen()
								+ ", Destino: " + i.getDestino() + ", Fecha: " + i.getFecha() + ", Hora: " + i.getHora()
								+ ", Plazas Totales: " + i.getPlazas_totales() + ", Plazas Disponibles: "
								+ i.getPlazas_disponibles());
					}
					System.out.println();
					System.out.println("Introduce el Codigo del vuelo que quiere comprar:");
					String codigoCompra = sc.next().toUpperCase();
					System.out.println("Introduzca su DNI: ");
					String clienteDNI = sc.next();
					System.out.println("Introduzca su nombre: ");
					String clienteNombre = sc.next();
					System.out.println("Introduzca su apellido: ");
					String clienteApellido = sc.next();
					System.out.println("Introduzca su DNI del pagador: ");
					String clienteDNIPagador = sc.next();
					System.out.println("Introduzca la tarjeta de credito del pago a efectuar: ");
					sc.nextLine();
					String clienteTarjeta = sc.nextLine();
					String codigoVenta = MongoDB.randomCodigoVenta();
					System.out.println("Su Codigo de Venta es: " + codigoVenta + "\r\n");
					MongoDB.insertarVendidos(mongo, codigoCompra, clienteDNI, clienteApellido, clienteNombre, clienteDNIPagador, clienteTarjeta, codigoVenta);
					MongoDB.restarPlazas(mongo, codigoCompra);
					
					break;
				case 2:
					// aqui va deletear
					break;
				case 3:
					// aqui va modificar

					break;
				case 4:
					salir = true;
					System.out.println("Adios! Buen dia");
					break;
				default:
					System.out.println("Solo numeros entre 1 y 4");
				}
			} catch (InputMismatchException e) {
				System.out.println("Debes insertar un numero");
				sc.next();
			}
		}
	}
}