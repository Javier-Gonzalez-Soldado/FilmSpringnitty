package es.uah.clienteFilmaff.service;


import es.uah.clienteFilmaff.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class UsuariosServiceImpl implements IUsuariosService {

    @Autowired
    RestTemplate template;

    String url = "http://localhost:8090/api/usuarios/usuarios";


    @Override
    public List<Usuario> buscarTodosLista(){
        Usuario[] usuarios = template.getForObject(url, Usuario[].class);
        return Arrays.asList(usuarios);
    }
    @Override
    public Page<Usuario> buscarTodos(Pageable pageable) {
        Usuario[] usuarios = template.getForObject(url, Usuario[].class);
        List<Usuario> usuariosList = Arrays.asList(usuarios);

        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Usuario> list;

        if (usuariosList.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, usuariosList.size());
            list = usuariosList.subList(startItem, toIndex);
        }

        Page<Usuario> page = new PageImpl<>(list, PageRequest.of(currentPage, pageSize), usuariosList.size());
        return page;
    }


    @Override
    public Usuario buscarUsuarioPorId(Integer idUsuario) {
        Usuario usuario = template.getForObject(url + "/" + idUsuario, Usuario.class);
        return usuario;
    }

    @Override
    public Usuario buscarUsuarioAutenticado(Authentication authentication)
    {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return buscarUsuarioPorCorreo(userDetails.getUsername());
        }
        return null;
    }

    @Override
    public Page<Usuario> buscarUsuariosPorNombre(String nombre, Pageable pageable) {
        Usuario[] usuarios = template.getForObject(url+"/nombre/"+nombre, Usuario[].class);
        List<Usuario> usuariosList = Arrays.asList(usuarios);

        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Usuario> list;

        if (usuariosList.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, usuariosList.size());
            list = usuariosList.subList(startItem, toIndex);
        }

        Page<Usuario> page = new PageImpl<>(list, PageRequest.of(currentPage, pageSize), usuariosList.size());
        return page;
    }

    @Override
    public Usuario buscarUsuarioPorCorreo(String correo) {
        Usuario usuario = template.getForObject(url+"/email/"+correo, Usuario.class);
        return usuario;
    }
    @Override
    public Page<Usuario> buscarUsuariosPorCorreo(String correo, Pageable pageable) {
        Usuario[] usuarios = template.getForObject(url+"/correo/"+correo, Usuario[].class);
        List<Usuario> usuariosList = Arrays.asList(usuarios);

        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Usuario> list;

        if (usuariosList.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, usuariosList.size());
            list = usuariosList.subList(startItem, toIndex);
        }

        Page<Usuario> page = new PageImpl<>(list, PageRequest.of(currentPage, pageSize), usuariosList.size());
        return page;
    }

    @Override
    public void guardarUsuario(Usuario usuario) {
        if (usuario.getIdUsuario() != null && usuario.getIdUsuario() > 0) {
            template.put(url, usuario);
        } else {
            usuario.setIdUsuario(0);
            template.postForObject(url, usuario, String.class);
        }
    }

    @Override
    public void eliminarUsuario(Integer idUsuario) {
        template.delete(url+"/"+idUsuario);
    }

    @Override
    public Usuario login(String correo, String clave) {
        Usuario usuario = template.getForObject(url+"/login/"+correo+"/"+clave,
                Usuario.class);
        return usuario;
    }

    @Override
    public Usuario buscarUsuarioPorUsername(String email){
        Usuario usuario = template.getForObject(url+"/email/"+email, Usuario.class);
        return usuario;
    }

}
