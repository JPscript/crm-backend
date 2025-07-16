package org.ediae.tfm.crmapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.ediae.tfm.crmapi.constant.GeneralConstants;
import org.ediae.tfm.crmapi.security.JwtService;
import org.ediae.tfm.crmapi.dto.LoginRequest;
import org.ediae.tfm.crmapi.entity.AppUser;
import org.ediae.tfm.crmapi.exception.GeneralException;
import org.ediae.tfm.crmapi.service.iAppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appUser")
public class AppUserController {

    @Autowired
    private iAppUserService appUserService;
    @Autowired
    private JwtService jwtService;


    @PostMapping("/registro")
    public ResponseEntity<ModelMap> registerAppUser(@RequestBody AppUser appUser) {
        try {
            // Log incoming data before returning
            System.out.println("[DEBUG] Incoming appUser: " + new ObjectMapper().writeValueAsString(appUser));

            ModelMap successBody = GeneralUtilsController.crearRespuestaModelMapOk(
                    appUserService.registerAppUser(appUser)
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(successBody); // 201 Created
        } catch (GeneralException genEx) {
            ModelMap errorBody = GeneralUtilsController.crearRespuestaModelMapError(genEx);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    @PostMapping("/crearAppUser")
    public ResponseEntity<ModelMap> createAppUser(@RequestBody AppUser appUser) {
        try {
            // Log incoming data before returning
            System.out.println("[DEBUG] Incoming appUser: " + new ObjectMapper().writeValueAsString(appUser));

            ModelMap successBody = GeneralUtilsController.crearRespuestaModelMapOk(
                    appUserService.createAppUser(appUser)
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(successBody); // 201 Created
        } catch (GeneralException genEx) {
            ModelMap errorBody = GeneralUtilsController.crearRespuestaModelMapError(genEx);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
        } catch (JsonProcessingException e) {
            ModelMap errorBody = GeneralUtilsController.crearRespuestaModelMapError(
                    new GeneralException(GeneralConstants.JSON_PROCESSING_ERROR_CODE, GeneralConstants.JSON_PROCESSING_ERROR_MESSAGE)
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody);
    }
        }

    @GetMapping("/obtenerTodosAppUser")
    public ModelMap getAllAppUsers() {
        try {
            return GeneralUtilsController.crearRespuestaModelMapOk(appUserService.findAllAppUsers());
        } catch (Exception ex) {
            return GeneralUtilsController.crearRespuestaModelMapError(ex);
        }
    }

    @GetMapping("/obtenerAppUserById/{id}")
    public ModelMap getAppUserById(@PathVariable Long id) {
        try {
            return GeneralUtilsController.crearRespuestaModelMapOk(appUserService.findAppUserById(id));
        } catch (Exception ex) {
            return GeneralUtilsController.crearRespuestaModelMapError(ex);
        }
    }

    @GetMapping("/obtenerAppUserByEmail/{email}")
    public ModelMap getAppUserByEmail(@PathVariable String email) {
        try {
            return GeneralUtilsController.crearRespuestaModelMapOk(appUserService.findAppUserByEmail(email));
        } catch (Exception ex) {
            return GeneralUtilsController.crearRespuestaModelMapError(ex);
        }
    }

    @GetMapping("/obtenerAppUserByName")
    public ModelMap getAppUserByName(@RequestParam String name) {
        try {
            return GeneralUtilsController.crearRespuestaModelMapOk(appUserService.findAppUserByName(name));
        } catch (Exception ex) {
            return GeneralUtilsController.crearRespuestaModelMapError(ex);
        }
    }

    @PutMapping("/actualizarAppUser/{id}")
    public ModelMap updateAppUser(@PathVariable Long id, @RequestBody AppUser appUser) {
        appUser.setId(id);
        try {
            return GeneralUtilsController.crearRespuestaModelMapOk(appUserService.updateAppUser(appUser));
        } catch (GeneralException ex) {
            return GeneralUtilsController.crearRespuestaModelMapError(ex);
        } catch (Exception ex) {
            return GeneralUtilsController.crearRespuestaModelMapError(new GeneralException(
                    GeneralConstants.APPUSER_UPDATE_ERROR_CODE,
                    GeneralConstants.APPUSER_UPDATE_ERROR_MESSAGE
            ));
        }
    }

    @DeleteMapping("eliminarAppUser/{id}")
    public ModelMap deleteAppUserById(@PathVariable Long id) {
        try {
            return GeneralUtilsController.crearRespuestaModelMapOk(appUserService.deleteAppUserById(id));
        } catch (Exception ex) {
            return GeneralUtilsController.crearRespuestaModelMapError(ex);
        }
    }

    @PostMapping("/login")
    public ModelMap login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            AppUser appUser = appUserService.login(loginRequest.getEmail(), loginRequest.getPassword());
            String token = jwtService.generateToken(appUser);
            ResponseCookie cookie = ResponseCookie.from("jwt", token)
                    .httpOnly(true)
                    .secure(true) // set to true when using HTTPS
                    .path("/")
                    .maxAge(86400)
                    .sameSite("None")
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            ModelMap modelMap = GeneralUtilsController.crearRespuestaModelMapOk(appUser);
            modelMap.put("loginMessage", "User ID " + appUser.getId() + " ha iniciado la sesión con éxito");
            return modelMap;
        } catch(GeneralException genEx){
            return GeneralUtilsController.crearRespuestaModelMapError(genEx);
        }
    }
    @GetMapping("/me")
    public ModelMap getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof AppUser user) {
            return GeneralUtilsController.crearRespuestaModelMapOk(user);
        } else {
            return GeneralUtilsController.crearRespuestaModelMapError(new Exception("Usuario no autenticado"));
        }
    }

    @PostMapping("/logout")
    public ModelMap logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        return GeneralUtilsController.crearRespuestaModelMapOk("Sesión cerrada con éxito");
    }
    @GetMapping("/appUser/validate-session")
    public ResponseEntity<?> validateSession(HttpServletRequest request) {
        return ResponseEntity.ok().build(); // Spring Security + JwtAuthFilter will block this if JWT is invalid
    }
}
