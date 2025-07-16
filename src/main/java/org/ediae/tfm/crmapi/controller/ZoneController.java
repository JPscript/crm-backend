package org.ediae.tfm.crmapi.controller;


import org.ediae.tfm.crmapi.service.IZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/zone")
public class ZoneController {

    @Autowired
    private IZoneService zoneService;

    @GetMapping("/listarZonas")
    public ModelMap findAllZones() {
        try {
            return GeneralUtilsController.crearRespuestaModelMapOk(zoneService.findAllZones());
        } catch (Exception ex) {
            return GeneralUtilsController.crearRespuestaModelMapError(ex);
        }
    }

    @GetMapping("/encontrarZona/{id}")
    public ModelMap getZoneById(@PathVariable Long id) {
        try {
            return GeneralUtilsController.crearRespuestaModelMapOk(zoneService.findZoneById(id));
        }catch (Exception ex) {
            return GeneralUtilsController.crearRespuestaModelMapError(ex);
        }
    }
}
