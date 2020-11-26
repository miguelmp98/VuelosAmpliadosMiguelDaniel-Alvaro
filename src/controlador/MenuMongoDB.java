package controlador;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import org.bson.Document;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;

import modelo.Vuelo;
import vista.main;

public class MenuMongoDB {
	private int lastID;
	private MongoClient mongo;
	private MongoDatabase db;
	private MongoCollection coleccionVuelos;

	public MenuMongoDB() {
		try {
			mongo = new MongoClient("localhost", 27017);
			db = mongo.getDatabase("VuelosAmpliada");
			coleccionVuelos = db.getCollection("vuelos2_0");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int numAsientos(String codVuelo) {
		try {
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("codigo", codVuelo);
			FindIterable fi = coleccionVuelos.find(whereQuery);
			MongoCursor cursor = fi.cursor();
			Document doc = (Document) cursor.next();
			ArrayList<Document> vendidos = new ArrayList<Document>();
			vendidos.addAll((ArrayList<Document>) doc.get("vendidos"));
			return vendidos.size();
		} catch (Exception e) {
			return 0;
		}
	}

	public ArrayList<Vuelo> mostrarMongo() {
		try {
			FindIterable fi = coleccionVuelos.find();
			MongoCursor cur = fi.cursor();
			ArrayList<Vuelo> AVuelos = new ArrayList<Vuelo>();

			while (cur.hasNext()) {
				Document doc = (Document) cur.next();
				String codigo_vuelo = doc.getString("codigo");
				String origen = doc.getString("origen");
				String destino = doc.getString("destino");
				String fecha = doc.getString("fecha");
				String hora = doc.getString("hora");
				int plazas_totales = leerInt(doc, "plazas_totales");
				int plazas_disponibles = leerInt(doc, "plazas_disponibles");
				Vuelo vuelo = new Vuelo(codigo_vuelo, origen, destino, fecha, hora, plazas_totales, plazas_disponibles);
				AVuelos.add(vuelo);
			}
			return AVuelos;
		} catch (Exception e) {
			System.out.println("Error al mostrar los vuelos disponibles \r\n");
			return null;
		}
	}

	public void insertarVendidos(String codVuelo, String clienteDNI, String clienteApellido, String clienteNombre, String clienteDNIPagador, String clienteTarjeta, String codigoVenta) {
		try {
			Document quienCambio = new Document("codigo", codVuelo);
			int numAsientos = numAsientos(codVuelo);
			numAsientos++;
			Document cambios = new Document().append("asiento", numAsientos).append("dni", clienteDNI)
					.append("apellido", clienteApellido).append("nombre", clienteNombre)
					.append("dniPagador", clienteDNIPagador).append("tarjeta", clienteTarjeta)
					.append("codigoVenta", codigoVenta);
			coleccionVuelos.updateOne(quienCambio, Updates.addToSet("vendidos", cambios));
			
			System.out.println("Vuelo comprado correctamente \r\n");
		} catch (Exception e) {
			System.out.println("Error al insertar en vendidos \r\n");
		}
	}

	public void restarPlazas(String codVuelo) {
		try {
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("codigo", codVuelo);
			FindIterable fi = coleccionVuelos.find(whereQuery);
			MongoCursor cur = fi.cursor();

			Document doc = (Document) cur.next();
			
			int plazasDisponibles = leerInt(doc, "plazas_disponibles");
			plazasDisponibles--;
			Document quienCambio = new Document("codigo", codVuelo);
			Document cambios = new Document("plazas_disponibles", plazasDisponibles);
			Document auxSet = new Document("$set", cambios);
			coleccionVuelos.updateOne(quienCambio, auxSet);

		} catch (Exception e) {
			System.out.println("Error al modificar las plazas disponibles\r\n");
		}
	}

	public int leerInt(Document doc, String nombreCampo) {
		int valor;
		try {
			valor = doc.getInteger(nombreCampo);
		} catch (ClassCastException e) {
			valor = (int) Math.round(doc.getDouble(nombreCampo));
		}
		return valor;
	}

	public String randomCodigoVenta() {
		String values = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		StringBuilder sb = new StringBuilder();
		Random rnd = new Random();
		while (sb.length() < 9) {
			int index = (int) (rnd.nextFloat() * values.length());
			sb.append(values.charAt(index));
		}
		String codigoVenta = sb.toString();
		return codigoVenta;
	}

	public void cancelarMongo(String codVuelo, String clienteDNI, String codigoVenta) {
		try {
			Document quienCambio = new Document("codigo", codVuelo);
			Document cambiosaRealizar = new Document("dni", clienteDNI).append("codigoVenta", codigoVenta);
			Document auxSet1 = new Document("vendidos", cambiosaRealizar);
			Document auxSet2 = new Document("$pull", auxSet1);
			coleccionVuelos.updateOne(quienCambio, auxSet2);

			System.out.println("Vuelo cancelado correctamente \r\n");

		} catch (Exception e) {
			System.out.println("Error al cancelar un vuelo \r\n");
		}
	}

	public void sumarPlazas(String codgoVuelo) {
		try {
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("codigo", codgoVuelo);
			FindIterable fi = coleccionVuelos.find(whereQuery);
			MongoCursor cur = fi.cursor();

			Document doc = (Document) cur.next();
			int plazasDisponibles = leerInt(doc, "plazas_disponibles");
			plazasDisponibles++;
			Document quienCambio = new Document("codigo", codgoVuelo);
			Document cambios = new Document("plazas_disponibles", plazasDisponibles);
			Document auxSet = new Document("$set", cambios);
			coleccionVuelos.updateOne(quienCambio, auxSet);

		} catch (Exception e) {
			System.out.println("Error al modificar las plazas disponibles \r\n");
		}
	}
	
	public void modificarVueloComprado(String codVuelo, String dniActual,String dniPagador, String codigoVenta) {
		Scanner sc = new Scanner(System.in);
		try {
			BasicDBObject condicion = new BasicDBObject();
			condicion.put("codigo", codVuelo);
			condicion.put("vendidos.dni", dniActual);
			condicion.put("vendidos.codigoVenta", codigoVenta);
			condicion.put("vendidos.dniPagador", dniPagador);

			FindIterable fi = coleccionVuelos.find(condicion);
			MongoCursor cursor = fi.cursor();
			Document doc = (Document) cursor.next();
			ArrayList<Document> vendidos = new ArrayList<Document>();
			vendidos.addAll((ArrayList<Document>) doc.get("vendidos"));
			int numAsiento = 0;
			for (int i = 0; i < vendidos.size(); i++) {
				String dniCheck = vendidos.get(i).getString("dni");
				String codigoVentaCheck = vendidos.get(i).getString("codigoVenta");
				String dniPagadorCheck = vendidos.get(i).getString("dniPagador");			
				if(dniCheck.equals(dniActual) && codigoVentaCheck.equals(codigoVenta) && dniPagadorCheck.equals(dniPagador)) {
					 numAsiento = leerInt(vendidos.get(i), "asiento");
				}
			}
			
			System.out.println("INSERTE LOS NUEVOS DATOS A MODIFICAR:  ");
			System.out.println("DNI:");
			String nuevoDni = sc.nextLine();
			System.out.println("NOMBRE:");
			String nombre = sc.nextLine();
			System.out.println("APELLIDO:");
			String apellido = sc.nextLine();
			System.out.println("TARJETA DE CRÉDITO:");
			String tarjeta = sc.nextLine();
			System.out.println("DNI QUE HA PAGADO");
			dniPagador = sc.nextLine();

			BasicDBObject cambios = new BasicDBObject();		
			//$ para acceder a posición
			cambios.put("vendidos.$.asiento", numAsiento);
			cambios.put("vendidos.$.dni", nuevoDni);
			cambios.put("vendidos.$.apellido", apellido);
			cambios.put("vendidos.$.nombre", nombre);
			cambios.put("vendidos.$.dniPagador", dniPagador);
			cambios.put("vendidos.$.tarjeta", tarjeta);
			cambios.put("vendidos.$.codigoVenta", codigoVenta);

			BasicDBObject operacion = new BasicDBObject();
			operacion.put("$set", cambios);
			coleccionVuelos.updateOne(condicion, operacion);

			System.out.println("El Vuelo que compraste ha sido modificado correctamente \r\n");
		} catch (Exception e) {
			System.out.println("Error al modificar la informacion de la venta \r\n");
		}
	}

	public void vueloLleno(String codVuelo) {

		BasicDBObject condicion = new BasicDBObject();
		condicion.put("codigo", codVuelo);
		FindIterable fi = coleccionVuelos.find(condicion);
		MongoCursor cur = fi.cursor();
		Document doc = (Document) cur.next();

		int plazas_disponibles = leerInt(doc, "plazas_disponibles");
		if (plazas_disponibles <= 0) {
			System.out.println("Lo sentimos, el Vuelo " + codVuelo + " esta completo! :C");
			main.seleccionarMetodo();
		}
	}

}
