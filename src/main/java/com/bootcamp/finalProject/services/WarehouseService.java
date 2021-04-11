package com.bootcamp.finalProject.services;

import com.bootcamp.finalProject.dtos.OrderDTO;
import com.bootcamp.finalProject.dtos.OrderRequestDTO;
import com.bootcamp.finalProject.dtos.SubsidiaryResponseDTO;
import com.bootcamp.finalProject.exceptions.DeliveryStatusException;
import com.bootcamp.finalProject.exceptions.OrderIdNotFoundException;
import com.bootcamp.finalProject.exceptions.OrderTypeException;
import com.bootcamp.finalProject.exceptions.SubsidiaryNotFoundException;
import com.bootcamp.finalProject.model.Order;
import com.bootcamp.finalProject.model.Subsidiary;
import com.bootcamp.finalProject.repositories.ISubsidiaryRepository;
import com.bootcamp.finalProject.repositories.OrderRepository;
import com.bootcamp.finalProject.utils.OrderNumberCMUtil;
import com.bootcamp.finalProject.utils.OrderResponseMapper;
import com.bootcamp.finalProject.utils.SubsidiaryResponseMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.bootcamp.finalProject.utils.ValidationPartUtils.DSOrderTypeValidation;
import static com.bootcamp.finalProject.utils.ValidationPartUtils.deliveryStatusValidation;

@Service
public class WarehouseService implements IWarehouseService {

    SubsidiaryResponseMapper subsidiaryMapper = new SubsidiaryResponseMapper();

    private OrderRepository orderRepository;

    private ISubsidiaryRepository subsidiaryRepository;

    public WarehouseService(OrderRepository orderRepository, ISubsidiaryRepository subsidiaryRepository) {
        this.orderRepository = orderRepository;
        this.subsidiaryRepository = subsidiaryRepository;
    }

    @Override
    public SubsidiaryResponseDTO findSubsidiaryOrders(OrderRequestDTO orderRequest) throws OrderTypeException, DeliveryStatusException, SubsidiaryNotFoundException {
        List<Order> orders;
        Subsidiary subsidiary;
        if (deliveryStatusValidation(orderRequest.getDeliveryStatus())) {
            Sort sort = DSOrderTypeValidation(orderRequest.getOrder());
            Long idSubsidiary = orderRequest.getDealerNumber();
            subsidiary = subsidiaryRepository.findById(idSubsidiary).orElse(null);
            if (subsidiary != null) {
                if (orderRequest.getDeliveryStatus() == null) {
                    orders = orderRepository.findByIdSubsidiary(idSubsidiary, sort);
                } else {
                    orders = orderRepository.findByIdSubsidiaryAndDeliveryStatus(idSubsidiary, orderRequest.getDeliveryStatus(), sort);
                }
                subsidiary.setOrders(orders);
            } else {
                throw new SubsidiaryNotFoundException();
            }
        } else {
            throw new DeliveryStatusException();
        }
        return subsidiaryMapper.toDTO(subsidiary);
    }

    @Override
    public OrderDTO findByOrderNumberCM(String orderNumberCM) throws OrderIdNotFoundException, SubsidiaryNotFoundException {
        Long id = Long.valueOf(OrderNumberCMUtil.getNumberOR(orderNumberCM));

        Long idSubsidiary = Long.valueOf(OrderNumberCMUtil.getNumberCE(orderNumberCM));

        Subsidiary s = subsidiaryRepository.findById(idSubsidiary).orElseThrow(SubsidiaryNotFoundException::new);
        Order o = orderRepository.findByIdOrderAndSubsidiary(id, s).orElseThrow(OrderIdNotFoundException::new);
        return new OrderResponseMapper().toDTO(o, s.getIdSubsidiary());
    }
}