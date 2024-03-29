package es.uah.clienteFilmaff.controller;

import es.uah.clienteFilmaff.model.Actor;
import es.uah.clienteFilmaff.model.Critica;
import es.uah.clienteFilmaff.model.Pelicula;
import es.uah.clienteFilmaff.paginator.PageRender;
import es.uah.clienteFilmaff.service.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/cpeliculas")
public class PeliculasController {

    @Autowired
    IPeliculasService peliculasService;

    @Autowired
    IActoresService actoresService;

    @Autowired
    private IUploadFileService uploadFileService;

    @Autowired
    IPaisesService paisesService;

    @Autowired
    ICriticasService criticasService;


    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> verFoto(@PathVariable String filename) {

        Resource recurso = null;

        try {
            recurso = uploadFileService.load(filename);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
                .body(recurso);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Actor.class, new ActorEditor(actoresService));
    }


    @GetMapping(value = {"/", "/home", ""})
    public String home (Model model){
        return "home";
    }

    @GetMapping(value = "/panelControl")
    public String panelControl (Model model){
        return "panelControl";
    }

    @GetMapping("/nueva")
    public String nueva (Model model) {
        List<Actor> actorList = actoresService.listaActores();
        model.addAttribute("titulo", "Nueva Pelicula");
        model.addAttribute("paises", paisesService.obtenerListPaises());
        Pelicula pelicula = new Pelicula();
        model.addAttribute("pelicula", pelicula);
        model.addAttribute("listadoActores", actorList);
        return "peliculas/formPelicula";
    }

    @GetMapping("/tabla")
    public String tablaPeliculas(Model model, @RequestParam(name = "page", defaultValue = "0") int page)
    {
        Pageable pageable = PageRequest.of(page, 5);
        Page<Pelicula> listado = peliculasService.buscarTodos(pageable);
        PageRender<Pelicula> pageRender = new PageRender<Pelicula>("/cpeliculas/tabla",listado);
        model.addAttribute("titulo", "Tabla Películas");
        model.addAttribute("listadoPeliculas", listado);
        model.addAttribute("page",pageRender);
        return "peliculas/tablaPeliculas";
    }

    @GetMapping("/listado")
    public String listadoPeliculas(Model model, @RequestParam(name = "page", defaultValue = "0") int page)
    {
        Pageable pageable = PageRequest.of(page, 8);
        Page<Pelicula> listado = peliculasService.buscarTodos(pageable);
        PageRender<Pelicula> pageRender = new PageRender<Pelicula>("/cpeliculas/listado",listado);
        model.addAttribute("titulo", "Listado de todas las Peliculas");
        model.addAttribute("listadoPeliculas", listado);
        model.addAttribute("page",pageRender);
        return "peliculas/listadoPeliculas";
    }

    @GetMapping("/idpelicula/{id}")
    public String buscarPeliculaPorId(Model model, @PathVariable("id") Integer id) {
        Pelicula pelicula = peliculasService.buscarPeliculaPorId(id);
        model.addAttribute("pelicula", pelicula);
        return "peliculas/formPelicula";
    }

    @GetMapping("/detallePelicula/{id}")
    public String detallePelicula(Model model, @PathVariable("id") Integer id) {
        Pelicula pelicula = peliculasService.buscarPeliculaPorId(id);
        List<Critica> criticas = criticasService.buscarCriticaPorIdDetallePelicula(id);
        double notaMedia = criticasService.obtenerNotaMediaCriticasPeliculaId(id);
        DecimalFormat df = new DecimalFormat("#.#");
        model.addAttribute("pelicula", pelicula);
        model.addAttribute("criticas", criticas);
        model.addAttribute("notaMedia",df.format(notaMedia));
        return "peliculas/detallePelicula";
    }

    @GetMapping("/titulo/{pageSize}")
    public String buscarPeliculaPorTitulo(Model model, @PathVariable("pageSize") int pageSize,
                                          @RequestParam(name="page", defaultValue = "0") int page,
                                          @RequestParam("titulo") String titulo)
    {
        Pageable pageable = PageRequest.of(page,pageSize);
        Page<Pelicula> listado;
        if(titulo.equals("")){
            listado = peliculasService.buscarTodos(pageable);
        }
        else{
            listado = peliculasService.buscarPeliculaPorTitulo(titulo, pageable);
        }

        PageRender<Pelicula> pageRender = pageSize == 8 ?
                new PageRender<Pelicula>("/cpeliculas/titulo/8?titulo=" + titulo, listado) :
                new PageRender<Pelicula>("/cpeliculas/titulo/5?titulo=" + titulo, listado);

        model.addAttribute("titulo", "Listado de Peliculas por Titulo");
        model.addAttribute("listadoPeliculas", listado);
        model.addAttribute("page", pageRender);
        return pageSize == 8 ? "peliculas/listadoPeliculas" :
                                "peliculas/tablaPeliculas";
    }

