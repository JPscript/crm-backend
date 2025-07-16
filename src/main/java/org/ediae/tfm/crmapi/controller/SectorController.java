package org.ediae.tfm.crmapi.controller;

import org.ediae.tfm.crmapi.service.ISectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sector")
public class SectorController {

    @Autowired
    private ISectorService sectorService;

    @GetMapping("/listarSectores")
    public ModelMap findAllSectors() {
        try {
            return GeneralUtilsController.crearRespuestaModelMapOk(sectorService.findAllSectors());
        } catch (Exception ex) {
            return GeneralUtilsController.crearRespuestaModelMapError(ex);
        }
    }

    @GetMapping("/encontrarSector/{id}")
    public ModelMap getSectorById(@PathVariable Long id) {
        try {
            return GeneralUtilsController.crearRespuestaModelMapOk(sectorService.findSectorById(id));
        }catch (Exception ex) {
            return GeneralUtilsController.crearRespuestaModelMapError(ex);
        }
    }
}
