package com.mcomobile.mercadillos;

public class ItemCompra {
	protected String id;
	protected String rutaImagen;
	protected String nombre;
	protected String tipo;
	
	
	public ItemCompra() {
		this.nombre = "";
		this.tipo = "";
		this.rutaImagen = "";
	}
	
	public ItemCompra(String id, String nombre, String tipo) {
		this.id = id;
		this.nombre = nombre;
		this.tipo = tipo;
		this.rutaImagen = "";
	}
	
	public ItemCompra(String id, String nombre, String tipo, String rutaImagen) {
		this.id = id;
		this.nombre = nombre;
		this.tipo = tipo;
		this.rutaImagen = rutaImagen;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getRutaImagen() {
		return rutaImagen;
	}
	
	public void setRutaImagen(String rutaImagen) {
		this.rutaImagen = rutaImagen;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public String getTipo() {
		return tipo;
	}
	
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
}
