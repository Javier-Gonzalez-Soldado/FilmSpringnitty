package es.uah.clienteFilmaff.service;

import es.uah.clienteFilmaff.model.Rol;

import java.util.List;

public interface IRolesService {
    List<Rol> buscarTodos();

    Rol buscarRolPorId(Integer idRol);

    void guardarRol(Rol rol);

    void eliminarRol(Integer idRol);
}
