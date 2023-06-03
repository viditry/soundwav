package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.AnomalyItemDTO;
import com.mycompany.myapp.service.dto.SoundWavDTO;

import java.util.Optional;

public interface SoundWavService {

    Boolean saveWaveData();


    Optional<SoundWavDTO> findOne(String id);

}
