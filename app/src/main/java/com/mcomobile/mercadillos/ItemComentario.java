package com.mcomobile.mercadillos;

public class ItemComentario {
	protected String nombre;
	protected String tipo;
	
	
	public ItemComentario() {
		this.nombre = "";
		this.tipo = "";
	}
	
	public ItemComentario(String nombre, String tipo) {
		this.nombre = nombre;
		this.tipo = tipo;
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
