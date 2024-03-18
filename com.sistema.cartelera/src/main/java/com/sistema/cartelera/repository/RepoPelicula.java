package com.sistema.cartelera.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sistema.cartelera.entity.Pelicula;

public interface RepoPelicula extends JpaRepository<Pelicula, Integer>{

}
