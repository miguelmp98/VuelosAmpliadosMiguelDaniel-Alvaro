package vista;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import controlador.MenuMongoDB;
import modelo.Vuelo;

public class main {
	public static void main(String[] args) throws IOException, SQLException {
		MenuMongoDB MongoDB = new MenuMongoDB();
		Scanner sc = new Scanner(System.in);

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

					System.out.println("Vuelos Disponibles");
					ArrayList<Vuelo> AVuelos = MongoDB.mostrarMongo();
					// Preguntar si quieren realizar la conexion en el main al grupo
					for (Vuelo i : AVuelos) {
						System.out.println("Id: " + i.getId() + ", Codigo: " + i.getCodigo_vuelo() + ", ORIGEN: "
								+ i.getOrigen() + ", Destino: " + i.getDestino() + ", Fecha: " + i.getFecha()
								+ ", Hora: " + i.getHora() + ", Plazas Totales: " + i.getPlazas_totales()
								+ ", Plazas Disponibles: " + i.getPlazas_disponibles());
					}

					System.out.println("Introduce el Codigo del vuelo que quiere comprar");
					String codigoCompra = sc.next();
					System.out.println("Introduzca su DNI");
					String clienteDNI = sc.next();
					System.out.println("Introduzca su nombre");
					String clienteNombre = sc.next();
					System.out.println("Introduzca su apellido");
					String clienteApellido = sc.next();
					System.out.println("Introduzca su DNI del pagador");
					String clienteDNIPagador = sc.next();	
					 String codigoVenta = MongoDB.randomCodigoVenta();
					 System.out.println("Codigo de Venta:" + codigoVenta);
					
					break;
				case 2:
				//aqui pones tus cositas miik
					break;
				case 3:
				//aqui pones tus cositas griso
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
