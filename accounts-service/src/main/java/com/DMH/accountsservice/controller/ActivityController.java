package com.DMH.accountsservice.controller;

import com.DMH.accountsservice.dto.ActivityDTO;
import com.DMH.accountsservice.dto.ActivityFilterDTO;
import com.DMH.accountsservice.entities.Activity;
import com.DMH.accountsservice.exceptions.InvalidTokenException;
import com.DMH.accountsservice.exceptions.ResourceNotFoundException;
import com.DMH.accountsservice.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.List;

@RestController
@RequestMapping("/accounts/{accountId}/activity")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @GetMapping
    public ResponseEntity<?> getAllActivities(@PathVariable Long accountId, @RequestHeader("Authorization") String token) {
        try {
            // No se pasa el token, ya que la validación se realiza en el filtro
            List<ActivityDTO> activities = activityService.getAllActivitiesByAccountId(accountId);
            return ResponseEntity.ok(activities); // 200 OK
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(403).body("Sin permisos"); // 403 Forbidden
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body("Cuenta no encontrada"); // 404 Not Found
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Bad request"); // 400 Bad Request
        }
    }

    @GetMapping("/{activityId}")
    public ResponseEntity<?> getActivityDetail(
            @PathVariable Long accountId,
            @PathVariable Long activityId,
            @RequestHeader("Authorization") String token) {
        try {
            ActivityDTO activity = activityService.getActivityDetail(accountId, activityId);
            return ResponseEntity.ok(activity); // 200 OK
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(403).body("Sin permisos"); // 403 Forbidden
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body("TransferID inexistente"); // 404 Not Found
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Bad request"); // 400 Bad Request
        }
    }

    @PostMapping("/filter")
    public ResponseEntity<List<ActivityDTO>> filterActivities(
            @PathVariable Long accountId,
            @RequestBody ActivityFilterDTO filter) {
        List<ActivityDTO> activities = activityService.filterActivities(filter);
        return ResponseEntity.ok(activities); // 200 OK
    }

    @GetMapping("/{activityId}/receipt")
    public ResponseEntity<byte[]> downloadActivityReceipt(@PathVariable Long accountId, @PathVariable Long activityId) throws ResourceNotFoundException {
        Activity activity = activityService.getActivityById(activityId);
        ByteArrayOutputStream pdfStream = activityService.generateActivityReceipt(activity);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=comprobante_operacion.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");

        return new ResponseEntity<>(pdfStream.toByteArray(), headers, HttpStatus.OK);
    }
}


