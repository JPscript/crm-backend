package org.ediae.tfm.crmapi.service.impl;

import org.ediae.tfm.crmapi.constant.GeneralConstants;
import org.ediae.tfm.crmapi.entity.AppUser;
import org.ediae.tfm.crmapi.entity.Role;
import org.ediae.tfm.crmapi.exception.GeneralException;
import org.ediae.tfm.crmapi.exception.GeneralExceptionRegister;
import org.ediae.tfm.crmapi.repository.AppUserRepository;
import org.ediae.tfm.crmapi.repository.RoleRepository;
import org.ediae.tfm.crmapi.service.iAppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AppUserServiceImpl implements iAppUserService {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AppUserServiceImpl.class);

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public AppUser createAppUser(AppUser appUser) throws GeneralException {
        if (appUserRepository.findAppUserByEmail(appUser.getEmail()).isPresent()) {
            throw new GeneralException(
                    GeneralConstants.APPUSER_EMAIL_IN_USE_ERROR_CODE,
                    GeneralConstants.APPUSER_CREATION_ERROR_MESSAGE + ". " + GeneralConstants.APPUSER_EMAIL_IN_USE_ERROR_MESSAGE);
        }
        try{
            Role role = roleRepository.findById(appUser.getRole().getId())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
            appUser.setRole(role);
            appUser.setId(null);
            appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
            return appUserRepository.save(appUser);

        } catch (Exception ex) {
            throw new GeneralException(
                    GeneralConstants.GENERAL_ERROR_CODE,
                    GeneralConstants.GENERAL_ERROR_MESSAGE);
        }
    }
    @Override
    public AppUser registerAppUser(AppUser appUser) throws GeneralException {
        if (appUserRepository.findAppUserByEmail(appUser.getEmail()).isPresent()) {
            throw new GeneralException(
                    GeneralConstants.APPUSER_EMAIL_IN_USE_ERROR_CODE,
                    GeneralConstants.APPUSER_CREATION_ERROR_MESSAGE + ". " + GeneralConstants.APPUSER_EMAIL_IN_USE_ERROR_MESSAGE);
        }
        try{
            Role defaultRole = roleRepository.findById(1L).orElseThrow(() -> new RuntimeException("Role not found"));
            appUser.setRole(defaultRole);
            appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
            return appUserRepository.save(appUser);

        } catch (Exception ex) {
            logger.error("Error registrando usuario", ex);
            throw new GeneralExceptionRegister(
                    GeneralConstants.GENERAL_ERROR_CODE,
                    GeneralConstants.GENERAL_ERROR_MESSAGE, ex);
        }
    }

    @Override
    public List<AppUser> findAllAppUsers() throws GeneralException {
        try{
            return appUserRepository.findAll();
        } catch (Exception ex) {
            throw new GeneralException(
                    GeneralConstants.GENERAL_ERROR_CODE,
                    GeneralConstants.GENERAL_ERROR_MESSAGE);
        }
    }

    @Override
    public Optional<AppUser> findAppUserById(Long id) throws GeneralException {
        try{
            return appUserRepository.findById(id);
        } catch (Exception ex) {
            throw new GeneralException(
                    GeneralConstants.APPUSER_NOT_FOUND_CODE,
                    GeneralConstants.APPUSER_NOT_FOUND_MESSAGE);
        }
    }
    @Override
    public AppUser findAppUserByEmail(String email) throws GeneralException {
        try {
            Optional<AppUser> optionalAppUser = appUserRepository.findAppUserByEmail(email);
            if(optionalAppUser.isPresent()) {
                return optionalAppUser.get();
            } else {
                throw new GeneralException(
                        GeneralConstants.APPUSER_EMAIL_NOT_FOUND_CODE,
                        GeneralConstants.APPUSER_EMAIL_NOT_FOUND_MESSAGE);
            }
        } catch (GeneralException genEx) {
            throw genEx;
        } catch (Exception ex) {
            throw new GeneralException(
                    GeneralConstants.GENERAL_ERROR_CODE,
                    GeneralConstants.GENERAL_ERROR_MESSAGE);
        }
    }

    @Override
    public List<AppUser> findAppUserByName(String name) throws GeneralException {
        try{
            List<AppUser> appUsers = appUserRepository.findByNameContainingIgnoreCase(name);
            if(!appUsers.isEmpty()) {
                return appUsers;
            } else {
                throw new GeneralException(
                        GeneralConstants.APPUSER_NAME_SEARCH_ERROR_CODE,
                        GeneralConstants.APPUSER_NAME_SEARCH_ERROR_MESSAGE);
            }
        } catch (GeneralException genEx) {
            throw genEx;
        } catch (Exception ex) {
            throw new GeneralException(
                    GeneralConstants.GENERAL_ERROR_CODE,
                    GeneralConstants.GENERAL_ERROR_MESSAGE);
        }
    }

    @Override
    public AppUser updateAppUser(AppUser appUser) throws GeneralException {
        Optional<AppUser> existingUserOpt = appUserRepository.findAppUserByEmail(appUser.getEmail());
        if (existingUserOpt.isPresent() && !existingUserOpt.get().getId().equals(appUser.getId())) {
            throw new GeneralException(
                    GeneralConstants.APPUSER_EMAIL_IN_USE_ERROR_CODE,
                    GeneralConstants.APPUSER_UPDATE_ERROR_MESSAGE + ". " + GeneralConstants.APPUSER_EMAIL_IN_USE_ERROR_MESSAGE);
        }try {
            Role role = roleRepository.findById(appUser.getRole().getId())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
            appUser.setRole(role);
            appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
            return appUserRepository.save(appUser);
        } catch (Exception ex) {
            throw new GeneralException(
                    GeneralConstants.APPUSER_UPDATE_ERROR_CODE,
                    GeneralConstants.APPUSER_UPDATE_ERROR_MESSAGE);
        }
    }
    @Override
    public boolean deleteAppUserById(Long id) throws GeneralException {
        if (!appUserRepository.existsById(id)) {
            throw new GeneralException(
                    GeneralConstants.APPUSER_NOT_FOUND_CODE,
                    GeneralConstants.APPUSER_NOT_FOUND_MESSAGE);
        }
        else try {
            appUserRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new GeneralException(
                    GeneralConstants.APPUSER_DELETE_ERROR_CODE,
                    GeneralConstants.APPUSER_DELETE_ERROR_MESSAGE
            );
        }
    }

    @Override
    public AppUser login(String email, String password) throws GeneralException {
        try { Optional<AppUser> optionalAppUser = appUserRepository.findAppUserByEmail(email);
            if (optionalAppUser.isEmpty()) {
                throw new GeneralException(
                        GeneralConstants.APPUSER_LOGIN_ERROR_CODE,
                        GeneralConstants.APPUSER_LOGIN_ERROR_MESSAGE);
            }
            AppUser appUser = optionalAppUser.get();
            if (!passwordEncoder.matches(password, appUser.getPassword())) {
                throw new GeneralException(
                        GeneralConstants.APPUSER_LOGIN_ERROR_CODE,
                        GeneralConstants.APPUSER_LOGIN_ERROR_MESSAGE);
            }
            System.out.println("Login successful");
            return appUser;
        }catch (GeneralException genEx) {
            throw genEx;
        }catch (Exception ex) {
            throw new GeneralException(
                    GeneralConstants.GENERAL_ERROR_CODE,
                    GeneralConstants.GENERAL_ERROR_MESSAGE);
        }
    }
}
