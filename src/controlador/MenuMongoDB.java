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

public class MenuMongoDB {
	int lastID;

	public MenuMongoDB() {

	}

	public MongoClient crearConexion() {
		MongoClient mongo = null;
		try {
			mongo = new MongoClient("localhost", 27017);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mongo;
	}

	public int lastID(MongoClient mongo, String codigoCompra) {
		try {
			MongoDatabase db = mongo.getDatabase("VuelosAmpliada");
			MongoCollection collection = db.getCollection("vuelos2_0");
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("codigo", codigoCompra);
			FindIterable fi = collection.find(whereQuery);
			MongoCursor cursor = fi.cursor();
			Document doc = (Document) cursor.next();
			ArrayList<Document> vendidos = new ArrayList<Document>();
			vendidos.addAll((ArrayList<Document>) doc.get("vendidos"));

			return vendidos.size();
		} catch (Exception e) {
			return 0;
		}
	}

	public ArrayList<Vuelo> mostrarMongo(MongoClient mongo) {
		try {
			MongoDatabase db = mongo.getDatabase("VuelosAmpliada");
			MongoCollection colleccionVuelos = db.getCollection("vuelos2_0");
			FindIterable fi = colleccionVuelos.find();
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

	public void insertarVendidos(MongoClient mongo, String codigoCompra, String clienteDNI, String clienteApellido,
			String clienteNombre, String clienteDNIPagador, String clienteTarjeta, String codigoVenta) {
		try {
			MongoDatabase db = mongo.getDatabase("VuelosAmpliada");
			MongoCollection colleccionVuelos = db.getCollection("vuelos2_0");

			Document quienCambio = new Document("codigo", codigoCompra);
			int numAsientos = lastID(mongo, codigoCompra);
			numAsientos++;
			Document cambios = new Document().append("asiento", numAsientos).append("dni", clienteDNI)
					.append("apellido", clienteApellido).append("nombre", clienteNombre)
					.append("dniPagador", clienteDNIPagador).append("tarjeta", clienteTarjeta)
					.append("codigoVenta", codigoVenta);
			colleccionVuelos.updateOne(quienCambio, Updates.addToSet("vendidos", cambios));

		} catch (Exception e) {
			System.out.println("Error al insertar en vendidos \r\n");
		}
	}

	public void restarPlazas(MongoClient mongo, String codigoCompra) {
		try {
			MongoDatabase db = mongo.getDatabase("VuelosAmpliada");
			MongoCollection coleccionVuelos = db.getCollection("vuelos2_0");
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("codigo", codigoCompra);
			FindIterable fi = coleccionVuelos.find(whereQuery);
			MongoCursor cur = fi.cursor();

			Document doc = (Document) cur.next();
			int plazasDisponibles = leerInt(doc, "plazas_disponibles");
			plazasDisponibles--;
			Document quienCambio = new Document("codigo", codigoCompra);
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

	public void cancelarMongo(MongoClient mongo, String codigo, String clienteDNI, String codigoVenta) {
		MongoDatabase db = mongo.getDatabase("VuelosAmpliada");
		MongoCollection colleccionVuelos = db.getCollection("vuelos2_0");
		Document quienCambio = new Document("codigo", codigo);
		Document cambiosaRealizar = new Document("dni", clienteDNI).append("codigoVenta", codigoVenta);
		Document auxSet1 = new Document("vendidos", cambiosaRealizar);
		Document auxSet2 = new Document("$pull", auxSet1);
		colleccionVuelos.updateOne(quienCambio, auxSet2);

	}

	public void sumarPlazas(MongoClient mongo, String codigoCompra) {
		try {
			MongoDatabase db = mongo.getDatabase("VuelosAmpliada");
			MongoCollection coleccionVuelos = db.getCollection("vuelos2_0");
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("codigo", codigoCompra);
			FindIterable fi = coleccionVuelos.find(whereQuery);
			MongoCursor cur = fi.cursor();

			Document doc = (Document) cur.next();
			int plazasDisponibles = leerInt(doc, "plazas_disponibles");
			plazasDisponibles++;
			Document quienCambio = new Document("codigo", codigoCompra);
			Document cambios = new Document("plazas_disponibles", plazasDisponibles);
			Document auxSet = new Document("$set", cambios);
			coleccionVuelos.updateOne(quienCambio, auxSet);

		} catch (Exception e) {
			System.out.println("Error al modificar las plazas disponibles\r\n");
		}
	}
}
