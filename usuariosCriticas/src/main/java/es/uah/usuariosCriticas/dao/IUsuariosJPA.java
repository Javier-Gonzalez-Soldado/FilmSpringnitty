package es.uah.usuariosCriticas.dao;

import es.uah.usuariosCriticas.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUsuariosJPA extends JpaRepository<Usuario, Integer> {

    Usuario findByNombre(String nombre);

    Usuario findByCorreo(String correo);

}