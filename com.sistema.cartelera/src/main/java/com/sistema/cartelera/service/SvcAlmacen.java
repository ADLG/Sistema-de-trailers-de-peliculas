package com.sistema.cartelera.service;

import java.nio.file.Path;

import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;

public interface SvcAlmacen
{
	public void iniciarAlmacenArchivos();
	public String almacenarArchivo(MultipartFile archivo);
	public Path cargarArchivo(String nombreArchivo);
	public org.springframework.core.io.Resource cargarComoRecurso(String nombreArchivo);
	public void eliminararchivo(String nombreArchivo);
}
