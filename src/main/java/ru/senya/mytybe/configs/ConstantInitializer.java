package ru.senya.mytybe.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.senya.mytybe.repos.service.ConstantsRepository;

import static ru.senya.mytybe.MytybeApplication.*;

@SuppressWarnings("ALL")
//@Component
public class ConstantInitializer {

    private final ConstantsRepository constantsRepository;
    private final Logger logger = LoggerFactory.getLogger(ConstantInitializer.class);

    @Autowired
    public ConstantInitializer(ConstantsRepository constantsRepository) {
        this.constantsRepository = constantsRepository;
    }

    private void init() {
        try {
            MAIN_IP = constantsRepository.findByK("MAIN_IP").get().getVal();
            MAIN_PORT = constantsRepository.findByK("MAIN_PORT").get().getVal();
            STORAGE_PORT = constantsRepository.findByK("STORAGE_PORT").get().getVal();
            STORAGE_PORT = constantsRepository.findByK("STORAGE_PORT").get().getVal();
        } catch (NullPointerException e) {
            logger.error("constants are null");
        }
    }
}
