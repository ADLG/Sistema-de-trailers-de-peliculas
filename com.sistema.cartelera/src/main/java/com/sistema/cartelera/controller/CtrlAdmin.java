package com.sistema.cartelera.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.sistema.cartelera.entity.Genero;
import com.sistema.cartelera.entity.Pelicula;
import com.sistema.cartelera.repository.RepoGenero;
import com.sistema.cartelera.repository.RepoPelicula;
import com.sistema.cartelera.service.SvcAlmacenImp;

@Controller
@RequestMapping("/admin")
public class CtrlAdmin
{
	@Autowired
	private RepoPelicula repoPelicula;

	@Autowired
	private RepoGenero repoGenero;

	@Autowired
	private SvcAlmacenImp svcAlmacen;

	@GetMapping("")
	public ModelAndView verPaginaInicio(@PageableDefault(sort = "titulo",size = 10)Pageable pageable)
	{
		Page<Pelicula> peliculas = repoPelicula.findAll(pageable);
		return new ModelAndView("admin/index").addObject("peliculas",peliculas);
	}

	@GetMapping("/peliculas/nuevo")
	public ModelAndView mostrarFormularioNuevaPelicula()
	{
		List<Genero> generos = repoGenero.findAll(Sort.by("titulo"));
		return new ModelAndView("admin/nueva-pelicula").addObject("pelicula",new Pelicula()).addObject("generos",generos);
	}

	@PostMapping("/peliculas/nuevo")
	public ModelAndView registrarPelicula(@Validated Pelicula pelicula, BindingResult bindingResult)
	{
		if(bindingResult.hasErrors() || pelicula.getPortada().isEmpty())
		{
			if (pelicula.getPortada().isEmpty())
			{
				bindingResult.rejectValue("portada","MultipartNotEmpty");
			}
			List<Genero> generos = repoGenero.findAll(Sort.by("titulo"));
			return new ModelAndView("admin/nueva-pelicula").addObject("pelicula",pelicula).addObject("generos",generos);
		}

		String rutaPortada = svcAlmacen.almacenarArchivo(pelicula.getPortada());
		pelicula.setRutaPortada(rutaPortada);

		repoPelicula.save(pelicula);
		return new ModelAndView("redirect:/admin");
	}

	@GetMapping("/peliculas/{id}/editar")
	public ModelAndView editarPelicula(@PathVariable("id") Integer id)
	{
		Pelicula pelicula = repoPelicula.getOne(id);
		List<Genero> generos = repoGenero.findAll(Sort.by("titulo"));
		
		return new ModelAndView("admin/editar-pelicula")
		.addObject("pelicula",pelicula).addObject("generos",generos);
	}

	@PostMapping("/peliculas/{id}/editar")
	public ModelAndView actualizarPelicula(@PathVariable("id") Integer id,@Validated Pelicula pelicula, BindingResult bindingResult)
	{
		if (bindingResult.hasErrors())
		{
			List<Genero> generos = repoGenero.findAll(Sort.by("titulo"));
			return new ModelAndView("admin/nueva-pelicula")
			.addObject("pelicula",pelicula).addObject("generos",generos);
		}

		Pelicula peliculaDB = repoPelicula.getOne(id);
		peliculaDB.setTitulo(pelicula.getTitulo());
		peliculaDB.setSinopsis(pelicula.getSinopsis());
		peliculaDB.setFechaEstreno(pelicula.getFechaEstreno());
		peliculaDB.setYoutubeTrailerId(pelicula.getYoutubeTrailerId());
		peliculaDB.setGeneros(pelicula.getGeneros());

		if (!pelicula.getPortada().isEmpty())
		{
			svcAlmacen.eliminararchivo(peliculaDB.getRutaPortada());
			String rutaPortada = svcAlmacen.almacenarArchivo(pelicula.getPortada());
			peliculaDB.setRutaPortada(rutaPortada);	
		}

		repoPelicula.save(peliculaDB);
		return new ModelAndView("redirect:/admin");
	}

	@PostMapping("/peliculas/{id}/eliminar")
	public String eliminarPelicula(@PathVariable("id") Integer id)
	{
		Pelicula pelicula = repoPelicula.getOne(id);
		repoPelicula.delete(pelicula);
		svcAlmacen.eliminararchivo(pelicula.getRutaPortada());

		return "redirect:/admin";
	}
}
