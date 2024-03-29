package es.uah.usuariosCriticas.service;

import es.uah.usuariosCriticas.model.Usuario;

import java.util.List;

public interface IUsuariosService {
    List<Usuario> buscarTodos();

    Usuario buscarUsuarioPorId(Integer idUsuario);

    List<Usuario> buscarUsuarioPorNombre(String nombre);

    List<Usuario> buscarUsuarioPorCorreo(String correo);

    void guardarUsuario(Usuario usuario);

    void eliminarUsuario(Integer idUsuario);

    void actualizarUsuario(Usuario usuario);

    void eliminarCritica(Integer idUsuario, Integer idCritica);

    Usuario buscarUsuarioPorCorreoClave(String correo, String clave);
    Usuario buscarUsuarioPorEmail(String username);


}
