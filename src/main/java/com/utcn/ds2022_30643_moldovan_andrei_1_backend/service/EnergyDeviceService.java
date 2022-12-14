package com.utcn.ds2022_30643_moldovan_andrei_1_backend.service;

import com.utcn.ds2022_30643_moldovan_andrei_1_backend.dto.EnergyDeviceDto;
import com.utcn.ds2022_30643_moldovan_andrei_1_backend.dto.MeasurementDto;
import com.utcn.ds2022_30643_moldovan_andrei_1_backend.entity.EnergyDevice;
import com.utcn.ds2022_30643_moldovan_andrei_1_backend.entity.Measurement;
import com.utcn.ds2022_30643_moldovan_andrei_1_backend.entity.User;
import com.utcn.ds2022_30643_moldovan_andrei_1_backend.exception.EnergyDeviceNotFoundException;
import com.utcn.ds2022_30643_moldovan_andrei_1_backend.persistance.api.RepositoryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnergyDeviceService {
    private final RepositoryFactory factory;

    @Transactional
    public EnergyDeviceDto findById(Integer id){
        return EnergyDeviceDto.energyDeviceDtoFromEnergyDevice(factory.createEnergyDeviceRepository().findById(id).orElseThrow(EnergyDeviceNotFoundException::new));
    }

    @Transactional
    public List<EnergyDeviceDto> findAll(){
        return factory.createEnergyDeviceRepository().findAll().stream().map(EnergyDeviceDto::energyDeviceDtoFromEnergyDevice).collect(Collectors.toList());
    }

    @Transactional
    public EnergyDeviceDto insert(EnergyDeviceDto dto){
        EnergyDevice device = EnergyDeviceDto.energyDeviceFromEnergyDeviceDto(dto);
        return EnergyDeviceDto.energyDeviceDtoFromEnergyDevice(factory.createEnergyDeviceRepository().save(device));
    }

    @Transactional
    public EnergyDeviceDto update(EnergyDeviceDto dto){
        EnergyDevice device = EnergyDeviceDto.energyDeviceFromEnergyDeviceDto(dto);
        return EnergyDeviceDto.energyDeviceDtoFromEnergyDevice(factory.createEnergyDeviceRepository().save(device));
    }

    @Transactional
    public void remove(Integer id){
        Optional<EnergyDevice> device = factory.createEnergyDeviceRepository().findById(id);
        device.ifPresent(value -> {
            List<User> users = factory.createUserRepository().findAll();
            for(User user: users){
                for(EnergyDevice uDevice: user.getDevices())
                    if(uDevice.equals(value)){
                        user.removeDevice(uDevice);
                        break;
                    }
            }
            List<Measurement> measurements = factory.createMeasurementRepository().findAll();
            for(Measurement measurement: measurements){
                if(measurement.getDevice().equals(value)){
                    factory.createMeasurementRepository().remove(measurement);
                }
            }
            factory.createEnergyDeviceRepository().remove(value);
        });
    }

    @Transactional
    public MeasurementDto addMeasurement(EnergyDeviceDto dto, Double consumption){
        EnergyDevice device = EnergyDeviceDto.energyDeviceFromEnergyDeviceDto(dto);
        return MeasurementDto.measurementDtoFromMeasurement(factory.createMeasurementRepository().save(device.createMeasurement(consumption)));
    }

    @Transactional
    public MeasurementDto addMeasurement(EnergyDeviceDto deviceDto, MeasurementDto measurementDto){
        Measurement m = MeasurementDto.measurementFromMeasurementDto(measurementDto);
        return MeasurementDto.measurementDtoFromMeasurement(factory.createMeasurementRepository().save(m));
    }
}
