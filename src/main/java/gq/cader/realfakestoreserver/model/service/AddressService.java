package gq.cader.realfakestoreserver.model.service;

import gq.cader.realfakestoreserver.exception.AddressException;
import gq.cader.realfakestoreserver.model.entity.Address;
import gq.cader.realfakestoreserver.model.repository.AddressRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressService {

    /*private static final Logger LOG =
        LoggerFactory.getLogger(AddressService.class);*/
    private  AddressRepository addressRepository;

    @Autowired
    public AddressService(AddressRepository addressRepository){
        this.addressRepository = addressRepository;
    }
    public  Address findById(Integer id){
        return addressRepository.findById(id).orElseThrow(
            () ->new AddressException("Address:" + id + " Not Found"));
    }
}
