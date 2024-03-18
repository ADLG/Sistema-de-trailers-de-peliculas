package com.sistema.cartelera.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.sistema.cartelera.entity.Pelicula;
import com.sistema.cartelera.repository.RepoPelicula;

@Controller
@RequestMapping("")
public class CtrlHome
{
	@Autowired
	private RepoPelicula repoPelicula;

	@GetMapping("")
	public ModelAndView verPaginaInicio()
	{
		List<Pelicula> ultimasPeliculas = repoPelicula.findAll(PageRequest.of(0, 5,Sort.by("fechaEstreno").descending())).toList();
		return new ModelAndView("index").addObject("ultimasPeliculas",ultimasPeliculas);
	}

	@GetMapping("/peliculas")
	public ModelAndView listarPeliculas(@PageableDefault(sort="fechaEstreno",direction = Sort.Direction.DESC)Pageable pageable)
	{
		Page<Pelicula> peliculas = repoPelicula.findAll(pageable);
		return new ModelAndView("peliculas").addObject("peliculas",peliculas);
	}

	@GetMapping("/peliculas/{id}")
	public ModelAndView mostrarDetallesPelicula(@PathVariable("id") Integer id)
	{
		Pelicula pelicula = repoPelicula.getOne(id);
		return new ModelAndView("pelicula").addObject("pelicula",pelicula);
	}
}
