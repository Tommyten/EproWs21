package de.thbingen.epro.service;

import de.thbingen.epro.model.business.OkrUser;
import de.thbingen.epro.model.dto.OkrUserDto;
import de.thbingen.epro.model.mapper.OkrUserMapper;
import de.thbingen.epro.repository.OkrUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OkrUserService {

    private final OkrUserRepository OkrUserRepository;
    private final OkrUserMapper OkrUserMapper;

    public OkrUserService(OkrUserRepository OkrUserRepository, OkrUserMapper OkrUserMapper) {
        this.OkrUserRepository = OkrUserRepository;
        this.OkrUserMapper = OkrUserMapper;
    }

    public List<OkrUserDto> findAll() {
        List<OkrUser> OkrUsers = OkrUserRepository.findAll();
        return OkrUserMapper.okrUserListToOkrUserDtoList(OkrUsers);
    }

    public Optional<OkrUserDto> findById(Long id) {
        Optional<OkrUser> OkrUser = OkrUserRepository.findById(id);
        return OkrUser.map(OkrUserMapper::okrUserToDto);
    }

    public OkrUserDto saveOkrUser(OkrUserDto OkrUserDto) {
        OkrUser OkrUser = OkrUserMapper.dtoToOkrUser(OkrUserDto);
        return OkrUserMapper.okrUserToDto(OkrUserRepository.save(OkrUser));
    }

    public boolean existsById(Long id) {
        return OkrUserRepository.existsById(id);
    }

    public void deleteById(Long id) {
        OkrUserRepository.deleteById(id);
    }

}