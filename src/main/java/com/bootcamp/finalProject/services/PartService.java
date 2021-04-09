package com.bootcamp.finalProject.services;

import com.bootcamp.finalProject.dtos.OrderRequestDTO;
import com.bootcamp.finalProject.dtos.OrderResponseDTO;
import com.bootcamp.finalProject.dtos.PartRequestDTO;
import com.bootcamp.finalProject.dtos.PartResponseDTO;
import com.bootcamp.finalProject.exceptions.OrderTypeException;
import com.bootcamp.finalProject.exceptions.TypeOfQueryException;
import com.bootcamp.finalProject.mnemonics.DeliveryStatus;
import com.bootcamp.finalProject.model.Order;
import com.bootcamp.finalProject.model.Part;
import com.bootcamp.finalProject.mnemonics.QueryType;
import com.bootcamp.finalProject.repositories.OrderRepository;
import com.bootcamp.finalProject.repositories.PartRepository;
import com.bootcamp.finalProject.utils.PartResponseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.bootcamp.finalProject.utils.ValidationPartUtils.*;

@Service
public class PartService implements IPartService {

    PartResponseMapper mapper = new PartResponseMapper();

    @Autowired
    private PartRepository partRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Override
    public void save(Part part) {
        partRepository.save(part);
    }

    @Override
    public void delete(Long id) {
        partRepository.deleteById(id);
    }

    @Override
    public Part findById(Long id) {
        return partRepository.findById(id).orElse(null);
    }

    @Override
    public List<Part> findAll() {
        return partRepository.findAll();
    }

    public List<PartResponseDTO> findPart(PartRequestDTO partRequest) throws TypeOfQueryException, OrderTypeException {
        List<Part> parts = new ArrayList<>();
        if (typeOfQueryValidation(partRequest.getQueryType())) {
            Sort sort = POrderTypeValidation(partRequest.getOrder());
            switch (partRequest.getQueryType()) {
                case QueryType.COMPLETE:
                    parts = partRepository.findAll(sort);
                    break;
                case QueryType.PARTIAL:
                    parts = partRepository.findByLastModification(partRequest.getDate(), sort);
                    break;
                case QueryType.VARIATION:
                    parts = partRepository.findByPriceCreateAt(partRequest.getDate(), sort);
                    break;
                default:
                    throw new TypeOfQueryException();
            }
        }
        return mapper.toDTO(parts);
    }

    public List<OrderResponseDTO> findOrder(OrderRequestDTO orderRequest) throws OrderTypeException {
        List<Order> orders = new ArrayList<>();
        if (deliveryStatusValidation(orderRequest.getDeliveryStatus())) {
            Sort sort = DSOrderTypeValidation(orderRequest.getOrder());
            if (orderRequest.getDeliveryStatus() == null) {
                orders = orderRepository.findAll(sort);
            } else {
                orders = orderRepository.findByDeliveryStatus(orderRequest.getDeliveryStatus(), sort);
            }

        }
        //TODO: AGREGAR LO DEL MAPER.
        //return mapper.toDTO(orders);
        return null;
    }
}