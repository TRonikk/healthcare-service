package service.medical;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.medical.MedicalService;
import ru.netology.patient.service.medical.MedicalServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MedicalServiceImplTests {

    @Test
    void testMessageForCheckBloodPressure() {
        String patientId = "test-id";
        BloodPressure normalPressure = new BloodPressure(120, 80);
        BloodPressure incorrectPressure = new BloodPressure(100, 70);

        PatientInfo patientInfo = new PatientInfo(
                patientId,"Иван", "Петров", LocalDate.of(1980, 11, 26),
                        new HealthInfo(new BigDecimal("36.6"), normalPressure));

        // Моки
        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
        SendAlertService sendAlertService = Mockito.mock(SendAlertService.class);

        // Конфигурация мока
        Mockito.when(patientInfoRepository.getById(patientId)).thenReturn(patientInfo);

        // Создаем сервис мед. показаний
        MedicalService medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);

        // Проверка давления
        medicalService.checkBloodPressure(patientId, incorrectPressure);

        // Захват аргумента и проверка вызова
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(sendAlertService, Mockito.times(1)).send(captor.capture());

        // Проверка, что сообщение соответствует ожидаемому
        String expectedMessage = "Warning, patient with id: test-id, need help";
        Assertions.assertEquals(expectedMessage, captor.getValue());
    }

    @Test
    void testMessageForCheckTemperature() {
        String patientId = "test-id";
        BigDecimal normalTemperature = new BigDecimal("36.9");
        BigDecimal incorrectTemperature = new BigDecimal("35.0");

        PatientInfo patientInfo = new PatientInfo(
                patientId,"Иван", "Петров", LocalDate.of(1980, 11, 26),
                new HealthInfo(normalTemperature, new BloodPressure(120, 80)));

        // Моки
        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
        SendAlertService sendAlertService = Mockito.mock(SendAlertService.class);

        // Конфигурация мока
        Mockito.when(patientInfoRepository.getById(patientId)).thenReturn(patientInfo);

        // Создаем сервис мед. показаний
        MedicalService medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);

        // Проверка температуры
        medicalService.checkTemperature(patientId, incorrectTemperature);

        // Захват аргумента и проверка вызова
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(sendAlertService, Mockito.times(1)).send(captor.capture());

        // Проверка, что сообщение соответствует ожидаемому
        String expectedMessage = "Warning, patient with id: test-id, need help";
        Assertions.assertEquals(expectedMessage, captor.getValue());
    }

    @Test
    void checkNoMessagesWhenTheIndicatorsAreNormal() {
        String patientId = "test-id";
        BigDecimal normalTemperature = new BigDecimal("36.6");
        BloodPressure normalPressure = new BloodPressure(120, 80);

        PatientInfo patientInfo = new PatientInfo(
                patientId,"Иван", "Петров", LocalDate.of(1980, 11, 26),
                new HealthInfo(normalTemperature, normalPressure));

        // Моки
        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
        SendAlertService sendAlertService = Mockito.mock(SendAlertService.class);

        // Конфигурация мока
        Mockito.when(patientInfoRepository.getById(patientId)).thenReturn(patientInfo);

        // Создаем сервис мед. показаний
        MedicalService medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);

        // Проверка давления
        medicalService.checkBloodPressure(patientId, normalPressure);

        // Проверка температуры
        medicalService.checkTemperature(patientId, normalTemperature);

        // Проверка, что метод sendAlertService.send() НЕ вызывается
        Mockito.verify(sendAlertService, Mockito.never()).send(Mockito.anyString());
    }
}
