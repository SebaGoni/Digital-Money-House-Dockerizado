package com.DMH.accountsservice.service;

import com.DMH.accountsservice.dto.ActivityDTO;
import com.DMH.accountsservice.dto.ActivityFilterDTO;
import com.DMH.accountsservice.entities.Account;
import com.DMH.accountsservice.entities.Activity;
import com.DMH.accountsservice.exceptions.ResourceNotFoundException;
import com.DMH.accountsservice.repository.AccountsRepository;
import com.DMH.accountsservice.repository.ActivityRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private AccountsRepository accountsRepository;

    // Obtener todas las actividades por cuenta, ordenadas por fecha (descendente)
    public List<ActivityDTO> getAllActivitiesByAccountId(Long accountId) throws ResourceNotFoundException {
        // Buscar la cuenta por ID
        Account account = accountsRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        // Obtener las actividades de la cuenta
        List<Activity> activities = activityRepository.findAllByAccountIdOrderByDateDesc(accountId);

        // Convertir las entidades Activity a DTO
        return activities.stream()
                .map(this::convertToDto)  // Método para convertir Activity a ActivityDTO
                .collect(Collectors.toList());
    }

    // Obtener el detalle de una actividad específica (activityId)
    public ActivityDTO getActivityDetail(Long accountId, Long id)
            throws ResourceNotFoundException {

        // Buscar la actividad por accountId y activityId
        Activity activity = activityRepository.findByAccountIdAndId(accountId, id)
                .orElseThrow(() -> new ResourceNotFoundException("ActivityID inexistente"));

        // Convertir la actividad a DTO
        return convertToDto(activity);
    }

    public List<ActivityDTO> filterActivities(ActivityFilterDTO filter) {
        // Establecer valores predeterminados para montos si no se proporcionan
        BigDecimal minAmount = filter.getMinAmount() != null ? filter.getMinAmount() : BigDecimal.ZERO;
        BigDecimal maxAmount = filter.getMaxAmount() != null ? filter.getMaxAmount() : BigDecimal.valueOf(1000000000);

        LocalDate startDate = filter.getStartDate();
        LocalDate endDate = filter.getEndDate();
        String activityType = filter.getActivityType(); // Puede ser null

        // Llamar al repositorio con los filtros aplicados
        List<Activity> activities = activityRepository.filterActivities(
                minAmount,
                maxAmount,
                startDate, // Puede ser null
                endDate,   // Puede ser null
                activityType // Puede ser null
        );

        return activities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Activity getActivityById(Long activityId) throws ResourceNotFoundException {
        return activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));
    }

    // Método para generar el comprobante de actividad en PDF
    public ByteArrayOutputStream generateActivityReceipt(Activity activity) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Agregar información de la actividad al PDF
            document.add(new Paragraph("Comprobante de Actividad"));
            document.add(new Paragraph("ID de Actividad: " + activity.getId()));
            document.add(new Paragraph("Tipo de Actividad: " + activity.getType()));
            document.add(new Paragraph("Monto: " + activity.getAmount()));
            document.add(new Paragraph("Fecha: " + activity.getDate().toString()));
            document.add(new Paragraph("Descripción: " + activity.getDescription()));

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return outputStream;
    }

    // Método para convertir una entidad Activity a ActivityDTO
    private ActivityDTO convertToDto(Activity activity) {
        ActivityDTO dto = new ActivityDTO();
        dto.setId(activity.getId());
        dto.setAccountId(activity.getAccountId());
        dto.setAmount(activity.getAmount());
        dto.setType(activity.getType());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // Puedes ajustar el formato si es necesario
        dto.setDate(activity.getDate().format(formatter));
        dto.setCvu(activity.getDescription());
        return dto;
    }
}




