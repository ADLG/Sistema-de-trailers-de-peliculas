package com.sistema.cartelera.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import com.sistema.cartelera.exceptions.AlmacenException;
import com.sistema.cartelera.exceptions.FileNotFoundException;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

@Service
public class SvcAlmacenImp implements SvcAlmacen
{
	@Value("${storage.location}")
	private String storageLocation;

	@PostConstruct
	@Override
	public void iniciarAlmacenArchivos()
	{
		try
		{
			Files.createDirectories(Paths.get(storageLocation));
		} catch (IOException e)
		{
			throw new AlmacenException("Error al inicializar la ubicación en el almacen de archivos");
		}
	}

	@Override
	public String almacenarArchivo(MultipartFile archivo)
	{
		String nombreArchivo = archivo.getOriginalFilename();
		if (archivo.isEmpty())
		{
			throw new AlmacenException("No se puede almacenar un archivo vacio");
		}
		try
		{
			InputStream inputStram = archivo.getInputStream();
			Files.copy(inputStram, Paths.get(storageLocation).resolve(nombreArchivo),StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e)
		{
			throw new AlmacenException("Error al almacenar el archivo: "+nombreArchivo,e);
		}
		return nombreArchivo;
	}

	@Override
	public Path cargarArchivo(String nombreArchivo)
	{
		return Paths.get(storageLocation).resolve(nombreArchivo);
	}

	@Override
	public org.springframework.core.io.Resource cargarComoRecurso(String nombreArchivo)
	{
		try
		{
			Path archivo = cargarArchivo(nombreArchivo);
			org.springframework.core.io.Resource recurso = new UrlResource(archivo.toUri());
			if (recurso.exists() || recurso.isReadable())
			{
				return recurso;
			}
			else
			{
				throw new FileNotFoundException("No se pudo encontrar el archivo "+nombreArchivo);
			}
		} catch (MalformedURLException e) {
			throw new FileNotFoundException("No se pudo encontrar el archivo "+nombreArchivo,e);
		}
	}

	@Override
	public void eliminararchivo(String nombreArchivo)
	{
		Path archivo = cargarArchivo(nombreArchivo);
		try
		{
			FileSystemUtils.deleteRecursively(archivo);
		} catch (Exception e)
		{
			System.out.println(e);
		}
	}

}
