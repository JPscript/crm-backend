package org.ediae.tfm.crmapi.controller;

import org.ediae.tfm.crmapi.constant.GeneralConstants;
import org.ediae.tfm.crmapi.entity.Customer;
import org.ediae.tfm.crmapi.exception.GeneralException;
import org.ediae.tfm.crmapi.service.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cliente")
public class CustomerController {

    @Autowired
    private ICustomerService customerService;

    @GetMapping("/todos")
    public ModelMap getCustomer()  {
    try {
        return GeneralUtilsController.crearRespuestaModelMapOk(customerService.findAllCustomers());
        }catch (Exception ex) {
            return GeneralUtilsController.crearRespuestaModelMapError(ex);
        }
    }

    @GetMapping("/obtener/{id}")
    public ModelMap getCustomerById( @PathVariable Long id) {
        try {
            return GeneralUtilsController.crearRespuestaModelMapOk(customerService.findCustomerById(id));
        }catch (Exception ex) {
            return GeneralUtilsController.crearRespuestaModelMapError(ex);
        }
    }

    @DeleteMapping("/borrar/{id}")
    public ModelMap delete(@PathVariable Long id) {
        try {
            return GeneralUtilsController.crearRespuestaModelMapOk(customerService.deleteCustomerById(id));
        }  catch (Exception ex) {
            return GeneralUtilsController.crearRespuestaModelMapError(ex);
        }
    }


    @PostMapping("/crearCliente")
    public ModelMap createCustomer(@RequestBody Customer customer) {
        try {
            return GeneralUtilsController.crearRespuestaModelMapOk(customerService.createCustomer(customer));
        }  catch (Exception ex) {
            return GeneralUtilsController.crearRespuestaModelMapError(ex);
        }
    }

    @PutMapping("/actualizarCliente")
    public ModelMap updateCustomer(@RequestBody Customer customer) {
        try {
            return GeneralUtilsController.crearRespuestaModelMapOk(
                    customerService.updateCustomer(customer)
            );
        } catch (GeneralException ge) {
            return GeneralUtilsController.crearRespuestaModelMapError(ge); // ← IMPORTANTE
        } catch (Exception e) {
            return GeneralUtilsController.crearRespuestaModelMapError(e);
        }
    }

}
