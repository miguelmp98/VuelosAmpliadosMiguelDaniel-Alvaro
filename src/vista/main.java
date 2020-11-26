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
		System.out.println("Bienvenido a la gestion de tus vuelos.");

		seleccionarMetodo();

	}

	public static void seleccionarMetodo() {
		MenuMongoDB MongoDB = new MenuMongoDB();
		Scanner sc = new Scanner(System.in);		
		String codVuelo;
		String clienteDNI; 
		String clienteNombre;
		String clienteApellido;
		String clienteDNIPagador;
		String clienteTarjeta;
		String codigoVenta;
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

					ArrayList<Vuelo> AVuelos = MongoDB.mostrarMongo();
					System.out.println("Vuelos Disponibles: ");
					for (Vuelo i : AVuelos) {
						System.out.println("Codigo Vuelo: " + i.getCodigo_vuelo() + ", ORIGEN: " + i.getOrigen()
								+ ", Destino: " + i.getDestino() + ", Fecha: " + i.getFecha() + ", Hora: " + i.getHora()
								+ ", Plazas Totales: " + i.getPlazas_totales() + ", Plazas Disponibles: "
								+ i.getPlazas_disponibles());
					}
					System.out.println();
					System.out.println("Introduzca el Codigo del vuelo que quiere comprar:");
					codVuelo = sc.next().toUpperCase();
					MongoDB.vueloLleno(codVuelo);			
					System.out.println("Introduzca su DNI: ");
					clienteDNI = sc.next();
					System.out.println("Introduzca su nombre: ");
					clienteNombre = sc.next();
					System.out.println("Introduzca su apellido: ");
					clienteApellido = sc.next();
					System.out.println("Introduzca su DNI del pagador: ");
					clienteDNIPagador = sc.next();
					System.out.println("Introduzca la tarjeta de credito del pago a efectuar: ");
					sc.nextLine();
					clienteTarjeta = sc.nextLine();
					codigoVenta = MongoDB.randomCodigoVenta();
					System.out.println("Su Codigo de Venta es: " + codigoVenta + "\r\n");
					MongoDB.insertarVendidos(codVuelo, clienteDNI, clienteApellido, clienteNombre, clienteDNIPagador, clienteTarjeta, codigoVenta);
					MongoDB.restarPlazas(codVuelo);
									
					break;
				case 2:
					System.out.println("Introduzca el codigo de vuelo");
					codVuelo = sc.next().toUpperCase();
					System.out.println("Introduzca tu DNI: ");
					clienteDNI = sc.next();
					System.out.println("Introduzca el codigo de Venta: ");
					codigoVenta = sc.next();
					MongoDB.cancelarMongo(codVuelo, clienteDNI, codigoVenta);				
					MongoDB.sumarPlazas(codVuelo);
					
					break;
				case 3:
					System.out.println("Introduzca el codigo de vuelo a modificar: ");
					codVuelo = sc.next().toUpperCase();
                    System.out.println("Introduzca DNI actual: ");
                    clienteDNI = sc.next();
                    System.out.println("Introduzca DNI del pagador: ");
                    clienteDNIPagador = sc.next();
                    System.out.println("Introduzca el codigo de venta: ");
                    codigoVenta = sc.next();
                    MongoDB.modificarVueloComprado(codVuelo, clienteDNI, clienteDNIPagador, codigoVenta);
                    
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