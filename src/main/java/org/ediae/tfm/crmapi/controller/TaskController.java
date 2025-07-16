package org.ediae.tfm.crmapi.controller;

import org.ediae.tfm.crmapi.constant.GeneralConstants;
import org.ediae.tfm.crmapi.entity.AppUser;
import org.ediae.tfm.crmapi.entity.Task;
import org.ediae.tfm.crmapi.exception.GeneralException;
import org.ediae.tfm.crmapi.service.ITaskService;
import org.ediae.tfm.crmapi.service.iAppUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final ITaskService taskService;
    private final iAppUserService appUserService;
    public TaskController(ITaskService taskService, iAppUserService appUserService) {
        this.taskService = taskService;
        this.appUserService = appUserService;
    }

    @PostMapping("/crearTarea")
    public ModelMap crearTarea(@RequestBody Task task) {
        try {
            return GeneralUtilsController.crearRespuestaModelMapOk(taskService.create(task));
        } catch (Exception ex) {
            return GeneralUtilsController.crearRespuestaModelMapError(ex);
        }
    }

    @GetMapping("/listarTareas")
    public ModelMap getAll() {
        try {
            return GeneralUtilsController.crearRespuestaModelMapOk(taskService.findAll());
        }catch (Exception ex) {
            return GeneralUtilsController.crearRespuestaModelMapError(ex);
        }
    }

    @GetMapping("/encontrarPorId/{id}")
    public ModelMap getById(@PathVariable Long id) {
        try {
            return GeneralUtilsController.crearRespuestaModelMapOk(taskService.findById(id));
        }catch (Exception ex) {
            return GeneralUtilsController.crearRespuestaModelMapError(ex);
        }
    }

    @PutMapping("/actualizarTarea")
    public ModelMap update( @RequestBody Task task) {
        try {
            return GeneralUtilsController.crearRespuestaModelMapOk(taskService.update(task));
        }catch (Exception ex) {
            return GeneralUtilsController.crearRespuestaModelMapError(ex);
        }
    }

    @DeleteMapping("eliminarTarea/{id}")
    public ModelMap delete(@PathVariable Long id) {
        try {
            return GeneralUtilsController.crearRespuestaModelMapOk(taskService.delete(id));
        }catch (Exception ex) {
            return GeneralUtilsController.crearRespuestaModelMapError(ex);
        }
    }

    @GetMapping("/estado/{status}")
    public ModelMap getByStatus(@PathVariable Task.Status status) {
        try {
            return GeneralUtilsController.crearRespuestaModelMapOk(taskService.findByStatus(status));
        }catch (Exception ex) {
            return GeneralUtilsController.crearRespuestaModelMapError(ex);
        }
    }

    @GetMapping("/usuario/{userId}")
    public ModelMap findByUser(@PathVariable Long userId) {
        try {
            // el servicio subyacente lanza GeneralException si no encuentra
            List<Task> tareas = taskService.findByUserId(userId);
            return GeneralUtilsController.crearRespuestaModelMapOk(tareas);
        } catch (GeneralException ge) {
            return GeneralUtilsController.crearRespuestaModelMapError(ge);
        }
    }

    @GetMapping("/cliente/{customerId}")
    public ModelMap findByCustomer(@PathVariable Long customerId) {
        try {
            List<Task> tareas = taskService.findByCustomerId(customerId);
            return GeneralUtilsController.crearRespuestaModelMapOk(tareas);
        } catch (GeneralException ge) {
            return GeneralUtilsController.crearRespuestaModelMapError(ge);
        } catch (Exception ex) {
            return GeneralUtilsController.crearRespuestaModelMapError(ex);
        }
    }
}