    @GetMapping("/genero/{pageSize}")
    public String buscarPeliculaPorGenero(Model model, @PathVariable("pageSize") int pageSize,
                                          @RequestParam(name="page", defaultValue = "0") int page,
                                          @RequestParam("genero") String genero)
    {
        Pageable pageable = PageRequest.of(page,pageSize);
        Page<Pelicula> listado;
        if(genero.equals("")){
            listado = peliculasService.buscarTodos(pageable);
        }
        else{
            listado = peliculasService.buscarPeliculaPorGenero(genero, pageable);
        }

        PageRender<Pelicula> pageRender = pageSize == 8 ?
                new PageRender<Pelicula>("/cpeliculas/genero/8?genero=" + genero, listado) :
                new PageRender<Pelicula>("/cpeliculas/genero/5?genero=" + genero, listado);

        model.addAttribute("titulo", "Listado de Peliculas por Genero");
        model.addAttribute("listadoPeliculas", listado);
        model.addAttribute("page", pageRender);
        return pageSize == 8 ? "peliculas/listadoPeliculas" :
                                "peliculas/tablaPeliculas";
    }


    @GetMapping("/actor/{pageSize}")
    public String buscarPeliculaPorActor(Model model, @PathVariable("pageSize") int pageSize,
                                         @RequestParam(name="page", defaultValue = "0") int page,
                                          @RequestParam("actor") String actor)
    {
        Pageable pageable = PageRequest.of(page,pageSize);
        Page<Pelicula> listado;
        if(actor.equals("")){
            listado = peliculasService.buscarTodos(pageable);
        }
        else{
            listado = peliculasService.buscarPeliculaPorActor(actor, pageable);
        }

        PageRender<Pelicula> pageRender = pageSize == 8 ?   new PageRender<Pelicula>("/cpeliculas/listado", listado) :
                                                            new PageRender<Pelicula>("/cpeliculas/tabla", listado);

        model.addAttribute("titulo", "Listado de Peliculas por Actores");
        model.addAttribute("listadoPeliculas", listado);
        model.addAttribute("page", pageRender);
        return pageSize == 8 ? "peliculas/listadoPeliculas" :
                                "peliculas/tablaPeliculas";
    }

    @GetMapping("/anio/{pageSize}")
    public String buscarPeliculaPorAnio(Model model, @PathVariable("pageSize") int pageSize,
                                        @RequestParam(name="page", defaultValue = "0") int page,
                                        @RequestParam("min") Integer min, @RequestParam("max") Integer  max)
    {
        Pageable pageable = PageRequest.of(page,pageSize);

        Page<Pelicula> listado = peliculasService.buscarPeliculaPorAnio(min,max,pageable);

        PageRender<Pelicula> pageRender = pageSize == 8 ?
                new PageRender<Pelicula>("/cpeliculas/anio/8?min=" + min + "&max=" + max, listado) :
                new PageRender<Pelicula>("/cpeliculas/anio/5?min=" + min + "&max=" + max, listado);

        model.addAttribute("titulo", "Listado de Peliculas por Año");
        model.addAttribute("listadoPeliculas", listado);
        model.addAttribute("page", pageRender);
        return pageSize == 8 ? "peliculas/listadoPeliculas" :
                "peliculas/tablaPeliculas";
    }



    @PostMapping("/guardar/")
    public String guardarPelicula(Model model, Pelicula pelicula,
                                  @RequestParam("file") MultipartFile foto, RedirectAttributes attributes) {

        if (!foto.isEmpty()) {

            if (pelicula.getIdPelicula() != null && pelicula.getIdPelicula() > 0 && pelicula.getImagen() != null
                    && pelicula.getImagen().length() > 0) {

                uploadFileService.delete(pelicula.getImagen());
            }

            String uniqueFilename = null;
            try {
                uniqueFilename = uploadFileService.copy(foto);
            } catch (IOException e) {
                e.printStackTrace();
            }

            attributes.addFlashAttribute("msg", "Has subido correctamente '" + uniqueFilename + "'");

            pelicula.setImagen(uniqueFilename);
        }

        peliculasService.guardarPelicula(pelicula);
        model.addAttribute("titulo", "Nueva Pelicula");
        attributes.addFlashAttribute("msg", "Los datos del curso fueron guardados!");
        return "redirect:/cpeliculas/tabla";
    }

    @GetMapping("/editar/{id}")
    public String editarPelicula (Model model, @PathVariable("id") Integer id) {
        Pelicula pelicula = peliculasService.buscarPeliculaPorId(id);
        model.addAttribute("titulo", "Editar Pelicula");
        model.addAttribute("listadoActores", actoresService.listaActores());
        model.addAttribute("paises", paisesService.obtenerListPaises());
        model.addAttribute("actoresSeleccionados",peliculasService.idActoresPelicula(pelicula));
        model.addAttribute("pelicula",pelicula);
        return "peliculas/formPelicula";
    }

    @GetMapping("/borrar/{id}")
    public String eliminarPelicula(Model model, @PathVariable("id") Integer id, RedirectAttributes attributes) {
        Pelicula pelicula = peliculasService.buscarPeliculaPorId(id);
        if (uploadFileService.delete(pelicula.getImagen())) {
            attributes.addFlashAttribute("msg", "Imagen " + pelicula.getImagen() + " eliminada con exito!");
        }

        peliculasService.eliminarPelicula(id);
        attributes.addFlashAttribute("msg", "Los datos del curso fueron borrados!");

        return "redirect:/cpeliculas/tabla";
    }
}
