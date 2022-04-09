package nl.hva.miw.pirate_bank_setup.controller;

import nl.hva.miw.pirate_bank_setup.service.CreateBankService;
import nl.hva.miw.pirate_bank_setup.service.CreateUsersAndCustomersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
public class LauncherController {

    CreateBankService createBankService;
    CreateUsersAndCustomersService createUsersAndCustomersService;


    @Autowired
    public LauncherController(CreateBankService createBankService, CreateUsersAndCustomersService createUsersAndCustomersService) {
        this.createBankService = createBankService;
        this.createUsersAndCustomersService = createUsersAndCustomersService;
    }

   @GetMapping(value = "/start")
    public boolean startFillingDatabase() {
        createBankService.createBank();
        createUsersAndCustomersService.createOtherUsersAndAttributes();
        return true;
    }

}

    
    
    
    
    
    
    
    
    
    
    
    


