package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.SoundWav;
import com.mycompany.myapp.service.dto.SoundWavDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {})
public interface SoundWavMapper extends EntityMapper<SoundWavDTO, SoundWav> {
}
