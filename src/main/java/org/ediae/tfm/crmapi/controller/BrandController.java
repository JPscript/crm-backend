package org.ediae.tfm.crmapi.controller;

import org.ediae.tfm.crmapi.entity.Brand;
import org.ediae.tfm.crmapi.service.IBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Autowired
    private IBrandService brandService;

    @PostMapping("/crearMarca")
    public ModelMap createBrand(@RequestBody Brand brand) {
        try {
            return GeneralUtilsController.crearRespuestaModelMapOk(brandService.createBrand(brand));
        } catch (Exception ex) {
            return GeneralUtilsController.crearRespuestaModelMapError(ex);
        }
    }

    @GetMapping("/listarMarcas")
    public ModelMap findAllBrands() {
        try {
            return GeneralUtilsController.crearRespuestaModelMapOk(brandService.findAllBrands());
        } catch (Exception ex) {
            return GeneralUtilsController.crearRespuestaModelMapError(ex);
        }
    }

    @GetMapping("/marca")
    public ModelMap findBrandByName(@RequestParam String name) {
        try {
            return GeneralUtilsController.crearRespuestaModelMapOk(brandService.findByName(name));
        } catch (Exception ex) {
            return GeneralUtilsController.crearRespuestaModelMapError(ex);
        }
    }

    @GetMapping("/encontrarMarca/{id}")
    public ModelMap getById(@PathVariable Long id) {
        try {
            return GeneralUtilsController.crearRespuestaModelMapOk(brandService.findBrandById(id));
        }catch (Exception ex) {
            return GeneralUtilsController.crearRespuestaModelMapError(ex);
        }
    }

    @PutMapping("/actualizarMarca/{id}")
    public ModelMap updateBrand(@PathVariable Long id, @RequestBody Brand brand) {
        try {
            brand.setId(id);
            return GeneralUtilsController.crearRespuestaModelMapOk(brandService.updateBrand(brand));
        } catch (Exception ex) {
            return GeneralUtilsController.crearRespuestaModelMapError(ex);
        }
    }

    @DeleteMapping("/eliminarMarca/{id}")
    public ModelMap deleteBrandById(@PathVariable Long id) {
        try {
            return GeneralUtilsController.crearRespuestaModelMapOk(brandService.deleteBrandById(id));
        } catch (Exception ex) {
            return GeneralUtilsController.crearRespuestaModelMapError(ex);
        }
    }
}
