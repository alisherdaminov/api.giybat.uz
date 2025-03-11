package api.giybat.uz.services;

import api.giybat.uz.enums.AppLanguage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

// ResourceBundleService bu - umumiy 1 ta service class uni har qanday joydan language kk bolsa chaqirib ishlatamiz
@Service
public class ResourceBundleService {


    @Autowired
    private ResourceBundleMessageSource bundleMessageSource;

    public String getMessage(String code, AppLanguage appLanguage) {
        return bundleMessageSource.getMessage(code, null, new Locale(appLanguage.name()));
    }
}
