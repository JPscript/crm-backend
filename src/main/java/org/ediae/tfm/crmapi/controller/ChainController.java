package org.ediae.tfm.crmapi.controller;

import org.ediae.tfm.crmapi.service.IChainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chain")
public class ChainController {

    @Autowired
    private IChainService chainService;

    @GetMapping("/listarCadenas")
    public ModelMap findAllChains() {
        try {
            return GeneralUtilsController.crearRespuestaModelMapOk(chainService.findAllChains());
        } catch (Exception ex) {
            return GeneralUtilsController.crearRespuestaModelMapError(ex);
        }
    }

    @GetMapping("/encontrarCadena/{id}")
    public ModelMap getChainById(@PathVariable Long id) {
        try {
            return GeneralUtilsController.crearRespuestaModelMapOk(chainService.findChainById(id));
        }catch (Exception ex) {
            return GeneralUtilsController.crearRespuestaModelMapError(ex);
        }
    }
}
