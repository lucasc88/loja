package br.com.centraldaassinatura.loja.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.com.centraldaassinatura.loja.model.Announcement;

//Classe que realiza a conversão de Anuncios p/ ser melhor manipulada no xhtml
@FacesConverter("announcementConverter")
public class ConverterAnnouncement implements Converter {

    @Override
    public Object getAsObject(FacesContext context, 
            UIComponent component, String id) {
        if (id == null || id.trim().isEmpty()) return null;
        System.out.println("Convertendo para Objeto: " + id);
        Announcement announcement = new Announcement();
        return announcement;
    }

    @Override
    public String getAsString(FacesContext context, 
			UIComponent component, Object announcementObject) {
        if (announcementObject == null) return null;
        System.out.println("Convertendo para String: " + announcementObject);
        Announcement announcement = (Announcement) announcementObject;
        return announcement.getId().toString();
    }

}