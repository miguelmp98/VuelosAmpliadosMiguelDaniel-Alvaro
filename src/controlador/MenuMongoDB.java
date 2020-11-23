package controlador;


import java.util.ArrayList;
import java.util.Random;
import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

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

	public int lastID(MongoClient mongo) {
     
        MongoDatabase db = mongo.getDatabase("adat_vuelos");
        MongoCollection collecion = db.getCollection("vuelos");
        FindIterable fi = collecion.find();
        MongoCursor cursor = fi.cursor();
        
        lastID = 0;
        
        while (cursor.hasNext()) {
            cursor.next();
            lastID++;
        }
        lastID++;
        return lastID;
    }

	public ArrayList<Vuelo> mostrarMongo() {
		MongoClient mongo = crearConexion();
		MongoDatabase db = mongo.getDatabase("VuelosAmpliada");
		MongoCollection colleccionVuelos = db.getCollection("vuelos2_0");

		FindIterable fi = colleccionVuelos.find();
		MongoCursor cur = fi.cursor();
		ArrayList<Vuelo> AVuelos = new ArrayList<Vuelo>();
		
		while (cur.hasNext()) {
			Document doc = (Document) cur.next();
			int id = leerInt(doc, "id");
			String codigo_vuelo = doc.getString("codigo");
			String origen = doc.getString("origen");
			String destino = doc.getString("destino");
			String fecha = doc.getString("fecha");
			String hora = doc.getString("hora");
			int plazas_totales = leerInt(doc, "plazas_totales");
			int plazas_disponibles = leerInt(doc, "plazas_disponibles");
			Vuelo vuelo = new Vuelo(id, codigo_vuelo, origen, destino, fecha, hora, plazas_totales, plazas_disponibles);
			AVuelos.add(vuelo);
			
		}
		return AVuelos;
	}
	
	public void modificarMongo(int id, String campoMod, String valorMod) {
		MongoClient mongo = crearConexion();
		MongoDatabase db = mongo.getDatabase("adat_vuelos");
		MongoCollection colleccionVuelos = db.getCollection("vuelos");
		
		Document quienCambio = new Document("ID", id);
		Document cambios = new Document(campoMod, valorMod);
		Document auxSet = new Document("$set", cambios);
		colleccionVuelos.updateOne(quienCambio, auxSet);

		System.out.println("Registro modificado con exito \r\n");
	
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
}
