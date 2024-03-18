package com.sistema.cartelera.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistema.cartelera.service.SvcAlmacenImp;

@RestController
@RequestMapping("/assets")
public class CtrlAssets
{
	@Autowired
	private SvcAlmacenImp svcAlmacen;

	@GetMapping("/{filename:.+}")
	public Resource obtenerRecurso(@PathVariable("filename") String filename)
	{
		return svcAlmacen.cargarComoRecurso(filename);
	}
}
